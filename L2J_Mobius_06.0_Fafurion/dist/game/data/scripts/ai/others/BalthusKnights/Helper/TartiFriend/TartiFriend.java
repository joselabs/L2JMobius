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
package ai.others.BalthusKnights.Helper.TartiFriend;

import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.commons.util.Rnd;
import org.l2jmobius.gameserver.enums.ChatType;
import org.l2jmobius.gameserver.instancemanager.QuestManager;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.StatSet;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.events.impl.creature.OnCreatureDamageReceived;
import org.l2jmobius.gameserver.model.holders.SkillHolder;
import org.l2jmobius.gameserver.model.instancezone.Instance;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.network.NpcStringId;
import org.l2jmobius.gameserver.network.serverpackets.PlaySound;

import ai.AbstractNpcAI;
import instances.BalthusKnights.AntharasNest;
import quests.Q10555_ChargeAtAntharas.Q10555_ChargeAtAntharas;

/**
 * Tarti Friend AI
 * @author Kazumi
 */
public final class TartiFriend extends AbstractNpcAI
{
	// NPCs
	private static final int TARTI_FRIEND = 34365;
	// Monster
	// private static final int HATCHLING = 24089;
	private static final int GEM_DRAGON_ANTHARAS = 24091;
	private static final int ANTHARAS = 24087;
	private static final int ANTHARAS_TRANSFORM = 24088;
	// Skills
	private static final SkillHolder HydroAttackSkill = new SkillHolder(32131, 1);
	// private static final SkillHolder AirRushSkill = new SkillHolder(32132, 1);
	// Misc
	private static final int p_CheckInterval = 3000;
	private static final int p_CheckFirstAntharasInterval = 18000;
	private static final int p_CheckAntharasInterval = 3000;
	private static final int p_CheckFirstTransInterval = 13000;
	private static final int p_CheckTransInterval = 3000;
	private static final int p_TalkInterval = 15000;
	private static boolean _firstAttacked;
	private static boolean _firstAntharasTalk;
	
	public TartiFriend()
	{
		addFirstTalkId(TARTI_FRIEND);
		addSpawnId(TARTI_FRIEND, ANTHARAS, ANTHARAS_TRANSFORM);
		addKillId(GEM_DRAGON_ANTHARAS);
	}
	
