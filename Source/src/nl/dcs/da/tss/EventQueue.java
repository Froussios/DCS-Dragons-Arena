package nl.dcs.da.tss;

import nl.dcs.da.tss.events.Event;
import nl.dcs.da.tss.events.MarkEvent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

public class EventQueue
		implements Iterable<Event>, Serializable
{

	private final SortedSet<Event> events = new TreeSet<>();


	/**
	 * Add a new event to the event queue
	 * 
	 * @param event
	 */
	public synchronized void add(Event event)
	{
		events.add(event);
	}


	/**
	 * Gets all the events inside a a range of time
	 * 
	 * @param since The oldest event, inclusive
	 * @param until The most recent event, exclusive
	 * @return The events, from older to recent
	 */
	public synchronized Iterable<Event> getEvents(long since, long until)
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


	@Override
	public Iterator<Event> iterator()
	{
		return events.iterator();
	}

}
