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
package org.l2jmobius.gameserver.network.serverpackets.monsterbook;

import java.util.ArrayList;
import java.util.List;

import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.gameserver.data.xml.MonsterBookData;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.holders.MonsterBookCardHolder;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Mobius
 */
public class ExMonsterBook extends ServerPacket
{
	final Player _player;
	final List<Integer> _cardIds = new ArrayList<>();
	
	public ExMonsterBook(Player player)
	{
		_player = player;
		for (MonsterBookCardHolder card : MonsterBookData.getInstance().getMonsterBookCards())
		{
			if (player.getMonsterBookKillCount(card.getId()) > 0)
			{
				_cardIds.add(card.getId());
			}
		}
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_MONSTER_BOOK.writeId(this, buffer);
		buffer.writeShort(_cardIds.size()); // loop count
		for (int cardId : _cardIds)
		{
			buffer.writeShort(cardId); // card id
			buffer.writeByte(_player.getMonsterBookRewardLevel(cardId)); // player reward level
			buffer.writeInt(_player.getMonsterBookKillCount(cardId)); // player kills
		}
	}
}
