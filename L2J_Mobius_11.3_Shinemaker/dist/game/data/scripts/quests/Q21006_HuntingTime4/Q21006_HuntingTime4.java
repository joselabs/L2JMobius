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
package quests.Q21006_HuntingTime4;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestDialogType;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.newquestdata.NewQuest;
import org.l2jmobius.gameserver.model.quest.newquestdata.QuestCondType;
import org.l2jmobius.gameserver.network.serverpackets.quest.ExQuestDialog;
import org.l2jmobius.gameserver.network.serverpackets.quest.ExQuestNotification;

/**
 * @author CostyKiller
 */
public class Q21006_HuntingTime4 extends Quest
{
	private static final int QUEST_ID = 21006;
	private static final int[] MONSTERS_NOX =
	{
		// Nox Area 1 (Lv. 116)
		27734, // Daril Nox Ar
		27735, // Seo Nox Ar
		27736, // Floato Nox Ar
		27737, // Nox Ar Hunter
		27738, // Nox Ar Sorceress
		27739, // Saida Nox Ar
		// Nox Area 2 (Lv. 120)
		27740, // Atron Nox Mide
		27741, // Craigo Nox Mide
		27742, // Kerberos Nox Mide
		27743, // Nox Mide Hunter
		27744, // Nox Mide Sorceress
		27745, // Saida Nox Mide
		27746, // Nox Mide Blader
		// Nox Area 3 (Lv. 124)
		27747, // Atron Nox Telro
		27748, // Craigo Nox Telro
		27749, // Beor Nox Telro
		27750, // Nox Telro Hunter
		27751, // Nox Telro Sorceress
		27752, // Saida Nox Telro
		27753, // Nox Telro Blader
		27754, // Nox Telro Guard
	};
	
	public Q21006_HuntingTime4()
	{
		super(QUEST_ID);
		addKillId(MONSTERS_NOX);
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