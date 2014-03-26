package nl.dcs.network.server;

import nl.dcs.da.tss.events.Event;
import nl.dcs.network.client.ClientInterface;
import org.apache.commons.collections4.bag.TreeBag;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

import java.rmi.NotBoundException;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.rmi.server.ServerNotActiveException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.dcs.da.tss.State;
import nl.dcs.da.tss.TSS;
import nl.dcs.da.tss.TimedTSS;
import nl.dcs.da.tss.util.Alarm;
import nl.dcs.network.NetworkRessource;

/**
 * @author Ivanis
 */
public class Server extends NetworkRessource implements ServerInterface {

    private static final long serialVersionUID = 5446617274331655787L;

    /**
     *
     * @param args
     * @throws RemoteException
     */
    public static void main(String[] args){
        try {
            if (args.length != 1) {
                System.exit(1);
            }
            System.out.println(new StringBuilder().append("Server start ").append(args[0]).toString());
            Server server = new Server(Integer.parseInt(args[0]));
            server.expose();
            
            Scanner input = new Scanner(System.in);
            do {
                System.out.print("> ");
                String command = input.next().toLowerCase();
                switch (command) {
                    case "send":
                        break;
                    case "open":
                        server.open();
                        break;
                    case "start":
                        server.start();
                        ;
                        break;
                    case "terminate":
                    case "exit":
                        server.terminate();
                    default:
                        System.out.println("Unknown command");
                }
            } while (true);
        } catch (UnknownHostException | MalformedURLException | RemoteException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main (){
        Server.main(new String[]{"1"});
    }

    private final InetAddress ip;
    private final Integer port;
    private final TreeBag<Event> eventQueue;
    private final HashMap<Long, ClientInterface> clients;
    private final Integer id;
    private final Integer window;
    private final TSS state;
    
    
    private Server(int id, InetAddress ip, Integer port, Integer window) throws RemoteException {
        
        super();
        if (window < 0 || window > DNS.getNbServers()) throw new IllegalArgumentException();
        this.ip = ip;
        this.port = port;
        this.eventQueue = new TreeBag();
        this.id = id;
        this.window = window;
        this.clients = new HashMap<>();
        this.state =  new TimedTSS (new State (), 50L);
        Logger.getLogger(this.getClass().getName()).log(Level.FINE, "Creation of server object : {0}", this.id);
        System.out.println(new StringBuilder().append("Creation of server object : ").append(this.id).toString());

    }

    private Server(int id, InetAddress ip, Integer port) throws RemoteException {
        
        this(id, ip, port, 2);

    }
    
    private Server(int id, InetAddress ip) throws RemoteException {
        
        this(id, ip, 1099, 2);

    }
    /**
     *
     * @param name
     * @param port
     * @param broadcast
     * @throws RemoteException
     */
    private Server(int id, Integer port) throws RemoteException {
        this(id, InetAddress.getLoopbackAddress(), port);
    }
    
    public Server(int id,  String address, int window) throws UnknownHostException, RemoteException, MalformedURLException{
        this.id = id;
        address = address.replace("rmi:", "");
        if (address.length() - address.replace(":", "").length() != 1) throw new MalformedURLException ();
        String[] tokens = address.split("/");
        int port = 0; InetAddress ip = InetAddress.getLoopbackAddress();
        for (String token : tokens){
            if (token.contains(":")) {
                String[] subtoken = token.split(":");
                ip = subtoken[0].isEmpty() ? InetAddress.getLoopbackAddress() : InetAddress.getByName(subtoken[0]);
                port = subtoken.length <= 1 ? 1099 : Integer.parseInt(subtoken[1]);
            }
        }
        this.ip = ip;
        this.state =  new TimedTSS (new State (), 50L);
        this.port = port;
        this.clients = new HashMap<>();
        this.eventQueue = new TreeBag<>();
        this.window = window;
    }
    
    public Server (int id) throws UnknownHostException, RemoteException, MalformedURLException{
        this(id, DNS.getServerAddress(id), 2);
    }
    
    public Server (int id, int window) throws UnknownHostException, RemoteException, MalformedURLException{
        this(id, DNS.getServerAddress(id), window);
    }
    
    /**
     * Expose the server interface to the other netwok ressource using the RMI registery
     * @throws RemoteException
     */
    public void expose() throws RemoteException {
        Registry registry = LocateRegistry.createRegistry(this.port);
        registry.rebind("SERVER", this);
        Logger.getLogger(this.getClass().getName()).log(Level.FINE, "Server published : {0}", this.toString());
        System.out.println(new StringBuilder().append("Server published : ").append("SERVER").toString());
    }

    private void terminate() throws RemoteException {
        for (Long id : clients.keySet()) {
            this.unregister(id, clients.get(id));
        }
        clients.clear();
        System.exit(0);
    }



    /**
     * #{@link ServerInterface#sendEvent(long, nl.dcs.da.tss.events.Event) }
     * @param sender
     * @param e
     * @throws RemoteException
     * @throws NotBoundException
     * @throws ServerNotActiveException
     * @throws java.net.MalformedURLException
     */
    @Override
    public void sendEvent(long sender, Event e) throws RemoteException, NotBoundException, ServerNotActiveException, MalformedURLException {

        if (this.beforeSend(e) && clients.containsKey(sender)) {
                spreadServers(e, DNS.getNbServers());
                spreadClients(e);
        }
    }
    
    @Override
    public void transferEvent(Event e) throws RemoteException, NotBoundException, ServerNotActiveException, MalformedURLException {
        if (this.beforeSend(e)) {
                spreadServers(e, this.window);
                spreadClients(e);
        }
    }
    
    private void spreadClients (Event e) throws RemoteException{
       for (ClientInterface client : this.clients.values()) {
          client.update(e);
       }
    }
    
    private void spreadServers (Event e, int nbServer) throws RemoteException, NotBoundException, ServerNotActiveException, MalformedURLException {
        for (int i = this.id; i < nbServer; i++){
            DNS.lookup(i).transferEvent(e);
        }
        
        for (int i = 0; i > this.id; i++){
            DNS.lookup(i).transferEvent(e);
        }
    }
    
    private boolean beforeSend (Event e) throws ServerNotActiveException {
        System.out.println(e);
        System.out.println(Server.getClientHost());
        eventQueue.add(e);
        return eventQueue.getCount(e) == 1;
    }
    
    

    /**
     *
     * @param sender
     * @param c
     * @return 
     * @throws RemoteException
     */
    @Override
    public TSS register(long sender, ClientInterface c) throws RemoteException {
        this.clients.put(sender, c);
        return this.state;
    }

    /**
     *
     * @param sender
     * @param c
     * @throws RemoteException
     */
    @Override
    public void unregister(long sender, ClientInterface c) throws RemoteException {
        this.clients.remove(sender);
        System.gc();
        System.runFinalization();
    }

    /**
     * Send an event to the other servers telling them that the server is open for registration
     */
    public void open() {

    }

    /**
     * Send an event to the other servers telling them that the game has started
     */
    public void start() {

    }

    /**
     *
     * @return
     */
    public InetAddress getIp() {
        return ip;
    }

    /**
     *
     * @return
     */
    public Integer getPort() {
        return port;
    }

    

    /**
     * Return the the RMI address associated with this server
     * @return a rmi address usable to reach this server
     */
    public String getRMIAddress() {
        return new StringBuilder().append("//")
                .append(this.ip.getHostAddress())
                .append(":")
                .append(this.port)
                .append("/")
                .toString();
    }
}
