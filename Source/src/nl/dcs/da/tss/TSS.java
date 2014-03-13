package nl.dcs.da.tss;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;

import nl.dcs.da.tss.events.Event;

/**
 * An implementation of the Trailing State synchronisation algorithm. The
 * algorithms maintains a set of game states at various delays from real time,
 * and uses previous states to recover more recent states that are out of sync.
 * 
 * In this implementation, trailing states are spaced apart with exponentially
 * increasing intervals. This guarantees that an event that is N time units late
 * will cause up to 2N time units to be replayed. Therefore, disruptions in
 * recent states are cheap to recover, while the memory cost of having a long
 * recovery window is reduced.
 * 
 * @author Chris
 * 
 */
public class TSS
		implements Battlefield
{

	private final ArrayDeque<SynchronizedState> states = new ArrayDeque<>();
	private final ArrayList<Long> delays = new ArrayList<>();
	private final ArrayList<Boolean> inSync = new ArrayList<>();

	private long simulationClock;
	private final EventQueue events = new EventQueue();


	/**
	 * Get the event queue for this instance.
	 * 
	 * @return
	 */
	public Iterable<Event> getEventQueue()
	{
		return this.events;
	}


	/**
	 * Create a new TSS synchronisation component
	 * 
	 * @param maxDelay Upon receipt of event that are older than maxDelay, an
	 *            OutOfSyncException may be thrown
	 */
	public TSS(State start, long maxDelay)
	{
		if (maxDelay <= 0)
			throw new IllegalArgumentException("Maximum delay cannot be 0");

		// Create trail of states
		long delay = 1;
		while (delay <= maxDelay)
		{
			SynchronizedState state = new SynchronizedState(0 - delay, events);
			state.loadFrom(start);
			states.add(state);
			delays.add(delay);
			inSync.add(true);

			System.out.println("Created state@" + state.getClock());

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
					throw new OutOfSyncException("Last state @" + state.getClock() + " is out of sync.");

				System.out.println("State @" + state.getClock() + " is out of sync. Recovering from @" + previousState.getClock());

				// Recover from previous state
				long clock = state.getClock();
				state.loadFrom(previousState);
				state.setClock(clock);
				state.catchup();
			}

			previousState = state;
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

		// Delete events that are too old
		long before = getLastState().getClock();
		events.trimEvents(before);
	}


	/**
	 * DEBUGGING ONLY
	 * 
	 * @param point
	 * @param value
	 */
	public synchronized void set(Point point, Actor value)
	{
		for (State state : states)
			state.set(point, value.clone());
	}


	/**
	 * Get every state in this TSS instance
	 * 
	 * @return
	 */
	public synchronized Iterable<SynchronizedState> getStates()
	{
		return states;
	}


	/**
	 * Get the contents of the leading state at a specific point.
	 */
	@Override
	public synchronized Actor get(Point point)
	{
		return getLeadingState().get(point);
	}


	/**
	 * Get the contents of the leading state at a specific point.
	 */
	@Override
	public synchronized Actor get(int x, int y)
	{
		return getLeadingState().get(x, y);
	}


	/**
	 * Find the location of an actor in the leading state.
	 */
	@Override
	public synchronized Point findActor(long id)
	{
		return getLeadingState().findActor(id);
	}


	/**
	 * Add a listener for changes in the leading state.
	 */
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


	/**
	 * Get the least up-to-date state.The instance may be modified after being
	 * returned.
	 * 
	 * @return
	 */
	protected synchronized SynchronizedState getLastState()
	{
		return states.getLast();
	}


	/**
	 * Human-readable representation of the leading state.
	 */
	@Override
	public String toString()
	{
		return getLeadingState().toString();
	}

}
