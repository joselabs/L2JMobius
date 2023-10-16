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
package ai.others.BalthusKnights.Helper.BalthusKnightAteld;

import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.holders.SkillHolder;
import org.l2jmobius.gameserver.model.instancezone.Instance;

import ai.AbstractNpcAI;

/**
 * Balthus Knight Ateld AI
 * @author Kazumi
 */
public final class BalthusKnightAteld extends AbstractNpcAI
{
	// NPCs
	private static final int ATELD = 34369;
	// Monsters
	private static final int ANTHARAS_TRANSFORM = 24088;
	// Skills
	// TODO: It's "Balthus Volcanic Destruction" on Orfen
	private static final SkillHolder ElementalSpikeSkill = new SkillHolder(32152, 1);
	// Misc
	private static final int p_CheckFirstInterval = 15000;
	private static final int p_CheckInterval = 3000;
	
	public BalthusKnightAteld()
	{
		addSpawnId(ATELD);
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		final Instance instance = npc.getInstanceWorld();
		
		switch (npc.getId())
		{
			case ATELD:
			{
				if ((instance != null) && (instance.getTemplateId() == 271))
				{
					final Npc antharas_trans = instance.getNpc(ANTHARAS_TRANSFORM);
					if (antharas_trans != null)
					{
						npc.setTarget(antharas_trans);
					}
					npc.setInvul(true);
					npc.setTalkable(false);
					npc.setRandomWalking(false);
					npc.setTargetable(false);
					ThreadPool.schedule(new CheckTask(npc, instance), p_CheckFirstInterval);
				}
				break;
			}
		}
		return super.onSpawn(npc);
	}
	
	private class CheckTask implements Runnable
	{
		private final Npc _npc;
		private final Instance _instance;
		
		public CheckTask(Npc npc, Instance instance)
		{
			_npc = npc;
			_instance = instance;
		}
		
		@Override
		public void run()
		{
			if ((_instance != null) && (_instance.getStatus() < 5))
			{
				if ((_npc != null) && !_npc.isDead() && !_npc.isDecayed())
				{
					ThreadPool.schedule(new CheckTask(_npc, _instance), p_CheckInterval);
					final Npc antharas_trans = _instance.getNpc(ANTHARAS_TRANSFORM);
					if (antharas_trans != null)
					{
						final double distance = _npc.calculateDistance2D(antharas_trans);
						if ((distance > 300) || (distance < 200))
						{
							_npc.setRunning();
							addMoveToDesire(_npc, antharas_trans.getPointInRange(200, 300), 23);
						}
						else
						{
							addSkillCastDesire(_npc, antharas_trans, ElementalSpikeSkill, 20000);
						}
					}
				}
			}
		}
	}
	
	public static void main(String[] args)
	{
		new BalthusKnightAteld();
	}
}