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
package org.l2jmobius.gameserver.network.clientpackets.teleports;

import org.l2jmobius.Config;
import org.l2jmobius.gameserver.data.xml.TeleportListData;
import org.l2jmobius.gameserver.instancemanager.CastleManager;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.holders.TeleportListHolder;
import org.l2jmobius.gameserver.model.itemcontainer.Inventory;
import org.l2jmobius.gameserver.model.siege.Castle;
import org.l2jmobius.gameserver.model.skill.CommonSkill;
import org.l2jmobius.gameserver.network.PacketLogger;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.clientpackets.ClientPacket;

/**
 * @author NviX, Mobius
 */
public class ExRequestTeleport extends ClientPacket
{
	private int _teleportId;
	
	@Override
	protected void readImpl()
	{
		_teleportId = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		final TeleportListHolder teleport = TeleportListData.getInstance().getTeleport(_teleportId);
		if (teleport == null)
		{
			PacketLogger.warning("No registered teleport location for id: " + _teleportId);
			return;
		}
		
		// Dead characters cannot use teleports.
		if (player.isDead())
		{
			player.sendPacket(SystemMessageId.DEAD_CHARACTERS_CANNOT_USE_TELEPORTS);
			return;
		}
		
		// Players should not be able to teleport if in a special location.
		if ((player.getMovieHolder() != null) || player.isFishing() || player.isInInstance() || player.isOnEvent() || player.isInOlympiadMode() || player.inObserverMode() || player.isInTraingCamp() || player.isInTimedHuntingZone() || player.isInStoreMode())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_TELEPORT_RIGHT_NOW);
			return;
		}
		
		// Teleport in combat configuration.
		if (!Config.TELEPORT_WHILE_PLAYER_IN_COMBAT && (player.isInCombat() || player.isCastingNow()))
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_TELEPORT_IN_COMBAT);
			return;
		}
		
		// Karma related configurations.
		if ((!Config.ALT_GAME_KARMA_PLAYER_CAN_TELEPORT || !Config.ALT_GAME_KARMA_PLAYER_CAN_USE_GK) && (player.getReputation() < 0))
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_TELEPORT_RIGHT_NOW);
			return;
		}
		
		// Cannot escape effect.
		if (player.cannotEscape())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_TELEPORT_RIGHT_NOW);
			return;
		}
		
		final Location location = teleport.getLocation();
		if (!Config.TELEPORT_WHILE_SIEGE_IN_PROGRESS)
		{
			final Castle castle = CastleManager.getInstance().getCastle(location.getX(), location.getY(), location.getZ());
			if ((castle != null) && castle.getSiege().isInProgress())
			{
				player.sendPacket(SystemMessageId.YOU_CANNOT_TELEPORT_TO_A_VILLAGE_THAT_IS_IN_A_SIEGE);
				return;
			}
		}
		
		if (player.getLevel() > Config.MAX_FREE_TELEPORT_LEVEL)
		{
			final int price = teleport.getPrice();
			if (price > 0)
			{
				// Check if player has fee.
				if (teleport.isSpecial())
				{
					if (player.getInventory().getInventoryItemCount(Inventory.LCOIN_ID, -1) < price)
					{
						player.sendPacket(SystemMessageId.THERE_ARE_NOT_ENOUGH_L_COINS);
						return;
					}
				}
				else if (player.getAdena() < price)
				{
					player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
					return;
				}
				// Reduce items.
				if (teleport.isSpecial())
				{
					player.destroyItemByItemId("Teleport", Inventory.LCOIN_ID, price, player, true);
				}
				else
				{
					player.reduceAdena("Teleport", price, player, true);
				}
			}
		}
		
		player.abortCast();
		player.stopMove(null);
		
		player.setTeleportLocation(location);
		player.doCast(CommonSkill.TELEPORT.getSkill());
	}
}