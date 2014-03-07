package nl.dcs.da.tss;

import java.util.ArrayList;
import java.util.List;

public class State
	implements Battlefield 
{
	
	private long clock;	
	private Element[][] battlefield = new Element[25][25];
	
	List<Listener> listeners = new ArrayList<Listener>();
	
	
	public void Consume(Event event)
	{
		// TODO
		if (event instanceof PlayerMove)
		{
			Consume((PlayerMove) event);
		}
	}
	
	
	public void Consume(PlayerMove move)
	{
		Point current = this.getActorLocation(move.getPlayer());
		Point target            = move.getTarget();
		Player player = this.getPlayer(current);
		
		// Ignore if illegal move
		if ( !current.adjacent(target))
			return; // Too far to jump to
		if ( this.getPlayer(target) != null )
			return; // Location not empty
		if ( !player.equals(move.getPlayer()) )
			return; // Commanding wrong player
		
		// Execute event
		this.set(current, null);
		this.set(target, player);
		
		// Notify listeners of changes
		onChanged();
	}
	
	
	/**
	 * Sets the clock and consumes events to catch up.
	 * The new time can only be greater than the current time.
	 * @param time The new simulation time for this state.
	 */
	public void setClock(long time)
	{
		if (time < this.clock)
			throw new IllegalArgumentException("Cannot roll back time");
		
		clock = time;
		
		// TODO consume events
	}
	
	
	public Point getActorLocation(long actor)
	{
		// TODO optimise
		for ( int x=0 ; x<battlefield.length ; x++ )
			for ( int y=0 ; y<battlefield[x].length ; y++ )
				if ( battlefield[x][y] != null )
					if ( battlefield[x][y].equals(actor) )
						return new Point(x,y);
		return null;
	}
	
	
	public Actor getActor(long actorId)
	{
		// optimise
		for ( int x=0 ; x<battlefield.length ; x++ )
			for ( int y=0 ; y<battlefield[x].length ; y++ )
				if ( battlefield[x][y] != null )
					if ( battlefield[x][y].equals(actorId) )
						return (Actor) battlefield[x][y];
		return null;
	}
	
	
	public Player getPlayer(Point location)
	{
		Element element = get(location);
		if ( element instanceof Player )
			return (Player) element;
		else
			return null;
	}

	
	/**
	 * Sets the value at a place at the battlefield
	 * @param target The location in the battlefield
	 * @param value The element
	 * @return The new value at the location
	 */
	public Element set(Point target, Element value)
	{
		return this.battlefield[target.getX()][target.getY()] = value;
	}
	

	@Override
	public Element get(Point point)
	{
		return get(point.getX(), point.getY());
	}


	@Override
	public Element get(int x, int y)
	{
		return this.battlefield[x][y];
	}


	@Override
	public void addListener(Listener listener)
	{
		this.listeners.add(listener);		
	}
	
	
	/**
	 * Notify all listeners that an action has been executed
	 */
	public void onChanged()
	{
		for ( Listener listener : listeners )
			listener.onStateChanged();
	}
	
	
	@Override
	public String toString()
	{
		String rv = "";
		for ( int y=0 ; y<25 ; y++ )
		{
			for ( int x=0 ; x<25 ; x++ )
			{
				Element el = get(x,y);
				if ( el instanceof Player )
					rv += "P";
				else if ( el instanceof Dragon )
					rv += "D";
				else
					rv += " ";
			}
			rv += "\n";
		}
		return rv;
	}
}
