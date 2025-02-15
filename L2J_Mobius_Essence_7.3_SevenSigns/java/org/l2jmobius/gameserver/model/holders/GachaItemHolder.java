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
package org.l2jmobius.gameserver.model.holders;

import org.l2jmobius.gameserver.enums.UniqueGachaRank;

public class GachaItemHolder extends ItemHolder
{
	private final int _itemChance;
	private final int _enchantLevel;
	private final UniqueGachaRank _rank;
	
	public GachaItemHolder(int itemId, long itemCount, int itemChance, int enchantLevel, UniqueGachaRank rank)
	{
		super(itemId, itemCount);
		_itemChance = itemChance;
		_enchantLevel = enchantLevel;
		_rank = rank;
	}
	
	public int getItemChance()
	{
		return _itemChance;
	}
	
	public int getEnchantLevel()
	{
		return _enchantLevel;
	}
	
	public UniqueGachaRank getRank()
	{
		return _rank;
	}
}
