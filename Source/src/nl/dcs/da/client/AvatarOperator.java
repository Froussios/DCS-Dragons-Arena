package nl.dcs.da.client;

import nl.dcs.da.tss.OutOfSyncException;
import nl.dcs.da.tss.Point;
import nl.dcs.da.tss.TSS;
import nl.dcs.da.tss.events.ActorAttack;
import nl.dcs.da.tss.events.Event;
import nl.dcs.da.tss.events.Heal;
import nl.dcs.da.tss.events.PlayerMove;


public class AvatarOperator
{

	private final long me;
	private final TSS game;


	// TODO track if player is alive

	/**
	 * A new client that can act inside a game. Actions carry the most recent
	 * timestamp.
	 * 
	 * 
	 * @param id The id of the client's avatar.
	 * @param game The game this client is playing on.
	 */
	public AvatarOperator(long id, TSS game)
	{
		this.me = id;
		this.game = game;
	}


	/**
	 * Move to a new location.
	 * 
	 * @param target The new location.
	 */
	public void Move(Point target)
	{
		PlayerMove move = new PlayerMove(game.getSimulationTime(), me, target);
		submitEvent(move);
	}


	/**
	 * Strike at a location on the map
	 * 
	 * @param target
	 */
	public void Attack(Point target)
	{
		ActorAttack attack = new ActorAttack(game.getSimulationTime(), me, target);
		submitEvent(attack);
	}


	/**
	 * Heal another player
	 * 
	 * @param who The other player's id
	 */
	public void Heal(long who)
	{
		Heal heal = new Heal(game.getSimulationTime(), me, who);
		submitEvent(heal);
	}


	/**
	 * Get this client's location on the battlefield
	 * 
	 * @return
	 */
	public Point getLocation()
	{
		Point location = game.findActor(me);
		return location;
	}


	/**
	 * Get whether this client has a playing avatar in the game.
	 * 
	 * @return true if the palyer can still act in the game
	 */
	public boolean inGame()
	{
		return this.getLocation() != null;
	}


	/**
	 * Submit a new event to the game
	 * 
	 * @param event
	 */
	protected void submitEvent(Event event) throws IllegalStateException
	{
		if (!inGame())
			throw new IllegalStateException("Client not in game");

		try
		{
			boolean accepted = game.receiveEvent(event);
			if (!accepted)
			{
				// TODO Notify too soon since previous action
			}
		}
		catch (OutOfSyncException e)
		{
			throw new IllegalStateException("Event at simulation time caused the game to go out-of-sync.");
		}
	}

}
