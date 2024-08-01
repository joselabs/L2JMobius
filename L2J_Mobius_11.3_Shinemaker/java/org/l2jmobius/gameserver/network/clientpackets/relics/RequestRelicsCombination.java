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
package org.l2jmobius.gameserver.network.clientpackets.relics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.l2jmobius.Config;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.holders.PlayerRelicData;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.clientpackets.ClientPacket;
import org.l2jmobius.gameserver.network.serverpackets.relics.ExRelicsCombination;
import org.l2jmobius.gameserver.network.serverpackets.relics.ExRelicsList;

/**
 * @author CostyKiller
 */
public class RequestRelicsCombination extends ClientPacket
{
	private int _relicsUsedGrade;
	private int _relicsUsedCount;
	private final List<Integer> _ingredientIds = new LinkedList<>();
	
	@Override
	protected void readImpl()
	{
		_relicsUsedGrade = readInt();
		_relicsUsedCount = readInt();
		while (remaining() > 0)
		{
			_ingredientIds.add(readInt());
		}
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		final Collection<PlayerRelicData> storedRelics = player.getRelics();
		final List<Integer> unconfirmedRelics = new ArrayList<>();
		for (PlayerRelicData relic : storedRelics)
		{
			if ((relic.getRelicIndex() >= 300) && (relic.getRelicCount() == 1)) // Unconfirmed relics are set on summon to index 300.
			{
				unconfirmedRelics.add(relic.getRelicId());
			}
		}
		if (unconfirmedRelics.size() == Config.RELIC_UNCONFIRMED_LIST_LIMIT) // If you have 100 relics in your confirmation list, restrictions are applied to the relic summoning and compounding functions.
		{
			player.sendPacket(SystemMessageId.SUMMON_COMPOUND_IS_UNAVAILABLE_AS_YOU_HAVE_MORE_THAN_100_UNCONFIRMED_RELICS);
			return;
		}
		
		if (Config.RELIC_SYSTEM_DEBUG_ENABLED)
		{
			player.sendMessage("Compound Ingredients: " + _relicsUsedCount);
		}
		
		// Check ingredient relics.
		int i = 0;
		for (int ingredientId : _ingredientIds)
		{
			i++;
			PlayerRelicData ingredientRelic = null;
			for (PlayerRelicData relic : storedRelics)
			{
				if ((relic.getRelicId() == ingredientId) && (relic.getRelicIndex() < 300)) // Only relics with index 0 and can be compounded.
				{
					ingredientRelic = relic;
					break;
				}
			}
			if ((ingredientRelic != null) && (ingredientRelic.getRelicCount() > 0))
			{
				ingredientRelic.setRelicCount(ingredientRelic.getRelicCount() - 1);
				if (Config.RELIC_SYSTEM_DEBUG_ENABLED)
				{
					player.sendMessage("Ingredient Relic " + i + " data updated, ID: " + ingredientRelic.getRelicId() + ", Count: " + ingredientRelic.getRelicCount());
				}
			}
		}
		
		// Store relics.
		player.storeRelics();
		player.sendPacket(new ExRelicsList(player)); // Update confirmed relic list relics count.
		player.sendPacket(new ExRelicsCombination(player, _relicsUsedGrade, _relicsUsedCount));
	}
}
