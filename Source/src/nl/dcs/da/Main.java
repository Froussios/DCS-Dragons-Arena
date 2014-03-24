package nl.dcs.da;

import java.util.Scanner;

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
import nl.dcs.da.tss.TSS;
import nl.dcs.da.tss.TimedTSS;
import nl.dcs.da.tss.events.ActorAttack;
import nl.dcs.da.tss.events.Connect;
import nl.dcs.da.tss.events.Event;
import nl.dcs.da.tss.events.Heal;
import nl.dcs.da.tss.events.OpenGame;
import nl.dcs.da.tss.events.PlayerMove;
import nl.dcs.da.tss.events.StartGame;
import nl.dcs.da.tss.util.Alarm.AlarmRunningException;
import nl.dcs.da.tss.util.IncGenerator;

public class Main
		implements Battlefield.Listener
{

	private static final Scanner scanner = new Scanner(System.in);
	private static final State start = new State();
	private static final long maxDelay = 30000;
	private final IncGenerator idGenerator = new IncGenerator();
	private TimedTSS state;
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
		state = new TimedTSS(start, maxDelay);
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
						Player p3 = new Player(20, 5, idGenerator.next());
						feedEvent(new Connect(p3.getID(), p3, new Point(0, 0)));
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
						me.move(new Point(x, y));
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
					case "myminions":
					{
						State s = state.snapshot();
						for (Point point : s)
						{
							Player player = s.getAsPlayer(point);
							if (player != null)
							{
								PlayerAI pai = new PlayerAI(player.getID(), state, 5000);
								pai.start();
								System.out.println("Automanted player " + player.getID());
							}

							Dragon dragon = s.getAsDragon(point);
							if (dragon != null)
							{
								DragonAI dai = new DragonAI(dragon.getID(), state, 5000);
								dai.start();
								System.out.println("Automanted dragon " + dragon.getID());
							}
						}
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
		catch (Exception any)
		{
			any.printStackTrace();
		}
		finally
		{
			scanner.close();
		}
	}


	private void test()
	{
		TSS tt = new TSS(start, maxDelay);
		tt.loadFrom(this.state);
		this.state.loadFrom(tt);
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
