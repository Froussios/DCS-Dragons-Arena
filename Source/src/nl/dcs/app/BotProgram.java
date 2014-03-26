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


	private final TSS game;
	private final ClientNetwork connection;
	private TimedAvatarOperator AI;


	/**
	 * New bot
	 * 
	 * @throws RemoteException
	 */
	public BotProgram() throws RemoteException
	{
		this.game = new TimedTSS(new State(), 30000);
		this.game.addListener(this);
		this.connection = new ClientNetwork(game);
	}


	/**
	 * Run the program.
	 * 
	 * @param as The role for the bot to assume
	 * @param id The in-game id for the bot.
	 * @param actionInterval How often the bot should execute an action.
	 */
	public void run(Role as, long id, long actionInterval)
	{
		if (as == null)
			throw new IllegalArgumentException("Did not specify what to play as.");

		// TODO Connect

		// TODO Get game

		// TODO Create AI

		// TODO Start acting on receive
	}


	public void runIteractive() throws RemoteException, NotBoundException
	{
		Scanner scanner = new Scanner(System.in);

		// TODO Connect
		System.out.print("Enter the server : ");
		String serverName = scanner.next();
		this.connection.connect(serverName);

		// TODO Get game

		// TODO Create AI
		System.out.print("<id> <role>: ");
		long id = scanner.nextLong();
		String r = scanner.next().toLowerCase();
		if (r.equals("dragon"))
			AI = new DragonAI(id, game, 5000);
		else if (r.equals("player"))
			AI = new PlayerAI(id, game, 5000);
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

}
