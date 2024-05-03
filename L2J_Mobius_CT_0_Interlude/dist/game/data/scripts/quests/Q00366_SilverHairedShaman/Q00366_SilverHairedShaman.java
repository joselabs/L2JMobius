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
package quests.Q00366_SilverHairedShaman;

import java.util.HashMap;
import java.util.Map;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00366_SilverHairedShaman extends Quest
{
	// NPC
	private static final int DIETER = 30111;
	// Item
	private static final int HAIR = 5874;
	// Drop chances
	private static final Map<Integer, Integer> CHANCES = new HashMap<>();
	static
	{
		CHANCES.put(20986, 560000);
		CHANCES.put(20987, 660000);
		CHANCES.put(20988, 620000);
	}
	
	public Q00366_SilverHairedShaman()
	{
		super(366);
		registerQuestItems(HAIR);
		addStartNpc(DIETER);
		addTalkId(DIETER);
		addKillId(20986, 20987, 20988);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		final QuestState st = getQuestState(player, false);
		if (st == null)
		{
			return htmltext;
		}
		
		if (event.equals("30111-2.htm"))
		{
			st.startQuest();
		}
		else if (event.equals("30111-6.htm"))
		{
			st.exitQuest(true, true);
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		
		switch (st.getState())
		{
			case State.CREATED:
			{
				htmltext = (player.getLevel() < 48) ? "30111-0.htm" : "30111-1.htm";
				break;
			}
			case State.STARTED:
			{
				final int count = getQuestItemsCount(player, HAIR);
				if (count == 0)
				{
					htmltext = "30111-3.htm";
				}
				else
				{
					htmltext = "30111-4.htm";
					takeItems(player, HAIR, -1);
					giveAdena(player, 12070 + (500 * count), true);
				}
				break;
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player player, boolean isSummon)
	{
		if (getRandom(1000000) < CHANCES.get(npc.getId()))
		{
			final Player luckyPlayer = getRandomPartyMember(player, npc);
			if (luckyPlayer != null)
			{
				giveItemRandomly(luckyPlayer, npc, HAIR, 1, 0, 1, true);
			}
		}
		return super.onKill(npc, player, isSummon);
	}
}
