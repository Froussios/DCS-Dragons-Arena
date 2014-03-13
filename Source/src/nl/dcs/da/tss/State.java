package nl.dcs.da.tss;

import java.util.ArrayList;
import java.util.List;

import nl.dcs.da.tss.events.ActorAttack;
import nl.dcs.da.tss.events.Event;
import nl.dcs.da.tss.events.Heal;
import nl.dcs.da.tss.events.PlayerMove;
import nl.dcs.da.tss.util.StateLogger;


/**
 * A state of the game that can consume events and hold logs.
 * 
 * @author Chris
 * 
 */
public class State
		implements Battlefield
{

	public static final int size = 25;


	private final Actor[][] battlefield = new Actor[25][25];
	private final List<Listener> listeners = new ArrayList<Listener>();
	private final StateLogger history = new StateLogger();


	/**
	 * Consume an event
	 * 
	 * @param event the event to be consumed
	 */
	public synchronized void consume(Event event)
	{
		if (event instanceof PlayerMove)
		{
			consume((PlayerMove) event);
		}
		else if (event instanceof ActorAttack)
		{
			consume((ActorAttack) event);
		}
		else if (event instanceof Heal)
		{
			consume((Heal) event);
		}
		else
		{
			throw new IllegalArgumentException("Tried to consume unknown event type");
		}
	}


	/**
	 * Consume a move event
	 * 
	 * @param move
	 */
	public synchronized void consume(PlayerMove move)
	{
		Point current = this.findActor(move.getPlayer());
		Point target = move.getTarget();
		Player player = this.getAsPlayer(current);

		// Detect illegal move
		String ignore = null;
		// Player not in game
		if (current == null)
			ignore = "player not on battlefield";
		// Too far to jump to
		else if (!current.adjacent(target))
			ignore = "too far";
		// Location not empty
		else if (this.get(target) != null)
			ignore = "location not empty";
		// Commanding wrong player
		else if (!player.equals(move.getPlayer()))
			ignore = "not commanding this player";
		else
			ignore = null;

		if (ignore != null)
		{
			// Ignore illegal move
			history.log("IGNORED:", move, ":", ignore);
		}
		else
		{
			// Execute event
			this.set(current, null);
			this.set(target, player);

			history.log(move);

			// Notify listeners of changes
			onChanged();
		}
	}


	/**
	 * Consume an attack event
	 * 
	 * @param attack
	 */
	public synchronized void consume(ActorAttack attack)
	{
		Point current = this.findActor(attack.getAttacker());
		Point target = attack.getTarget();
		Actor attacker = this.get(current);
		Actor victim = this.get(target);

		// Detect illegal move
		String ignore = null;
		// Player not found
		if (current == null)
			ignore = "Actor not on the battlefield";
		// Victim not found
		else if (victim == null)
			ignore = "Target not found";
		// Target too far
		else if (current.distance(target) > 2)
			ignore = "Target too far";
		// Target null or not an opponent)
		else if ((attacker instanceof Player && victim instanceof Player) || (attacker instanceof Dragon && victim instanceof Dragon))
			ignore = "Attacker and victim are not opponents";
		// Commanding wrong actor
		else if (!attacker.equals(attack.getAttacker()))
			ignore = "Commanding wrong actor";
		else
			ignore = null;


		if (ignore != null)
		{
			// Ignore illegal move
			history.log("IGNORED:", attack, ":", ignore);
		}
		else
		{
			// Deliver damage
			int ap = attacker.getAP();
                    int receiveDamage = victim.receiveDamage(ap);

			history.log(attack);

			// Remove victim if dead
			if (victim.getHP() <= 0)
			{
				this.set(target, null);
				history.log(victim, "died");
			}

			// Notify listeners
			onChanged();
		}
	}


	/**
	 * Consume a healing event
	 * 
	 * @param heal
	 */
	public synchronized void consume(Heal heal)
	{
		Point healerLocation = this.findActor(heal.getHealer());
		Point targetLocation = this.findActor(heal.getTarget());
		Player healer = getAsPlayer(healerLocation);
		Player target = getAsPlayer(targetLocation);

		// Detect illegal move
		String ignore = null;
		// healer not found
		if (healer == null)
			ignore = "Healer not found or not a player";
		// target not found
		else if (target == null)
			ignore = "Target not found or not a player";
		// target too far
		else if (healerLocation.distance(targetLocation) > 5)
			ignore = "Target too far";
		// Healing self
		else if (healer.equals(target))
			ignore = "Cannot heal one's self";
		else
			ignore = null;

		if (ignore != null)
		{
			// Ignore illegal move
			history.log("IGNORED:", heal, ":", ignore);
		}
		else
		{
			// Execute event
			int ap = healer.getAP();
			target.heal(ap);

			history.log(heal);

			// Notify listeners
			this.onChanged();
		}
	}


	/**
	 * Find the location of an actor
	 * 
	 * @param actor The id of the actor
	 * @return The location of the actor
	 */
	@Override
	public synchronized Point findActor(long actor)
	{
		// TODO optimise
		for (int x = 0; x < battlefield.length; x++)
			for (int y = 0; y < battlefield[x].length; y++)
				if (battlefield[x][y] != null)
					if (battlefield[x][y].equals(actor))
						return new Point(x, y);
		return null;
	}


	/**
	 * Get player at location
	 * 
	 * @param location
	 * @return A player instance, or null if none is found
	 */
	public synchronized Player getAsPlayer(Point location)
	{
		if (location == null)
			return null;

		Actor element = get(location);
		if (element instanceof Player)
			return (Player) element;
		else
			return null;
	}


	/**
	 * Get dragon at location
	 * 
	 * @param location
	 * @return A dragon instance, or null if none is found
	 */
	public synchronized Dragon getAsDragon(Point location)
	{
		if (location == null)
			return null;

		Actor element = get(location);
		if (element instanceof Dragon)
			return (Dragon) element;
		else
			return null;
	}


	/**
	 * Sets the value at a place at the battlefield
	 * 
	 * @param target The location in the battlefield
	 * @param value The element
	 * @return The new value at the location
	 */
	public synchronized Actor set(Point target, Actor value)
	{
		return this.battlefield[target.getX()][target.getY()] = value;
	}


	/**
	 * The the contents at location
	 */
	@Override
	public synchronized Actor get(Point point)
	{
		if (point == null)
			throw new IllegalArgumentException();

		return get(point.getX(), point.getY());
	}


	/**
	 * Get the contents at location
	 */
	@Override
	public synchronized Actor get(int x, int y)
	{
		return this.battlefield[x][y];
	}


	/**
	 * Get a deep copy of this state
	 */
	@Override
	public synchronized State clone()
	{
		State clone = new State();
		for (int x = 0; x < size; x++)
			for (int y = 0; y < size; y++)
			{
				Point point = new Point(x, y);
				Actor value = get(point);
				if (value != null)
					value = value.clone();
				clone.set(point, value);
			}
		clone.history.addAll(this.history);
		return clone;
	}


	/**
	 * Copies the state of another State instance. Keeps listeners
	 * 
	 * @param other the original state to copy from
	 */
	public synchronized void loadFrom(State other)
	{
		// Copy map
		for (int x = 0; x < size; x++)
			for (int y = 0; y < size; y++)
			{
				Point point = new Point(x, y);
				Actor value = other.get(point);
				if (value != null)
					value = value.clone();
				this.set(point, value);
			}

		// Copy logs
		this.history.clear();
		this.history.addAll(other.history);

		this.onChanged();
	}


	/**
	 * Same as clone
	 */
	@Override
	public synchronized State snapshot()
	{
		return clone();
	}


	/**
	 * Add another listener to the subscribers' list
	 */
	@Override
	public synchronized void addListener(Listener listener)
	{
		this.listeners.add(listener);
	}


	/**
	 * Notify all listeners that an action has been executed
	 */
	public void onChanged()
	{
		for (Listener listener : listeners)
			listener.onStateChanged();
	}


	/**
	 * Get the logs for this state
	 * 
	 * @return
	 */
	public synchronized Iterable<String> getHistory()
	{
		return history;
	}


	/**
	 * Human-readable representation of this state's map.
	 */
	@Override
	public String toString()
	{
		String rv = "";
		for (int y = 0; y < 25; y++)
		{
			for (int x = 0; x < 25; x++)
			{
				Actor el = get(x, y);
				if (el instanceof Player)
					rv += " P ";
				else if (el instanceof Dragon)
					rv += " D ";
				else
					rv += " . ";
			}
			rv += "\n";
		}
		return rv;
	}
}
