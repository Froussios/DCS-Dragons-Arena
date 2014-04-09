package nl.dcs.network.client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.util.Random;

import nl.dcs.da.tss.EventQueue;
import nl.dcs.da.tss.OutOfSyncException;
import nl.dcs.da.tss.SynchronizedState;
import nl.dcs.da.tss.TSS;
import nl.dcs.da.tss.events.Event;
import nl.dcs.network.NetworkRessource;
import nl.dcs.network.server.DNS;
import nl.dcs.network.server.ServerInterface;

/**
 * Created by Ivanis on 18/03/14.
 */
public class ClientNetwork
		extends NetworkRessource
		implements ClientInterface
{

	private static final long serialVersionUID = 9038266067124556457L;

	private final TSS state;
	private ServerInterface server;
	private final long id;
	private String serverAddress;


	public ClientNetwork(TSS state)
			throws RemoteException
	{
		this(state, new Random().nextLong());
	}


	public ClientNetwork(TSS state, long id)
			throws RemoteException
	{
		super();
		this.state = state;
		this.id = id;
	}


	public static void main(String[] args)
	{

        try {
            ClientNetwork c = new ClientNetwork(new TSS(new SynchronizedState(0, new EventQueue()), TSS.RECOMMENDED_MAX_DELAY));
            System.out.println(c.getId());
            c.connect("//:1099/");
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }

    }


	public long getId()
	{
		return id;
	}


	@Override
	public void update(Event e)
			throws RemoteException, OutOfSyncException
	{
		System.out.println("New event from server: " + e);

		this.state.receiveEvent(e);
	}


	public void connect(int i)
			throws RemoteException, NotBoundException
	{
		this.server = DNS.lookup(i);
		TSS state = this.server.register(this.id, this);
		this.state.loadFrom(state);

	}


	public void connect(String address)
			throws RemoteException, NotBoundException
	{
		this.server = this.lookup(address);
        if (this.server == null){
            return;
        }
		TSS state = this.server.register(this.id, this);
		this.state.loadFrom(state);

		if (this.serverAddress == null || this.serverAddress.isEmpty())
			this.serverAddress = address;
	}


	public void reconnect()
			throws RemoteException, NotBoundException
	{
		this.connect(this.serverAddress);
	}


	public void disconnect()
			throws RemoteException
	{
		this.server.unregister(id, this);
	}


	public void sendEvent(Event e)
			throws RemoteException, NotBoundException, ServerNotActiveException, OutOfSyncException
	{
		this.server.sendEvent(id, e);

		System.out.println("New event  to  server: " + e);

	}
}
