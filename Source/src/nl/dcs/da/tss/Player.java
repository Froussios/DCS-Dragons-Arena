package nl.dcs.da.tss;


public class Player extends Actor
{	
	public Player(int hp, int ap)
	{
		super(hp,ap);
		
		if ( hp<10 || hp>20 || ap<1 || ap>10)
			throw new IllegalArgumentException("Invalid AP nad HP values for new player");
	} 
}
