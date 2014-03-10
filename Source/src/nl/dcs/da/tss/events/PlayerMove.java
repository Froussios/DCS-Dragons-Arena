package nl.dcs.da.tss.events;

import nl.dcs.da.tss.Point;


public class PlayerMove extends Event
{

	private final Point target;


	public long getPlayer()
	{
		return this.getIssuer();
	}


	public Point getTarget()
	{
		return this.target;
	}


	/**
	 * Create a new player move event
	 * 
	 * @param playerId The id of the player moving
	 * @param target The location the player is moving to
	 */
	public PlayerMove(long time, long playerId, Point target)
	{
		super(time, playerId);
		this.target = target;
	}


	@Override
	public String toString()
	{
		return "PlayerMove@" + getSimulationTime() + ": " + getPlayer() + " to " + target;
	}

}
