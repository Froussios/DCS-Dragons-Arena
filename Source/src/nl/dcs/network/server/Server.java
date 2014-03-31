package nl.dcs.network.server;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.dcs.da.tss.OutOfSyncException;
import nl.dcs.da.tss.State;
import nl.dcs.da.tss.TSS;
import nl.dcs.da.tss.TimedTSS;
import nl.dcs.da.tss.events.Connect;
import nl.dcs.da.tss.events.Event;
import nl.dcs.da.tss.events.MarkEvent;
import nl.dcs.da.tss.events.OpenGame;
import nl.dcs.da.tss.events.StartGame;
import nl.dcs.network.NetworkRessource;
import nl.dcs.network.client.ClientInterface;

import org.apache.commons.collections4.bag.TreeBag;

/**
 * @version 0.1
 * @author Ivanis
 */
public class Server
		extends NetworkRessource
		implements ServerInterface
{

	private static final long serialVersionUID = 5446617274331655787L;
	private static final long maxClientDelay = 150000;

	private final InetAddress ip;
	private final Integer port;
	private final TreeBag<Event> eventBag;
	private final HashMap<Long, ClientInterface> clients;
	private final Integer id;
	private final Integer window;
	private final TSS state;


	/**
	 * Constructor
	 * 
	 * @param id server's identifier
	 * @param ip server's address default to
	 *            {@link java.net.InetAddress#getLoopbackAddress()}
	 * @param port server"s port default to 1099
	 * @param window number of server to transfert the data upon reception
	 *            default to 2
	 * @throws RemoteException
	 */
	private Server(int id, InetAddress ip, Integer port, Integer window) throws RemoteException
	{

		super();
		if (window < 0 || window > DNS.getNbServers())
		{
			throw new IllegalArgumentException();
		}
		this.ip = ip;
		this.port = port;
		this.eventBag = new TreeBag<Event>();
		this.id = id;
		this.window = window;
		this.clients = new HashMap<>();
		this.state = new TimedTSS(new State(), 50L);
		Logger.getLogger(this.getClass().getName()).log(Level.FINE, "Creation of server object : {0}", this.id);
		System.out.println(new StringBuilder().append("Creation of server object : ").append(this.id).toString());
	}


	private Server(int id, InetAddress ip, Integer port) throws RemoteException
	{
		this(id, ip, port, 2);
	}


	private Server(int id, InetAddress ip) throws RemoteException
	{
		this(id, ip, 1099, 2);
	}


	private Server(int id, Integer port) throws RemoteException
	{
		this(id, InetAddress.getLoopbackAddress(), port);
	}


	/**
	 * Constructor
	 * 
	 * @param id the identifier in the network
	 * @param address the rmi address of the server
	 * @param window the number of server you should contact
	 * @throws UnknownHostException if the host is not known
	 * @throws RemoteException @see
	 *             java.rmi.server.UnicastRemoteObject#UnicastRemoteObject()
	 * @throws MalformedURLException if the address is not correct
	 */
	public Server(int id, String address, int window) throws UnknownHostException, RemoteException, MalformedURLException
	{
		super();
		this.id = id;
		address = address.replace("rmi:", "");
		if (address.length() - address.replace(":", "").length() != 1)
		{
			throw new MalformedURLException();
		}
		String[] tokens = address.split("/");
		int port = 0;
		InetAddress ip = InetAddress.getLoopbackAddress();
		for (String token : tokens)
		{
			if (token.contains(":"))
			{
				String[] subtoken = token.split(":");
				ip = subtoken[0].isEmpty() ? InetAddress.getLoopbackAddress() : InetAddress.getByName(subtoken[0]);
				port = subtoken.length <= 1 ? 1099 : Integer.parseInt(subtoken[1]);
			}
		}
		this.ip = ip;
		this.state = new TimedTSS(new State(), 60000L);
		this.port = port;
		this.clients = new HashMap<>();
		this.eventBag = new TreeBag<>();
		this.window = window;


		Logger.getLogger(this.getClass().getName()).log(Level.FINE, "Creation of server object : {0}", this.id);
		System.out.println(new StringBuilder().append("Creation of server object : ").append(this.id).toString());
	}


	public Server(int id) throws UnknownHostException, RemoteException, MalformedURLException
	{
		this(id, DNS.getServerAddress(id), 2);
	}


	public Server(int id, int window) throws UnknownHostException, RemoteException, MalformedURLException
	{
		this(id, DNS.getServerAddress(id), window);
	}


	/**
	 * Launch the server in wait of event to transfer
	 * 
	 * @param args Only one is accepted, the id of the server (Bound to change
	 *            during production)
	 */
	public static void main(String[] args)
	{
		try
		{
			if (args.length != 1)
			{
				System.exit(1);
			}
			System.out.println(new StringBuilder().append("Server start ").append(args[0]).toString());
			Server server = new Server(Integer.parseInt(args[0]));
			System.out.println(server);
			server.expose();

			Scanner input = new Scanner(System.in);
			while (true)
			{
				System.out.print("> ");
				String command = input.next().toLowerCase();
				switch (command)
				{
					case "send":
						server.transferEvent(new MarkEvent(server.state.getSimulationTime()));
						break;
					case "open":
						server.transferEvent(new OpenGame());
						break;
					case "start":
						server.transferEvent(new StartGame());
						break;
					case "terminate":
					case "exit":
						server.terminate();
					default:
						System.out.println("Unknown command");
				}
			}
		}
		catch (UnknownHostException | MalformedURLException | RemoteException | OutOfSyncException | NotBoundException | ServerNotActiveException ex)
		{
			Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
		}
	}


	/**
	 * Expose the server interface to the other network resources using the RMI
	 * registry
	 * 
	 * @throws RemoteException
	 */
	public void expose() throws RemoteException
	{
		Registry registry = LocateRegistry.createRegistry(this.port);
		registry.rebind("SERVER", this);
		Logger.getLogger(this.getClass().getName()).log(Level.FINE, "Server published : {0}", this.toString());
		System.out.println("Server published : " + "SERVER");
	}


	private void terminate() throws RemoteException
	{

		for (Long id : clients.keySet())
		{
			this.unregister(id, clients.get(id));
		}
		clients.clear();
		System.exit(0);
	}


	/**
	 * {@inheritDoc}
	 * 
	 * @param sender
	 * @param event
	 * @throws RemoteException
	 * @throws NotBoundException
	 * @throws ServerNotActiveException
	 */
	@Override
	public void sendEvent(long sender, Event event) throws RemoteException, NotBoundException, ServerNotActiveException, OutOfSyncException
	{
		if (this.verifyEvent(event, sender))
		{
			if (this.addToEventBag(event))
			{
				spreadServers(event, DNS.getNbServers());
				spreadClients(event);
			}
			state.receiveEvent(event);
		}

	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void transferEvent(Event event) throws RemoteException, NotBoundException, ServerNotActiveException, OutOfSyncException
	{
		if (this.addToEventBag(event))
		{
			spreadServers(event, this.window);
			spreadClients(event);
		}
		state.receiveEvent(event);
	}


	private void spreadClients(Event e) throws RemoteException, OutOfSyncException
	{
		for (Long id : this.clients.keySet())
		{
			clients.get(id).update(e);
		}
	}


	/**
	 * Send event to nbServer other servers in the order of declaration in the
	 * list of servers If the server number added to nbServer is higher than the
	 * total number of server, it will send to servers start from 0
	 * 
	 * @param event the event to send
	 * @param nbServer the number of server to send the event to
	 * @throws RemoteException
	 * @throws NotBoundException
	 * @throws ServerNotActiveException
	 * @throws OutOfSyncException
	 */
	private void spreadServers(Event event, int nbServer) throws RemoteException, NotBoundException, ServerNotActiveException, OutOfSyncException
	{
		for (int i = this.id + 1; i < this.id + nbServer + 1 && i < DNS.getNbServers(); i++)
		{
			System.out.println("sending to " + i);
			DNS.lookup(i).transferEvent(event);
		}

		if (this.id + nbServer + 1 > DNS.getNbServers())
		{
			for (int i = 0; i < this.id - nbServer; i++)
			{
				System.out.println("sending to " + i);
				DNS.lookup(i).transferEvent(event);
			}
		}
	}


	/**
	 * Add the event to the event bag
	 * 
	 * @param event the event to add to the bag
	 * @return true if the event if added for the first time
	 */
	private boolean addToEventBag(Event event)
	{
		eventBag.add(event);
		System.out.println(event + "\n count : " + eventBag.getCount(event));
		return eventBag.getCount(event) == 1;
	}


	/**
	 * Add the client to the list of client connected to this server
	 * 
	 * @param sender the id of the client
	 * @param client the client object
	 * @return the state of the game on the server
	 * @throws RemoteException
	 */
	@Override
	public TSS register(long sender, ClientInterface client) throws RemoteException
	{
		this.clients.put(sender, client);
		return this.state;
	}


	/**
	 * Remove a client from the list of clients connected to this server
	 * 
	 * @param sender the id of the client
	 * @param client the client object
	 * @throws RemoteException in case of problem with RMI
	 * @see UnicastRemoteObject
	 */
	@Override
	public void unregister(long sender, ClientInterface client) throws RemoteException
	{
		this.clients.remove(sender);
		System.gc();
		System.runFinalization();
	}


	/**
	 * Return the the RMI address associated with this server
	 * 
	 * @return a rmi address usable to reach this server registry
	 */
	public String getRMIAddress()
	{
		return "rmi://" + this.ip.getHostAddress() + ":" + this.port + "/";
	}


	@Override
	public String toString()
	{
		return "Server{" + "ip=" + ip + ", port=" + port + ", eventBag=" + eventBag.size() + ", id=" + id + ", window=" + window + '}';
	}


	/**
	 * Check if this event should be accepted by the server, when received from
	 * a client.
	 * 
	 * @param event The event to be filtered
	 * @return true if the event can be accepted
	 */
	public boolean verifyEvent(Event event, long senderId)
	{
		boolean accepted = true;

		// NOTE: do not reject moves after gameover: gameover might be revised
		// and the game resumed

		// Event too retrospective
		if (state.getSimulationTime() - event.getSimulationTime() > maxClientDelay)
			accepted = false;

		// Event in the future
		if (state.getSimulationTime() - event.getSimulationTime() < 1000)
			accepted = false;

		// Game not open or playing
		if (state.getPhase().equals(State.GameState.Closed))
			accepted = false;

		// Attempted action pre-game
		if (state.getPhase().equals(State.GameState.Open) && !(event instanceof Connect))
			accepted = false;

		// Attempted new connection mid-game
		if (!state.getPhase().equals(State.GameState.Open) && (event instanceof Connect))
			accepted = false;

		// Control events are reserved for clients
		if (event instanceof StartGame || event instanceof OpenGame)
			accepted = false;

		// Clients can only control their own character
		if (event.getIssuer() != senderId)
			accepted = false;

		if (!clients.containsKey(senderId))
			accepted = false;
		return accepted;
	}
}
