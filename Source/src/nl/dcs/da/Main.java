package nl.dcs.da;

import java.util.Scanner;

import nl.dcs.da.tss.Actor;
import nl.dcs.da.tss.Battlefield;
import nl.dcs.da.tss.Dragon;
import nl.dcs.da.tss.Player;
import nl.dcs.da.tss.Point;
import nl.dcs.da.tss.State;
import nl.dcs.da.tss.events.ActorAttack;
import nl.dcs.da.tss.events.Heal;
import nl.dcs.da.tss.events.PlayerMove;

public class Main implements Battlefield.Listener
{

	private final State state = new State();


	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		Main m = new Main();
		m.run();
	}


	public void run()
	{
		state.addListener(this);
		state.set(new Point(10, 10), new Player(20, 5));

		System.out.println(state);

		Scanner scanner = new Scanner(System.in);

		try
		{
			while (true)
			{
				System.out.print(" > ");
				String command = scanner.next().toLowerCase();
				int x, y, id;
				Actor e = null;

				switch (command)
				{
					case "exit":
					case "stop":
						System.out.println("Bye");
						return;
					case "map":
						System.out.println(state);
						break;
					case "print":
						x = scanner.nextInt();
						y = scanner.nextInt();
						e = state.get(new Point(x, y));
						if (e != null)
							System.out.println(state.get(new Point(x, y)).details());
						else
							System.out.println("null");
						break;
					case "player":
						x = scanner.nextInt();
						y = scanner.nextInt();
						state.set(new Point(x, y), new Player(20, 5));
						break;
					case "dragon":
						x = scanner.nextInt();
						y = scanner.nextInt();
						state.set(new Point(x, y), new Dragon(50, 10));
						break;
					case "move":
						id = scanner.nextInt();
						x = scanner.nextInt();
						y = scanner.nextInt();
						PlayerMove move = new PlayerMove(0, id, new Point(x, y));
						state.consume(move);
						System.out.println("Submitted: " + move);
						break;
					case "attack":
						id = scanner.nextInt();
						x = scanner.nextInt();
						y = scanner.nextInt();
						ActorAttack attack = new ActorAttack(0, id, new Point(x, y));
						state.consume(attack);
						System.out.println("Submitted: " + attack);
						break;
					case "heal":
						id = scanner.nextInt();
						long idt = scanner.nextInt();
						Heal heal = new Heal(0, id, idt);
						state.consume(heal);
						System.out.println("Submitted: " + heal);
						break;
					case "history":
						for (String message : state.getHistory())
							System.out.println(" - " + message);
						break;
				}
			}
		}
		finally
		{
			scanner.close();
		}
	}


	@Override
	public void onStateChanged()
	{
		System.out.println(state);
	}
}
