/*
 * Copyright (c) 2013 L2jMobius
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
 * IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package ai.areas.LairOfAntharas;

import java.util.Calendar;

import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.gameserver.data.SpawnTable;
import org.l2jmobius.gameserver.data.xml.NpcData;
import org.l2jmobius.gameserver.instancemanager.DBSpawnManager;
import org.l2jmobius.gameserver.instancemanager.ZoneManager;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.Spawn;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.templates.NpcTemplate;
import org.l2jmobius.gameserver.model.zone.ZoneType;
import org.l2jmobius.gameserver.network.NpcStringId;
import org.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2jmobius.gameserver.network.serverpackets.PlaySound;

import ai.AbstractNpcAI;

/**
 * Lair of Antharas Karim AI
 * @URL https://l2central.info/essence/locations/special_zones/antharas_lair/
 */
public class Karim extends AbstractNpcAI
{
	// NPCs
	private static final int KARIM = 25913;
	private static final ZoneType ZONE = ZoneManager.getInstance().getZoneByName("antharas_lair");
	
	private static final Location[] LOCATIONS =
	{
		new Location(144801, 114629, -3717),
		new Location(147294, 116274, -3712),
		new Location(148694, 115718, -3724),
		new Location(147470, 112505, -3724)
	};
	
	private Karim()
	{
		addKillId(KARIM);
		
		// Schedule spawn every hour from 10 minutes to any hour till 20 minutes past that hour (for example, from 18:50 till 19:20).
		final long currentTime = System.currentTimeMillis();
		final Calendar spawnCalendar = Calendar.getInstance();
		final Calendar deSpawnCalendar = Calendar.getInstance();
		
		spawnCalendar.add(Calendar.HOUR_OF_DAY, 0); // gets hour in 24h format
		spawnCalendar.set(Calendar.MINUTE, 50);
		spawnCalendar.set(Calendar.SECOND, 0);
		
		deSpawnCalendar.add(Calendar.HOUR_OF_DAY, 0); // gets hour in 24h format
		deSpawnCalendar.set(Calendar.MINUTE, 20);
		deSpawnCalendar.set(Calendar.SECOND, 0);
		
		final long spawnCalendarTime = spawnCalendar.getTimeInMillis();
		final long deSpawnCalendarTime = deSpawnCalendar.getTimeInMillis();
		
		final long startSpawnDelay = Math.max(0, spawnCalendarTime - currentTime);
		final long startDeSpawnDelay = Math.max(0, deSpawnCalendarTime - currentTime);
		
		ThreadPool.scheduleAtFixedRate(this::onSpawn, startSpawnDelay, 3600000); // 3600000 = 1 hours
		ThreadPool.scheduleAtFixedRate(this::onDeSpawnSpawn, startDeSpawnDelay, 3600000); // 3600000 = 1 hours
	}
	
	private void onDeSpawnSpawn()
	{
		for (Spawn spawn : SpawnTable.getInstance().getSpawns(KARIM))
		{
			for (Npc monster : spawn.getSpawnedNpcs())
			{
				if (!monster.isDead())
				{
					DBSpawnManager.getInstance().deleteSpawn(spawn, true);
					monster.deleteMe();
				}
			}
		}
	}
	
	private void onSpawn()
	{
		final NpcTemplate template = NpcData.getInstance().getTemplate(KARIM);
		if (template != null)
		{
			try
			{
				final Spawn spawn = new Spawn(template);
				spawn.setXYZ(getRandomEntry(LOCATIONS));
				spawn.setRespawnDelay(86400000);
				DBSpawnManager.getInstance().addNewSpawn(spawn, true);
				if (ZONE != null)
				{
					ZONE.broadcastPacket(new ExShowScreenMessage(NpcStringId.ANTHARAS_FOLLOWER_KARIM_APPEARED, 2, 5000));
					ZONE.broadcastPacket(new ExShowScreenMessage("Karim has spawned!", 5000));
					ZONE.broadcastPacket(new PlaySound("BS02_A"));
				}
				else
				{
					LOGGER.warning(getClass().getSimpleName() + ": Zone is not initialized properly!");
				}
			}
			catch (Exception e)
			{
				LOGGER.warning(getClass().getSimpleName() + ": Problem with onSpawn! " + e.getMessage());
			}
		}
	}
	
	public static void main(String[] args)
	{
		new Karim();
	}
}