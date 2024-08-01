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
package org.l2jmobius.gameserver.network.clientpackets.relics;

import org.l2jmobius.Config;
import org.l2jmobius.gameserver.data.xml.RelicData;
import org.l2jmobius.gameserver.data.xml.SkillData;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.holders.PlayerRelicData;
import org.l2jmobius.gameserver.model.holders.RelicDataHolder;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.network.clientpackets.ClientPacket;
import org.l2jmobius.gameserver.network.serverpackets.relics.ExRelicsActiveInfo;

/**
 * @author CostyKiller
 */
public class RequestRelicsActive extends ClientPacket
{
	private int _relicId;
	
	@Override
	protected void readImpl()
	{
		_relicId = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		int relicLevel = 0;
		if (player == null)
		{
			return;
		}
		
		for (PlayerRelicData relic : player.getRelics())
		{
			if (relic.getRelicId() == _relicId)
			{
				relicLevel = relic.getRelicLevel();
				break;
			}
		}
		final int skillId = RelicData.getInstance().getRelicSkillId(_relicId);
		final int skillLevel = relicLevel + 1;
		player.sendPacket(new ExRelicsActiveInfo(player, _relicId, relicLevel));
		
		final Skill relicSkill = SkillData.getInstance().getSkill(skillId, skillLevel);
		if (relicSkill != null)
		{
			// Remove previous active relic skill.
			for (RelicDataHolder relic : RelicData.getInstance().getRelics())
			{
				final Skill skill = player.getKnownSkill(relic.getSkillId());
				if (skill != null)
				{
					player.removeSkill(skill);
					if (Config.RELIC_SYSTEM_DEBUG_ENABLED)
					{
						player.sendMessage("Relic Skill Id: " + skill.getId() + " Lvl: " + skill.getLevel() + " was removed.");
					}
				}
			}
			
			player.addSkill(relicSkill, true);
			if (Config.RELIC_SYSTEM_DEBUG_ENABLED)
			{
				player.sendMessage("Relic Skill Id: " + skillId + " Lvl: " + skillLevel + " was added.");
			}
		}
		else
		{
			player.sendMessage("Relic skill does not exist!");
		}
	}
}
