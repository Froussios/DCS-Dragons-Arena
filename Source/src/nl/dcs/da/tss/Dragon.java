package nl.dcs.da.tss;


public class Dragon
		extends Actor
{

	public Dragon(int hp, int ap)
	{
		super(hp, ap);

		if (hp < 50 || hp > 100 || ap < 5 || ap > 20)
			throw new IllegalArgumentException("Invalid AP nad HP values for new dragon");
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
		Dragon clone = new Dragon(getMaxHP(), getAP());
		clone.setHP(getHP());
		clone.setID(getID());
		return clone;
	}

}
