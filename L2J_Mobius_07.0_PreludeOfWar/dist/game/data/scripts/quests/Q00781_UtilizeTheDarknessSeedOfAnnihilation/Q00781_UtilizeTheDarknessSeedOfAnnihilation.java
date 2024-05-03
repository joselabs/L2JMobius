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
package quests.Q00781_UtilizeTheDarknessSeedOfAnnihilation;

import org.l2jmobius.gameserver.model.Party;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

/**
 * Utilize the Darkness - Seed of Annihilation (781)
 * @author Kazumi
 */
public class Q00781_UtilizeTheDarknessSeedOfAnnihilation extends Quest
{
	// NPCs
	private static final int KLEMIS = 32734;
	private static final int[] MONSTER_LIST =
	{
		// Seed of Annihilation - Bistakon
		22746, // Bgurent
		22747, // Brakian
		22748, // Groikan
		22749, // Treykan
		22750, // Elite Bgurent
		22751, // Elite Brakian
		22752, // Elite Groikan
		22753, // Elite Treykan
		// Seed of Annihilation - Reptilikon
		22754, // Turtlelian
		22755, // Krajian
		22756, // Tardyon
		22757, // Elite Turtlelian
		22758, // Elite Krajian
		22759, // Elite Tardyon
		// Seed of Annihilation - Kokracon
		22760, // Kanibi
		22761, // Kiriona
		22762, // Kaiona
		22763, // Elite Kanibi
		22764, // Elite Kiriona
		22765, // Elite Kaiona
	
	};
	// Items
	private static final int SOUL_STONE_DUST = 15536;
	private static final int SOUL_STONE_FRAGMENT = 15486;
	
	// Misc
	private static final int MIN_LEVEL = 85;
	private static final int MIN_COUNT = 50;
	private static final int MAX_COUNT = 500;
	
	public Q00781_UtilizeTheDarknessSeedOfAnnihilation()
	{
		super(781);
		addStartNpc(KLEMIS);
		addTalkId(KLEMIS);
		addKillId(MONSTER_LIST);
		registerQuestItems(SOUL_STONE_DUST);
		addCondMinLevel(MIN_LEVEL, "clemis_q0781_02.htm");
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
			case "clemis_q0781_03.htm":
			case "clemis_q0781_09.htm":
			{
				htmltext = event;
				break;
			}
			case "clemis_q0781_04.htm":
			{
				qs.startQuest();
				break;
			}
			case "clemis_q0781_08.htm":
			{
				if (qs.isCond(2) || qs.isCond(3))
				{
					if (player.getLevel() >= MIN_LEVEL)
					{
						final long itemCount = getQuestItemsCount(player, SOUL_STONE_DUST);
						takeItems(player, SOUL_STONE_DUST, itemCount);
						giveItems(player, SOUL_STONE_FRAGMENT, itemCount / 5);
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
				htmltext = "clemis_q0781_01.htm";
				break;
			}
			case State.STARTED:
			{
				switch (qs.getCond())
				{
					case 1:
					{
						htmltext = "clemis_q0781_05.htm";
						break;
					}
					case 2:
					{
						htmltext = "clemis_q0781_06.htm";
						break;
					}
					case 3:
					{
						htmltext = "clemis_q0781_07.htm";
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
				giveItemRandomly(killer, npc, SOUL_STONE_DUST, 1, MAX_COUNT, 0.25, true);
				if ((getQuestItemsCount(killer, SOUL_STONE_DUST) >= MIN_COUNT) && qs.isCond(1))
				{
					qs.setCond(2, true);
				}
				if ((getQuestItemsCount(killer, SOUL_STONE_DUST) == MAX_COUNT) && qs.isCond(2))
				{
					qs.setCond(3, true);
				}
			}
		}
	}
}
