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

		System.out.println("Connecting as " + as + "-" + id + " at server " + server);

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
		this.AI.addListener(this);

		// Join game
		System.out.println("Joining game...");
		Connect connect = new Connect(id, actor, Point.random());
		this.game.receiveEvent(connect);
		this.onAction(connect);
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
		System.out.println("Starting command-line input");
		bot.new Monitor().start();
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
				e.printStackTrace();
			}
		}

		// System.out.println(this.game);
		System.out.println("State change: " + cause);
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
			System.out.println("Bot performing action: " + action);

			try
			{
				this.connection.sendEvent(action);
			}
			catch (RemoteException | NotBoundException | ServerNotActiveException | OutOfSyncException e)
			{
				e.printStackTrace();
				System.out.println("No longer connected to a server. Restart to continue.");
				System.out.flush();
				System.err.flush();
				System.exit(1);
			}
		}
	}

	/**
	 * Asynchronous command-line reader for debugging
	 * 
	 * @author Chris
	 * 
	 */
	class Monitor
			extends Thread
	{

		private final Scanner scanner = new Scanner(System.in);


		@Override
		public void run()
		{
			System.out.println("Interactive command-line started");
			while (true)
			{
				String command = scanner.next().toLowerCase();
				switch (command)
				{
					case "print":
					{
						System.out.println(game);
						break;
					}
					case "events":
					{
						for (Event event : game.getEventQueue())
							System.out.println(event);
						break;
					}
					default:
					{
						System.out.println("Learn to type");
					}
				}
			}
		}
	}

}