	@Override
	public final String onFirstTalk(Npc npc, Player player)
	{
		return "tarti_friend_tu001.htm";
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		final Instance instance = npc.getInstanceWorld();
		
		switch (npc.getId())
		{
			case TARTI_FRIEND:
			{
				ThreadPool.schedule(() ->
				{
					final StatSet npcVars = npc.getVariables();
					npc.setInvul(true);
					_firstAttacked = true;
					if (instance.getTemplateId() == 271)
					{
						_firstAntharasTalk = true;
					}
					
					getTimers().addTimer("p_CheckTimer", p_CheckInterval, npc, null);
					if ((instance.getTemplateId() == 269) || (instance.getTemplateId() == 270))
					{
						getTimers().addTimer("p_TalkTimer", p_TalkInterval, npc, null);
					}
					
					final Player player = instance.getFirstPlayer();
					if (player != null)
					{
						npcVars.set("PLAYER_OBJECT", player);
					}
					
				}, 3000); // 3 Sec
				break;
			}
			case ANTHARAS:
			{
				final Npc tarti = instance.getNpc(TARTI_FRIEND);
				getTimers().cancelTimer("p_CheckTimer", tarti, null);
				getTimers().addTimer("p_CheckAntharasTimer", p_CheckFirstAntharasInterval, tarti, null);
				break;
			}
			case ANTHARAS_TRANSFORM:
			{
				final Npc tarti = instance.getNpc(TARTI_FRIEND);
				getTimers().cancelTimer("p_CheckAntharasTimer", tarti, null);
				getTimers().addTimer("p_CheckTransTimer", p_CheckFirstTransInterval, tarti, null);
				break;
			}
		}
		return super.onSpawn(npc);
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
					final StatSet npcVars = npc.getVariables();
					final Player target = npcVars.getObject("PLAYER_OBJECT", Player.class);
					if (target != null)
					{
						final double distance = npc.calculateDistance2D(target);
						if (distance > 200)
						{
							final Location loc = new Location(target.getX(), target.getY(), target.getZ() + 50);
							final Location randLoc = new Location(loc.getX() + getRandom(-10, 50), loc.getY() + getRandom(-100, 100), loc.getZ());
							if (distance > 800)
							{
								npc.teleToLocation(loc);
							}
							else
							{
								npc.setRunning();
							}
							addMoveToDesire(npc, randLoc, 23);
						}
					}
				}
			}
		}
		else if ((instance != null) && event.equals("p_TalkTimer"))
		{
			synchronized (npc)
			{
				if (!npc.isDead() && !npc.isDecayed())
				{
					getTimers().addTimer("p_TalkTimer", p_TalkInterval, npc, null);
					switch (instance.getTemplateId())
					{
						case 269:
						{
							switch (Rnd.get(3))
							{
								case 1:
								{
									instance.broadcastPacket(new PlaySound(3, "Npcdialog1.tarti_ep50_battle_1", 0, 0, 0, 0, 0));
									npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.WHO_LL_BE_THE_MOST_SURPRISED_IF_WE_BECOME_THE_BALTHUS_KNIGHTS);
									break;
								}
								case 2:
								{
									instance.broadcastPacket(new PlaySound(3, "Npcdialog1.tarti_ep50_battle_2", 0, 0, 0, 0, 0));
									npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.YOU_THINK_THAT_WILL_SCARE_ME);
									break;
								}
								case 3:
								{
									instance.broadcastPacket(new PlaySound(3, "Npcdialog1.tarti_ep50_battle_3", 0, 0, 0, 0, 0));
									npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.I_LL_CATCH_ANTHARAS_I_WON_T_GIVE_HIM_UP_NOT_EVEN_TO_YOU);
									break;
								}
							}
							break;
						}
						case 270:
						{
							switch (Rnd.get(2))
							{
								case 1:
								{
									instance.broadcastPacket(new PlaySound(3, "Npcdialog1.tarti_ep50_battle_4", 0, 0, 0, 0, 0));
									npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.YOU_LOOK_JUST_LIKE_THAT_HATCHLING_I_MET);
									break;
								}
								case 2:
								{
									instance.broadcastPacket(new PlaySound(3, "Npcdialog1.tarti_ep50_battle_5", 0, 0, 0, 0, 0));
									npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.YOU_RE_NOT_TRYING_TO_GET_TO_ANTHARAS_WITHOUT_US_ARE_YOU);
									break;
								}
							}
							break;
						}
					}
				}
			}
		}
		else if ((instance != null) && event.equals("p_CheckAntharasTimer"))
		{
			synchronized (npc)
			{
				if (!npc.isDead() && !npc.isDecayed())
				{
					getTimers().addTimer("p_CheckAntharasTimer", p_CheckAntharasInterval, npc, null);
					final Npc antharas = instance.getNpc(ANTHARAS);
					if (antharas != null)
					{
						npc.setTarget(antharas);
						npc.setRunning();
						addMoveToDesire(npc, npc.getTarget().getLocation(), 23);
						addSkillCastDesire(npc, antharas, HydroAttackSkill, 10000);
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
					if (_firstAntharasTalk)
					{
						instance.broadcastPacket(new PlaySound(3, "Npcdialog1.tarti_ep50_battle_7", 0, 0, 0, 0, 0));
						npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.I_WASN_T_AWARE_THAT_ANTHARAS_COULD_CHANGE_FORMS);
						_firstAntharasTalk = false;
					}
					final Npc antharas_trans = instance.getNpc(ANTHARAS_TRANSFORM);
					if (antharas_trans != null)
					{
						npc.setTarget(antharas_trans);
						npc.setRunning();
						addMoveToDesire(npc, npc.getTarget().getLocation(), 23);
						addSkillCastDesire(npc, antharas_trans, HydroAttackSkill, 10000);
					}
				}
			}
		}
	}
	
	public void onCreatureDamageReceived(OnCreatureDamageReceived event)
	{
		final Npc npc = event.getTarget().asNpc();
		final Instance instance = npc.getInstanceWorld();
		final Npc tarti = instance.getNpc(TARTI_FRIEND);
		
		if (event.getAttacker().isPlayer())
		{
			switch (instance.getTemplateId())
			{
				case 269:
				case 270:
				{
					if (tarti != null)
					{
						tarti.setTarget(npc);
						tarti.setRunning();
						addMoveToDesire(tarti, tarti.getTarget().getLocation(), 23);
						addSkillCastDesire(tarti, npc, HydroAttackSkill, 10000);
						break;
					}
					break;
				}
				case 271:
				{
					if ((tarti != null) && (!instance.isStatus(3)))
					{
						tarti.setTarget(npc);
						tarti.setRunning();
						if (tarti.getTarget() != null)
						{
							addMoveToDesire(tarti, tarti.getTarget().getLocation(), 23);
						}
						addSkillCastDesire(tarti, npc, HydroAttackSkill, 10000);
						if (_firstAttacked)
						{
							instance.broadcastPacket(new PlaySound(3, "Npcdialog1.tarti_ep50_battle_6", 0, 0, 0, 0, 0));
							npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.I_LL_PROTECT_YOU);
							_firstAttacked = false;
						}
						break;
					}
					break;
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
				if ((instance != null) && (!_firstAttacked))
				{
					_firstAttacked = true;
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	public static void main(String[] args)
	{
		new TartiFriend();
	}
}
