package nl.dcs.da.tss;

public interface Battlefield
{
	public static interface Listener 
	{
		public void onStateChanged();
	}
	
	public Element get(Point point);
	public Element get(int x, int y);
	
	public void addListener(Listener listener);
}