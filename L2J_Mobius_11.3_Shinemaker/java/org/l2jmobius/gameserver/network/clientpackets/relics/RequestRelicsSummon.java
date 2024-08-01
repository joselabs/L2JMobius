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

import org.l2jmobius.Config;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.request.RelicCouponRequest;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.model.variables.AccountVariables;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.clientpackets.ClientPacket;
import org.l2jmobius.gameserver.network.serverpackets.relics.ExRelicsSummonResult;

/**
 * @author CostyKiller
 */
public class RequestRelicsSummon extends ClientPacket
{
	private int _couponId;
	
	@Override
	protected void readImpl()
	{
		_couponId = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		final Item couponItem = player.getInventory().getItemByItemId(_couponId);
		if (couponItem == null)
		{
			player.sendPacket(SystemMessageId.FAILURE_ALL_MATERIALS_ARE_LOST);
			return;
		}
		
		if (player.getAccountVariables().getInt(AccountVariables.UNCONFIRMED_RELICS_COUNT, 0) == Config.RELIC_UNCONFIRMED_LIST_LIMIT)
		{
			player.sendPacket(SystemMessageId.SUMMON_COMPOUND_IS_UNAVAILABLE_AS_YOU_HAVE_MORE_THAN_100_UNCONFIRMED_RELICS);
			return;
		}
		
		player.addRequest(new RelicCouponRequest(player));
		
		final int relicSummonCount;
		if ((_couponId == 83004) || (_couponId == 83006))
		{
			relicSummonCount = 11;
		}
		else
		{
			relicSummonCount = 1;
		}
		
		player.sendPacket(new ExRelicsSummonResult(player, _couponId, relicSummonCount));
		player.destroyItem("RelicSummon", couponItem, 1, player, true);
	}
}