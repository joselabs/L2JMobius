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
package ai.others.BalthusKnights.Helper.SupportTroopLieutenantRash;

import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.instancezone.Instance;

import ai.AbstractNpcAI;

/**
 * Support Troop Lieutenant Rash AI
 * @author Kazumi
 */
public final class SupportTroopLieutenantRash extends AbstractNpcAI
{
	// NPCs
	private static final int RASH = 34371;
	// Monsters
	private static final int ANTHARAS_TRANSFORM = 24088;
	// Misc
	private static final int p_CheckFirstInterval = 1000;
	private static final int p_CheckInterval = 3000;
	
	public SupportTroopLieutenantRash()
	{
		addSpawnId(RASH);
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		final Instance instance = npc.getInstanceWorld();
		if ((instance != null) && (instance.getTemplateId() == 271))
		{
			npc.setInvul(true);
			npc.setTalkable(false);
			final Npc antharasTrans = instance.getNpc(ANTHARAS_TRANSFORM);
			ThreadPool.schedule(new CheckTask(npc, instance), p_CheckFirstInterval);
			npc.setRandomWalking(false);
			npc.setTarget(antharasTrans);
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
					addAttackDesire(_npc, antharas_trans);
				}
			}
		}
	}
	
	public static void main(String[] args)
	{
		new SupportTroopLieutenantRash();
	}
}