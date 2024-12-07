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
package quests.Q10517_FafurionsMinions;

import java.util.HashSet;
import java.util.Set;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.enums.QuestType;
import org.l2jmobius.gameserver.model.Party;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.ListenerRegisterType;
import org.l2jmobius.gameserver.model.events.annotations.Id;
import org.l2jmobius.gameserver.model.events.annotations.RegisterEvent;
import org.l2jmobius.gameserver.model.events.annotations.RegisterType;
import org.l2jmobius.gameserver.model.events.impl.creature.npc.OnNpcMenuSelect;
import org.l2jmobius.gameserver.model.events.impl.creature.player.OnPlayerLogin;
import org.l2jmobius.gameserver.model.holders.NpcLogListHolder;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

import quests.Q10516_UnveiledFafurionTemple.Q10516_UnveiledFafurionTemple;

/**
 * Fafurion's Minions (10517)
 * @author Kazumi
 */
public final class Q10517_FafurionsMinions extends Quest
{
	// NPCs
	private static final int LUPICIA = 34489;
	// MOBs
	private static final int[] FF_MINIONS =
	{
		24318, // Temple Guard Captain
		24319, // Elite Guardian Warrior
		24320, // Elite Guardian Archer
		24321, // Temple Patrol Guard
		24322, // Temple Knight Recruit
		24323, // Temple Guard
		24324, // Temple Guardian Warrior
		24325, // Temple Wizard
		24326, // Temple Guardian Wizard
		24327, // Temple Priest
		24328, // Temple Guardian Priest
		24329, // Starving Water Dragon
	};
	// Item
	private static final int MINIONS_SYMBOL = 80323;
	private static final int SUPERIOR_CODEX_CHAPTER_1 = 46150;
	// Misc
	private static final int MIN_LEVEL = 110;
	private static final int REQUIRED_DROP_COUNT = 10;
	private static final int REQUIRED_KILL_COUNT = 250;
	private static final String KILL_COUNT_VAR = "KillCount_10517";
	
	public Q10517_FafurionsMinions()
	{
		super(10517);
		addStartNpc(LUPICIA);
		addTalkId(LUPICIA);
		addKillId(FF_MINIONS);
		registerQuestItems(MINIONS_SYMBOL);
		addCondMinLevel(MIN_LEVEL, "rupicia_q10517_02.htm");
		addCondCompletedQuest(Q10516_UnveiledFafurionTemple.class.getSimpleName(), "rupicia_q10517_02.htm");
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
			htmltext = "rupicia_q10517_05.htm";
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
				if (player.getLevel() >= MIN_LEVEL)
				{
					htmltext = "rupicia_q10517_01.htm";
					break;
				}
				break;
			}
			case State.STARTED:
			{
				if (qs.getCond() == 1)
				{
					htmltext = "rupicia_q10517_06.htm";
					break;
				}
				htmltext = "rupicia_q10517_07.htm";
				break;
			}
			case State.COMPLETED:
			{
				htmltext = getAlreadyCompletedMsg(player);
				break;
			}
		}
		return htmltext;
	}
	
	@RegisterEvent(EventType.ON_NPC_MENU_SELECT)
	@RegisterType(ListenerRegisterType.NPC)
	@Id(LUPICIA)
	public final void onNpcMenuSelect(OnNpcMenuSelect event)
	{
		final Player player = event.getTalker();
		final QuestState qs = getQuestState(player, false);
		final Npc npc = event.getNpc();
		final int ask = event.getAsk();
		final int reply = event.getReply();
		
		if (ask == 10517)
		{
			switch (reply)
			{
				case 1:
				{
					showHtmlFile(player, "rupicia_q10517_03.htm", npc);
					break;
				}
				case 2:
				{
					showHtmlFile(player, "rupicia_q10517_04.htm", npc);
					break;
				}
				case 10:
				{
					if (player.getLevel() >= MIN_LEVEL)
					{
						if (qs.isCond(2))
						{
							qs.exitQuest(QuestType.ONE_TIME, true);
							giveItems(player, SUPERIOR_CODEX_CHAPTER_1, 1);
							addExpAndSp(player, 333371214000L, 333371160);
							showHtmlFile(player, "rupicia_q10517_08.htm", npc);
							break;
						}
						break;
					}
					getNoQuestLevelRewardMsg(player);
					break;
				}
			}
		}
	}
	
	@Override
	public String onKill(Npc npc, Player player, boolean isSummon)
	{
		final Party party = player.getParty();
		if (party != null)
		{
			party.getMembers().forEach(p -> onKill(npc, p));
		}
		else
		{
			onKill(npc, player);
		}
		return super.onKill(npc, player, isSummon);
	}
	
	public void onKill(Npc npc, Player player)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isCond(1))
		{
			giveItemRandomly(player, npc, MINIONS_SYMBOL, 1, REQUIRED_DROP_COUNT, 0.2, true);
			
			final int killCount = qs.getInt(KILL_COUNT_VAR) + 1;
			if (killCount < REQUIRED_KILL_COUNT)
			{
				qs.set(KILL_COUNT_VAR, killCount);
				playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
			
			if ((killCount >= REQUIRED_KILL_COUNT) && (getQuestItemsCount(player, MINIONS_SYMBOL) >= REQUIRED_DROP_COUNT))
			{
				qs.setCond(2, true);
			}
			
			sendNpcLogList(player);
		}
	}
	
	@Override
	public Set<NpcLogListHolder> getNpcLogList(Player player)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isCond(1))
		{
			final Set<NpcLogListHolder> holder = new HashSet<>();
			holder.add(new NpcLogListHolder(551710, true, qs.getInt(KILL_COUNT_VAR))); // Defeat Fafurion's Kin
			holder.add(new NpcLogListHolder(80323, false, (int) getQuestItemsCount(player, MINIONS_SYMBOL))); // Minion's Symbol
			return holder;
		}
		return super.getNpcLogList(player);
	}
	
	@RegisterEvent(EventType.ON_PLAYER_LOGIN)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public final void onLogin(OnPlayerLogin evt)
	{
		final Player player = evt.getPlayer();
		sendNpcLogList(player);
	}
}
