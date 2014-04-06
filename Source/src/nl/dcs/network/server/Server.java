package nl.dcs.network.server;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ServerNotActiveException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import nl.dcs.da.tss.OutOfSyncException;
import nl.dcs.da.tss.State;
import nl.dcs.da.tss.TSS;
import nl.dcs.da.tss.TimedTSS;
import nl.dcs.da.tss.events.Event;
import nl.dcs.da.tss.events.MarkEvent;
import nl.dcs.da.tss.events.OpenGame;
import nl.dcs.da.tss.events.StartGame;
import nl.dcs.network.NetworkRessource;
import nl.dcs.network.client.ClientInterface;

import org.apache.commons.collections4.bag.TreeBag;

/**
 * @author Ivanis
 * @version 0.1
 */

public class Server
		extends NetworkRessource
		implements ServerInterface
{

	private static final long serialVersionUID = 5446617274331655787L;
	private static final long maxClientDelay = 150000;
	private static final long maxServerWatch = 20000;
	private static final String rmiAddressPattern = "(rmi:)?//([a-zA-Z][a-zA-Z0-9]*)?:?(\\d{4})?/";

	private final Integer port;
	private final TreeBag<Event> eventBag = new TreeBag<>();
	private final HashMap<Long, ClientInterface> clients = new HashMap<>();
	private final HashMap<Integer, Long> watchedServer = new HashMap<>();
	private final ConcurrentSkipListMap<Integer, String> serverAddress = new ConcurrentSkipListMap<>();
	private final Integer id;
	private final TSS state = new TimedTSS(new State(), 60000L);
	private Integer window = 2;


	public Server(Integer id, Integer port, Integer window)
			throws RemoteException
	{
		this.port = port;
		this.id = id;
		this.window = window;
		this.serverAddress.put(id, this.getRMIAddress());
	}


	/**
	 * Constructor from string representing the address of the rmi registry
	 * 
	 * @param id the identifier in the network
	 * @param address the rmi address of the server
	 * @param window the number of server you should contact
	 * @throws UnknownHostException if the host is not known
	 * @throws RemoteException @see
	 *             java.rmi.server.UnicastRemoteObject#UnicastRemoteObject()
	 * @throws MalformedURLException if the address is not correct
	 */
	public Server(int id, String address, int window)
			throws UnknownHostException, RemoteException, MalformedURLException
	{
		super();
		this.id = id;
		if (!address.matches(Server.rmiAddressPattern))
			throw new MalformedURLException();
		address = address.replace("rmi:", "").replace("/", "");
		int port = 0;
		InetAddress ip = InetAddress.getLocalHost();
		if (address.contains(":"))
		{
			String[] subtoken = address.split(":");
			ip = subtoken[0].isEmpty() ? InetAddress.getLocalHost() : InetAddress.getByName(subtoken[0]);
			port = subtoken[1].isEmpty() ? 1099 : Integer.parseInt(subtoken[1]);
		}

		this.port = port;
		this.window = window;
		this.serverAddress.put(id, address);
		String s = new StringBuilder().append("Creation of server object : ").append(this.id).toString();
		Logger.getLogger(this.getClass().getName()).fine(s);
		System.out.println(s);
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
			Server server;
			OptionParser parser = new OptionParser();

			parser.nonOptions().ofType(Integer.class);
			OptionSpec<Integer> window = parser.accepts("window").withOptionalArg().ofType(Integer.class).defaultsTo(2);
			OptionSpec<Integer> port = parser.accepts("port").withOptionalArg().ofType(Integer.class).defaultsTo(1099);

			OptionSet set = parser.parse(args);
			set.valueOf(window);


			if (set.nonOptionArguments().size() != 1)
			{
				System.out.println("Missing id");
				System.exit(1);
			}
			if (!(set.nonOptionArguments().get(0) instanceof Integer))
			{
				throw new IllegalArgumentException("id is not a int");
			}
			int id = (Integer) set.nonOptionArguments().get(0);
			server = new Server(id, set.valueOf(port), set.valueOf(window));
			server.expose();
			System.out.println(server);
			System.out.println("Server start " + args[0]);
			server.getLogger().fine("Server ready to receive events");
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
						break;
					case "list":
						server.listServer();
						break;
					case "add":
						server.addServer(input.nextInt(), input.next());
						break;
					case "refresh":
						server.refresh(input.nextInt());
					default:
						System.out.println("Unknown command");
				}
			}
		}
		catch (RemoteException | OutOfSyncException | NotBoundException | ServerNotActiveException ex)
		{
			Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
		}
	}


	private void listServer()
	{
		for (Integer id : serverAddress.keySet())
		{
			System.out.println(id + ":" + serverAddress.get(id));
		}
	}


	@Override
	public void putServer(int id, String address)
	{
		this.serverAddress.put(id, address);
	}


	private void refresh(int i)
			throws RemoteException, NotBoundException
	{
		if (i == this.id)
		{
			throw new IllegalArgumentException();
		}
		lookup(i).watch(this.id);
	}


	/**
	 * Expose the server interface to the other network resources using the RMI
	 * registry
	 * 
	 * @throws RemoteException
	 */
	public void expose()
			throws RemoteException
	{
		Registry registry = LocateRegistry.createRegistry(this.port);
		registry.rebind("SERVER", this);
		Logger.getLogger(this.getClass().getName()).fine("Server published : " + this + " on " + "SERVER");
	}


	private void terminate()
			throws RemoteException
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
	public void sendEvent(long sender, Event event)
			throws RemoteException, NotBoundException, ServerNotActiveException, OutOfSyncException
	{
		if (this.verifyEvent(event, sender))
		{
			if (this.addToEventBag(event))
			{
				spreadServers(event, DNS.getNbServers());
				spreadClients(event);
			}
		}

	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void transferEvent(Event event)
			throws RemoteException, NotBoundException, ServerNotActiveException, OutOfSyncException
	{
		if (this.addToEventBag(event))
		{
			System.out.println(event);
			spreadServers(event, this.window);
			spreadClients(event);
		}
	}


	@Override
	public TSS watch(int id)
			throws RemoteException
	{
		watchedServer.put(id, this.state.getSimulationTime());
		return this.state;
	}


	/**
	 * Spread an event to the clients known to this server
	 * 
	 * @param event The event to be spread
	 * @throws RemoteException
	 * @throws OutOfSyncException
	 */
	private void spreadClients(Event event)
			throws RemoteException, OutOfSyncException
	{
		for (Long id : this.clients.keySet())
		{
			if (id != event.getIssuer())
			{
				clients.get(id).update(event);
			}
		}
	}


	/**
	 * Send event to nbServer other servers in the order of declaration in the
	 * list of servers If the server number added to nbServer is higher than the
	 * total number of server, it will send to servers start from 0 If the
	 * number of servers is below the window then it will do nothing
	 * 
	 * @param event The event to be spread
	 * @param nbServer the number of server to send the event to
	 * @throws RemoteException
	 * @throws NotBoundException
	 * @throws ServerNotActiveException
	 * @throws OutOfSyncException
	 */
	private void spreadServers(Event event, int nbServer)
			throws RemoteException, NotBoundException, ServerNotActiveException, OutOfSyncException
	{
		if (this.window > this.serverAddress.keySet().size())
		{
			String message = "Not enough servers to operate";
			System.out.println(message);
			this.getLogger().log(Level.SEVERE, message);
			return;
		}
		int count = 0;
		Integer key = this.id;
		do
		{
			key = this.serverAddress.higherKey(key);
			if (key == null)
			{
				key = this.serverAddress.firstKey();
			}
			System.out.println("sending to : " + key);
			this.lookup(key).transferEvent(event);
			count++;
		} while (count < window);

		// Send event to watched servers
		for (int serverId : this.watchedServer.keySet())
		{
			System.out.println("Sending to " + this.serverAddress.get(serverId));
			Logger.getLogger(this.getClass().getName()).fine("sending to " + serverId);
			this.lookup(serverId).transferEvent(event);
			if (event.getSimulationTime() > this.watchedServer.get(serverId) + Server.maxServerWatch)
			{
				this.watchedServer.remove(serverId);
			}
		}
	}


	/**
	 * Add the event to the event bag
	 * 
	 * @param event the event to add to the bag
	 * @return true if the event if added for the first time
	 * @throws OutOfSyncException
	 */
	private boolean addToEventBag(Event event)
			throws OutOfSyncException
	{
		eventBag.add(event);
		getLogger().fine(event + "\n count : " + eventBag.getCount(event));
		boolean fresh = eventBag.getCount(event) == 1;
		if (fresh)
			this.state.receiveEvent(event);
		return fresh;
	}


	@Override
	public TSS register(long sender, ClientInterface client)
			throws RemoteException
	{
		if (this.state.getPhase() == State.GameState.Open)
		{
			this.clients.put(sender, client);
			return this.state;
		}
		return null;
	}


	@Override
	public void unregister(long sender, ClientInterface client)
			throws RemoteException
	{
		this.clients.remove(sender);
		System.gc();
		System.runFinalization();
	}


	public void addServer(int id, String address)
			throws RemoteException
	{
		if (!address.matches(Server.rmiAddressPattern))
		{
			System.out.println("The address in not in a correct format");
			return;
		}
		if (this.serverAddress.containsKey(id) && this.serverAddress.get(id) == address)
		{
			System.out.println("The address is already known with the id : " + id);
			return;
		}
		if (this.serverAddress.containsValue(address))
		{
			System.out.println("The address is already known but not with the id : " + id);
			this.listServer();
			return;
		}
		this.watch(id);
		for (Integer i : this.serverAddress.keySet())
		{
			try
			{
				String a = this.serverAddress.get(i);
				this.lookup(address).putServer(i, a);
				this.lookup(a).putServer(id, address);
			}
			catch (RemoteException e)
			{
				e.printStackTrace();
			}
		}

	}


	/**
	 * Return the the RMI address associated with this server
	 * 
	 * @return a rmi address usable to reach this server registry
	 */
	public String getRMIAddress()
	{
		try
		{
			return "rmi://" + InetAddress.getLocalHost().getHostAddress() + ":" + this.port + "/";
		}
		catch (UnknownHostException e)
		{
			e.printStackTrace();
		}
		return null;
	}


	@Override
	public String toString()
	{
		try
		{
			return "Server{" + "ip=" + InetAddress.getLocalHost() + ", port=" + port + ", eventBag=" + eventBag.size() + ", id=" + id + ", window=" + window + '}';
		}
		catch (UnknownHostException e)
		{
			e.printStackTrace();
		}
		return null;
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

		EventFilter filter = new EventFilter(this.state, senderId);
		accepted = filter.acceptEventFromClient(event);

		if (!clients.containsKey(senderId))
			accepted = false;

		return accepted;
	}


	public ServerInterface lookup(int id)
	{
		return this.lookup(this.serverAddress.get(id));
	}


}
