package nl.dcs.da.tss;


/**
 * An exception that is thrown when the game is out of sync with the network
 * without the possibility of recovering.
 * 
 * @author Chris
 * 
 */
public class OutOfSyncException
		extends Exception
{

	public OutOfSyncException()
	{
		this("Game lost sync");
	}


	public OutOfSyncException(String message)
	{
		this(message, null);
	}


	public OutOfSyncException(String message, Exception inner)
	{
		super(message, inner);
	}
}
