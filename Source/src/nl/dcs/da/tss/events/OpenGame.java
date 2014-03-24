package nl.dcs.da.tss.events;


/**
 * The event of the game becoming open for players to sign up.
 * 
 * @author Chris
 * 
 */
public class OpenGame
		extends Event
{

	/**
	 * The event of the game becoming open for players to sign up.
	 */
	public OpenGame()
	{
		super(-3, -1);
	}


	@Override
	public String toString()
	{
		return "OpenGame@" + getSimulationTime();
	}

}
