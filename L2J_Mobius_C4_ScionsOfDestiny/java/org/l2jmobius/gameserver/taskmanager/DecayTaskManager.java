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
package org.l2jmobius.gameserver.taskmanager;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.commons.util.CommonUtil;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.instance.RaidBoss;

/**
 * @author la2 Lets drink to code!
 */
public class DecayTaskManager implements Runnable
{
	protected static final Logger LOGGER = Logger.getLogger(DecayTaskManager.class.getName());
	
	protected static final Map<Creature, Long> DECAY_SCHEDULES = new ConcurrentHashMap<>();
	
	protected DecayTaskManager()
	{
		ThreadPool.scheduleAtFixedRate(this, 10000, 5000);
	}
	
	@Override
	public void run()
	{
		if (DECAY_SCHEDULES.isEmpty())
		{
			return;
		}
		
		try
		{
			final long currentTime = System.currentTimeMillis();
			final Iterator<Entry<Creature, Long>> iterator = DECAY_SCHEDULES.entrySet().iterator();
			Entry<Creature, Long> entry;
			Creature creature;
			int delay;
			
			while (iterator.hasNext())
			{
				entry = iterator.next();
				creature = entry.getKey();
				
				if (creature instanceof RaidBoss)
				{
					delay = 30000;
				}
				else
				{
					delay = 8500;
				}
				if ((currentTime - entry.getValue()) > delay)
				{
					creature.onDecay();
					iterator.remove();
				}
			}
		}
		catch (Throwable t)
		{
			// TODO: Find out the reason for exception. Unless caught here, mob decay would stop.
			LOGGER.warning(CommonUtil.getStackTrace(t));
		}
	}
	
	public void addDecayTask(Creature actor)
	{
		DECAY_SCHEDULES.put(actor, System.currentTimeMillis());
	}
	
	public void addDecayTask(Creature actor, int interval)
	{
		DECAY_SCHEDULES.put(actor, System.currentTimeMillis() + interval);
	}
	
	public void cancelDecayTask(Creature actor)
	{
		try
		{
			DECAY_SCHEDULES.remove(actor);
		}
		catch (NoSuchElementException e)
		{
		}
	}
	
	@Override
	public String toString()
	{
		String ret = "============= DecayTask Manager Report ============\r\n";
		ret += "Tasks count: " + DECAY_SCHEDULES.size() + "\r\n";
		ret += "Tasks dump:\r\n";
		
		final Long current = System.currentTimeMillis();
		for (Creature actor : DECAY_SCHEDULES.keySet())
		{
			ret += "Class/Name: " + actor.getClass().getSimpleName() + "/" + actor.getName() + " decay timer: " + (current - DECAY_SCHEDULES.get(actor)) + "\r\n";
		}
		
		return ret;
	}
	
	public static DecayTaskManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final DecayTaskManager INSTANCE = new DecayTaskManager();
	}
}
