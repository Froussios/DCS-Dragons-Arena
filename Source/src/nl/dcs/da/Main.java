package nl.dcs.da;

import nl.dcs.da.tss.*;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		State state = new State();
		state.set(new Point(0,0), new Player(20,5));
		
		System.out.println(state);
	}

}
