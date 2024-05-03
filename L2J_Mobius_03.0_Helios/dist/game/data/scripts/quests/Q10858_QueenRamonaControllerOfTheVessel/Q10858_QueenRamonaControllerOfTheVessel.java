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
package quests.Q10858_QueenRamonaControllerOfTheVessel;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;
import org.l2jmobius.gameserver.util.Util;

import quests.Q10856_SuperionAppears.Q10856_SuperionAppears;

/**
 * Queen Ramona, Controller of the Vessel (10858)
 * @author Kazumi
 */
public class Q10858_QueenRamonaControllerOfTheVessel extends Quest
{
	// NPCs
	private static final int KEKROPUS = 34222;
	private static final int RAMONA = 26143;
	// Item
	private static final int SUPER_GIANT_CHAPTER_1 = 46150;
	// Misc
	private static final int MIN_LEVEL = 102;
	
	public Q10858_QueenRamonaControllerOfTheVessel()
	{
		super(10858);
		addStartNpc(KEKROPUS);
		addTalkId(KEKROPUS);
		addKillId(RAMONA);
		addCondMinLevel(MIN_LEVEL, "leader_kekrops_q10858_02.htm");
		addCondCompletedQuest(Q10856_SuperionAppears.class.getSimpleName(), "leader_kekrops_q10858_02.htm");
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return getNoQuestMsg(player);
		}
		String htmltext = null;
		switch (event)
		{
			case "leader_kekrops_q10858_03.htm":
			case "leader_kekrops_q10858_04.htm":
			{
				htmltext = event;
				break;
			}
			case "leader_kekrops_q10858_05.htm":
			{
				qs.startQuest();
				break;
			}
			case "leader_kekrops_q10858_08.htm":
			{
				if (qs.isCond(2))
				{
					if (player.getLevel() >= MIN_LEVEL)
					{
						qs.exitQuest(false, true);
						giveItems(player, SUPER_GIANT_CHAPTER_1, 1);
						addExpAndSp(player, 1630746824, 14221620);
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
		String htmltext = getNoQuestMsg(player);
		final QuestState qs = getQuestState(player, true);
		switch (qs.getState())
		{
			case State.CREATED:
			{
				htmltext = "leader_kekrops_q10858_01.htm";
				break;
			}
			case State.STARTED:
			{
				switch (qs.getCond())
				{
					case 1:
					{
						htmltext = "leader_kekrops_q10858_06.htm";
						break;
					}
					case 2:
					{
						htmltext = "leader_kekrops_q10858_07.htm";
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
		final QuestState qs = getRandomPartyMemberState(killer, 1, 3, npc);
		if ((qs != null) && qs.isCond(1) && Util.checkIfInRange(1500, npc, qs.getPlayer(), false))
		{
			qs.setCond(2, true);
		}
		return super.onKill(npc, killer, isSummon);
	}
}
