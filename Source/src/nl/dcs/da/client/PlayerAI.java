package nl.dcs.da.client;

import java.util.Collection;

import nl.dcs.da.tss.Dragon;
import nl.dcs.da.tss.Player;
import nl.dcs.da.tss.Point;
import nl.dcs.da.tss.TSS;


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
	protected void makeMove() throws CharacterDeadException
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
					this.heal(player.getID());
					return;
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
				this.attack(point);
				return;
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
					if (position.getX() > target.getX())
						this.move(position.shift(-1, 0));
					else if (position.getX() < target.getX())
						this.move(position.shift(+1, 0));
					else if (position.getY() > target.getY())
						this.move(position.shift(0, -1));
					else if (position.getY() < target.getY())
						this.move(position.shift(0, +1));
					return;
				}
			}
		}

		// There must be an action taken
		System.err.println("Player " + getID() + " could not perform an action");
	}

}
