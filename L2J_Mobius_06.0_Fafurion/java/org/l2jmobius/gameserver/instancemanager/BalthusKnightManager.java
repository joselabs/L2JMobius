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
package org.l2jmobius.gameserver.instancemanager;

import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.itemcontainer.PlayerInventory;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;

/**
 * This class manage the Balthus Knight Path.
 * @author Kazumi
 */
public final class BalthusKnightManager
{
	/**
	 * Remove Dragon Weapons on enter world
	 * @param player
	 */
	public static final void removeDragonWeapons(Player player)
	{
		PlayerInventory inv = player.getInventory();
		
		if (inv.getItemByItemId(48194) != null)
		{
			inv.destroyItemByItemId("AntharasShaperRemoval", 48194, 1, player, player);
		}
		else if (inv.getItemByItemId(48195) != null)
		{
			inv.destroyItemByItemId("AntharasSlasherRemoval", 48195, 1, player, player);
		}
		else if (inv.getItemByItemId(48196) != null)
		{
			inv.destroyItemByItemId("AntharasThrowerRemoval", 48196, 1, player, player);
		}
		else if (inv.getItemByItemId(48197) != null)
		{
			inv.destroyItemByItemId("AntharasBusterRemoval", 48197, 1, player, player);
		}
		else if (inv.getItemByItemId(48198) != null)
		{
			inv.destroyItemByItemId("AntharasCutterRemoval", 48198, 1, player, player);
		}
		else if (inv.getItemByItemId(48199) != null)
		{
			inv.destroyItemByItemId("AntharasStormerRemoval", 48199, 1, player, player);
		}
		else if (inv.getItemByItemId(48200) != null)
		{
			inv.destroyItemByItemId("AntharasFighterRemoval", 48200, 1, player, player);
		}
		else if (inv.getItemByItemId(48201) != null)
		{
			inv.destroyItemByItemId("AntharasDualswordRemoval", 48201, 1, player, player);
		}
	}
	
	/**
	 * Check spawn location on enter world
	 * @param player
	 */
	public final static void checkSpawnLocation(Player player)
	{
		switch (player.getVariables().getInt(PlayerVariables.BALTHUS_PHASE, 1))
		{
			case 1:
			{
				// Teleport to Nest
				player.teleToLocation(190441, 168020, -11232);
				break;
			}
			case 2:
			{
				// Teleport to Barracks
				player.teleToLocation(178306, 177018, -12104);
				break;
			}
			default:
			{
				// Reset Phase
				player.getVariables().set(PlayerVariables.BALTHUS_PHASE, 2);
				// Teleport to Barracks
				player.teleToLocation(178306, 177018, -12104);
				break;
			}
		}
	}
	
	/**
	 * Gets the single instance of {@code BalthusKnightManager}.
	 * @return single instance of {@code BalthusKnightManager}
	 */
	public static BalthusKnightManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final BalthusKnightManager INSTANCE = new BalthusKnightManager();
	}
}
