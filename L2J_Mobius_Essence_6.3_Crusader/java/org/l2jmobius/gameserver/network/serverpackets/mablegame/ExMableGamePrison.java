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
package org.l2jmobius.gameserver.network.serverpackets.mablegame;

import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

public class ExMableGamePrison extends ServerPacket
{
	private final int _minDice;
	private final int _maxDice;
	private final int _remainCount;
	
	public ExMableGamePrison(int minDice, int maxDice, int remainCount)
	{
		_minDice = minDice;
		_maxDice = maxDice;
		_remainCount = remainCount;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_MABLE_GAME_PRISON.writeId(this, buffer);
		buffer.writeInt(_minDice); // MinDiceForLeavePrison
		buffer.writeInt(_maxDice); // MaxDiceForLeavePrison
		buffer.writeInt(_remainCount);
	}
}
