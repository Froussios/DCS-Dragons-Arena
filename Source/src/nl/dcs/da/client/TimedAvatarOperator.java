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
			// TODO stop trying after a given amount of time.
			System.err.println("Character " + getID() + " tried to act while dead.");
		}

	}
}
