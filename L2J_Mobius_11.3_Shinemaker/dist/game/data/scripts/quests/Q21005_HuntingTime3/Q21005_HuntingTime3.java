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
package quests.Q21005_HuntingTime3;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestDialogType;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.newquestdata.NewQuest;
import org.l2jmobius.gameserver.model.quest.newquestdata.QuestCondType;
import org.l2jmobius.gameserver.network.serverpackets.quest.ExQuestDialog;
import org.l2jmobius.gameserver.network.serverpackets.quest.ExQuestNotification;

import quests.Q21006_HuntingTime4.Q21006_HuntingTime4;

/**
 * @author CostyKiller
 */
public class Q21005_HuntingTime3 extends Quest
{
	private static final int QUEST_ID = 21005;
	private static final int[] MONSTERS_ANIMA =
	{
		// Anima Area 1 (Lv. 116)
		27755, // Daril Anima Ar
		27756, // Seo Anima Ar
		27757, // Floato Anima Ar
		27758, // Anima Ar Hunter
		27759, // Anima Ar Sorceress
		27760, // Saida Anima Ar
		// Anima Area 2 (Lv. 120)
		27761, // Atron Anima Mide
		27762, // Craigo Anima Mide
		27763, // Kerberos Anima Mide
		27764, // Anima Mide Hunter
		27765, // Anima Mide Sorceress
		27766, // Saida Anima Mide
		27767, // Anima Mide Blader
		// Anima Area 3 (Lv. 124)
		27768, // Atron Anima Telro
		27769, // Craigo Anima Telro
		27770, // Beor Anima Telro
		27771, // Anima Telro Hunter
		27772, // Anima Telro Sorceress
		27773, // Saida Anima Telro
		27774, // Anima Telro Blader
		27775, // Anima Telro Guard
	};
	
	public Q21005_HuntingTime3()
	{
		super(QUEST_ID);
		addKillId(MONSTERS_ANIMA);
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
					
					final QuestState nextQuestState = player.getQuestState(Q21006_HuntingTime4.class.getSimpleName());
					if (nextQuestState == null)
					{
						player.sendPacket(new ExQuestDialog(21006, QuestDialogType.ACCEPT));
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
		if ((questState != null) && questState.isCond(QuestCondType.STARTED))
		{
			final NewQuest data = getQuestData();
			if (data.getGoal().getItemId() > 0)
			{
				final int itemCount = (int) getQuestItemsCount(killer, data.getGoal().getItemId());
				if (itemCount < data.getGoal().getCount())
				{
					giveItems(killer, data.getGoal().getItemId(), 1);
					final int newItemCount = (int) getQuestItemsCount(killer, data.getGoal().getItemId());
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
				killer.sendPacket(new ExQuestNotification(questState));
			}
		}
		
		return super.onKill(npc, killer, isSummon);
	}
}