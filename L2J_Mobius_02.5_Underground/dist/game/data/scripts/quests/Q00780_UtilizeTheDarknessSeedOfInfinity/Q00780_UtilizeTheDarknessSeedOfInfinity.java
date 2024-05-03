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
package quests.Q00780_UtilizeTheDarknessSeedOfInfinity;

import org.l2jmobius.gameserver.model.Party;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

/**
 * Utilize the Darkness - Seed of Infinity (780)
 * @author Kazumi
 */
public class Q00780_UtilizeTheDarknessSeedOfInfinity extends Quest
{
	// NPCs
	private static final int TEPIOS = 32530;
	private static final int[] MONSTER_LIST =
	{
		23405, // Suffering Zealot
		23406, // Suffering Mutant
		23407, // Suffering Hacker
		23409, // Erosion Herald
		23411, // Erosion Mutant
		23412, // Erosion Hacker
		23413, // Erosion Ark
	};
	// Items
	private static final int MARRED_SOUL_CRYSTAL = 38580;
	private static final int FREED_SOUL_CRYSTAL = 38576;
	
	// Misc
	private static final int MIN_LEVEL = 95;
	private static final int MIN_COUNT = 50;
	private static final int MAX_COUNT = 500;
	
	public Q00780_UtilizeTheDarknessSeedOfInfinity()
	{
		super(780);
		addStartNpc(TEPIOS);
		addTalkId(TEPIOS);
		addKillId(MONSTER_LIST);
		registerQuestItems(MARRED_SOUL_CRYSTAL);
		addCondMinLevel(MIN_LEVEL, "officer_tepios_q0780_02.htm");
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		
		String htmltext = event;
		
		switch (event)
		{
			case "officer_tepios_q0780_03.htm":
			case "officer_tepios_q0780_09.htm":
			{
				htmltext = event;
				break;
			}
			case "officer_tepios_q0780_04.htm":
			{
				qs.startQuest();
				break;
			}
			case "officer_tepios_q0780_08.htm":
			{
				if (qs.isCond(2) || qs.isCond(3))
				{
					if (player.getLevel() >= MIN_LEVEL)
					{
						final long itemCount = getQuestItemsCount(player, MARRED_SOUL_CRYSTAL);
						takeItems(player, MARRED_SOUL_CRYSTAL, itemCount);
						giveItems(player, FREED_SOUL_CRYSTAL, itemCount / 5);
						addExpAndSp(player, 1637472704, 0);
						qs.exitQuest(true, true);
						break;
					}
					htmltext = getNoQuestLevelRewardMsg(player);
					break;
				}
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
			case State.CREATED:
			{
				htmltext = "officer_tepios_q0780_01.htm";
				break;
			}
			case State.STARTED:
			{
				switch (qs.getCond())
				{
					case 1:
					{
						htmltext = "officer_tepios_q0780_05.htm";
						break;
					}
					case 2:
					{
						htmltext = "officer_tepios_q0780_06.htm";
						break;
					}
					case 3:
					{
						htmltext = "officer_tepios_q0780_07.htm";
						break;
					}
				}
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
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		final Party party = killer.getParty();
		if (party != null)
		{
			party.getMembers().forEach(p -> onKill(npc, p));
		}
		else
		{
			onKill(npc, killer);
		}
		
		return super.onKill(npc, killer, isSummon);
	}
	
	public void onKill(Npc npc, Player killer)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && (npc.calculateDistance3D(killer) <= 1000))
		{
			if (qs.isCond(1) || qs.isCond(2))
			{
				giveItemRandomly(killer, npc, MARRED_SOUL_CRYSTAL, 1, MAX_COUNT, 0.25, true);
				if ((getQuestItemsCount(killer, MARRED_SOUL_CRYSTAL) >= MIN_COUNT) && qs.isCond(1))
				{
					qs.setCond(2, true);
				}
				if ((getQuestItemsCount(killer, MARRED_SOUL_CRYSTAL) == MAX_COUNT) && qs.isCond(2))
				{
					qs.setCond(3, true);
				}
			}
		}
	}
}
