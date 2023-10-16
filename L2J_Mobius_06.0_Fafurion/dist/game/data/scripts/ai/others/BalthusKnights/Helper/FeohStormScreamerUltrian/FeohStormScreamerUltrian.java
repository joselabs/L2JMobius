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
package ai.others.BalthusKnights.Helper.FeohStormScreamerUltrian;

import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.commons.util.Rnd;
import org.l2jmobius.gameserver.enums.ChatType;
import org.l2jmobius.gameserver.enums.ClassId;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.holders.SkillHolder;
import org.l2jmobius.gameserver.model.instancezone.Instance;
import org.l2jmobius.gameserver.network.NpcStringId;
import org.l2jmobius.gameserver.network.serverpackets.PlaySound;

import ai.AbstractNpcAI;

/**
 * Feoh Storm Screamer Ultrian AI
 * @author Kazumi
 */
public final class FeohStormScreamerUltrian extends AbstractNpcAI
{
	// NPCs
	private static final int ULTRIAN = 34380;
	// Monsters
	private static final int ANTHARAS = 24087;
	private static final int INVISIBLE_NPC = 18918;
	// Skills
	private static final SkillHolder ElementalSpikeSkill = new SkillHolder(32152, 1);
	private static final SkillHolder UpdraftDestructionSkill = new SkillHolder(32153, 1);
	private static final SkillHolder QuadrupleBlasterSkill = new SkillHolder(32154, 1);
	// Misc
	private static final int p_CheckInterval = 4000;
	private static final int p_TalkInterval = 15000;
	
	public FeohStormScreamerUltrian()
	{
		addSpawnId(ULTRIAN, INVISIBLE_NPC);
		addFirstTalkId(ULTRIAN);
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		final Instance instance = npc.getInstanceWorld();
		
		switch (npc.getId())
		{
			case ULTRIAN:
			{
				if ((instance != null) && (instance.getTemplateId() == 271))
				{
					npc.setInvul(true);
					npc.setTalkable(false);
					final Npc antharas = instance.getNpc(ANTHARAS);
					if (instance.getFirstPlayer().getClassId() == ClassId.FEOH_WIZARD)
					{
						ThreadPool.schedule(new TalkTask(npc, instance), p_TalkInterval);
					}
					
					ThreadPool.schedule(() ->
					{
						ThreadPool.schedule(new CheckTask(npc, instance), p_CheckInterval);
						npc.setRandomWalking(false);
						npc.setTargetable(false);
						npc.setTarget(antharas);
					}, 7000L); // 7 sec
				}
				break;
			}
			case INVISIBLE_NPC:
			{
				final Npc ultrian = instance.getNpc(ULTRIAN);
				if (ultrian != null)
				{
					final Player player = instance.getFirstPlayer();
					if (player.getClassId() == ClassId.FEOH_WIZARD)
					{
						ultrian.setWalking();
						ultrian.setTalkable(true);
						ultrian.setTargetable(true);
						ultrian.teleToLocation(171771, 190975, -11536, 25797, instance);
						// TODO: Check how NPC sit down
					}
					else
					{
						ThreadPool.schedule(() ->
						{
							ultrian.decayMe();
						}, 5000L); // 5 sec
					}
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
			if ((_instance != null) && (_instance.getStatus() < 4))
			{
				if ((_npc != null) && !_npc.isDead() && !_npc.isDecayed())
				{
					ThreadPool.schedule(new CheckTask(_npc, _instance), p_CheckInterval);
					final Npc antharas = _instance.getNpc(ANTHARAS);
					_npc.setTarget(antharas);
					if (Rnd.get(100) <= 60)
					{
						addSkillCastDesire(_npc, _npc.getTarget(), ElementalSpikeSkill, 20000);
					}
					else
					{
						if (Rnd.get(100) <= 60)
						{
							addSkillCastDesire(_npc, _npc.getTarget(), UpdraftDestructionSkill, 20000);
						}
						else
						{
							addSkillCastDesire(_npc, _npc.getTarget(), QuadrupleBlasterSkill, 20000);
						}
					}
				}
			}
		}
	}
	
	private class TalkTask implements Runnable
	{
		private final Npc _npc;
		private final Instance _instance;
		
		public TalkTask(Npc npc, Instance instance)
		{
			_npc = npc;
			_instance = instance;
		}
		
		@Override
		public void run()
		{
			
			if ((_instance != null) && (_instance.getStatus() < 4))
			{
				if ((_npc != null) && !_npc.isDead() && !_npc.isDecayed())
				{
					ThreadPool.schedule(new TalkTask(_npc, _instance), p_TalkInterval);
					switch (Rnd.get(1, 4))
					{
						case 1:
						{
							_instance.broadcastPacket(new PlaySound(3, "Npcdialog1.oltrian_ep50_battle_1", 0, 0, 0, 0, 0));
							_npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.THAT_DRAGON_IS_JUST_BIG_NOTHING_ELSE);
							break;
						}
						case 2:
						{
							_instance.broadcastPacket(new PlaySound(3, "Npcdialog1.oltrian_ep50_battle_2", 0, 0, 0, 0, 0));
							_npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.HEY_DON_T_YOU_HAVE_ANY_STRONGER_ATTACKS);
							break;
						}
						case 3:
						{
							_instance.broadcastPacket(new PlaySound(3, "Npcdialog1.oltrian_ep50_battle_3", 0, 0, 0, 0, 0));
							_npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.DON_T_REST_ATTACK_THAT_S_THE_SMARTEST_THING_YOU_CAN_DO);
							break;
						}
						case 4:
						{
							_instance.broadcastPacket(new PlaySound(3, "Npcdialog1.oltrian_ep50_battle_4", 0, 0, 0, 0, 0));
							_npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.OH_NOW_IT_S_TIME_TO_REALLY_SHOW_WHAT_YOU_CAN_DO);
							break;
						}
					}
				}
			}
		}
	}
	
	@Override
	public final String onFirstTalk(Npc npc, Player player)
	{
		String htmltext = null;
		htmltext = "balthus_feoh001.htm";
		return htmltext;
	}
	
	public static void main(String[] args)
	{
		new FeohStormScreamerUltrian();
	}
}