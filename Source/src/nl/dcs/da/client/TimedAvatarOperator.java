package nl.dcs.da.client;

import nl.dcs.da.tss.State.GameState;
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
	private long failtime = 0;
	private static final long maxFailtime = 2;


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


	/**
	 * The function that will be executed asynchronously. 
	 */
	@Override
	public void update(long interval)
	{
		// TODO stop trying at gameover
		
		try
		{
			// Make a move if the player is alive and the game is playing
			if (this.getGame().getPhase().equals(GameState.Playing))
			{
				if (this.inGame())
					makeMove();
				else
					throw new CharacterDeadException();
			}
		}
		catch (CharacterDeadException e)
		{
			// Stop trying after it is no longer possible for the game to be revised into the player being alive.
			this.failtime += interval;
			if (this.failtime >= maxFailtime)
				this.alarm.stop();
			
			System.err.println("Character " + getID() + " tried to act while dead. Failtime " + this.failtime);
		}

	}
}
