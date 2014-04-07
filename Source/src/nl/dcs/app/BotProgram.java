package nl.dcs.app;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.util.Scanner;

import nl.dcs.da.client.DragonAI;
import nl.dcs.da.client.PlayerAI;
import nl.dcs.da.client.TimedAvatarOperator;
import nl.dcs.da.client.TimedAvatarOperator.ActionListener;
import nl.dcs.da.tss.Actor;
import nl.dcs.da.tss.Battlefield;
import nl.dcs.da.tss.Dragon;
import nl.dcs.da.tss.OutOfSyncException;
import nl.dcs.da.tss.Player;
import nl.dcs.da.tss.Point;
import nl.dcs.da.tss.State;
import nl.dcs.da.tss.TSS;
import nl.dcs.da.tss.TimedTSS;
import nl.dcs.da.tss.events.Connect;
import nl.dcs.da.tss.events.Event;
import nl.dcs.da.tss.events.StartGame;
import nl.dcs.da.tss.util.Alarm.AlarmRunningException;
import nl.dcs.network.client.ClientNetwork;


public class BotProgram
		implements Battlefield.Listener, ActionListener
{

	/**
	 * The role for the bot to assume.
	 * 
	 * @author Chris
	 * 
	 */
	public enum Role
	{
		Player, Dragon
	};


	private final TSS game; // Keep final
	private ClientNetwork connection;
	private TimedAvatarOperator AI;


	/**
	 * New bot
	 * 
	 * @throws RemoteException
	 */
	public BotProgram()
	{
		this.game = new TimedTSS(new State(), TSS.RECOMMENDED_MAX_DELAY);
		this.game.addListener(this);
	}


	/**
	 * Run the program.
	 * 
	 * @param as The role for the bot to assume
	 * @param id The in-game id for the bot.
	 * @param actionInterval How often the bot should execute an action.
	 * @throws NotBoundException
	 * @throws RemoteException
	 * @throws OutOfSyncException
	 */
	public void run(Role as, long id, String server, long actionInterval)
			throws RemoteException, NotBoundException, OutOfSyncException
	{
		if (as == null)
			throw new IllegalArgumentException("Did not specify what to play as.");

		System.out.println("Connecting as " + as + "-" + id + " at server.");

		// Connect
		System.out.println("Connecting to server...");
		this.connection = new ClientNetwork(game, id);
		this.connection.connect(server); // Also loads state

		// TODO Get game

		// Create AI
		System.out.println("Setting up AI...");
		Actor actor = null;
		if (as.equals(Role.Dragon))
		{
			AI = new DragonAI(id, game, 2000);
			actor = new Dragon(id);
		}
		else if (as.equals(Role.Player))
		{
			AI = new PlayerAI(id, game, 2000);
			actor = new Player(id);
		}

		// Start acting on receive
		this.game.addListener(this);

		// Join game
		System.out.println("Joining game...");
		Connect connect = new Connect(id, actor, Point.random());
		this.game.receiveEvent(connect);
		this.onAction(connect);
	}


	public void runIteractive()
			throws RemoteException, NotBoundException, ServerNotActiveException, OutOfSyncException
	{
		Scanner scanner = new Scanner(System.in);

		System.out.print("<id> <role>: ");
		long id = scanner.nextLong();
		String role = scanner.next().toLowerCase();

		// TODO Connect
		while (true)
		{
			try
			{
				System.out.print("Enter the server : ");
				String serverAddress = scanner.next();
				this.connection = new ClientNetwork(game, id);
				this.connection.connect(serverAddress); // Also loads game
				break;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		// TODO Create AI
		Actor actor = null;
		if (role.equals("dragon"))
		{
			AI = new DragonAI(id, game, 5000);
			actor = new Dragon(id);
		}
		else if (role.equals("player"))
		{
			AI = new PlayerAI(id, game, 5000);
			actor = new Player(id);
		}
		AI.addListener(this);

		System.out.println(this.game);

		// Join game
		Connect connect = new Connect(id, actor, Point.random());
		this.game.receiveEvent(connect);
		this.onAction(connect);

		// Start acting on receive
		this.game.addListener(this);
	}


	@Override
	public void onStateChanged(Object cause)
	{
		if (cause instanceof StartGame)
		{
			// TODO this can happen twice, i.e. when recovering in TSS
			try
			{
				this.AI.start();
			}
			catch (AlarmRunningException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		System.out.println("Received: " + cause);
		System.out.println(this.game);
	}


	/**
	 * Run an interactive client
	 * 
	 * @param args
	 * @throws RemoteException
	 * @throws NotBoundException
	 * @throws OutOfSyncException
	 */
	public static void main(String[] args)
			throws RemoteException, NotBoundException, OutOfSyncException
	{
		long id = 1;
		String server = "rmi://localhost:1100/";
		Role role = Role.Player;
		long frequency = 3000;

		// Parse command-line options
		for (int i = 0; i < args.length; i++)
			if (args[i].equals("-id"))
				id = Long.parseLong(args[++i]);
			else if (args[i].equals("-server"))
				server = args[++i];
			else if (args[i].equals("-role"))
				role = Role.valueOf(args[++i]);
			else if (args[i].equals("-freq"))
				frequency = Long.parseLong(args[++i]);
			else
				System.out.println("Ignored unknown option " + args[i]);

		// Start bot
		System.out.println("Starting bot...");
		BotProgram bot = new BotProgram();
		bot.run(role, id, server, frequency);
	}


	/**
	 * Runs when the AI executes an action
	 * 
	 * @param action The action executed. May be null
	 */
	@Override
	public void onAction(Event action)
	{
		if (action != null)
		{
			try
			{
				this.connection.sendEvent(action);
			}
			catch (RemoteException | NotBoundException | ServerNotActiveException | OutOfSyncException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("No longer connected to a server. Restart to continue.");
				System.exit(1);
			}
		}
	}

}
