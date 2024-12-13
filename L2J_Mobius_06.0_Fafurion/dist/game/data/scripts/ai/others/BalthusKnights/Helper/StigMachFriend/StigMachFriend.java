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
package ai.others.BalthusKnights.Helper.StigMachFriend;

import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.commons.util.Rnd;
import org.l2jmobius.gameserver.enums.ChatType;
import org.l2jmobius.gameserver.instancemanager.QuestManager;
import org.l2jmobius.gameserver.model.StatSet;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.events.impl.creature.OnCreatureAttacked;
import org.l2jmobius.gameserver.model.holders.SkillHolder;
import org.l2jmobius.gameserver.model.instancezone.Instance;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.network.NpcStringId;
import org.l2jmobius.gameserver.network.serverpackets.PlaySound;

import ai.AbstractNpcAI;
import instances.BalthusKnights.AntharasNest;
import quests.Q10555_ChargeAtAntharas.Q10555_ChargeAtAntharas;

/**
 * Stig Mach Friend AI
 * @author Kazumi
 */
public final class StigMachFriend extends AbstractNpcAI
{
	// NPCs
	private static final int STIG_MACH_FRIEND = 34366;
	// Monsters
	private static final int GEM_DRAGON_ANTHARAS = 24091;
	private static final int ANTHARAS = 24087;
	private static final int ANTHARAS_TRANSFORM = 24088;
	// Skills
	private static final SkillHolder ChainHydraSkill = new SkillHolder(32135, 1);
	private static final SkillHolder GustBladeSkill = new SkillHolder(32136, 1);
	// Misc
	private static final int p_CheckInterval = 3000;
	private static final int p_CheckTransInterval = 3000;
	private static final int p_CheckFirstTransInterval = 1000;
	private static boolean _firstAttacked;
	
	public StigMachFriend()
	{
		addSpawnId(STIG_MACH_FRIEND, ANTHARAS, ANTHARAS_TRANSFORM);
		addFirstTalkId(STIG_MACH_FRIEND);
		addKillId(GEM_DRAGON_ANTHARAS);
		setCreatureAttackedId(this::onCreatureAttacked, STIG_MACH_FRIEND);
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		final Instance instance = npc.getInstanceWorld();
		
		switch (npc.getId())
		{
			case STIG_MACH_FRIEND:
			{
				if ((instance != null) && (instance.getTemplateId() == 271))
				{
					_firstAttacked = true;
					npc.setInvul(true);
				}
				break;
			}
			case ANTHARAS:
			{
				final Npc stig = instance.getNpc(STIG_MACH_FRIEND);
				if (!stig.isInCombat() || (stig.getTarget() == null))
				{
					ThreadPool.schedule(() ->
					{
						getTimers().addTimer("p_CheckTimer", p_CheckInterval, stig, null);
						stig.setRandomWalking(false);
						stig.setTarget(npc);
						stig.setRunning();
						addAttackDesire(stig, npc);
					}, 18000L); // 18 sec
				}
				break;
			}
			case ANTHARAS_TRANSFORM:
			{
				final Npc stig = instance.getNpc(STIG_MACH_FRIEND);
				getTimers().cancelTimersOf(stig);
				getTimers().addTimer("p_CheckTransTimer", p_CheckFirstTransInterval, stig, null);
				break;
			}
		}
		return super.onSpawn(npc);
	}
	
	@Override
	public final String onFirstTalk(Npc npc, Player player)
	{
		String htmltext = null;
		if (player.getVariables().getInt(PlayerVariables.BALTHUS_PHASE, 1) == 2)
		{
			htmltext = "stig_friend001.htm";
		}
		else
		{
			htmltext = "stig_friend002.htm";
		}
		return htmltext;
	}
	
	public void onCreatureAttacked(OnCreatureAttacked event)
	{
		final Npc npc = event.getTarget().asNpc();
		final Creature attacker = event.getAttacker();
		final Instance instance = npc.getInstanceWorld();
		
		if (instance.getStatus() == 1)
		{
			npc.setTarget(attacker);
			addAttackDesire(npc, attacker);
			if (_firstAttacked)
			{
				_firstAttacked = false;
				npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.I_WON_T_LET_SOME_GEM_DRAGON_DEFEAT_ME);
				switch (Rnd.get(2))
				{
					case 1:
					{
						instance.broadcastPacket(new PlaySound(3, "Npcdialog1.stig_ep50_battle_1", 0, 0, 0, 0, 0));
						break;
					}
					default:
					{
						instance.broadcastPacket(new PlaySound(3, "Npcdialog1.stig_ep50_battle_2", 0, 0, 0, 0, 0));
						break;
					}
				}
			}
		}
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		if (npc.getId() == GEM_DRAGON_ANTHARAS)
		{
			final QuestState qs = killer.getQuestState(Q10555_ChargeAtAntharas.class.getSimpleName());
			if ((qs != null) && qs.isCond(2))
			{
				final Quest instance = QuestManager.getInstance().getQuest(AntharasNest.class.getSimpleName());
				if (instance != null)
				{
					_firstAttacked = true;
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public void onTimerEvent(String event, StatSet params, Npc npc, Player player)
	{
		Instance instance = npc.getInstanceWorld();
		if ((instance != null) && event.equals("p_CheckTimer"))
		{
			synchronized (npc)
			{
				if (!npc.isDead() && !npc.isDecayed())
				{
					getTimers().addTimer("p_CheckTimer", p_CheckInterval, npc, null);
					if (Rnd.get(1000) <= 333)
					{
						final Npc antharas = instance.getNpc(ANTHARAS);
						npc.setTarget(antharas);
						if (Rnd.get(1000) <= 333)
						{
							addSkillCastDesire(npc, npc.getTarget(), ChainHydraSkill, 20000);
						}
						else
						{
							addSkillCastDesire(npc, npc.getTarget(), GustBladeSkill, 20000);
						}
					}
					else
					{
						final Npc antharas = instance.getNpc(ANTHARAS);
						addAttackDesire(npc, antharas);
					}
				}
			}
		}
		else if ((instance != null) && event.equals("p_CheckTransTimer"))
		{
			synchronized (npc)
			{
				if (!npc.isDead() && !npc.isDecayed())
				{
					getTimers().addTimer("p_CheckTransTimer", p_CheckTransInterval, npc, null);
					if (Rnd.get(1000) <= 333)
					{
						final Npc antharas_trans = instance.getNpc(ANTHARAS_TRANSFORM);
						npc.setTarget(antharas_trans);
						if (Rnd.get(1000) <= 333)
						{
							addSkillCastDesire(npc, npc.getTarget(), ChainHydraSkill, 20000);
						}
						else
						{
							addSkillCastDesire(npc, npc.getTarget(), GustBladeSkill, 20000);
						}
					}
					else
					{
						final Npc antharas_trans = instance.getNpc(ANTHARAS_TRANSFORM);
						addAttackDesire(npc, antharas_trans);
					}
				}
			}
		}
	}
	
	public static void main(String[] args)
	{
		new StigMachFriend();
	}
}
