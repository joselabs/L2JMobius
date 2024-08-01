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
package quests.Q20283_FreePrisonersInLangkLizardmenTemple2;

import org.l2jmobius.gameserver.data.xml.TeleportListData;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestDialogType;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.newquestdata.NewQuest;
import org.l2jmobius.gameserver.model.quest.newquestdata.NewQuestLocation;
import org.l2jmobius.gameserver.model.quest.newquestdata.QuestCondType;
import org.l2jmobius.gameserver.network.serverpackets.quest.ExQuestDialog;
import org.l2jmobius.gameserver.network.serverpackets.quest.ExQuestNotification;

/**
 * @author CostyKiller
 */
public class Q20283_FreePrisonersInLangkLizardmenTemple2 extends Quest
{
	private static final int QUEST_ID = 20283;
	
	private static final int[] MONSTERS =
	{
		24693, // Langk Lizardman Defender
		24694, // Langk Lizardman Guardian
	};
	
	// Box
	private static final int MYSTERIOUS_BOX = 34706;
	
	// Staff Drop Chance
	private static final int DROP_CHANCE = 5;
	
	// Mysterious Box Spawn Chance
	private static final int SPAWN_CHANCE = 10;
	
	public Q20283_FreePrisonersInLangkLizardmenTemple2()
	{
		super(QUEST_ID);
		addKillId(MONSTERS);
		addFirstTalkId(MYSTERIOUS_BOX);
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
				}
				break;
			}
			case "TELEPORT":
			{
				final QuestState questState = getQuestState(player, false);
				final NewQuestLocation questLocation = getQuestData().getLocation();
				if (questState == null)
				{
					final Location location = TeleportListData.getInstance().getTeleport(questLocation.getStartLocationId()).getLocation();
					teleportToQuestLocation(player, location);
					sendAcceptDialog(player);
				}
				else if (questState.isCond(QuestCondType.STARTED))
				{
					if (questLocation.getQuestLocationId() > 0)
					{
						final Location location = TeleportListData.getInstance().getTeleport(questLocation.getQuestLocationId()).getLocation();
						teleportToQuestLocation(player, location);
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
			if (questState.isCond(QuestCondType.STARTED))
			{
				if (npc.getId() == MYSTERIOUS_BOX)
				{
					npc.onDecay();
					if (getRandom(100) < DROP_CHANCE)
					{
						final NewQuest data = getQuestData();
						if (data.getGoal().getItemId() > 0)
						{
							final int itemCount = (int) getQuestItemsCount(player, data.getGoal().getItemId());
							if (itemCount < data.getGoal().getCount())
							{
								giveItems(player, data.getGoal().getItemId(), 1);
								final int newItemCount = (int) getQuestItemsCount(player, data.getGoal().getItemId());
								questState.setCount(newItemCount);
							}
						}
						else
						{
							final int currentCount = questState.getCount();
							if (currentCount != data.getGoal().getCount())
							{
								questState.setCount(currentCount + 1);
							}
						}
						
						if (questState.getCount() == data.getGoal().getCount())
						{
							questState.setCond(QuestCondType.DONE);
							player.sendPacket(new ExQuestNotification(questState));
						}
					}
				}
			}
			if (questState.isCond(QuestCondType.NONE))
			{
				player.sendPacket(new ExQuestDialog(QUEST_ID, QuestDialogType.START));
			}
			else if (questState.isCond(QuestCondType.DONE))
			{
				player.sendPacket(new ExQuestDialog(QUEST_ID, QuestDialogType.END));
			}
		}
		
		npc.showChatWindow(player);
		return null;
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		final QuestState questState = getQuestState(killer, false);
		if ((questState != null) && !questState.isCompleted())
		{
			if (getRandom(100) < SPAWN_CHANCE)
			{
				addSpawn(MYSTERIOUS_BOX, npc, true, 0, true);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}