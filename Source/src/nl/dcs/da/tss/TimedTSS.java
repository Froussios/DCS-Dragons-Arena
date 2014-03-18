package nl.dcs.da.tss;

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
	 * @param updater The alarm that defines time for this instance.
	 * @throws AlarmRunningException The alarm supplied must not be already in
	 *             use
	 */
	public TimedTSS(State start, long maxDelay, Alarm updater) throws AlarmRunningException
	{
		super(start, maxDelay);

		this.alarm = updater;
		this.alarm.subscribe(this);
		this.alarm.start();
	}


	@Override
	public void update(long interval)
	{
		this.incrementTime(interval);

	}



}
