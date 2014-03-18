package nl.dcs.da.tss;

import java.util.ArrayList;
import java.util.Collection;

public class Point
		implements Comparable<Point>
{

	private final int x, y;


	/**
	 * Get the X coordinate
	 * 
	 * @return
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
		neighbours.add(new Point(getX() + 1, getY() + 1));
		neighbours.add(new Point(getX() + 1, getY() - 1));
		neighbours.add(new Point(getX() - 1, getY() + 1));
		neighbours.add(new Point(getX() - 1, getY() - 1));

		ArrayList<Point> killset = new ArrayList<Point>(8);
		for (Point point : neighbours)
			if (!point.isInsideBattlefield())
				killset.add(point);
		for (Point point : killset)
			neighbours.remove(point);

		return neighbours;
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
