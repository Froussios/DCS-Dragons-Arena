package nl.dcs.da.tss.util;


public class IncGenerator
{

	public final long start = 1;

	private long next = start;


	/**
	 * New IncGenerator
	 */
	public IncGenerator()
	{

	}


	/**
	 * Get the next unique number
	 * 
	 * @return
	 */
	public long next()
	{
		return next++;
	}

}
