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
package ai.bosses.TowerOfInsolence;

import org.l2jmobius.commons.time.SchedulingPattern;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;

import ai.AbstractNpcAI;

/**
 * @author gugaf
 */
public class Galaxia extends AbstractNpcAI
{
	// NPC
	private static final int GALAXIA = 29208;
	// Location
	private static final Location GALAXIA_LOC = new Location(115213, 16623, 10080);
	// Misc
	private static final String GALAXIA_RESPAWN_PATTERN = "30 22 * * 2";
	private SchedulingPattern _respawnPattern = null;
	
	public Galaxia()
	{
		addKillId(GALAXIA);
		
		_respawnPattern = new SchedulingPattern(GALAXIA_RESPAWN_PATTERN);
		
		final long nextRespawnTime = getNextRespawnTime();
		if (nextRespawnTime > 0)
		{
			startQuestTimer("respawn_galaxia", nextRespawnTime, null, null);
		}
		else
		{
			addSpawn(GALAXIA, GALAXIA_LOC, false, getDespawnTime());
		}
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		if (event.equalsIgnoreCase("respawn_galaxia"))
		{
			addSpawn(GALAXIA, GALAXIA_LOC, false, getDespawnTime());
		}
		return super.onEvent(event, npc, player);
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		final long nextRespawnTime = getNextRespawnTime();
		if (nextRespawnTime > 0)
		{
			startQuestTimer("respawn_galaxia", nextRespawnTime, null, null);
		}
		else
		{
			addSpawn(GALAXIA, GALAXIA_LOC, false, getDespawnTime());
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	private long getDespawnTime()
	{
		final long currentTime = System.currentTimeMillis();
		return _respawnPattern.next(currentTime) - currentTime - 60000 /* 1 minute less */;
	}
	
	private long getNextRespawnTime()
	{
		final long currentTime = System.currentTimeMillis();
		return _respawnPattern.next(currentTime) - currentTime;
	}
	
	public static void main(String[] args)
	{
		new Galaxia();
	}
}
