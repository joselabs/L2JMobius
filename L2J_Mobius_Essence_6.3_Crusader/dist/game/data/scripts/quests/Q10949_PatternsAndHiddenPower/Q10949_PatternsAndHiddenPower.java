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
package quests.Q10949_PatternsAndHiddenPower;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.holders.ItemHolder;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.network.NpcStringId;

/**
 * @author Serenitty
 */
public class Q10949_PatternsAndHiddenPower extends Quest
{
	// NPC
	private static final int ORVEN = 30857;
	// Items
	private static final ItemHolder ADVENTURE_DYE = new ItemHolder(97878, 1);
	private static final ItemHolder ADVENTURE_DYE_POWDER = new ItemHolder(97982, 1);
	// Misc
	private static final int MIN_LEVEL = 40;
	
	public Q10949_PatternsAndHiddenPower()
	{
		super(10949);
		addStartNpc(ORVEN);
		addTalkId(ORVEN);
		addCondMinLevel(MIN_LEVEL, "no_lvl.html");
		setQuestNameNpcStringId(NpcStringId.LV_40_PATTERNS_AND_HIDDEN_POWER);
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
			case "30857.htm":
			case "30857-01.htm":
			case "30857-02.htm":
			case "30857-03.htm":
			case "30857-04.htm":
			case "30857-05.htm":
			{
				htmltext = event;
				break;
			}
			case "StartPatterns":
			{
				qs.startQuest();
				htmltext = "30857-03.htm";
				break;
			}
			case "takeReward":
			{
				if (qs.isStarted())
				{
					giveItems(player, ADVENTURE_DYE);
					giveItems(player, ADVENTURE_DYE_POWDER);
					qs.exitQuest(false, true);
					htmltext = "30857-05.htm";
					break;
				}
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
			htmltext = "30857.htm";
		}
		else if (qs.isStarted())
		{
			htmltext = "30857-04.htm";
		}
		else if (qs.isCompleted())
		{
			htmltext = getAlreadyCompletedMsg(player);
		}
		return htmltext;
	}
}
