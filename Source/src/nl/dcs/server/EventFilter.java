package nl.dcs.server;

import nl.dcs.da.tss.State;
import nl.dcs.da.tss.TSS;
import nl.dcs.da.tss.events.Connect;
import nl.dcs.da.tss.events.Event;
import nl.dcs.da.tss.events.OpenGame;
import nl.dcs.da.tss.events.StartGame;


/**
 * A class that can check whether a server should accept an event from a client.
 * 
 * @author Chris
 * 
 */
public class EventFilter
{

	private final TSS context;
	private final long client;
	private final long maxClientDelay = 15000;


	/**
	 * Create a new server-side filter
	 * 
	 * @param context The live game to check against
	 */
	public EventFilter(TSS context, long client)
	{
		this.context = context;
		this.client = client;
	}


	/**
	 * Check if this event should be accepted by the server, when received from
	 * a client.
	 * 
	 * @param event The event to be filtered
	 * @return true if the event can be accepted
	 */
	public boolean acceptEventFromClient(Event event)
	{
		boolean accepted = true;
		String rejection = null;

		// NOTE: do not reject moves after gameover: gameover might be revised
		// and the game resumed

		// Event too retrospective
		if (context.getSimulationTime() - event.getSimulationTime() > maxClientDelay)
			rejection = "Event too retrospective";

		// Event in the future
		if (context.getSimulationTime() - event.getSimulationTime() < -1000)
			rejection = "Event in the future";

		// Game not open or playing
		if (context.getPhase().equals(State.GameState.Closed))
			rejection = "Game not open or playing";

		// Attempted new connection mid-game
		if (context.getPhase().equals(State.GameState.Open) && !(event instanceof Connect))
			rejection = "Attempted new connection mid-game";

		// Control events are reserved for clients
		if (event instanceof StartGame || event instanceof OpenGame)
			rejection = "Control events are reserved for clients";

		// Clients can only control their own character
		if (event.getIssuer() != this.client)
			rejection = "Clients can only control their own character";

		System.out.println("Rejected: " + event + ". Reason: " + rejection);

		accepted = rejection != null;
		return accepted;
	}


	/**
	 * Check if this event should be accepted by the server, when received from
	 * a server.
	 * 
	 * @param event
	 * @return true, if the event can be accepted
	 */
	public boolean acceptEventFromServer(Event event)
	{
		boolean ok = true;

		// Trust peers

		return ok;
	}

}
