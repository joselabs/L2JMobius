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

import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.holders.PlayerRelicData;
import org.l2jmobius.gameserver.model.variables.AccountVariables;
import org.l2jmobius.gameserver.network.clientpackets.ClientPacket;
import org.l2jmobius.gameserver.network.serverpackets.relics.ExRelicsActiveInfo;
import org.l2jmobius.gameserver.network.serverpackets.relics.ExRelicsExchangeList;
import org.l2jmobius.gameserver.network.serverpackets.relics.ExRelicsList;

/**
 * @author CostyKiller
 */
public class RequestRelicsOpenUI extends ClientPacket
{
	@Override
	protected void readImpl()
	{
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		int activeRelicId = 0;
		int activeRelicLevel = 0;
		for (PlayerRelicData relic : player.getRelics())
		{
			if (relic.getRelicId() == player.getAccountVariables().getInt(AccountVariables.ACTIVE_RELIC, 0))
			{
				activeRelicId = relic.getRelicId();
				activeRelicLevel = relic.getRelicLevel();
				break;
			}
		}
		player.sendPacket(new ExRelicsActiveInfo(player, activeRelicId, activeRelicLevel)); // Show stored active relic from acc var.
		player.sendPacket(new ExRelicsList(player)); // Update confirmed relic list relics count.
		player.sendPacket(new ExRelicsExchangeList(player)); // Update relic exchange/confirm list.
	}
}
