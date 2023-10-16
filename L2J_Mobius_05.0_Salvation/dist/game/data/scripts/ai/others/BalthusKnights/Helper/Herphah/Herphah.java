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
package ai.others.BalthusKnights.Helper.Herphah;

import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.commons.util.Rnd;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.holders.SkillHolder;
import org.l2jmobius.gameserver.model.instancezone.Instance;
import org.l2jmobius.gameserver.model.quest.QuestState;

import ai.AbstractNpcAI;
import quests.Q10555_ChargeAtAntharas.Q10555_ChargeAtAntharas;

/**
 * Herphah AI
 * @author Kazumi
 */
public final class Herphah extends AbstractNpcAI
{
	// NPCs
	private static final int HERPHAH = 34367;
	// Monsters
	private static final int ANTHARAS_TRANSFORM = 24088;
	// Skills
	private static final SkillHolder PowerBomberSkill = new SkillHolder(32133, 1);
	private static final SkillHolder HurricaneStormSkill = new SkillHolder(32134, 1);
	// Misc
	private static final int p_CheckFirstInterval = 1000;
	private static final int p_CheckInterval = 3000;
	
	public Herphah()
	{
		addSpawnId(HERPHAH);
		addFirstTalkId(HERPHAH);
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		final Instance instance = npc.getInstanceWorld();
		
		switch (npc.getId())
		{
			case HERPHAH:
			{
				if ((instance != null) && (instance.getTemplateId() == 271))
				{
					npc.setInvul(true);
					npc.setTalkable(false);
					final Npc antharas_trans = instance.getNpc(ANTHARAS_TRANSFORM);
					ThreadPool.schedule(new CheckTask(npc, instance), p_CheckFirstInterval);
					npc.setRandomWalking(false);
					npc.setTarget(antharas_trans);
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
			if (_instance != null)
			{
				if ((_npc != null) && !_npc.isDead() && !_npc.isDecayed())
				{
					ThreadPool.schedule(new CheckTask(_npc, _instance), p_CheckInterval);
					if (Rnd.get(1000) <= 333)
					{
						final Npc antharas_trans = _instance.getNpc(ANTHARAS_TRANSFORM);
						_npc.setTarget(antharas_trans);
						if (Rnd.get(1000) <= 333)
						{
							addSkillCastDesire(_npc, _npc.getTarget(), PowerBomberSkill, 20000);
						}
						else
						{
							addSkillCastDesire(_npc, _npc.getTarget(), HurricaneStormSkill, 20000);
						}
					}
					else
					{
						final Npc antharas_trans = _instance.getNpc(ANTHARAS_TRANSFORM);
						addAttackDesire(_npc, antharas_trans);
					}
				}
			}
		}
	}
	
	@Override
	public final String onFirstTalk(Npc npc, Player player)
	{
		String htmltext = null;
		final Instance instance = npc.getInstanceWorld();
		
		if (instance.getStatus() == 5)
		{
			htmltext = "herphah_friend001.htm";
			final QuestState qs = player.getQuestState(Q10555_ChargeAtAntharas.class.getSimpleName());
			if ((qs != null) && qs.isStarted())
			{
				qs.setCond(16, false);
				ThreadPool.schedule(() ->
				{
					takeItems(player, 48194, 1); // Antharas Shaper
					takeItems(player, 48195, 1); // Antharas Slasher
					takeItems(player, 48196, 1); // Antharas Thrower
					takeItems(player, 48197, 1); // Antharas Buster
					takeItems(player, 48198, 1); // Antharas Cutter
					takeItems(player, 48199, 1); // Antharas Stormer
					takeItems(player, 48200, 1); // Antharas Fighter
					takeItems(player, 48201, 1); // Antharas Dualsword
					instance.finishInstance(0);
				}, 5000L); // 5 sec
			}
		}
		return htmltext;
	}
	
	public static void main(String[] args)
	{
		new Herphah();
	}
}