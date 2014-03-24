package nl.dcs.da.client;

import java.util.Collection;

import nl.dcs.da.tss.Player;
import nl.dcs.da.tss.Point;
import nl.dcs.da.tss.TSS;


/**
 * An autonomous dragon that regularly performs an action
 * 
 * @author Chris
 * 
 */
public class DragonAI
		extends TimedAvatarOperator
{

	/**
	 * A new autonomous dragon
	 * 
	 * @param id The id of the character
	 * @param game The game to play on
	 * @param frequency Every how many seconds to perform an action
	 */
	public DragonAI(long id, TSS game, long frequency)
	{
		super(id, game, frequency);
	}


	@Override
	protected void makeMove() throws CharacterDeadException
	{
		// TODO null exception when dead

		Point position = this.getLocation();

		Collection<Point> neighbourhood2 = position.getNeighbours(2);

		// Find a player in range and attack
		for (Point point : neighbourhood2)
		{
			Player player = this.getBattlefield().getAsPlayer(point);

			if (player != null)
			{
				this.attack(point);
				return;
			}
		}

	}

}
