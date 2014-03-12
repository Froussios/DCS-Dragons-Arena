package nl.dcs.da.tss.util;

import java.util.ArrayList;


public class StateLogger
		extends ArrayList<String>
{

	/**
	 * Boring
	 */
	private static final long serialVersionUID = -772804910769638261L;



	public StateLogger()
	{
	}


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
		this.add(message);
	}


	/**
	 * Log a game event
	 * 
	 * @param message
	 */
	public void log(String message)
	{
		this.add(message);
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
		for (String message : this.subList(this.size() - count, this.size()))
			System.out.println(message);
	}
}
