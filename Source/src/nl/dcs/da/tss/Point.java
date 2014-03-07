package nl.dcs.da.tss;

public class Point
{
	private int x,y;
	
	public int getX() {return x;}
	public int getY() {return y;}
	
	public Point(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	
	/**
	 * get if the two points are adjacent
	 * @param other
	 * @return true, if a player can move from one to the other
	 */
	public boolean adjacent(Point other)
	{
		return Math.abs(this.x - other.x) == 1 
				^ Math.abs(this.y - other.y) == 1;
	}
	
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof Point)
		{
			Point other = (Point)obj;
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
