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
package ai.others.BalthusKnights.Helper.BalthusKnights;

import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.commons.util.Rnd;
import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.instance.Monster;
import org.l2jmobius.gameserver.model.instancezone.Instance;

import ai.AbstractNpcAI;

/**
 * Balthus Knights AI
 * @author Kazumi
 */
public final class BalthusKnights extends AbstractNpcAI
{
	// NPCs
	private static final int BALTHUS_KNIGHT = 34372;
	// Monsters
	private static final int HATCHLING = 24090;
	private static final int GEM_DRAGON_ANTHARAS = 24091;
	
	public BalthusKnights()
	{
		addSpawnId(BALTHUS_KNIGHT);
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		final Instance instance = npc.getInstanceWorld();
		
		if ((instance != null) && (instance.getTemplateId() == 271))
		{
			switch (instance.getStatus())
			{
				case 0:
				{
					npc.setInvul(true);
					addSpawn((Rnd.get(2) == 1) ? GEM_DRAGON_ANTHARAS : HATCHLING, npc.getX(), npc.getY(), npc.getZ(), 0, true, 0, false, instance.getId());
					ThreadPool.schedule(() ->
					{
						World.getInstance().forEachVisibleObjectInRange(npc, Monster.class, 200, mob ->
						{
							if ((mob != null) && (!mob.isDead()) && ((mob.getId() == HATCHLING) || (mob.getId() == GEM_DRAGON_ANTHARAS)))
							{
								npc.asAttackable().addDamageHate(mob, 0, 999);
								addAttackDesire(npc, mob);
							}
						});
					}, 2000); // 2 sec
					break;
				}
				case 3:
				{
					npc.setInvul(true);
					ThreadPool.schedule(() ->
					{
						World.getInstance().forEachVisibleObjectInRange(npc, Monster.class, 60, mob ->
						{
							if ((mob != null) && (!mob.isDead()) && ((mob.getId() == HATCHLING) || (mob.getId() == GEM_DRAGON_ANTHARAS)))
							{
								npc.asAttackable().addDamageHate(mob, 0, 999);
								addAttackDesire(npc, mob);
							}
						});
					}, 2000); // 2 sec
					break;
				}
			}
		}
		
		return super.onSpawn(npc);
	}
	
	public static void main(String[] args)
	{
		new BalthusKnights();
	}
}