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
package quests.Q00782_UtilizeTheDarknessSeedOfHellfire;

import org.l2jmobius.gameserver.model.Party;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

/**
 * Utilize the Darkness - Seed of Hellfire (782)
 * @author Kazumi
 */
public class Q00782_UtilizeTheDarknessSeedOfHellfire extends Quest
{
	// NPCs
	private static final int SIZRAK = 33669;
	private static final int[] MONSTER_LIST =
	{
		23213, // Beggar Zofan
		23214, // Beggar Zofan
		23215, // Zofan
		23216, // Zofan
		23217, // Young Zofan
		23218, // Young Zofan
		23219, // Engineer Zofan
		23220, // Kunda Watchman
		23221, // Adac the Engineer
		23222, // Borok the Engineer
		23223, // Koja the Engineer
		23224, // Kunda Guardian
		23225, // Kunda Berserker
		23226, // Kunda Executor
		23227, // Beggar Zofan
		23228, // Beggar Zofan
		23229, // Zofan
		23230, // Zofan
		23231, // Young Zofan
		23232, // Young Zofan
		23233, // Engineer Zofan
		23234, // Engineer Zofan
		23235, // Kunda
		23236, // Kunda
		23237, // Engineer Zofan
	};
	// Items
	private static final int DISABLED_PETRA = 34976;
	private static final int PETRA = 34959;
	
	// Misc
	private static final int MIN_LEVEL = 97;
	private static final int MIN_COUNT = 50;
	private static final int MAX_COUNT = 500;
	
	public Q00782_UtilizeTheDarknessSeedOfHellfire()
	{
		super(782);
		addStartNpc(SIZRAK);
		addTalkId(SIZRAK);
		addKillId(MONSTER_LIST);
		registerQuestItems(DISABLED_PETRA);
		addCondMinLevel(MIN_LEVEL, "sofa_sizraku_q0782_02.htm");
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
			case "sofa_sizraku_q0782_03.htm":
			case "sofa_sizraku_q0782_09.htm":
			{
				htmltext = event;
				break;
			}
			case "sofa_sizraku_q0782_04.htm":
			{
				qs.startQuest();
				break;
			}
			case "sofa_sizraku_q0782_08.htm":
			{
				if (qs.isCond(2) || qs.isCond(3))
				{
					if (player.getLevel() >= MIN_LEVEL)
					{
						final long itemCount = getQuestItemsCount(player, DISABLED_PETRA);
						takeItems(player, DISABLED_PETRA, itemCount);
						giveItems(player, PETRA, itemCount / 5);
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
				htmltext = "sofa_sizraku_q0782_01.htm";
				break;
			}
			case State.STARTED:
			{
				switch (qs.getCond())
				{
					case 1:
					{
						htmltext = "sofa_sizraku_q0782_05.htm";
						break;
					}
					case 2:
					{
						htmltext = "sofa_sizraku_q0782_06.htm";
						break;
					}
					case 3:
					{
						htmltext = "sofa_sizraku_q0782_07.htm";
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
				giveItemRandomly(killer, npc, DISABLED_PETRA, 1, MAX_COUNT, 0.25, true);
				if ((getQuestItemsCount(killer, DISABLED_PETRA) >= MIN_COUNT) && qs.isCond(1))
				{
					qs.setCond(2, true);
				}
				if ((getQuestItemsCount(killer, DISABLED_PETRA) == MAX_COUNT) && qs.isCond(2))
				{
					qs.setCond(3, true);
				}
			}
		}
	}
}
