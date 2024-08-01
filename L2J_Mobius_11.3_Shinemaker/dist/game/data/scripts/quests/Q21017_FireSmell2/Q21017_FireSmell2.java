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
package quests.Q21017_FireSmell2;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.events.Containers;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.ListenerRegisterType;
import org.l2jmobius.gameserver.model.events.annotations.Id;
import org.l2jmobius.gameserver.model.events.annotations.RegisterEvent;
import org.l2jmobius.gameserver.model.events.annotations.RegisterType;
import org.l2jmobius.gameserver.model.events.impl.conquest.OnConquestFlowerCollect;
import org.l2jmobius.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestDialogType;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.newquestdata.NewQuest;
import org.l2jmobius.gameserver.model.quest.newquestdata.QuestCondType;
import org.l2jmobius.gameserver.network.serverpackets.quest.ExQuestDialog;
import org.l2jmobius.gameserver.network.serverpackets.quest.ExQuestNotification;

import quests.Q21018_FireSmell3.Q21018_FireSmell3;

/**
 * @author CostyKiller
 */
public class Q21017_FireSmell2 extends Quest
{
	private static final int QUEST_ID = 21017;
	// Flowers
	private static final int LIFE_FLOWER = 34656;
	private static final int POWER_FLOWER = 34657;
	// Item
	private static final int BRIGHT_SCARLET_PETAL = 82662;
	
	public Q21017_FireSmell2()
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
					Containers.Global().addListener(new ConsumerEventListener(player, EventType.ON_CONQUEST_FLOWER_COLLECT, (OnConquestFlowerCollect eventListener) -> onConquestFlowerCollect(eventListener), this));
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
					final QuestState nextQuestState = player.getQuestState(Q21018_FireSmell3.class.getSimpleName());
					if (nextQuestState == null)
					{
						player.sendPacket(new ExQuestDialog(21018, QuestDialogType.ACCEPT));
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
	
	@RegisterEvent(EventType.ON_CONQUEST_FLOWER_COLLECT)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	@Id(LIFE_FLOWER)
	@Id(POWER_FLOWER)
	private void onConquestFlowerCollect(OnConquestFlowerCollect event)
	{
		final Player player = event.getPlayer();
		if ((event.getNpcId() == LIFE_FLOWER) || (event.getNpcId() == POWER_FLOWER))
		{
			final QuestState questState = getQuestState(player, false);
			if ((questState != null) && questState.isCond(QuestCondType.STARTED))
			{
				final NewQuest data = getQuestData();
				final int itemCount = (int) getQuestItemsCount(player, BRIGHT_SCARLET_PETAL);
				if (itemCount < data.getGoal().getCount())
				{
					giveItems(player, BRIGHT_SCARLET_PETAL, 1);
					final int newItemCount = (int) getQuestItemsCount(player, BRIGHT_SCARLET_PETAL);
					questState.setCount(newItemCount);
				}
				
				if (questState.getCount() == data.getGoal().getCount())
				{
					questState.setCond(QuestCondType.DONE);
					player.sendPacket(new ExQuestNotification(questState));
				}
			}
		}
	}
}