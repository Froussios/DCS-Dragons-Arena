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
	private int strikes = 0;
	private static final int maxStrikes = 2;


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
	 * Start performing actions
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
	protected abstract void makeMove() throws CharacterDeadException;


	@Override
	public void update(long interval)
	{
		// TODO stop strying at gameover
		
		try
		{
			if (this.inGame())
			{
				makeMove();
				strikes = 0;
			}
			else
				throw new CharacterDeadException();
		}
		catch (CharacterDeadException e)
		{
			// Stop trying after a given amount of time.
			this.strikes++;
			if (this.strikes >= maxStrikes)
				this.alarm.stop();
			
			System.err.println("Character " + getID() + " tried to act while dead. Strike " + this.strikes);
		}

	}
}
