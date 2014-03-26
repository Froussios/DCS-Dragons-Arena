package nl.dcs.network.client;

import java.net.MalformedURLException;
import nl.dcs.da.tss.TSS;
import nl.dcs.da.tss.events.Event;
import nl.dcs.network.server.ServerInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import nl.dcs.network.NetworkRessource;
import nl.dcs.network.server.DNS;

/**
 * Created by Ivanis on 18/03/14.
 */
public class ClientNetwork extends NetworkRessource implements ClientInterface {
    private static final long serialVersionUID = 9038266067124556457L;

    private TSS state;
    private ServerInterface server;
    private long id;
    
    public void main (String[] args){
        
    }

    public ClientNetwork (TSS state) throws RemoteException {
        super();
        this.state = state;
    }

    @Override
    public void update(Event e) throws RemoteException {

    }

    @Override
    public void updateFromDump(TSS state) throws RemoteException {
        
    }

    public void connect (int i) throws RemoteException, NotBoundException, MalformedURLException{
            this.server = (ServerInterface) DNS.lookup(i);
            this.state = this.server.register(id, this);
    }

    public void disconnect () throws RemoteException{
        this.server.unregister(id, this);
    }
    
    public void sendEvent (Event e) throws RemoteException, NotBoundException, ServerNotActiveException, MalformedURLException{
        this.server.sendEvent(id, e);
    }
}
