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
package quests.Q10552_ChallengeBalthusKnight;

import org.l2jmobius.gameserver.instancemanager.QuestManager;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.ListenerRegisterType;
import org.l2jmobius.gameserver.model.events.annotations.Id;
import org.l2jmobius.gameserver.model.events.annotations.RegisterEvent;
import org.l2jmobius.gameserver.model.events.annotations.RegisterType;
import org.l2jmobius.gameserver.model.events.impl.creature.npc.OnNpcMenuSelect;
import org.l2jmobius.gameserver.model.holders.SkillHolder;
import org.l2jmobius.gameserver.model.instancezone.Instance;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.network.NpcStringId;
import org.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2jmobius.gameserver.network.serverpackets.ExTutorialShowId;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

import instances.BalthusKnights.HatchlingNest;

/**
 * Challenge! Balthus Knight! (10552)
 * @author Kazumi
 */
public final class Q10552_ChallengeBalthusKnight extends Quest
{
	// NPCs
	private static final int TARTI = 34359;
	private static final int STIG = 34361;
	// Items
	private static final int HATCHLING_SCALE = 48192;
	// Monster
	private static final int HATCHLING = 24089;
	// Skills
	private static final SkillHolder SKILL_BALTHUS_KNIGHT_MEMBER = new SkillHolder(32130, 1);
	// Misc
	private static final int ITEM_COUNT = 3;
	// TODO: Adding for retail way.
	// private static boolean _firstDrop = true;
	
	public Q10552_ChallengeBalthusKnight()
	{
		super(10552);
		addStartNpc(TARTI);
		addTalkId(TARTI, STIG);
		addKillId(HATCHLING);
		registerQuestItems(HATCHLING_SCALE);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		String htmltext = null;
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		
		if (event.equals("quest_accept"))
		{
			qs.startQuest();
			final ServerPacket packet = new ExTutorialShowId(13);
			player.sendPacket(packet);
			// TODO: Adding for retail way.
			// _firstDrop = true;
			final Quest instance = QuestManager.getInstance().getQuest(HatchlingNest.class.getSimpleName());
			if (instance != null)
			{
				instance.onEvent("enterInstance", npc, player);
			}
			htmltext = "tarti_balthus_q10552_03.htm";
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		switch (qs.getState())
		{
			case State.CREATED:
			{
				if (npc.getId() == TARTI)
				{
					htmltext = "tarti_balthus_q10552_01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				if (npc.getId() == TARTI)
				{
					switch (qs.getCond())
					{
						case 1:
						{
							// TODO: Adding for retail way.
							// _firstDrop = true;
							takeItems(player, HATCHLING_SCALE, -1);
							final Quest instance = QuestManager.getInstance().getQuest(HatchlingNest.class.getSimpleName());
							if (instance != null)
							{
								instance.onEvent("enterInstance", npc, player);
							}
							htmltext = "tarti_balthus_q10552_04.htm";
							break;
						}
						case 5:
						{
							htmltext = "tarti_balthus_q10552_05.htm";
							break;
						}
					}
				}
				else if (npc.getId() == STIG)
				{
					switch (qs.getCond())
					{
						case 1:
						{
							htmltext = "stig_q10552_01.htm";
							break;
						}
						case 5:
						{
							htmltext = "stig_q10552_02.htm";
							break;
						}
					}
				}
				break;
			}
			case State.COMPLETED:
				htmltext = getAlreadyCompletedMsg(player);
				break;
		}
		return htmltext;
	}
	
	@RegisterEvent(EventType.ON_NPC_MENU_SELECT)
	@RegisterType(ListenerRegisterType.NPC)
	@Id(TARTI)
	@Id(STIG)
	public final void onNpcMenuSelect(OnNpcMenuSelect event)
	{
		final Player player = event.getTalker();
		final QuestState qs = getQuestState(player, false);
		// final Npc npc = event.getNpc();
		final int ask = event.getAsk();
		final int reply = event.getReply();
		
		if (ask == 10552)
		{
			switch (reply)
			{
				case 1:
				{
					showHtmlFile(player, "tarti_balthus_q10552_02.htm");
					break;
				}
				case 10:
				{
					if (qs.getCond() == 5)
					{
						qs.exitQuest(false, true);
						showOnScreenMsg(player, NpcStringId.TALK_TO_BALTHUS_KNIGHT_CAPTAIN_STIG_MACH_AGAIN, ExShowScreenMessage.TOP_CENTER, 10000, false);
						player.addExpAndSp(414_745_398L, 50_000L);
						player.setTarget(player);
						player.doCast(SKILL_BALTHUS_KNIGHT_MEMBER.getSkill());
						showHtmlFile(player, "stig_q10552_03.htm");
					}
					break;
				}
			}
		}
	}
	
	@Override
	public String onKill(Npc npc, Player player, boolean isSummon)
	{
		final QuestState qs = getQuestState(player, true);
		if ((qs != null) && qs.isCond(1))
		{
			giveItemRandomly(player, npc, HATCHLING_SCALE, 1, ITEM_COUNT, 0.5, true);
			if ((getQuestItemsCount(player, HATCHLING_SCALE) >= ITEM_COUNT))
			{
				final Instance instance = player.getInstanceWorld();
				instance.finishInstance(0);
				final ServerPacket packet = new ExTutorialShowId(11);
				player.sendPacket(packet);
				player.getVariables().set(PlayerVariables.BALTHUS_PHASE, 2);
				showOnScreenMsg(player, NpcStringId.GO_UP_THE_STAIRS_TO_THE_2ND_FLOOR_AND_NTALK_TO_BALTHUS_KNIGHT_CAPTAIN_STIG_MACH, ExShowScreenMessage.TOP_CENTER, 10000, false);
				qs.setCond(5, true);
			}
		}
		return super.onKill(npc, player, isSummon);
	}
}