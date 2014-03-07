package nl.dcs.da.tss;


public class Event
{
	private long time = -1;
	private long issuer = -1;
	
	public long getSimulationTime() { return this.time; }


	
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

}
