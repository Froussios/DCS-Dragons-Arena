package nl.dcs.network.server;

import nl.dcs.da.tss.events.Event;
import nl.dcs.network.client.ClientInterface;
import org.apache.commons.collections4.bag.TreeBag;

import java.net.InetAddress;

import java.rmi.NotBoundException;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.rmi.server.ServerNotActiveException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.dcs.da.tss.events.MarkEvent;
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
    public static void main(String[] args) throws RemoteException {

        if (args.length != 1) {
            System.exit(1);
        }

        Server server = DNS.getServer(args[0]);
        server.expose();
        System.out.println(new StringBuilder().append("Server start ").append(args[0]).toString());
        try {
            Scanner input = new Scanner(System.in);
            do {
                System.out.print("> ");
                String command = input.next().toLowerCase();
                switch (command) {
                    case "send":
                        server.sendEvent(server.id, new MarkEvent(1L));
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
        } catch (RemoteException | NotBoundException | ServerNotActiveException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private InetAddress ip;
    private Integer port;
    private TreeBag eventQueue;
    private String name;
    private final String[] broadcast;
    private final HashMap<Long, ClientInterface> clients;
    private long id;

    /**
     *
     * @param id
     * @param name
     * @param port
     * @param broadcast
     * @throws RemoteException
     */
    public Server(Long id, String name, Integer port, String[] broadcast) throws RemoteException {
        super();
        this.ip = InetAddress.getLoopbackAddress();
        this.port = port;
        this.eventQueue = new TreeBag();
        this.name = name;
        this.clients = new HashMap<>();
        this.broadcast = broadcast;
        this.id = id;
        Logger.getLogger(this.getClass().getName()).log(Level.FINE, "Creation of server object : {0}", this.name);
        System.out.println(new StringBuilder().append("Creation of server object : ").append(this.name).toString());

    }

    /**
     *
     * @throws RemoteException
     */
    public void expose() throws RemoteException {
        Registry registry = LocateRegistry.createRegistry(this.port);
        registry.rebind(this.name, this);
        Logger.getLogger(this.getClass().getName()).log(Level.FINE, "Server published : {0}", this.toString());
        System.out.println(new StringBuilder().append("Server published : ").append(this.name).toString());
    }

    private void terminate() throws RemoteException {
        for (ClientInterface c : clients.values()) {
            this.unregister(this.id, c);
        }
        clients.clear();
        System.exit(0);
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return "Server{" + "ip=" + ip + ", port=" + port + ", name=" + name + '}';
    }

    /**
     * #{@link ServerInterface#sendEvent(long, nl.dcs.da.tss.events.Event) }
     * @param sender
     * @param e
     * @throws RemoteException
     * @throws NotBoundException
     * @throws ServerNotActiveException
     */
    @Override
    public void sendEvent(long sender, Event e) throws RemoteException, NotBoundException, ServerNotActiveException {
        System.out.println(e);
        System.out.println(Server.getClientHost());
        eventQueue.add(e);

        if (eventQueue.getCount(e) == 1) {

            //if this event comes from a client broadcast to ever server
            if (clients.containsKey(sender)) {
                for (String server : DNS.getServersNames()) {
                    DNS.find(server).sendEvent(id, e);
                }
            } else {
                // else broadcast to some server
                for (String server : this.broadcast) {
                    DNS.find(server).sendEvent(id, e);
                }
            }

            // spread to the clients
            for (ClientInterface client : this.clients.values()) {
                client.update(id, e);
            }
        }
    }

    /**
     *
     * @param sender
     * @param c
     * @throws RemoteException
     */
    @Override
    public void register(long sender, ClientInterface c) throws RemoteException {
        this.clients.put(sender, c);
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
     * Return the name of the server
     * @return the name of the server
     */
    public String getName() {
        return name;
    }

    /**
     * Return the the RMI address associated with this server
     * @return a rmi address usable to reach this server
     */
    public String getRMIAddress() {
        return new StringBuilder().append("//")
                .append(this.ip)
                .append(":")
                .append(this.port)
                .append("/")
                .append(this.name)
                .toString();
    }
}
