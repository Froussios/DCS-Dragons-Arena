package nl.dcs.da.tss.events;


public class Event implements Comparable<Event>
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


	@Override
	public String toString()
	{
		return "Event@" + time;
	}


	/**
	 * Compares events based on simulation time
	 */
	@Override
	public int compareTo(Event other)
	{
		return Long.compare(this.time, other.time);
	}

}
