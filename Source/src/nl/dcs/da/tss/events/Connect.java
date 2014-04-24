package nl.dcs.da.tss.events;

import nl.dcs.da.tss.Actor;
import nl.dcs.da.tss.Point;


/**
 * A player connects to the game.
 * 
 * @author Chris
 * 
 */
public class Connect
		extends Event
{

	private static final long serialVersionUID = -1514829199321256309L;

	private final Point startingPosition;
	private final Actor actor;


	/**
	 * Get the player that connected
	 * 
	 * @return The player id
	 */
	public long getPlayer()
	{
		return getIssuer();
	}


	/**
	 * The position this player will start on.
	 * 
	 * @return
	 */
	public Point getStartingPosition()
	{
		return this.startingPosition;
	}


	/**
	 * The actor to be added to the game.
	 */
	public Actor getActor()
	{
		return this.actor;
	}


	/**
	 * Position a new player on the map
	 * 
	 * @param playerId The id of the player connecting
	 * @param starting Position The position this player will start in
	 * @param actor The instance of the actor to be replicated.
	 */
	public Connect(long playerId, Actor actor, Point startingPosition)
	{
		super(-2, playerId);
		this.startingPosition = startingPosition;
		this.actor = actor;

		if (playerId != actor.getID())
			throw new IllegalArgumentException("actorId does not match the Actor instance provided.");
	}


	@Override
	public String toString()
	{
		return "Connect@" + getSimulationTime() + ": " + actor + " starts at " + getStartingPosition();
	}
}
