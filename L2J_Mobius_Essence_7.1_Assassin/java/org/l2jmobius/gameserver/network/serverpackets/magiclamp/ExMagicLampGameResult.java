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

import java.util.Collection;

import org.l2jmobius.gameserver.model.holders.MagicLampHolder;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Mobius
 */
public class ExMagicLampGameResult extends ServerPacket
{
	private final Collection<MagicLampHolder> _rewards;
	
	public ExMagicLampGameResult(Collection<MagicLampHolder> rewards)
	{
		_rewards = rewards;
	}
	
	@Override
	public void write()
	{
		ServerPackets.EX_MAGICLAMP_GAME_RESULT.writeId(this);
		writeInt(_rewards.size());
		for (MagicLampHolder lamp : _rewards)
		{
			writeByte(lamp.getType().getGrade());
			writeInt(lamp.getCount());
			writeLong(lamp.getExp());
			writeLong(lamp.getSp());
		}
	}
}