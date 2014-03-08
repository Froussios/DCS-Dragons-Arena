package nl.dcs.da.tss;

public class Point
{

	private final int x, y;


	public int getX()
	{
		return x;
	}


	public int getY()
	{
		return y;
	}


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
