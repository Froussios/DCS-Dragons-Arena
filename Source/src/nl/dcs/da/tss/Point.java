package nl.dcs.da.tss;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Point
		implements Comparable<Point>, Serializable
{

	private static final Random random = new Random();
	private static final long serialVersionUID = 6181684382275406670L;
	private final int x, y;



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
	 * Gets all the neighbours to this tile. Does not include points that are
	 * outside the map.
	 * 
	 * @return A collection of points for all the neighbours in the battlefield
	 */
	public Collection<Point> getNeighbours()
	{
		ArrayList<Point> neighbours = new ArrayList<Point>(8);

		neighbours.add(new Point(getX() + 1, getY()));
		neighbours.add(new Point(getX() - 1, getY()));
		neighbours.add(new Point(getX(), getY() + 1));
		neighbours.add(new Point(getX(), getY() - 1));
		// neighbours.add(new Point(getX() + 1, getY() + 1));
		// neighbours.add(new Point(getX() + 1, getY() - 1));
		// neighbours.add(new Point(getX() - 1, getY() + 1));
		// neighbours.add(new Point(getX() - 1, getY() - 1));

		ArrayList<Point> killset = new ArrayList<Point>(8);
		for (Point point : neighbours)
			if (!point.isInsideBattlefield())
				killset.add(point);
		for (Point point : killset)
			neighbours.remove(point);

		return neighbours;
	}


	/**
	 * Get all the points that are at most <code>distance</code> tiles away from
	 * this one.
	 * 
	 * @param distance The maximum distance
	 * @return
	 */
	public Collection<Point> getNeighbours(int distance)
	{
		Set<Point> neighbourhood = new HashSet<Point>();
		neighbourhood.add(this);
		for (int i = 0; i < distance; i++)
		{
			Set<Point> n = new HashSet<Point>();
			for (Point point : neighbourhood)
				n.addAll(point.getNeighbours());
			neighbourhood = n;

			ArrayList<Point> killset = new ArrayList<Point>(8);
			for (Point point : neighbourhood)
				if (!point.isInsideBattlefield())
					killset.add(point);
			for (Point point : killset)
				neighbourhood.remove(point);
		}

		return new ArrayList<Point>(neighbourhood);
	}


	/**
	 * Returns a new point that is shifted in relation to this.
	 * 
	 * @param x The shift on the x axis
	 * @param y the shift on the y axis
	 * @return The shifted point
	 */
	public Point shift(int x, int y)
	{
		return new Point(this.getX() + x, this.getY() + y);
	}


	/**
	 * Get a random point inside the 25x25 battlefield.
	 * 
	 * @return The randomised point.
	 */
	public static Point random()
	{
		return new Point(random.nextInt(25), random.nextInt(25));
	}


	/**
	 * Returns true if the point represents a valid point inside the battlefield
	 */
	protected boolean isInsideBattlefield()
	{
		return x >= 0 && x < 25 && y >= 0 && y < 25;
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
	public int compareTo(Point other)
	{
		int comparison = Integer.compare(this.getX(), other.getX());
		if (comparison == 0)
			return Integer.compare(this.getY(), other.getY());
		else
			return comparison;
	}


	@Override
	public int hashCode()
	{
		return getX() + 25 * getY();
	}


	@Override
	public String toString()
	{
		return "(" + x + "," + y + ")";
	}
}
