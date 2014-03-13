package nl.dcs.da.tss;

import java.io.Serializable;

public class Point implements Serializable
{

	private final int x, y;
    private static final long serialVersionUID = 6181684382275406670L;


	/**
	 * Get the X coordinate
	 * 
	 * @return the X coordinate
	 */
	public int getX()
	{
		return x;
	}


	/**
	 * The the Y coordinate
	 * 
	 * @return
	 */
	public int getY()
	{
		return y;
	}


	/**
	 * A new immutable Point
	 * 
	 * @param x
	 * @param y
	 */
	public Point(int x, int y)
	{
		this.x = x;
		this.y = y;
	}


	/**
	 * Get if the two points are adjacent
	 * 
	 * @param other
	 * @return true, if a player can move from one to the other
	 */
	public boolean adjacent(Point other)
	{
		return this.distance(other) == 1;
	}


	/**
	 * Get the distance between the two points
	 * 
	 * @param other
	 * @return
	 */
	public int distance(Point other)
	{
		return Math.abs(this.x - other.x) + Math.abs(this.y - other.y);
	}


	/**
	 * Two points are equal if all of their coordinates are the same
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof Point)
		{
			Point other = (Point) obj;
			return this.x == other.x && this.y == other.y;
		}
		else
			return false;
	}


	@Override
	public String toString()
	{
		return "(" + x + "," + y + ")";
	}
}
