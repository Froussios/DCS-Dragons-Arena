package nl.dcs.da.tss;



/**
 * The battlefield
 * 
 * @author Chris
 * 
 */
public interface Battlefield
{

	/**
	 * A interface for being notified when the battlefield changes. Implement
	 * this and subscribe to a battlefield.
	 * 
	 * @author Chris
	 * 
	 */
	public static interface Listener
	{

		public void onStateChanged(Object cause);
	}


	/**
	 * Get the contents at location
	 * 
	 * @param point The location
	 * @return The actor at that location. May be null.
	 */
	public Actor get(Point point);


	/**
	 * Get the contents at location
	 * 
	 * @param x
	 * @param y
	 * @return The actor at that location. May be null.
	 */
	public Actor get(int x, int y);


	/**
	 * Get the contents at location if it is a player, and null otherwise
	 * 
	 * @param point The location
	 * @return
	 */
	public Player getAsPlayer(Point point);


	/**
	 * Get the contents at location if it is a dragon, and null otherwise.
	 * 
	 * @param point The location
	 * @return
	 */
	public Dragon getAsDragon(Point point);


	/**
	 * The location of an actor
	 * 
	 * @param id The actor's id
	 * @return The location of the actor, or null if none is found
	 */
	public Point findActor(long id);


	/**
	 * The listener will be notified every time the battlefield changes
	 * 
	 * @param listener
	 */
	public void addListener(Listener listener);


	/**
	 * Get an instance of the current battlefield that will not be modified
	 * further
	 * 
	 * @return
	 */
	public Battlefield snapshot();
}
