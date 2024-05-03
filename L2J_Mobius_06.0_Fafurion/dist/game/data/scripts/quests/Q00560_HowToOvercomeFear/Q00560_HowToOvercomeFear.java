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
package quests.Q00560_HowToOvercomeFear;

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
import org.l2jmobius.gameserver.model.itemcontainer.Inventory;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

import quests.Q10517_FafurionsMinions.Q10517_FafurionsMinions;

/**
 * How to Overcome Fear (560)
 * @author Kazumi
 */
public final class Q00560_HowToOvercomeFear extends Quest
{
	// NPC
	private static final int LUPICIA = 34489;
	// Monsters
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
	private static final int FRAGMENT_OF_POWER = 80324;
	// Misc
	private static final int MIN_LEVEL = 110;
	private static final int REQUIRED_DROP_COUNT = 30;
	
	public Q00560_HowToOvercomeFear()
	{
		super(560);
		addStartNpc(LUPICIA);
		addTalkId(LUPICIA);
		addKillId(FF_MINIONS);
		registerQuestItems(FRAGMENT_OF_POWER);
		addCondMinLevel(MIN_LEVEL, "rupicia_q0560_02.htm");
		addCondCompletedQuest(Q10517_FafurionsMinions.class.getSimpleName(), "rupicia_q0560_02.htm");
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
		
		switch (event)
		{
			case "quest_accept":
			{
				qs.startQuest();
				htmltext = "rupicia_q0560_05.htm";
				break;
			}
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
			case State.COMPLETED:
			{
				if (!qs.isNowAvailable())
				{
					htmltext = getAlreadyCompletedMsg(player);
					break;
				}
				qs.setState(State.CREATED);
				// fallthrou
			}
			case State.CREATED:
			{
				if (player.getLevel() >= MIN_LEVEL)
				{
					htmltext = "rupicia_q0560_01.htm";
					break;
				}
				break;
			}
			case State.STARTED:
			{
				if (qs.getCond() == 1)
				{
					htmltext = "rupicia_q0560_06.htm";
					break;
				}
				htmltext = "rupicia_q0560_07.htm";
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
		
		if (ask == 560)
		{
			switch (reply)
			{
				case 1:
				{
					showHtmlFile(player, "rupicia_q0560_03.htm", npc);
					break;
				}
				case 2:
				{
					showHtmlFile(player, "rupicia_q0560_04.htm", npc);
					break;
				}
				case 10:
				{
					if (player.getLevel() >= MIN_LEVEL)
					{
						if (qs.isCond(2))
						{
							qs.exitQuest(QuestType.DAILY, true);
							giveItems(player, Inventory.ADENA_ID, 4_190_158L);
							addExpAndSp(player, 166685607000L, 166685580);
							showHtmlFile(player, "rupicia_q0560_08.htm", npc);
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
			giveItemRandomly(player, npc, FRAGMENT_OF_POWER, 1, REQUIRED_DROP_COUNT, 0.2, true);
			if (getQuestItemsCount(player, FRAGMENT_OF_POWER) >= REQUIRED_DROP_COUNT)
			{
				qs.setCond(2, true);
			}
		}
	}
}