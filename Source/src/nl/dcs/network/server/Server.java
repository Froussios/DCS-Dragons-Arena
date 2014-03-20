package nl.dcs.network.server;

import nl.dcs.da.tss.events.Event;
import nl.dcs.network.client.ClientInterface;
import org.apache.commons.collections4.bag.TreeBag;

import java.net.InetAddress;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Ivanis
 */
public class Server extends UnicastRemoteObject implements ServerInterface {
    
    private InetAddress ip;
    private Integer port;
    private TreeBag eventQueue;
    private String name;
    private HashMap<Long, ServerInterface> broadcast;
    private HashMap<Long, ClientInterface> clients;
    private long id;
    private Logger logger;
    
    public Server(String name, Integer port) throws RemoteException {
        this(name, port, null);
    }
    
    public Server(String name, Integer port, HashMap<Long, ServerInterface> broadcast) throws RemoteException {
        super();
        this.port = port;
        this.eventQueue = new TreeBag();
        this.name = name;
        this.broadcast = broadcast;
        this.clients = new HashMap<>();
        this.id = this.name.hashCode();
        this.logger = Logger.getLogger(this.getClass().getName());
        this.logger.log(Level.FINE, "Creation of server object : {0}", this.id);
    }
    
    public void expose (){
        try {
            Registry registry = LocateRegistry.createRegistry(this.port);
            registry.rebind("SERVER", this);
        } catch (RemoteException e) {
        }
        this.logger.log(Level.FINE, "Server published : {0}", this.toString());
    }
    
    public static void main(String[] args) throws RemoteException {
        System.out.println("Server start");
        if (args.length != 2){
            System.exit(1);
        }
        Server server = new Server(args[0], Integer.parseInt(args[1]));
        server.expose();
        try {
            Scanner input = new Scanner(System.in);
            do {
                System.out.print("> ");
                String command = input.nextLine().toLowerCase();
                switch (command) {
                    case "unexpose":
                        server.unexpose();
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
        } catch (RemoteException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void terminate() throws RemoteException {
        for (ClientInterface c : clients.values()) {
            this.unregister(this.id, c);
        }
        System.exit(0);
    }
    
    @Override
    public String toString() {
        return "Server{" + "ip=" + ip + ", port=" + port + ", name=" + name + '}';
    }
    
    @Override
    public void sendEvent(long sender, Event e) throws RemoteException {
        System.out.println(e);
        eventQueue.add(e);
        // spread to the other servers
        if (eventQueue.getCount(e) == 1) {
            for (ServerInterface server : this.broadcast.values()) {
                server.sendEvent(this.id, e);
            }
            // spread to the clients
            for (ClientInterface client : this.clients.values()) {
                System.out.println(client);
                client.update(id, e);
            }
        }
    }
    
    @Override
    public void register(long sender, ClientInterface c) throws RemoteException {
        this.clients.put(sender, c);
    }
    
    @Override
    public void unregister(long sender, ClientInterface c) throws RemoteException {
        if (clients.get(sender) == c) {
            this.clients.remove(sender);
        }
    }
    
    public void open() {
        
    }
    
    public void start() {
        
    }
    
    public InetAddress getIp() {
        return ip;
    }
    
    public void setIp(InetAddress ip) {
        this.ip = ip;
    }
    
    public Integer getPort() {
        return port;
    }
    
    public void setPort(Integer port) {
        this.port = port;
    }
    
    public TreeBag getEventQueue() {
        return eventQueue;
    }
    
    public void setEventQueue(TreeBag eventQueue) {
        this.eventQueue = eventQueue;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    private void unexpose() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}