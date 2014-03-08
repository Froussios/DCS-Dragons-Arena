package nl.dcs.da.tss;

import java.util.ArrayList;


public class TSS implements Battlefield
{

	private final ArrayList<State> states = new ArrayList<State>();
	private final ArrayList<Long> delays = new ArrayList<Long>();
	private final ArrayList<Boolean> inSync = new ArrayList<Boolean>();

	private final EventQueue events = new EventQueue();


	public TSS(long maxDelay)
	{
		if (maxDelay <= 0)
			throw new IllegalArgumentException("Maximum delay cannot be 0");

		long delay = 1;
		while (delay <= maxDelay)
		{
			states.add(new State());
			delays.add(delay);
			inSync.add(true);

			delay *= 2;
		}
	}



	@Override
	public Actor get(Point point)
	{

	}


	@Override
	public Actor get(int x, int y)
	{
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Point findActor(long id)
	{
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void addListener(Listener listener)
	{
		// TODO Auto-generated method stub

	}


	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub

	}


	/**
	 * Get a snapshot of the leading state
	 */
	@Override
	public synchronized State snapshot()
	{
		return getLeadingState().snapshot();
	}


	public State getLeadingState()
	{
		return states.get(0);
	}

}
