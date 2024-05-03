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
package quests.Q00579_BasicMissionBlazingSwamp;

import org.l2jmobius.gameserver.enums.Faction;
import org.l2jmobius.gameserver.enums.QuestType;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.ListenerRegisterType;
import org.l2jmobius.gameserver.model.events.annotations.Id;
import org.l2jmobius.gameserver.model.events.annotations.RegisterEvent;
import org.l2jmobius.gameserver.model.events.annotations.RegisterType;
import org.l2jmobius.gameserver.model.events.impl.creature.npc.OnNpcMenuSelect;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

/**
 * Basic Mission: Blazing Swamp (579)
 * @author Kazumi
 */
public final class Q00579_BasicMissionBlazingSwamp extends Quest
{
	// NPCs
	private static final int PENNY = 34413;
	private static final int HARPE = 34014;
	// Item
	private static final int SOE_SWAMP = 47063;
	// Misc
	private static final int MIN_LEVEL = 97;
	private static final int MAX_LEVEL = 99;
	
	public Q00579_BasicMissionBlazingSwamp()
	{
		super(579);
		addStartNpc(PENNY);
		addTalkId(PENNY, HARPE);
		addCondMinLevel(MIN_LEVEL, "");
		addCondMaxLevel(MAX_LEVEL, "");
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		String htmltext = null;
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		
		switch (event)
		{
			case "quest_accept":
			{
				qs.startQuest();
				htmltext = "adventurer_penny_q0579_05.htm";
				break;
			}
			case "NOTIFY_Q759":
			{
				if (qs.isCond(2))
				{
					qs.setCond(3, false);
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
		
		switch (qs.getState())
		{
			case State.COMPLETED:
			{
				if (!qs.isNowAvailable())
				{
					htmltext = getAlreadyCompletedMsg(player);
					break;
				}
				qs.setState(State.CREATED);
				// fallthrou
			}
			case State.CREATED:
			{
				htmltext = "adventurer_penny_q0579_01.htm";
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case PENNY:
					{
						switch (qs.getCond())
						{
							case 1:
							case 2:
							{
								htmltext = "adventurer_penny_q0579_06.htm";
								break;
							}
							case 3:
							{
								htmltext = "adventurer_penny_q0579_07.htm";
								break;
							}
						}
						break;
					}
					case HARPE:
					{
						switch (qs.getCond())
						{
							case 1:
							{
								qs.setCond(2);
								htmltext = "harpe_zu_hestui_q0579_01.htm";
								break;
							}
							case 2:
							{
								htmltext = "harpe_zu_hestui_q0579_02.htm";
								break;
							}
							case 3:
							{
								htmltext = "harpe_zu_hestui_q0579_03.htm";
								break;
							}
						}
						break;
					}
				}
				break;
			}
		}
		return htmltext;
	}
	
	@RegisterEvent(EventType.ON_NPC_MENU_SELECT)
	@RegisterType(ListenerRegisterType.NPC)
	@Id(PENNY)
	public final void onNpcMenuSelect(OnNpcMenuSelect event)
	{
		final Player player = event.getTalker();
		final QuestState qs = getQuestState(player, false);
		final Npc npc = event.getNpc();
		final int ask = event.getAsk();
		final int reply = event.getReply();
		
		if (ask == 579)
		{
			switch (reply)
			{
				case 1:
				{
					showHtmlFile(player, "adventurer_penny_q0579_03.htm", npc);
					break;
				}
				case 2:
				{
					showHtmlFile(player, "adventurer_penny_q0579_04.htm", npc);
					break;
				}
				case 10:
				{
					if (player.getLevel() >= MIN_LEVEL)
					{
						if (qs.isCond(3))
						{
							qs.exitQuest(QuestType.DAILY, true);
							addExpAndSp(player, 1346064975L, 1346055);
							giveItems(player, SOE_SWAMP, 1);
							addFactionPoints(player, Faction.ADVENTURE_GUILD, 140);
							showHtmlFile(player, "adventurer_penny_q0579_08.htm", npc);
							break;
						}
						break;
					}
					getNoQuestLevelRewardMsg(player);
					break;
				}
			}
		}
	}
}