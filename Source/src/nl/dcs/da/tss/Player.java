package nl.dcs.da.tss;


/**
 * A human-controlled in-game actor
 * 
 * @author Chris
 * 
 */
public class Player
		extends Actor
{

	private static final long serialVersionUID = -3559965971573899209L;


	/**
	 * Create a new player. The player starts at maximum health.
	 * 
	 * @param hp The player's max HP
	 * @param ap the player's AP
	 */
	public Player(int hp, int ap, long id)
	{
		super(hp, ap, id);

		if (hp < 10 || hp > 20 || ap < 1 || ap > 10)
			throw new IllegalArgumentException("Invalid AP nad HP values for new player");
	}


	/**
	 * Create a new player with HP uniformly random in [10,20] and AP uniformly
	 * random in [1,10]
	 */
	public Player(long id)
	{
		super(10, 20, 1, 10, id);
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
		Player clone = new Player(getMaxHP(), getAP(), getID());
		clone.setHP(getHP());
		// clone.setID(getID());
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


	@Override
	public String toString()
	{
		return "Player#" + getID();
	}
}
