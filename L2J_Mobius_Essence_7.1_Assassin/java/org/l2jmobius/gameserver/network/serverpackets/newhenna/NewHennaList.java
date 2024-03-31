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

package org.l2jmobius.gameserver.network.serverpackets.newhenna;

import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.item.henna.Henna;
import org.l2jmobius.gameserver.model.item.henna.HennaPoten;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Index, Serenitty
 */
public class NewHennaList extends ServerPacket
{
	private final HennaPoten[] _hennaId;
	private final int _dailyStep;
	private final int _dailyCount;
	private final int _availableSlots;
	
	public NewHennaList(Player player)
	{
		_dailyStep = player.getDyePotentialDailyStep();
		_dailyCount = player.getDyePotentialDailyCount();
		_hennaId = player.getHennaPotenList();
		_availableSlots = player.getAvailableHennaSlots();
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_NEW_HENNA_LIST.writeId(this, buffer);
		buffer.writeShort(_dailyStep);
		buffer.writeShort(_dailyCount);
		buffer.writeInt(_hennaId.length);
		for (int i = 1; i <= _hennaId.length; i++)
		{
			final HennaPoten hennaPoten = _hennaId[i - 1];
			final Henna henna = _hennaId[i - 1].getHenna();
			buffer.writeInt(henna != null ? henna.getDyeId() : 0);
			buffer.writeInt(hennaPoten.getPotenId());
			buffer.writeByte(i != _availableSlots);
			buffer.writeShort(hennaPoten.getEnchantLevel());
			buffer.writeInt(hennaPoten.getEnchantExp());
			buffer.writeShort(hennaPoten.getActiveStep());
			buffer.writeShort(_dailyStep);
			buffer.writeShort(_dailyCount);
		}
	}
}
