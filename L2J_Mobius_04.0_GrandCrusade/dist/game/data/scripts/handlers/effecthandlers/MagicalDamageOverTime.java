/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.effecthandlers;

import org.l2jmobius.gameserver.model.StatSet;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.effects.AbstractEffect;
import org.l2jmobius.gameserver.model.effects.EffectType;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.model.stats.Formulas;
import org.l2jmobius.gameserver.network.SystemMessageId;

/**
 * MagicalAttack-damage over time effect implementation.
 * @author Nik
 */
public class MagicalDamageOverTime extends AbstractEffect
{
	private final double _power;
	private final boolean _canKill;
	
	public MagicalDamageOverTime(StatSet params)
	{
		_power = params.getDouble("power", 0);
		_canKill = params.getBoolean("canKill", false);
		setTicks(params.getInt("ticks"));
		
		if (params.contains("amount"))
		{
			throw new IllegalArgumentException(getClass().getSimpleName() + " should use power instead of amount.");
		}
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.MAGICAL_DMG_OVER_TIME;
	}
	
	@Override
	public boolean onActionTime(Creature effector, Creature effected, Skill skill, Item item)
	{
		final Creature creature = effector;
		final Creature target = effected;
		
		if (target.isDead())
		{
			return false;
		}
		
		double damage = Formulas.calcMagicDam(creature, target, skill, creature.getMAtk(), _power, target.getMDef(), false, false, false); // In retail spiritshots change nothing.
		damage *= getTicksMultiplier();
		
		if (damage >= (target.getCurrentHp() - 1))
		{
			if (skill.isToggle())
			{
				target.sendPacket(SystemMessageId.YOUR_SKILL_HAS_BEEN_CANCELED_DUE_TO_LACK_OF_HP);
				return false;
			}
			
			// For DOT skills that will not kill effected player.
			if (!_canKill)
			{
				// Fix for players dying by DOTs if HP < 1 since reduceCurrentHP method will kill them
				if (target.getCurrentHp() <= 1)
				{
					return skill.isToggle();
				}
				damage = target.getCurrentHp() - 1;
			}
		}
		
		effector.doAttack(damage, effected, skill, true, false, false, false);
		return skill.isToggle();
	}
}
