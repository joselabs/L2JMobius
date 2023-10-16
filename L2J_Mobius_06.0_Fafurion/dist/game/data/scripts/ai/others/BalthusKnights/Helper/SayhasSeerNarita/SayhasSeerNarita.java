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
package ai.others.BalthusKnights.Helper.SayhasSeerNarita;

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
 * Sayha's Seer Narita AI
 * @author Kazumi
 */
public final class SayhasSeerNarita extends AbstractNpcAI
{
	// NPCs
	private static final int NARITA = 34381;
	// Monsters
	private static final int ANTHARAS = 24087;
	private static final int INVISIBLE_NPC = 18918;
	// Skills
	private static final SkillHolder HydroAttackSkill = new SkillHolder(32157, 1);
	// private static final SkillHolder FatalWoundRecoverySkill = new SkillHolder(32184, 1);
	// Misc
	private static final int p_CheckInterval = 4000;
	private static final int p_TalkInterval = 15000;
	
	public SayhasSeerNarita()
	{
		addSpawnId(NARITA, INVISIBLE_NPC);
		addFirstTalkId(NARITA);
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		final Instance instance = npc.getInstanceWorld();
		
		switch (npc.getId())
		{
			case NARITA:
			{
				if ((instance != null) && (instance.getTemplateId() == 271))
				{
					npc.setInvul(true);
					npc.setTalkable(false);
					final Npc antharas = instance.getNpc(ANTHARAS);
					if (instance.getFirstPlayer().getClassId() == ClassId.SAYHA_SEER_BALTHUS)
					{
						ThreadPool.schedule(new TalkTask(npc, instance), p_TalkInterval);
					}
					
					ThreadPool.schedule(() ->
					{
						ThreadPool.schedule(new CheckTask(npc, instance), p_CheckInterval);
						npc.setRandomWalking(false);
						npc.setTarget(antharas);
						npc.setRunning();
						addAttackDesire(npc, antharas);
					}, 7000L); // 7 sec
				}
				break;
			}
			case INVISIBLE_NPC:
			{
				final Npc narita = instance.getNpc(NARITA);
				if (narita != null)
				{
					final Player player = instance.getFirstPlayer();
					if (player.getClassId() == ClassId.SAYHA_SEER_BALTHUS)
					{
						narita.setRunning();
						narita.setTalkable(true);
						narita.teleToLocation(171771, 190975, -11536, 25797, instance);
						narita.setTarget(narita);
						// TODO: Check why the head goes crazy with this skill
						// narita.setTarget(narita, TargetChangeReason.STANDARD);
						// addSkillCastDesire(narita, npc.getTarget(), FatalWoundRecoverySkill, 1);
					}
					else
					{
						ThreadPool.schedule(() ->
						{
							narita.decayMe();
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
					addSkillCastDesire(_npc, _npc.getTarget(), HydroAttackSkill, 20000);
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
							_instance.broadcastPacket(new PlaySound(3, "Npcdialog1.narita_ep50_battle_1", 0, 0, 0, 0, 0));
							_npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.NEVER_LET_YOUR_GUARD_DOWN_OR_DANGER_WILL_FIND_YOU_BEFORE_YOU_KNOW_IT);
							break;
						}
						case 2:
						{
							_instance.broadcastPacket(new PlaySound(3, "Npcdialog1.narita_ep50_battle_2", 0, 0, 0, 0, 0));
							_npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.YOU_NEED_TO_USE_THE_ENERGY_OF_WIND_WELL);
							break;
						}
						case 3:
						{
							_instance.broadcastPacket(new PlaySound(3, "Npcdialog1.narita_ep50_battle_3", 0, 0, 0, 0, 0));
							_npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.TRUST_IN_YOUR_COMRADES_AND_GIVE_YOUR_BEST);
							break;
						}
						case 4:
						{
							_instance.broadcastPacket(new PlaySound(3, "Npcdialog1.narita_ep50_battle_4", 0, 0, 0, 0, 0));
							_npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.THERE_MUST_BE_MORE_WE_DON_T_KNOW_ABOUT_ANTHARAS);
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
		htmltext = "balthus_seer001.htm";
		return htmltext;
	}
	
	public static void main(String[] args)
	{
		new SayhasSeerNarita();
	}
}