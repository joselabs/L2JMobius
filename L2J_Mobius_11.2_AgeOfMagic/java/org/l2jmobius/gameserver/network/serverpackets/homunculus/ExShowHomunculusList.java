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
package org.l2jmobius.gameserver.network.serverpackets.homunculus;

import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.homunculus.Homunculus;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Mobius
 */
public class ExShowHomunculusList extends ServerPacket
{
	private final Player _player;
	
	public ExShowHomunculusList(Player player)
	{
		_player = player;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_SHOW_HOMUNCULUS_LIST.writeId(this, buffer);
		int counter = 0;
		final int slotCount = _player.getAvailableHomunculusSlotCount();
		buffer.writeInt(slotCount);
		for (int i = 0; i <= slotCount; i++)
		{
			if (_player.getHomunculusList().get(i) != null)
			{
				final Homunculus homunculus = _player.getHomunculusList().get(i);
				buffer.writeInt(counter); // slot
				buffer.writeInt(homunculus.getId()); // homunculus id
				buffer.writeInt(homunculus.getType());
				buffer.writeByte(homunculus.isActive());
				buffer.writeInt(homunculus.getTemplate().getBasicSkillId());
				buffer.writeInt(homunculus.getSkillLevel1() > 0 ? homunculus.getTemplate().getSkillId1() : 0);
				buffer.writeInt(homunculus.getSkillLevel2() > 0 ? homunculus.getTemplate().getSkillId2() : 0);
				buffer.writeInt(homunculus.getSkillLevel3() > 0 ? homunculus.getTemplate().getSkillId3() : 0);
				buffer.writeInt(homunculus.getSkillLevel4() > 0 ? homunculus.getTemplate().getSkillId4() : 0);
				buffer.writeInt(homunculus.getSkillLevel5() > 0 ? homunculus.getTemplate().getSkillId5() : 0);
				buffer.writeInt(homunculus.getTemplate().getBasicSkillLevel());
				buffer.writeInt(homunculus.getSkillLevel1());
				buffer.writeInt(homunculus.getSkillLevel2());
				buffer.writeInt(homunculus.getSkillLevel3());
				buffer.writeInt(homunculus.getSkillLevel4());
				buffer.writeInt(homunculus.getSkillLevel5());
				buffer.writeInt(homunculus.getLevel());
				buffer.writeInt(homunculus.getExp());
				buffer.writeInt(homunculus.getHp());
				buffer.writeInt(homunculus.getAtk());
				buffer.writeInt(homunculus.getDef());
				buffer.writeInt(homunculus.getCritRate());
			}
			else
			{
				buffer.writeInt(counter); // slot
				buffer.writeInt(0); // homunculus id
				buffer.writeInt(0);
				buffer.writeByte(0);
				buffer.writeInt(0);
				for (int j = 1; j <= 5; j++)
				{
					buffer.writeInt(0);
				}
				buffer.writeInt(0);
				for (int j = 1; j <= 5; j++)
				{
					buffer.writeInt(0);
				}
				buffer.writeInt(0); // Level
				buffer.writeInt(0); // HP
				buffer.writeInt(0); // HP
				buffer.writeInt(0); // Attack
				buffer.writeInt(0); // Defence
				buffer.writeInt(0); // Critical
			}
			counter++;
		}
	}
}
