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
package org.l2jmobius.gameserver.network.serverpackets.relics;

import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.variables.AccountVariables;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * @author CostyKiller
 */
public class ExRelicsActiveInfo extends ServerPacket
{
	private final Player _player;
	private final int _relicId;
	private final int _relicLevel;
	
	public ExRelicsActiveInfo(Player player, int relicId, int relicLevel)
	{
		_player = player;
		_relicId = relicId;
		_relicLevel = relicLevel;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_RELICS_ACTIVE_INFO.writeId(this, buffer);
		buffer.writeInt(_relicId);
		buffer.writeInt(_relicLevel);
		
		// TODO: This should not be in serverpackets.
		_player.getAccountVariables().set(AccountVariables.ACTIVE_RELIC, _relicId);
		_player.getAccountVariables().storeMe();
	}
}