package nl.dcs.app;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

import nl.dcs.da.client.DragonAI;
import nl.dcs.da.client.PlayerAI;
import nl.dcs.da.client.TimedAvatarOperator;
import nl.dcs.da.tss.Battlefield;
import nl.dcs.da.tss.State;
import nl.dcs.da.tss.TSS;
import nl.dcs.da.tss.TimedTSS;
import nl.dcs.da.tss.events.StartGame;
import nl.dcs.da.tss.util.Alarm.AlarmRunningException;
import nl.dcs.network.client.ClientNetwork;


public class BotProgram
		implements Battlefield.Listener
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
		this.game = new TimedTSS(new State(), 60000);
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
	 */
	public void run(Role as, long id, String server, long actionInterval) throws RemoteException, NotBoundException
	{
		if (as == null)
			throw new IllegalArgumentException("Did not specify what to play as.");

		// Connect
		this.connection = new ClientNetwork(game, id);
		this.connection.connect(server); // Also loads state

		// TODO Get game

		// Create AI
		if (as.equals(Role.Player))
			this.AI = new PlayerAI(id, this.game, 2000);
		else if (as.equals(Role.Dragon))
			this.AI = new DragonAI(id, this.game, 2000);

		// Start acting on receive
		this.game.addListener(this);
	}


	public void runIteractive() throws RemoteException, NotBoundException
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

		// TODO Get game

		// TODO Create AI
		if (role.equals("dragon"))
			AI = new DragonAI(id, game, 5000);
		else if (role.equals("player"))
			AI = new PlayerAI(id, game, 5000);

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
	}


	/**
	 * Run an interactive client
	 * 
	 * @param args
	 * @throws RemoteException
	 * @throws NotBoundException
	 */
	public static void main(String[] args) throws RemoteException, NotBoundException
	{
		System.out.println("Starting bot...");
		BotProgram bot = new BotProgram();
		bot.run(Role.Player, 1, "rmi://localhost:1100/", 3000);
	}

}
