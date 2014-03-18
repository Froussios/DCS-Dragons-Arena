package nl.dcs.da.tss.events;


/**
 * A user-submitted action (player or AI) can change the state of a game
 * 
 * @author Chris
 * 
 */
public class Event
		implements Comparable<Event>
{

	private long time = -1;
	private long issuer = -1;


	/**
	 * Get the simulation time
	 * 
	 * @return
	 */
	public long getSimulationTime()
	{
		return this.time;
	}


	/**
	 * Get the actor that created this event
	 * 
	 * @return
	 */
	public long getIssuer()
	{
		return this.issuer;
	}


	/**
	 * New event
	 * 
	 * @param simulationTime The simulation time the event occurs at
	 * @param issuer The creator of this event
	 */
	public Event(long simulationTime, long issuer)
	{
		this.time = simulationTime;
		this.issuer = issuer;
	}


	/**
	 * Human-readable representation of the event.
	 */
	@Override
	public String toString()
	{
		return "Event@" + time;
	}


	/**
	 * Compares events based on simulation time. Uses issuer id as a tie
	 * breaker. This has the implication that the same issuer can't create
	 * multiple events at the same time.
	 * 
	 * This creates a bias, consistently favouring clients with specific ids,
	 * when multiple actions occur at the same time. Some kind of bias is
	 * necessary, however, as the order has to be deterministic across multiple
	 * machines and multiple replays of the event sequence.
	 */
	@Override
	public int compareTo(Event other)
	{

		int comparison = Long.compare(this.time, other.time);
		if (comparison == 0)
			return Long.compare(this.issuer, other.issuer);
		else
			return comparison;
	}

}
