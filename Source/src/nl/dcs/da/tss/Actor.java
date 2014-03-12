package nl.dcs.da.tss;

import nl.dcs.da.tss.util.IncGenerator;


public abstract class Actor
{

	private static IncGenerator idGenerator = new IncGenerator();

	private long id;
	private final int startingHP;
	private int hp;
	private final int ap;


	protected void setID(long value)
	{
		this.id = value;
	}


	public long getID()
	{
		return this.id;
	}


	public int getHP()
	{
		return this.hp;
	}


	/**
	 * Set the actor's health points. The new HP will be automatically capped by
	 * the maximum HP of this actor
	 * 
	 * @param value The new value
	 * @return The current health points
	 */
	protected int setHP(int value)
	{
		this.hp = value;
		if (this.hp > this.startingHP)
			this.hp = this.startingHP;
		return this.hp;
	}


	protected int getMaxHP()
	{
		return this.startingHP;
	}


	public int getAP()
	{
		return this.ap;
	}


	/**
	 * Create a new dragon
	 * 
	 * @param hp The dragon's health points
	 * @param ap The dragon's attack points
	 */
	protected Actor(int hp, int ap)
	{
		this.id = idGenerator.next();

		this.startingHP = this.hp = hp;
		this.ap = ap;
	}


	/**
	 * Subtracts health points from the dragon
	 * 
	 * @param ap The amount of health points to remove
	 * @return the dragon's health after the attack
	 */
	public int receiveDamage(int ap)
	{
		this.hp -= ap;
		if (this.hp < 0)
			this.hp = 0;

		return this.hp;
	}


	@Override
	public abstract Actor clone();



	/**
	 * Returns true if both instances are actors and they have the same id
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof Actor)
		{
			Actor other = (Actor) obj;
			return this.id == other.id;
		}
		else if (obj instanceof Long)
		{
			Long other = (Long) obj;
			return this.id == other;
		}
		else
			return false;
	}


	@Override
	public String toString()
	{
		return "Actor#" + getID();
	}


	/**
	 * Show HP and AP
	 */
	public String details()
	{
		return "[Actor#" + getID() + " HP:" + getHP() + "/" + getMaxHP() + " AP:" + getAP() + "]";
	}

}
