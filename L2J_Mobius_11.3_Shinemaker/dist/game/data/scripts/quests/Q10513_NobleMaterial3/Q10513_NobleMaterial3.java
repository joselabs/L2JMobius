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
package quests.Q10513_NobleMaterial3;

import org.l2jmobius.gameserver.data.xml.TeleportListData;
import org.l2jmobius.gameserver.instancemanager.QuestManager;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.ListenerRegisterType;
import org.l2jmobius.gameserver.model.events.annotations.RegisterEvent;
import org.l2jmobius.gameserver.model.events.annotations.RegisterType;
import org.l2jmobius.gameserver.model.events.impl.creature.player.OnPlayerLevelChanged;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestDialogType;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.newquestdata.NewQuestLocation;
import org.l2jmobius.gameserver.model.quest.newquestdata.QuestCondType;
import org.l2jmobius.gameserver.network.serverpackets.quest.ExQuestDialog;
import org.l2jmobius.gameserver.network.serverpackets.quest.ExQuestNotification;

import quests.Q10514_NobleMaterial4.Q10514_NobleMaterial4;
import quests.Q10515_NobleMaterial4.Q10515_NobleMaterial4;
import quests.Q10516_NobleMaterial4.Q10516_NobleMaterial4;
import quests.Q10517_NobleMaterial4.Q10517_NobleMaterial4;

/**
 * @author Stayway
 */
public class Q10513_NobleMaterial3 extends Quest
{
	private static final int QUEST_ID = 10513;
	
	public Q10513_NobleMaterial3()
	{
		super(QUEST_ID);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		switch (event)
		{
			case "ACCEPT":
			{
				if (!canStartQuest(player))
				{
					break;
				}
				
				final QuestState questState = getQuestState(player, true);
				if (!questState.isStarted() && !questState.isCompleted())
				{
					questState.startQuest();
					giveStoryBuffReward(player);
				}
				break;
			}
			case "TELEPORT":
			{
				QuestState questState = getQuestState(player, false);
				if (questState == null)
				{
					if (!canStartQuest(player))
					{
						break;
					}
					
					questState = getQuestState(player, true);
					
					final NewQuestLocation questLocation = getQuestData().getLocation();
					if (questLocation.getStartLocationId() > 0)
					{
						final Location location = TeleportListData.getInstance().getTeleport(questLocation.getStartLocationId()).getLocation();
						if (teleportToQuestLocation(player, location))
						{
							questState.setCond(QuestCondType.ACT);
							sendAcceptDialog(player);
						}
					}
					break;
				}
				
				final NewQuestLocation questLocation = getQuestData().getLocation();
				if (questState.isCond(QuestCondType.STARTED))
				{
					if (questLocation.getQuestLocationId() > 0)
					{
						final Location location = TeleportListData.getInstance().getTeleport(questLocation.getQuestLocationId()).getLocation();
						if (teleportToQuestLocation(player, location) && (questLocation.getQuestLocationId() == questLocation.getEndLocationId()))
						{
							questState.setCond(QuestCondType.DONE);
							sendEndDialog(player);
						}
					}
				}
				else if (questState.isCond(QuestCondType.DONE) && !questState.isCompleted())
				{
					if (questLocation.getEndLocationId() > 0)
					{
						final Location location = TeleportListData.getInstance().getTeleport(questLocation.getEndLocationId()).getLocation();
						if (teleportToQuestLocation(player, location))
						{
							sendEndDialog(player);
						}
					}
				}
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
					
					final Quest nextQuest = QuestManager.getInstance().getQuest(Q10514_NobleMaterial4.class.getSimpleName());
					if ((nextQuest != null) && nextQuest.canStartQuest(player))
					{
						player.sendPacket(new ExQuestDialog(10514, QuestDialogType.ACCEPT));
					}
					
					final Quest nextQuest1 = QuestManager.getInstance().getQuest(Q10515_NobleMaterial4.class.getSimpleName());
					if ((nextQuest1 != null) && nextQuest1.canStartQuest(player))
					{
						player.sendPacket(new ExQuestDialog(10515, QuestDialogType.ACCEPT));
					}
					final Quest nextQuest2 = QuestManager.getInstance().getQuest(Q10516_NobleMaterial4.class.getSimpleName());
					if ((nextQuest2 != null) && nextQuest2.canStartQuest(player))
					{
						player.sendPacket(new ExQuestDialog(10516, QuestDialogType.ACCEPT));
					}
					final Quest nextQuest3 = QuestManager.getInstance().getQuest(Q10517_NobleMaterial4.class.getSimpleName());
					if ((nextQuest3 != null) && nextQuest3.canStartQuest(player))
					{
						player.sendPacket(new ExQuestDialog(10517, QuestDialogType.ACCEPT));
					}
					
				}
				break;
			}
		}
		return event;
	}
	
	@RegisterEvent(EventType.ON_PLAYER_LEVEL_CHANGED)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onPlayerLevelChange(OnPlayerLevelChanged event)
	{
		final Player player = event.getPlayer();
		if (player == null)
		{
			return;
		}
		
		final QuestState questState = getQuestState(player, false);
		if ((questState == null) && canStartQuest(player))
		{
			player.sendPacket(new ExQuestDialog(QUEST_ID, QuestDialogType.ACCEPT));
		}
		else if ((questState != null) && questState.isStarted() && !questState.isCompleted() && (player.getLevel() >= 100))
		{
			questState.setCount(getQuestData().getGoal().getCount());
			questState.setCond(QuestCondType.DONE);
			player.sendPacket(new ExQuestNotification(questState));
		}
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		final QuestState questState = getQuestState(player, false);
		if ((questState != null) && !questState.isCompleted())
		{
			if (questState.isCond(QuestCondType.STARTED))
			{
				if (questState.isStarted())
				{
					questState.setCount(getQuestData().getGoal().getCount());
					questState.setCond(QuestCondType.DONE);
					player.sendPacket(new ExQuestDialog(questState.getQuest().getId(), QuestDialogType.END));
				}
			}
			else if (questState.isCond(QuestCondType.DONE))
			{
				player.sendPacket(new ExQuestDialog(questState.getQuest().getId(), QuestDialogType.END));
			}
		}
		
		npc.showChatWindow(player);
		return null;
	}
}
