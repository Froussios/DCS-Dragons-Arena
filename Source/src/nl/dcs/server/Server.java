package nl.dcs.server;

import java.net.InetAddress;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.dcs.da.tss.events.Event;
import nl.dcs.da.tss.util.StateLogger;
import org.apache.commons.collections4.bag.TreeBag;

/**
 *
 * @author Ivanis
 */
public class Server extends UnicastRemoteObject implements RMIInterface{

    
    private  InetAddress ip;
    private Integer port;
    private  StateLogger log;  
    private  TreeBag eventQueue;
    private String name;
    private ArrayList<String> broadcast;
    
    public Server (InetAddress ip, Integer port, String name, ArrayList<String> broadcast) throws RemoteException{
        this.ip = ip;
        this.port = port;
        this.log = new StateLogger ();
        this.eventQueue = new TreeBag();
        this.name = name;
        this.broadcast = broadcast;
    } 

    @Override
    public void recieveEvent(Event e) throws RemoteException {
        System.out.println(e);
        log.log(e);
        eventQueue.add(e);
        if (eventQueue.getCount(e) == 1){
            for (String s : this.broadcast ){
                Server server = Servers.getServerByName(s); 
                
                Registry registry = LocateRegistry.getRegistry("127.0.0.1", server.port);
                
                RMIInterface stub;
                try {
                    stub = (RMIInterface) registry.lookup("Recieve");
                    stub.recieveEvent(e);
                    System.out.println(stub);
                } catch (        NotBoundException | AccessException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            }
        }
    }
    
    
    public static void main (String[] args) throws RemoteException{
        System.out.println("Server start");
       
        try {
            Server skeleton = Servers.getServerByName("EPSILON"); 
            Registry registry = LocateRegistry.createRegistry(skeleton.getPort());
            registry.rebind("Recieve", skeleton); 
            System.out.println(skeleton.getName());
        } catch (Exception ex) { 
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
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

    public StateLogger getStateLog() {
        return log;
    }

    public void setStateLog(StateLogger log) {
        this.log = log;
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

    @Override
    public String toString() {
        return "Server{" + "ip=" + ip + ", port=" + port + ", name=" + name + '}';
    }
 
}
