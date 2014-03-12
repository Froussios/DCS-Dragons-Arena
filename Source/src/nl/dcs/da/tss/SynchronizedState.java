package nl.dcs.da.tss;

import nl.dcs.da.tss.events.Event;


/**
 * A extension of State, that also stores a clock and automatically consumes
 * event that are due.
 * 
 * @author Chris
 * 
 */
public class SynchronizedState
		extends State
		implements Battlefield
{

	private long clock = Long.MIN_VALUE;
	private long lastCatchup = Long.MIN_VALUE;
	private final EventQueue events;


	/**
	 * Create a new state that contains logic for synchronisation
	 * 
	 * @param events The event queue to read events from. This is not edited.
	 * @param startingTime The initial clock value for the state. Set to a
	 *            negative value for delayed states
	 */
	public SynchronizedState(long startingTime, EventQueue events)
	{
		this.clock = startingTime;
		this.lastCatchup = this.clock;
		this.events = events;
	}


	/**
	 * Get the current simulation time for this state
	 * 
	 * @return
	 */
	public synchronized long getClock()
	{
		return this.clock;
	}


	/**
	 * Sets the clock and consumes events to catch up. The new time can only be
	 * greater than the current time.
	 * 
	 * @param time The new simulation time for this state.
	 */
	public synchronized void setClock(long time)
	{
		if (time < this.clock)
			throw new IllegalArgumentException("Cannot roll back time");

		clock = time;

		// Consume events
		this.catchup();
	}


	/**
	 * Consumes all the events that are older than the simulation time of this
	 * instance
	 */
	public synchronized void catchup()
	{
		for (Event event : events.getEvents(lastCatchup, clock))
			this.consume(event);

		this.lastCatchup = this.clock;
	}


	/**
	 * Copies the state of another SynchronizedState instance. Keeps listeners
	 * 
	 * @param other the original state to copy from
	 */
	public synchronized void loadFrom(SynchronizedState other)
	{
		// Copy clocks
		this.clock = other.clock;
		this.lastCatchup = other.lastCatchup;

		// Copy battlefield and history
		super.loadFrom(other);
	}


	/**
	 * Human-readable representation for this state. Includes simulation time
	 * and map.
	 */
	@Override
	public synchronized String toString()
	{
		String rv = "Time: " + this.getClock() + "\n";
		return rv + super.toString();
	}
}
