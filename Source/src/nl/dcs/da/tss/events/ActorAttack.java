package nl.dcs.da.tss.events;

import nl.dcs.da.tss.Point;



public class ActorAttack extends Event
{

	private final Point target;


	/**
	 * Get the target of the attack
	 * 
	 * @return
	 */
	public Point getTarget()
	{
		return this.target;
	}


	/**
	 * Get the id of the attacker
	 * 
	 * @return
	 */
	public long getAttacker()
	{
		return this.getIssuer();
	}


	/**
	 * New attack event
	 * 
	 * @param simulationTime The time the atack occurs at
	 * @param issuer The client issuing the attack
	 * @param target The target of the attack
	 */
	public ActorAttack(long simulationTime, long issuer, Point target)
	{
		super(simulationTime, issuer);
		this.target = target;
	}



	@Override
	public String toString()
	{
		return "ActorAttack@" + this.getSimulationTime() + ": #" + getIssuer() + " attacks " + target;
	}



}
