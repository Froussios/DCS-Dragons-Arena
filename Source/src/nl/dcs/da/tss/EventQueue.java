package nl.dcs.da.tss;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import nl.dcs.da.tss.events.Event;
import nl.dcs.da.tss.events.MarkEvent;

public class EventQueue
		implements Iterable<Event>, Serializable
{

	private final SortedSet<Event> events = new TreeSet<>();


	/**
	 * Add a new event to the event queue
	 * 
	 * @param event
	 */
	public synchronized boolean add(Event event)
	{
		return events.add(event);
	}


	/**
	 * Add all the events into the queue.
	 * 
	 * @param events
	 */
	public synchronized boolean addAll(Collection<Event> events)
	{
		return this.events.addAll(events);
	}


	/**
	 * Add all the events into the queue.
	 * 
	 * @param events The events to be added.
	 * @return
	 */
	public synchronized boolean addAll(Iterable<Event> events)
	{
		ArrayList<Event> e = new ArrayList<Event>();
		for (Event event : events)
			e.add(event);
		return this.events.addAll(e);
	}


	/**
	 * Gets all the events inside a a range of time
	 * 
	 * @param since The oldest event, inclusive
	 * @param until The most recent event, exclusive
	 * @return The events, from older to recent
	 */
	public synchronized Collection<Event> getEvents(long since, long until)
	{
		Event sinceE = new MarkEvent(since);
		Event untilE = new MarkEvent(until);
		return events.subSet(sinceE, untilE);
	}


	/**
	 * Removes events that are older than the specified date
	 * 
	 * @param before
	 */
	public synchronized void trimEvents(long before)
	{
		Event mark = new MarkEvent(before);
		ArrayList<Event> killset = new ArrayList<Event>(events.headSet(mark));
		events.removeAll(killset);
	}


	/**
	 * Remove every event from this queue.
	 */
	public synchronized void clear()
	{
		this.events.clear();
	}


	@Override
	public Iterator<Event> iterator()
	{
		return events.iterator();
	}

}
