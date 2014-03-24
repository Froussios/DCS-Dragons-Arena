package nl.dcs.da.tss.events;


/**
 * The event of the game closing to new sign ups and the battle beginning.
 * 
 * @author Chris
 * 
 */
public class StartGame
		extends Event
{

	/**
	 * The event of the game closing to new sign ups and the battle beginning.
	 */
	public StartGame()
	{
		super(-1, -1);
	}


	@Override
	public String toString()
	{
		return "StartGame@" + getSimulationTime();
	}

}
