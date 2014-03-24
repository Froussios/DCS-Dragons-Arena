package nl.dcs.da.client;

import nl.dcs.da.tss.TSS;
import nl.dcs.da.tss.util.Alarm;
import nl.dcs.da.tss.util.Alarm.AlarmRunningException;


/**
 * An automated player controller that performs a move automatically in
 * specified intervals
 * 
 * @author Chris
 * 
 */
public abstract class TimedAvatarOperator
		extends AvatarOperator
		implements Alarm.Listener
{

	private final Alarm alarm;


	/**
	 * A new autonomous character
	 * 
	 * @param id
	 * @param game
	 * @param frequency
	 */
	public TimedAvatarOperator(long id, TSS game, long frequency)
	{
		super(id, game);
		alarm = new Alarm(frequency);
		alarm.subscribe(this);
	}


	/**
	 * Start perfroming actions
	 * 
	 * @throws AlarmRunningException
	 */
	public void start() throws AlarmRunningException
	{
		this.alarm.start();
	}


	/**
	 * Perform the next move
	 */
	protected abstract void makeMove();


	@Override
	public void update(long interval)
	{
		makeMove();
	}

}
