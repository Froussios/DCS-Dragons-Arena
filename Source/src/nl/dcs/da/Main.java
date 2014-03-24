package nl.dcs.da;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeSet;

import nl.dcs.da.client.AvatarOperator;
import nl.dcs.da.client.DragonAI;
import nl.dcs.da.client.PlayerAI;
import nl.dcs.da.tss.Actor;
import nl.dcs.da.tss.Battlefield;
import nl.dcs.da.tss.Dragon;
import nl.dcs.da.tss.OutOfSyncException;
import nl.dcs.da.tss.Player;
import nl.dcs.da.tss.Point;
import nl.dcs.da.tss.State;
import nl.dcs.da.tss.SynchronizedState;
import nl.dcs.da.tss.TimedTSS;
import nl.dcs.da.tss.events.ActorAttack;
import nl.dcs.da.tss.events.Connect;
import nl.dcs.da.tss.events.Event;
import nl.dcs.da.tss.events.Heal;
import nl.dcs.da.tss.events.MarkEvent;
import nl.dcs.da.tss.events.OpenGame;
import nl.dcs.da.tss.events.PlayerMove;
import nl.dcs.da.tss.events.StartGame;
import nl.dcs.da.tss.util.Alarm;
import nl.dcs.da.tss.util.Alarm.AlarmRunningException;
import nl.dcs.da.tss.util.IncGenerator;

public class Main
		implements Battlefield.Listener
{

	private final IncGenerator idGenerator = new IncGenerator();
	private TimedTSS state;
	private static final Scanner scanner = new Scanner(System.in);
	AvatarOperator me;


	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception
	{
		Main m = new Main();
		m.run();
	}


	public void run() throws AlarmRunningException
	{
		State start = new State();
		// start.set(new Point(10, 10), new Player(20, 5, idGenerator.next()));
		// start.set(new Point(10, 11), new Player(20, 5, idGenerator.next()));
		// start.set(new Point(11, 11), new Dragon(50, 10, idGenerator.next()));
		state = new TimedTSS(start, 30000, new Alarm(100));
		state.addListener(this);
		me = new AvatarOperator(2, state);

		System.out.println(state);

		try
		{
			while (true)
			{
				System.out.print(" > ");
				String command = scanner.next().toLowerCase();
				int x, y;
				long id, time;
				Actor e = null;

				switch (command)
				{
					case "exit":
					case "stop":
					{
						System.out.println("Bye");
						return;
					}
					case "test":
					{
						test();
						break;
					}
					case "map":
					{
						System.out.println(state);
						break;
					}
					case "print":
					{
						x = scanner.nextInt();
						y = scanner.nextInt();
						e = state.get(new Point(x, y));
						if (e != null)
							System.out.println(state.get(new Point(x, y)).details());
						else
							System.out.println("null");
						break;
					}
					case "player":
					{
						x = scanner.nextInt();
						y = scanner.nextInt();
						Player player = new Player(idGenerator.next());
						Connect connect = new Connect(player.getID(), player, new Point(x, y));
						feedEvent(connect);
						break;
					}
					case "dragon":
					{
						x = scanner.nextInt();
						y = scanner.nextInt();
						Dragon dragon = new Dragon(idGenerator.next());
						Connect connect = new Connect(dragon.getID(), dragon, new Point(x, y));
						feedEvent(connect);
						break;
					}
					case "open":
					{
						OpenGame openGame = new OpenGame();
						feedEvent(openGame);
						Player p1 = new Player(20, 5, idGenerator.next());
						feedEvent(new Connect(p1.getID(), p1, new Point(10, 10)));
						Player p2 = new Player(20, 5, idGenerator.next());
						feedEvent(new Connect(p2.getID(), p2, new Point(10, 11)));
						Dragon d1 = new Dragon(50, 10, idGenerator.next());
						feedEvent(new Connect(d1.getID(), d1, new Point(11, 11)));
						break;
					}
					case "start":
					{
						StartGame startGame = new StartGame();
						feedEvent(startGame);
						break;
					}
					case "move":
					{
						time = scanner.nextLong();
						id = scanner.nextInt();
						x = scanner.nextInt();
						y = scanner.nextInt();
						PlayerMove move = new PlayerMove(time, id, new Point(x, y));
						feedEvent(move);
						break;
					}
					case "pmove":
					{
						x = scanner.nextInt();
						y = scanner.nextInt();
						me.Move(new Point(x, y));
						break;
					}
					case "attack":
					{
						time = scanner.nextLong();
						id = scanner.nextInt();
						x = scanner.nextInt();
						y = scanner.nextInt();
						ActorAttack attack = new ActorAttack(time, id, new Point(x, y));
						feedEvent(attack);
						break;
					}
					case "heal":
					{
						time = scanner.nextLong();
						id = scanner.nextInt();
						long idt = scanner.nextInt();
						Heal heal = new Heal(time, id, idt);
						feedEvent(heal);
						break;
					}
					case "history":
					{
						for (String message : state.snapshot().getHistory())
							System.out.println(" - " + message);
						break;
					}
					case "ff":
					{
						long timespan = scanner.nextLong();
						state.incrementTime(timespan);
						break;
					}
					case "clocks":
					{
						for (SynchronizedState st : state.getStates())
							System.out.println(st.getClock());
						break;
					}
					case "events":
					{
						for (Event event : state.getEventQueue())
							System.out.println(event);
						break;
					}
					case "autoplayer":
					{
						long playerid = scanner.nextLong();
						PlayerAI player = new PlayerAI(playerid, state, 5000);
						player.start();
						break;
					}
					case "autodragon":
					{
						long dragonid = scanner.nextLong();
						DragonAI dragon = new DragonAI(dragonid, state, 5000);
						dragon.start();
						break;
					}
					default:
					{
						System.out.println("Learn to type");
						break;
					}
				}
			}
		}
		finally
		{
			scanner.close();
		}
	}


	private static void test()
	{
		SortedSet<Event> ss = new TreeSet<Event>();
		ss.add(new Heal(10, 0, 0));
		ss.add(new PlayerMove(15, 0, new Point(0, 0)));
		ss.add(new Heal(20, 0, 0));

		Collection<Event> killset = new ArrayList<Event>(ss.headSet(new MarkEvent(16)));
		ss.removeAll(killset);

		for (Event e : ss)
			System.out.println(e);
	}


	private void feedEvent(Event event)
	{
		try
		{
			state.receiveEvent(event);
			System.out.println("Submitted: " + event);
		}
		catch (OutOfSyncException e)
		{
			e.printStackTrace();
		}
	}


	@Override
	public void onStateChanged(Object cause)
	{
		// System.out.println(state);
		System.out.println("MAP HAS CHANGED: cause: " + cause);
	}
}
