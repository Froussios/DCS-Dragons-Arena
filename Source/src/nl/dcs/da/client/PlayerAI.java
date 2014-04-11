package nl.dcs.da.client;

import java.util.Collection;

import nl.dcs.da.tss.Dragon;
import nl.dcs.da.tss.Player;
import nl.dcs.da.tss.Point;
import nl.dcs.da.tss.TSS;
import nl.dcs.da.tss.events.ActorAttack;
import nl.dcs.da.tss.events.Event;
import nl.dcs.da.tss.events.Heal;
import nl.dcs.da.tss.events.PlayerMove;


public class PlayerAI
		extends TimedAvatarOperator
{

	/**
	 * a new autonomous dragon
	 * 
	 * @param id The id of the character
	 * @param game The game to play on
	 * @param frequency Every how many seconds to perform an action
	 */
	public PlayerAI(long id, TSS game, long frequency)
	{
		super(id, game, frequency);
	}


	@Override
	protected Event makeMove()
			throws CharacterDeadException
	{
		Point position = this.getLocation();

		// 1. Heal nearby player if below 50%
		Collection<Point> neighbourhood5 = position.getNeighbours(5);
		for (Point point : neighbourhood5)
		{
			Player player = this.getBattlefield().getAsPlayer(point);
			if (player != null)
			{
				if (player.getMaxHP() > player.getHP() * 2)
				{
					Heal heal = this.heal(player.getID());
					return heal;
				}
			}
		}

		// 2. Strike dragon
		Collection<Point> neighbourhood2 = position.getNeighbours(2);
		for (Point point : neighbourhood2)
		{
			Dragon dragon = this.getBattlefield().getAsDragon(point);
			if (dragon != null)
			{
				ActorAttack attack = this.attack(point);
				return attack;
			}
		}

		// 3. Move to nearest dragon
		for (int fringe = 0; fringe < 25; fringe++)
		{
			Collection<Point> neighbourhood = position.getNeighbours(fringe);
			for (Point target : neighbourhood)
			{
				if (this.getBattlefield().getAsDragon(target) != null)
				{
					PlayerMove move = null;
					if (position.getX() > target.getX())
						move = this.move(position.shift(-1, 0));
					else if (position.getX() < target.getX())
						move = this.move(position.shift(+1, 0));
					else if (position.getY() > target.getY())
						move = this.move(position.shift(0, -1));
					else if (position.getY() < target.getY())
						move = this.move(position.shift(0, +1));
					return move;
				}
			}
		}

		// There must be an action taken
		System.out.println("Player " + getID() + " could not perform an action");
		return null;
	}

}
