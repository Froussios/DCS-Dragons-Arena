package nl.dcs.da.tss.events;



/**
 * Healing event
 * 
 * @author Chris
 * 
 */
public class Heal extends Event
{

	long targetId;


	/**
	 * Get the id of the player doing the healing
	 * 
	 * @return
	 */
	public long getTarget()
	{
		return this.targetId;
	}


	/**
	 * Get the id of the player doing the healing
	 * 
	 * @return
	 */
	public long getHealer()
	{
		return this.getIssuer();
	}


	/**
	 * New healing event
	 * 
	 * @param simulationTime The time the event occurs at
	 * @param issuer The player doing the healing
	 * @param target The player being healed
	 */
	public Heal(long simulationTime, long issuer, long target)
	{
		super(simulationTime, issuer);
		this.targetId = target;
	}


	@Override
	public String toString()
	{
		return "Heal@" + getSimulationTime() + ": " + getHealer() + " healed " + getTarget();
	}

}
