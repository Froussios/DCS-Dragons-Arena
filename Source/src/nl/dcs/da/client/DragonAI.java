package nl.dcs.da.client;

import java.util.Collection;

import nl.dcs.da.tss.Player;
import nl.dcs.da.tss.Point;
import nl.dcs.da.tss.TSS;
import nl.dcs.da.tss.events.ActorAttack;
import nl.dcs.da.tss.events.Event;


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
	protected Event makeMove()
			throws CharacterDeadException
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
				ActorAttack attack = this.attack(point);
				return attack;
			}
		}

		// There must be an action taken
		System.err.println("Dragon " + getID() + " could not perform an action");
		return null;

	}

}
