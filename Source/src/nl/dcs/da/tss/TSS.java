package nl.dcs.da.tss;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;

import nl.dcs.da.tss.events.Event;

public class TSS
		implements Battlefield
{

	private final ArrayDeque<SynchronizedState> states = new ArrayDeque<SynchronizedState>();
	private final ArrayList<Long> delays = new ArrayList<Long>();
	private final ArrayList<Boolean> inSync = new ArrayList<Boolean>();

	private long simulationClock;
	private final EventQueue events = new EventQueue();


	/**
	 * Create a new TSS synchronisation
	 * 
	 * @param maxDelay Upon receipt of event that are older than maxDelay, an
	 *            OutOfSyncException may be thrown
	 */
	public TSS(long maxDelay)
	{
		if (maxDelay <= 0)
			throw new IllegalArgumentException("Maximum delay cannot be 0");

		long delay = 1;
		while (delay <= maxDelay)
		{
			states.add(new SynchronizedState(0 - delay, events));
			delays.add(delay);
			inSync.add(true);

			delay *= 2;
		}
	}


	/**
	 * Receive event. The event may be either local or from the network. Local
	 * logs and states will be updated accordingly.
	 * 
	 * @param event The event
	 * @throws OutOfSyncException
	 */
	public synchronized void receiveEvent(Event event) throws OutOfSyncException
	{
		// Add event to history
		events.add(event);

		// Reset states as needed
		SynchronizedState previousState = null;
		Iterator<SynchronizedState> reverseIterator = states.descendingIterator();
		while (reverseIterator.hasNext())
		{
			SynchronizedState state = reverseIterator.next();

			if (state.getClock() > event.getSimulationTime())
			{
				// State is out of sync. Recover from previous state

				if (previousState == null)
					// There is no previous state to recover from
					throw new OutOfSyncException();

				// Recover from previous state
				state.loadFrom(previousState);
				state.catchup();
			}
		}
	}


	/**
	 * Get the current time for the simulation. This is not necessarily equal to
	 * the time of the leading state.
	 * 
	 * @return The time
	 */
	public synchronized long getSimulationTime()
	{
		return this.simulationClock;
	}


	/**
	 * Increment the time for this simulation
	 * 
	 * @param timespan The amount to increment the time by
	 */
	public synchronized void incrementTime(long timespan)
	{
		this.simulationClock += timespan;
		// Increment the time for every state
		for (SynchronizedState state : states)
			state.setClock(state.getClock() + timespan);
	}


	@Override
	public synchronized Actor get(Point point)
	{
		return getLeadingState().get(point);
	}


	@Override
	public synchronized Actor get(int x, int y)
	{
		return getLeadingState().get(x, y);
	}


	@Override
	public synchronized Point findActor(long id)
	{
		return getLeadingState().findActor(id);
	}


	@Override
	public synchronized void addListener(Listener listener)
	{
		getLeadingState().addListener(listener);
	}


	/**
	 * Get a snapshot of the leading state
	 */
	@Override
	public synchronized State snapshot()
	{
		return getLeadingState().snapshot();
	}


	/**
	 * Get the most up-to-date state. The instance may be modified after being
	 * returned.
	 * 
	 * @return
	 */
	protected synchronized SynchronizedState getLeadingState()
	{
		return states.getFirst();
	}

}
