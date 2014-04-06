package nl.dcs.da.client;

import nl.dcs.da.tss.Battlefield;
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


	/**
	 * Get the id of the player being controlled
	 * 
	 * @return
	 */
	public long getID()
	{
		return me;
	}


	/**
	 * Returns the battlefield this character is playing on
	 * 
	 * @return
	 */
	protected Battlefield getBattlefield()
	{
		return this.game;
	}


	/**
	 * Get the game this character is playing on.
	 * 
	 * @return
	 */
	protected TSS getGame()
	{
		return this.game;
	}


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
	 * Throw an exception if the character is no longer alive.
	 * 
	 * @throws CharacterDeadException
	 */
	protected void enforceAlive()
			throws CharacterDeadException
	{
		if (this.inGame())
			throw new AvatarOperator.CharacterDeadException("Character " + this.getID() + " is dead.");
	}


	/**
	 * Move to a new location.
	 * 
	 * @param target The new location.
	 * @throws
	 * @throws CharacterDeadException
	 */
	public PlayerMove move(Point target)
			throws CharacterDeadException
	{
		PlayerMove move = new PlayerMove(game.getSimulationTime(), me, target);
		submitEvent(move);
		return move;
	}


	/**
	 * Strike at a location on the map
	 * 
	 * @param target
	 * @throws
	 * @throws CharacterDeadException
	 */
	public ActorAttack attack(Point target)
			throws CharacterDeadException
	{
		ActorAttack attack = new ActorAttack(game.getSimulationTime(), me, target);
		submitEvent(attack);
		return attack;
	}


	/**
	 * Heal another player
	 * 
	 * @param who The other player's id
	 * @throws
	 * @throws CharacterDeadException
	 */
	public Heal heal(long who)
			throws CharacterDeadException
	{
		Heal heal = new Heal(game.getSimulationTime(), me, who);
		submitEvent(heal);
		return heal;
	}


	/**
	 * Get this client's location on the battlefield
	 * 
	 * @return
	 * @throws CharacterDeadException
	 */
	public Point getLocation()
	{
		Point location = game.findActor(me);
		return location;
	}


	/**
	 * Get whether this client has a playing avatar in the game.
	 * 
	 * @return true if the player can still act in the game
	 * @throws CharacterDeadException
	 */
	public boolean inGame()
	{
		return this.getLocation() != null;
	}


	/**
	 * Submit a new event to the game
	 * 
	 * @param event
	 * @throws CharacterDeadException
	 */
	protected void submitEvent(Event event)
			throws IllegalStateException, CharacterDeadException
	{
		synchronized (this.game)
		{
			if (!inGame())
				throw new CharacterDeadException();

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


	/**
	 * An exception that is thrown when an action is attempted after the
	 * character has died.
	 * 
	 * @author Chris
	 * 
	 */
	public static class CharacterDeadException
			extends Exception
	{

		private static final long serialVersionUID = 98727438463185411L;


		public CharacterDeadException(String message, Throwable inner)
		{
			super(message, inner);
		}


		public CharacterDeadException(String message)
		{
			super(message, null);
		}


		public CharacterDeadException()
		{
			super("Character is dead");
		}

	}

}
