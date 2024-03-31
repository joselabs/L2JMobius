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
package org.l2jmobius.gameserver.network.serverpackets.collection;

import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * Written by Berezkin Nikolay, on 12.04.2021
 */
public class ExCollectionRegister extends ServerPacket
{
	private final int _collectionId;
	private final int _index;
	private final Item _item;
	
	public ExCollectionRegister(int collectionId, int index, Item item)
	{
		_collectionId = collectionId;
		_index = index;
		_item = item;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_COLLECTION_REGISTER.writeId(this, buffer);
		buffer.writeShort(_collectionId);
		buffer.writeByte(1);
		buffer.writeByte(0x0E);
		buffer.writeByte(0);
		buffer.writeByte(_index);
		buffer.writeInt(_item.getId());
		buffer.writeShort(0);
		buffer.writeByte(0);
		buffer.writeInt(0);
	}
}
