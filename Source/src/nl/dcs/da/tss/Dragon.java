package nl.dcs.da.tss;


public class Dragon
		extends Actor
{

	/**
	 * Create a new dragon
	 * 
	 * @param hp The dragon's starting HP
	 * @param ap The dragin's AP
	 */
	public Dragon(int hp, int ap, long id)
	{
		super(hp, ap, id);

		if (hp < 50 || hp > 100 || ap < 5 || ap > 20)
			throw new IllegalArgumentException("Invalid AP nad HP values for new dragon");
	}


	/**
	 * Create a new dragon with HP uniformly random in [50,100] and AP uniformly
	 * random in [5,20].
	 */
	public Dragon(long id)
	{
		super(50, 100, 5, 20, id);
	}


	/**
	 * Show HP and AP
	 */
	@Override
	public String details()
	{
		return "[Dragon#" + getID() + " HP:" + getHP() + "/" + getMaxHP() + " AP:" + getAP() + "]";
	}


	@Override
	public Dragon clone()
	{
		Dragon clone = new Dragon(getMaxHP(), getAP(), getID());
		clone.setHP(getHP());
		// clone.setID(getID());
		return clone;
	}

}
