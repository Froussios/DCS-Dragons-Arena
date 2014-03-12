package nl.dcs.da.tss.events;


/**
 * A dummy event that only carries a timestamp, for comparison purposes
 * 
 * @author Chris
 * 
 */
public class MarkEvent
		extends Event
{

	/**
	 * Create a new mark at a specific time
	 * 
	 * @param time The simulation time for the event.
	 */
	public MarkEvent(long time)
	{
		super(time, -1);
	}

}
