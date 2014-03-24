package nl.dcs.da.tss;

import nl.dcs.da.tss.events.Event;
import nl.dcs.da.tss.events.StartGame;
import nl.dcs.da.tss.util.Alarm;
import nl.dcs.da.tss.util.Alarm.AlarmRunningException;


/**
 * Extention of TSS that automatically updates time with the wall clock
 * 
 * @author Chris
 * 
 */
public class TimedTSS
		extends TSS
		implements Alarm.Listener
{

	private final Alarm alarm;


	/**
	 * Create a new TSS instance
	 * 
	 * @param start The initial state of the game
	 * @param maxDelay The maximum delay for a late event
	 */
	public TimedTSS(State start, long maxDelay, Alarm updater)
	{
		super(start, maxDelay);

		this.alarm = new Alarm(50);
		this.alarm.subscribe(this);
	}


	@Override
	public synchronized boolean receiveEvent(Event event) throws OutOfSyncException
	{
		// Start clocks
		if (event instanceof StartGame)
		{
			try
			{
				this.alarm.start();
			}
			catch (AlarmRunningException e)
			{
				throw new IllegalStateException("Received StartGame event after the clock has started", e);
			}
		}

		// Consume event
		return super.receiveEvent(event);
	}


	@Override
	public void update(long interval)
	{
		this.incrementTime(interval);

	}



}