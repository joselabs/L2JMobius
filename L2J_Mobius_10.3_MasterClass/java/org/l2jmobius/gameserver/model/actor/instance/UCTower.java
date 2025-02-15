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
package org.l2jmobius.gameserver.model.actor.instance;

import org.l2jmobius.gameserver.enums.Team;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.templates.NpcTemplate;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.model.undergroundColiseum.UCTeam;

public class UCTower extends Folk
{
	private UCTeam _team;
	
	public UCTower(UCTeam team, NpcTemplate template)
	{
		super(template);
		_team = team;
	}
	
	@Override
	public boolean canBeAttacked()
	{
		return true;
	}
	
	@Override
	public boolean isAutoAttackable(Creature creature)
	{
		return true;
	}
	
	@Override
	public void reduceCurrentHp(double amountValue, Creature attacker, Skill skill, boolean isDOT, boolean directlyToHp, boolean critical, boolean reflect)
	{
		if (attacker.getTeam() == getTeam())
		{
			return;
		}
		
		if (amountValue < getStatus().getCurrentHp())
		{
			getStatus().setCurrentHp(getStatus().getCurrentHp() - amountValue);
		}
		else
		{
			doDie(attacker);
		}
	}
	
	@Override
	public boolean doDie(Creature killer)
	{
		if (!super.doDie(killer))
		{
			return false;
		}
		
		if (_team != null)
		{
			_team.deleteTower();
			_team = null;
		}
		
		return true;
	}
	
	@Override
	public Team getTeam()
	{
		return _team.getIndex() == 1 ? Team.BLUE : Team.RED;
	}
}
