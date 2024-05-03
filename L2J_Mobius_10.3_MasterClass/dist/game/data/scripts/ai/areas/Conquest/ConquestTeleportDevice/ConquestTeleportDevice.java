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
package ai.areas.Conquest.ConquestTeleportDevice;

import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;

import ai.AbstractNpcAI;

/**
 * Teleport Device AI.
 * @author CostyKiller
 */
public class ConquestTeleportDevice extends AbstractNpcAI
{
	// NPCs
	// private static final int DEVICE1 = 34596; // Teleport Device 1
	private static final int DEVICE2 = 34597; // Teleport Device 2
	
	// Locations
	private static final Location[] HUNT_LOCS =
	{
		new Location(-10724, -200409, -3468), // Zone 1 - Asa
		new Location(-28380, -214417, -3200), // Zone 2 - Anima
		new Location(-2570, -213261, -3603), // Zone 3 - Nox
		new Location(-11731, -215556, -2800), // Zone 4 - Callide Hall
		new Location(-24036, -220963, -3511) // Eigis Seat
	};
	
	private ConquestTeleportDevice()
	{
		addTalkId(DEVICE2);
		addFirstTalkId(DEVICE2);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		String htmltext = null;
		switch (event)
		{
			case "goHunting1":
			{
				player.teleToLocation(HUNT_LOCS[0], 0, player.getInstanceWorld());
				break;
			}
			case "goHunting2":
			{
				player.teleToLocation(HUNT_LOCS[1], 0, player.getInstanceWorld());
				break;
			}
			case "goHunting3":
			{
				player.teleToLocation(HUNT_LOCS[2], 0, player.getInstanceWorld());
				break;
			}
			case "goHunting4":
			{
				player.teleToLocation(HUNT_LOCS[3], 0, player.getInstanceWorld());
				break;
			}
			case "goHunting5":
			{
				player.teleToLocation(HUNT_LOCS[4], 0, player.getInstanceWorld());
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		if (npc.getId() == DEVICE2)
		{
			return "34597.htm";
		}
		return null;
	}
	
	public static void main(String[] args)
	{
		new ConquestTeleportDevice();
	}
}