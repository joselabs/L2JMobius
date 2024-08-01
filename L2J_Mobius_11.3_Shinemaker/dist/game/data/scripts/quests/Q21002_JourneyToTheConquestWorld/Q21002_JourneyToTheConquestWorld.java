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
package quests.Q21002_JourneyToTheConquestWorld;

import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.gameserver.data.xml.TeleportListData;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestDialogType;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.newquestdata.NewQuestLocation;
import org.l2jmobius.gameserver.model.quest.newquestdata.QuestCondType;
import org.l2jmobius.gameserver.network.serverpackets.quest.ExQuestDialog;

import quests.Q21003_HuntingTime1.Q21003_HuntingTime1;

/**
 * @author CostyKiller
 */
public class Q21002_JourneyToTheConquestWorld extends Quest
{
	private static final int QUEST_ID = 21002;
	
	public Q21002_JourneyToTheConquestWorld()
	{
		super(QUEST_ID);
		addItemTalkId(82176); // // Invitation from the Conquest world
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		switch (event)
		{
			case "ACCEPT":
			{
				final QuestState questState = getQuestState(player, true);
				if (!questState.isStarted() && !questState.isCompleted())
				{
					questState.startQuest();
					giveItems(player, 82176, 1); // Invitation from the Conquest world
				}
				break;
			}
			case "TELEPORT":
			{
				final NewQuestLocation questLocation = getQuestData().getLocation();
				final Location location = TeleportListData.getInstance().getTeleport(questLocation.getQuestLocationId()).getLocation();
				teleportToQuestLocation(player, location);
				ThreadPool.schedule(() -> player.sendPacket(new ExQuestDialog(QUEST_ID, QuestDialogType.ACCEPT)), 2500);
				
				break;
			}
			case "COMPLETE":
			{
				final QuestState questState = getQuestState(player, false);
				if (questState == null)
				{
					break;
				}
				
				if (questState.isCond(QuestCondType.DONE) && !questState.isCompleted())
				{
					questState.exitQuest(false, true);
					rewardPlayer(player);
					
					final QuestState nextQuestState = player.getQuestState(Q21003_HuntingTime1.class.getSimpleName());
					if (nextQuestState == null)
					{
						player.sendPacket(new ExQuestDialog(21003, QuestDialogType.ACCEPT));
					}
				}
				break;
			}
		}
		
		return null;
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		final QuestState questState = getQuestState(player, false);
		if ((questState != null) && !questState.isCompleted())
		{
			if (questState.isCond(QuestCondType.NONE))
			{
				player.sendPacket(new ExQuestDialog(QUEST_ID, QuestDialogType.START));
			}
			else if (questState.isCond(QuestCondType.STARTED) && (npc.getId() == getQuestData().getEndNpcId()))
			{
				questState.setCond(QuestCondType.DONE);
				player.sendPacket(new ExQuestDialog(QUEST_ID, QuestDialogType.END));
			}
			else if (questState.isCond(QuestCondType.DONE))
			{
				player.sendPacket(new ExQuestDialog(QUEST_ID, QuestDialogType.END));
			}
		}
		
		npc.showChatWindow(player);
		return null;
	}
}