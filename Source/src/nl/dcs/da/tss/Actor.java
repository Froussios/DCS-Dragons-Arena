package nl.dcs.da.tss;

import java.util.Random;


public class Actor extends Element
{
	private static Random random = new Random();
	
	private int id;
	private int startingHP;
	private int hp;
	private int ap;
	
	
	public int getID() { return this.id; }
	public int getHP() { return this.hp; }
	public int getAP() { return this.ap; }
	
	
	/**
	 * Create a new dragon
	 * @param hp The dragon's health points
	 * @param ap The dragon's attack points
	 */
	public Actor(int hp, int ap)
	{		
		this.id = random.nextInt();
		
		this.startingHP = this.hp = hp;
		this.ap = ap;
	}
	
	
	/**
	 * Substracts health points from the dragon
	 * @param ap The amount of health points to remove
	 * @return the dragon's health after the attack
	 */
	public int receiveDamage(int ap)
	{
		this.hp -= ap;
		if (this.hp < 0) this.hp = 0;
		
		return this.hp;
	}
	
	
	/**
	 * Returns true if both instances are actors and they have the same id
	 */
	@Override
	public boolean equals(Object obj)
	{
		if ( obj instanceof Actor)
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
	
}
