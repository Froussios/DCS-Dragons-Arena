package nl.dcs.da.tss.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public class StateLogger implements Iterable<String>
{

	private final List<String> history = new LinkedList<String>();


	/**
	 * Log a game event
	 * 
	 * @param args
	 */
	public void log(Object... args)
	{
		String message = "";
		for (Object arg : args)
			message += arg + " ";
		history.add(message);
	}


	/**
	 * Log a game event
	 * 
	 * @param message
	 */
	public void log(String message)
	{
		history.add(message);
	}


	@Override
	public Iterator<String> iterator()
	{
		return history.iterator();
	}


	/**
	 * Print the message history
	 */
	public void print()
	{
		for (String message : this)
			System.out.println(message);
	}


	/**
	 * Print the last messages
	 * 
	 * @param count The number of message to print
	 */
	public void print(int count)
	{
		for (String message : history.subList(history.size() - count, history.size()))
			System.out.println(message);
	}
}
