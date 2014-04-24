package nl.dcs.network.server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ServerNotActiveException;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import nl.dcs.da.tss.OutOfSyncException;
import nl.dcs.da.tss.Point;
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
		implements ServerInterface, TSS.Listener
{

	private static final long serialVersionUID = 5446617274331655787L;
	private static final long maxClientDelay = 150000;
	private static final long maxServerWatch = 20000;
	// TODO make real pattern
	private static final String rmiAddressPattern = "(rmi:)?//([a-zA-Z][a-zA-Z0-9]*)?:?(\\d{4})?/";

	private transient final Integer port;
	private transient final TreeBag<Event> eventBag = new TreeBag<>();
	private transient final ConcurrentHashMap<Long, ClientInterface> clients = new ConcurrentHashMap<>();
	private transient final ConcurrentHashMap<Integer, Long> watchedServer = new ConcurrentHashMap<>();
	private transient final ConcurrentSkipListMap<Integer, String> serverAddress = new ConcurrentSkipListMap<>();
	private final Integer id;
	private transient final TimedTSS state = new TimedTSS(TSS.RECOMMENDED_MAX_DELAY);
	private transient Integer window = 2;


	public Server(Integer id, Integer port, Integer window)
			throws RemoteException
	{
		this.port = port;
		this.id = id;
		this.window = window;
		this.serverAddress.put(id, this.getRMIAddress());

		this.state.addListener(this);
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
						long time = server.state.getSimulationTime() + server.eventBag.size();
						server.transferEvent(new MarkEvent(time));
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
					case "watchlist":
						for (Integer serverid : server.watchedServer.keySet())
							System.out.println(serverid);
						break;
					case "add":
						server.addServer(input.nextInt(), input.next());
						break;
					case "refresh":
						server.refresh(input.nextInt());
						break;
					case "print":
						System.out.println(server.state);
						break;
					case "events":
						for (Event event : server.state.getEventQueue())
							System.out.println(event);
						break;
					case "bag":
						for (Event event : server.eventBag.uniqueSet())
							System.out.println(event);
						break;

					case "load":
						server.loadFrom(input.next());
					default:
						System.out.println("Unknown command");
				}
			}
		}
		catch (Exception ex)
		{
			Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
		}
	}


	private void loadFrom(String path)
			throws IOException
	{
		BufferedReader br = null;

		try
		{

			String line;

			br = new BufferedReader(new FileReader(path));

			while ((line = br.readLine()) != null)
			{
				System.out.println(line);
				String[] token = line.split(" ");
				this.addServer(Integer.parseInt(token[0]), token[1]);

			}

		}
		finally
		{
			if (br != null)
				br.close();
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
		if (i == this.id || !this.serverAddress.keySet().contains(i))
		{
			this.getLogger().severe("Wrong id : " + i);
			System.out.println("Wrong id : " + i);
		}
		ServerInterface contactedServer = lookup(i);
		if (contactedServer != null)
		{
			this.state.loadFrom(contactedServer.watch(this.id));
			this.serverAddress.putAll(contactedServer.getServerList());
		}
		else
		{
			System.out.println("Unable to refresh");
		}
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
		Long start = System.currentTimeMillis();
		if (this.verifyEvent(event, sender))
		{
			if (this.addToEventBag(event))
			{
				spreadServers(event, this.serverAddress.size());
				spreadClients(event);
			}
		}
		Long end = System.currentTimeMillis();
		// System.out.println("Processed " + event + " in " + (end - start) +
		// "ms");
	}


	@Override
	public TimedTSS watch(int id)
			throws RemoteException
	{
		watchedServer.put(id, this.state.getSimulationTime());
		return this.state;
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


	/**
	 * Spread an event to the clients known to this server
	 * 
	 * @param event The event to be spread
	 * @throws OutOfSyncException
	 */
	private void spreadClients(final Event event)
			throws OutOfSyncException
	{
		// Take snapshot to prevent concurrent modifications
		Long[] clients_Snapshot = new Long[0];
		synchronized (this.clients)
		{
			clients_Snapshot = this.clients.keySet().toArray(clients_Snapshot);
		}

		for (final Long id : clients_Snapshot)
		{
			new Sender(this)
			{

				@Override
				public void run()
				{
					try
					{
						this.server.clients.get(id).update(event);
					}
					catch (RemoteException | OutOfSyncException e)
					{
						System.out.println("Dropping client " + id + ". Unable to reach.");
						synchronized (clients)
						{
							clients.remove(id);
						}
						// e.printStackTrace();
					}
				}
			}.start();

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
	private void spreadServers(final Event event, int nbServer)
			throws RemoteException, NotBoundException, ServerNotActiveException, OutOfSyncException
	{
		if (this.window > this.serverAddress.keySet().size())
		{
			String message = "Not enough servers to operate";
			System.out.println(message);
			this.getLogger().log(Level.SEVERE, message);
			return;
		}

		// Send to the nbServer next servers
		int count = 0;
		Integer key = this.id;
		do
		{
			key = this.serverAddress.higherKey(key);
			if (key == null)
			{
				key = this.serverAddress.firstKey();
			}
			System.out.println("sending " + event + " to : " + key + " address : " + this.serverAddress.get(key));
			this.getLogger().fine("sending " + event + " to : " + key + " address : " + this.serverAddress.get(key));
			final ServerInterface contactedServer = this.lookup(key);
			if (contactedServer != null)
			{
				new Sender(this)
				{

					@Override
					public void run()
					{
						try
						{
							contactedServer.transferEvent(event);

						}
						catch (RemoteException | OutOfSyncException | ServerNotActiveException | NotBoundException e)
						{
							this.server.getLogger().severe(e + " " + event);
						}
					}
				}.start();
			}
			count++;
		} while (count <= nbServer);

		// Send event to watched servers
		Integer[] watchedServers_Snapshot = new Integer[0];
		synchronized (this.watchedServer)
		{
			watchedServers_Snapshot = this.watchedServer.keySet().toArray(watchedServers_Snapshot);
		}
		for (final Integer serverId : watchedServers_Snapshot)
		{
			System.out.println("(Watch) sending " + event + " to : " + serverId + " address : " + this.serverAddress.get(serverId));
			this.getLogger().fine("(Watch) sending " + event + " to : " + serverId + " address : " + this.serverAddress.get(serverId));
			new Sender(this)
			{

				@Override
				public void run()
				{
					try
					{
						this.server.lookup(serverId).transferEvent(event);

					}
					catch (RemoteException | OutOfSyncException | ServerNotActiveException | NotBoundException | NullPointerException e)
					{

						this.server.getLogger().severe(e + " " + event);
					}
				}
			}.start();

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
		synchronized (this.eventBag)
		{
			this.eventBag.add(event);
			getLogger().fine(event + "\n count : " + this.eventBag.getCount(event));
			boolean newEvent = this.eventBag.getCount(event) == 1;
			if (newEvent)
				this.state.receiveEvent(event);
			return newEvent;
		}
	}


	@Override
	public TSS register(long sender, ClientInterface client)
			throws RemoteException
	{
		this.clients.put(sender, client);
		this.getLogger().fine("New client : " + sender);
		System.out.println("New client : " + sender);
		return this.state;
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
			throws RemoteException, UnknownHostException
	{
		address.replace("//:", "//" + Inet4Address.getLocalHost() + ":");
		// if (!address.matches(Server.rmiAddressPattern))
		// {
		// System.out.println("The address in not in a correct format");
		// return;
		// }
		if (id == this.id)
		{
			System.out.println("You can't modify the address of this server");
		}
		// this.watch(id);
		for (Integer i : this.serverAddress.keySet())
		{

			String a = this.serverAddress.get(i);
			a.replace("//:", "//" + Inet4Address.getLocalHost() + ":");
			this.lookup(address).putServer(i, a);
			ServerInterface contactedServer = this.lookup(a);
			if (contactedServer != null)
				contactedServer.putServer(id, address);
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
			return null;
		}
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
			return null;
		}

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
		boolean accepted = new EventFilter(this.state, senderId).acceptEventFromClient(event);

		if (!clients.containsKey(senderId))
			accepted = false;
		return accepted;
	}


	public ServerInterface lookup(int id)
	{
		return this.lookup(this.serverAddress.get(id));
	}


	/**
	 * Monitor game events
	 */
	@Override
	public void onStateChanged(Object cause)
	{
	}


	/**
	 * Monitor gameover
	 */
	@Override
	public void onGameOver()
	{
		System.out.println("Survivors:");
		for (Point point : this.state)
			if (this.state.get(point) != null)
				System.out.println(" " + this.state.get(point));
		System.out.println("Game is over. The server can now be closed.");
	}


	@Override
	public void ping()
	{
		// Return immediately
	}


	@Override
	public Map<Integer, String> getServerList()
			throws RemoteException
	{
		return this.serverAddress;
	}


}
