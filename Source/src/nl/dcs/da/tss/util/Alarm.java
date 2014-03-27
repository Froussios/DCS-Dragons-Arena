package nl.dcs.da.tss.util;


import java.util.logging.Logger;

/**
 * An alarm that fires in specified intervals
 * 
 * @author Chris
 * 
 */
public class Alarm
		implements Runnable
{

	/**
	 * An object that can subscribe to an Alarm
	 * 
	 * @author Chris
	 * 
	 */
	public static interface Listener
	{

		/**
		 * The function that is called when the alarm fires
		 * 
		 * @param interval The time that has passed since the Alarm instance was
		 *            first created or since it last fired.
		 */
		public void update(long interval);
	}

	private long lastfire;
	private final long minInterval;
	private Listener listener;
	private boolean running = false;
	private final long scale;
	private Thread thread = null;


	/**
	 * Create a new alarm, waiting to be started
	 * 
	 * @param minInterval Every how many milliseconds the alarm fires
	 */
	public Alarm(long minInterval)
	{
		this(minInterval, 1);
	}


	/**
	 * Create a new alarm, waiting to be started
	 * 
	 * @param minInterval Every how many time units the alarm fires
	 * @param scale How many milliseconds a time unit is
	 */
	public Alarm(long minInterval, long scale)
	{
		this.minInterval = minInterval;
		this.scale = scale;
	}


	/**
	 * Be notified when this alarm fires
	 * 
	 * @param listener The listener to be subscribed
	 * @return true, if no previous listener was overwritten. false otherwise.
	 */
	public synchronized boolean subscribe(Listener listener)
	{
		boolean clear = this.listener == null;
		this.listener = listener;
		return clear;
	}


	/**
	 * Starts the alarm on a new clock
	 * 
	 * @throws AlarmRunningException
	 */
	public synchronized void start() throws AlarmRunningException
	{
		if (!this.running && this.thread == null)
		{
			this.thread = new Thread(this);
			this.lastfire = System.currentTimeMillis();
			this.running = true;
			thread.start();
		}
		else
		{
			throw new AlarmRunningException();
		}
	}
	
	
	/**
	 * The next and all subsequent fires are cancelled, unless the Alarm is restarted.
	 */
	public synchronized void stop()
	{
		running = false;
	}


	@Override
	public void run()
	{
		while (running)
		{
			// Sleep for at least minInterval time
			try
			{
				Thread.sleep(minInterval * scale);
			}
			catch (InterruptedException e)
			{
                Logger.getLogger(this.getClass().toString()).severe(e.getMessage());
			}

			// Fire clock
			onFire();
		}
	}
	
	
	/**
	 * The alarm fires
	 */
	private void onFire()
	{
		long t = this.lastfire;
		this.lastfire = System.currentTimeMillis();

		// TODO think about this more
		Listener l = null;
		synchronized (this)
		{
			l = listener;
		}

		if (l != null)
		{
			l.update((lastfire - t) / scale);
		}
	}
	

	/**
	 * This exception is thrown when operations are attempted on a running
	 * alarm.
	 * 
	 * @author Chris
	 * 
	 */
	public static class AlarmRunningException
			extends Exception
	{

		public AlarmRunningException(String message, Throwable inner)
		{
			super(message, inner);
		}


		public AlarmRunningException(String message)
		{
			super(message, null);
		}


		public AlarmRunningException()
		{
			super("Alarm is running.");
		}
	}
}
