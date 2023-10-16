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
package quests.Q10309_DreamlandsMysteries;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.holders.ItemHolder;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.network.NpcStringId;
import org.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;

/**
 * @author Serenitty
 */
public class Q10309_DreamlandsMysteries extends Quest
{
	// NPC
	private static final int DREAM_PRIESTESS = 34304;
	// Monsters
	private static final int[] MONSTERS =
	{
		18678,
		18679,
		18682,
		18683,
		18683,
		18684,
		18685,
	};
	// Items
	private static final ItemHolder BOOST_ATK_SCROLL = new ItemHolder(94269, 10);
	private static final ItemHolder BOOST_DEF_SCROLL = new ItemHolder(94271, 10);
	private static final ItemHolder BERSERKER_SCROLL = new ItemHolder(94777, 10);
	// Misc
	private static final int MIN_LEVEL = 76;
	
	public Q10309_DreamlandsMysteries()
	{
		super(10309);
		addStartNpc(DREAM_PRIESTESS);
		addTalkId(DREAM_PRIESTESS);
		addKillId(MONSTERS);
		addCondMinLevel(MIN_LEVEL, "34304-06.htm");
		setQuestNameNpcStringId(NpcStringId.LV_76_DREAMLAND_S_MYSTERIES);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		
		String htmltext = null;
		switch (event)
		{
			case "34304-01.htm":
			case "34304-02.htm":
			case "34304-03.htm":
			case "34304-05.htm":
			{
				htmltext = event;
				break;
			}
			case "34304-04.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "34304-08.html":
			{
				if (qs.isStarted())
				{
					addExpAndSp(player, 100000000, 27000000);
					giveItems(player, BOOST_ATK_SCROLL);
					giveItems(player, BOOST_DEF_SCROLL);
					giveItems(player, BERSERKER_SCROLL);
					qs.exitQuest(false, true);
				}
				htmltext = event;
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
		if (qs.isCreated())
		{
			htmltext = "34304-01.htm";
		}
		else if (qs.isStarted())
		{
			if (qs.isCond(1))
			{
				htmltext = "34304-07.htm";
			}
			else if (qs.isCond(2))
			{
				htmltext = "34304-05.htm";
			}
		}
		else if (qs.isCompleted())
		{
			htmltext = getAlreadyCompletedMsg(player);
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isCond(1))
		{
			qs.setCond(2, true);
			showOnScreenMsg(killer, NpcStringId.LV_76_DREAMLAND_S_MYSTERIES_COMPLETED, ExShowScreenMessage.TOP_CENTER, 10000);
		}
		return super.onKill(npc, killer, isSummon);
	}
}
