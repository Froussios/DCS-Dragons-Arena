package nl.dcs.network.client;

import nl.dcs.da.tss.TSS;
import nl.dcs.da.tss.events.Event;
import nl.dcs.network.server.ServerInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by Ivanis on 18/03/14.
 */
public class NetworkClient extends UnicastRemoteObject implements ClientInterface {

    private TSS state;
    private ServerInterface server;
    private long id;
    public void main (String[] args){

    }

    public NetworkClient (TSS state) throws RemoteException {
        super();
        this.state = state;
    }

    @Override
    public void update(long sender, Event e) throws RemoteException {

    }

    @Override
    public void updateFromDump(long sender, TSS state) throws RemoteException {
        
    }

    public void connect () throws RemoteException, NotBoundException{
            Registry registry = LocateRegistry.getRegistry(1099);
            this.server = (ServerInterface) registry.lookup("SERVER");
            this.server.register(id, this);
    }

    public void disconnect () throws RemoteException{
        this.server.unregister(id, this);
    }
}
