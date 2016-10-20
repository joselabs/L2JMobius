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
package com.l2jmobius.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.items.instance.L2ItemInstance;

public final class WareHouseWithdrawalList extends AbstractItemPacket
{
	public static final int PRIVATE = 1;
	public static final int CLAN = 2;
	public static final int CASTLE = 3; // not sure
	public static final int FREIGHT = 4;
	private L2PcInstance _activeChar;
	private long _playerAdena;
	private L2ItemInstance[] _items;
	private L2ItemInstance[] _invItems;
	private final List<Integer> _itemsStackable = new ArrayList<>();
	/**
	 * <ul>
	 * <li>0x01-Private Warehouse</li>
	 * <li>0x02-Clan Warehouse</li>
	 * <li>0x03-Castle Warehouse</li>
	 * <li>0x04-Warehouse</li>
	 * </ul>
	 */
	private int _whType;
	
	public WareHouseWithdrawalList(L2PcInstance player, int type)
	{
		_activeChar = player;
		_whType = type;
		
		_playerAdena = _activeChar.getAdena();
		if (_activeChar.getActiveWarehouse() == null)
		{
			_log.warning("error while sending withdraw request to: " + _activeChar.getName());
			return;
		}
		
		_items = _activeChar.getActiveWarehouse().getItems();
		_invItems = _activeChar.getInventory().getItems();
		
		for (L2ItemInstance item : _items)
		{
			for (L2ItemInstance invitem : _invItems)
			{
				if (item.isStackable() && (item.getDisplayId() == invitem.getDisplayId()))
				{
					_itemsStackable.add(item.getDisplayId());
				}
			}
		}
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x42);
		writeH(_whType);
		writeQ(_playerAdena);
		writeH(_items.length);
		writeH(_itemsStackable.size());
		for (int itemId : _itemsStackable)
		{
			writeD(itemId);
		}
		writeD(_invItems.length);
		for (L2ItemInstance item : _items)
		{
			writeItem(item);
			writeD(item.getObjectId());
			writeD(0);
			writeD(0);
		}
	}
}
