package nl.dcs.network.client;

import nl.dcs.da.tss.OutOfSyncException;
import nl.dcs.da.tss.TSS;
import nl.dcs.da.tss.events.Event;
import nl.dcs.network.NetworkRessource;
import nl.dcs.network.server.DNS;
import nl.dcs.network.server.ServerInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.util.Random;

/**
 * Created by Ivanis on 18/03/14.
 */
public class ClientNetwork extends NetworkRessource implements ClientInterface {
    private static final long serialVersionUID = 9038266067124556457L;

    private final TSS state;
    private ServerInterface server;
    private long id;

    public ClientNetwork(TSS state) throws RemoteException {
        this(state, new Random().nextLong());
    }

    public ClientNetwork(TSS state, long id) throws RemoteException {
        super();
        this.state = state;
        this.id = id;
    }

    public void main(String[] args) {

    }

    public long getId() {
        return id;
    }

    @Override
    public void update(Event e) throws RemoteException, OutOfSyncException {
        this.state.receiveEvent(e);
    }


    public void connect(int i) throws RemoteException, NotBoundException {
        this.server = (ServerInterface) DNS.lookup(i);
        TSS state = this.server.register(this.id, this);
        if (state != null)
            this.state.loadFrom(state);
        else
            System.out.println("State load failed (Server not open)");
    }

    public void disconnect() throws RemoteException {
        this.server.unregister(id, this);
    }

    public void sendEvent(Event e) throws RemoteException, NotBoundException, ServerNotActiveException, OutOfSyncException {
        this.server.sendEvent(id, e);
    }
}
