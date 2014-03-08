package nl.dcs.da.tss;


/**
 * A human-controlled in-game actor
 * 
 * @author Chris
 * 
 */
public class Player extends Actor
{

	/**
	 * Create a new player. The player starts at maximum health.
	 * 
	 * @param hp The player's max HP
	 * @param ap the player's AP
	 */
	public Player(int hp, int ap)
	{
		super(hp, ap);

		if (hp < 10 || hp > 20 || ap < 1 || ap > 10)
			throw new IllegalArgumentException("Invalid AP nad HP values for new player");
	}


	/**
	 * Heal the player.
	 * 
	 * @param amount The amount to heal the player by.
	 * @return The new HP
	 */
	public int heal(int amount)
	{
		return setHP(getHP() + amount);
	}


	@Override
	public Player clone()
	{
		Player clone = new Player(getMaxHP(), getAP());
		clone.setHP(getHP());
		return clone;
	}


	/**
	 * Show HP and AP
	 */
	@Override
	public String details()
	{
		return "[Player#" + getID() + " HP:" + getHP() + "/" + getMaxHP() + " AP:" + getAP() + "]";
	}
}
