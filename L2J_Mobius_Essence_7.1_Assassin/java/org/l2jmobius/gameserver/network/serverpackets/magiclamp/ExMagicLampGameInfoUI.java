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
package org.l2jmobius.gameserver.network.serverpackets.magiclamp;

import java.util.List;

import org.l2jmobius.Config;
import org.l2jmobius.gameserver.data.xml.MagicLampData;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.holders.GreaterMagicLampHolder;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * @author L2CCCP
 */
public class ExMagicLampGameInfoUI extends ServerPacket
{
	private final Player _player;
	private final byte _mode;
	private final int _count;
	
	public ExMagicLampGameInfoUI(Player player, byte mode, int count)
	{
		_player = player;
		_mode = mode;
		_count = count;
	}
	
	@Override
	public void write()
	{
		ServerPackets.EX_MAGICLAMP_GAME_INFO.writeId(this);
		writeInt(_player.getMaxLampCount()); // MagicLampGameMaxCCount
		writeInt(_count); // MagicLampGameCCount
		writeInt(_mode == 0 ? Config.MAGIC_LAMP_CONSUME_COUNT : Config.MAGIC_LAMP_GREATER_CONSUME_COUNT); // MagicLampCountPerGame
		writeInt(_player.getLampCount()); // MagicLampCount
		writeByte(_mode); // GameMode
		final List<GreaterMagicLampHolder> greater = MagicLampData.getInstance().getGreaterLamps();
		writeInt(greater.size()); // costItemList
		for (GreaterMagicLampHolder lamp : greater)
		{
			writeInt(lamp.getItemId()); // ItemClassID
			writeLong(lamp.getCount()); // ItemAmountPerGame
			writeLong(_player.getInventory().getInventoryItemCount(lamp.getItemId(), -1)); // ItemAmount
		}
	}
}