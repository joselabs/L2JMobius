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
package handlers.itemhandlers;

import org.l2jmobius.gameserver.handler.IItemHandler;
import org.l2jmobius.gameserver.model.actor.Playable;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.request.RelicCouponRequest;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.model.variables.AccountVariables;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.relics.ExRelicsSummonResult;

/**
 * @author CostyKiller
 */
public class RelicSummonCoupon implements IItemHandler
{
	@Override
	public boolean useItem(Playable playable, Item item, boolean forceUse)
	{
		if (!playable.isPlayer())
		{
			playable.sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_THIS_ITEM);
			return false;
		}
		
		final Player player = playable.getActingPlayer();
		if (player.isCastingNow())
		{
			return false;
		}
		
		// If you have 100 relics in your confirmation list, restrictions are applied to the relic summoning and compounding functions.
		if (player.getAccountVariables().getInt(AccountVariables.UNCONFIRMED_RELICS_COUNT, 0) == 100)
		{
			player.sendPacket(SystemMessageId.SUMMON_COMPOUND_IS_UNAVAILABLE_AS_YOU_HAVE_MORE_THAN_100_UNCONFIRMED_RELICS);
			return false;
		}
		
		if (player.hasRequest(RelicCouponRequest.class))
		{
			return false;
		}
		player.addRequest(new RelicCouponRequest(player));
		
		int relicSummonCount = 0;
		if ((item.getId() == 83004) || (item.getId() == 83006))
		{
			relicSummonCount = 11;
		}
		else
		{
			relicSummonCount = 1;
		}
		
		player.sendPacket(new ExRelicsSummonResult(player, item.getId(), relicSummonCount));
		player.destroyItem("RelicSummon", item, 1, player, true);
		return true;
	}
}
