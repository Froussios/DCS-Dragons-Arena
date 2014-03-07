package nl.dcs.da.tss;


public class Dragon extends Actor
{

	public Dragon(int hp, int ap)
	{
		super(hp,ap);
		
		if ( hp<50 || hp>100 || ap<5 || ap>20)
			throw new IllegalArgumentException("Invalid AP nad HP values for new dragon");
	}

}
