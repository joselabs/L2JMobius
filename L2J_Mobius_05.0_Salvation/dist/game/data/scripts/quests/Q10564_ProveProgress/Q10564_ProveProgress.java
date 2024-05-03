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
package quests.Q10564_ProveProgress;

import org.l2jmobius.gameserver.enums.Faction;
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

import quests.Q10563_ControlOfPower.Q10563_ControlOfPower;

/**
 * Prove Progress (10564)
 * @author Kazumi
 */
public final class Q10564_ProveProgress extends Quest
{
	// NPCs
	private static final int HERPHAH = 34362;
	private static final int PENNY = 34413;
	// Items
	private static final int SOULSHOT_R_GRADE = 33780;
	private static final int B_SPIRITSHOT_R_GRADE = 33794;
	private static final int PA_ART_OF_DESUCTION = 37928;
	private static final int OLD_ELEMENTAL_SHIRT = 47005;
	// Misc
	private static final int MIN_LEVEL = 85;
	private static final int MAX_LEVEL = 99;
	
	public Q10564_ProveProgress()
	{
		super(10564);
		addStartNpc(HERPHAH);
		addTalkId(HERPHAH, PENNY);
		addCondMinLevel(MIN_LEVEL, "herphah_q10564_02.htm");
		addCondMaxLevel(MAX_LEVEL, "");
		addCondCompletedQuest(Q10563_ControlOfPower.class.getSimpleName(), "herphah_q10564_02a.htm");
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
		
		if (event.equals("quest_accept"))
		{
			qs.startQuest();
			htmltext = "herphah_q10564_05.htm";
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
			case State.CREATED:
			{
				htmltext = "herphah_q10564_01.htm";
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case HERPHAH:
					{
						switch (qs.getCond())
						{
							case 1:
							{
								htmltext = "herphah_q10564_06.htm";
								break;
							}
							case 2:
							{
								htmltext = "herphah_q10564_07.htm";
								break;
							}
							case 3:
							{
								htmltext = "herphah_q10564_08.htm";
								break;
							}
						}
						break;
					}
					case PENNY:
					{
						switch (qs.getCond())
						{
							case 1:
							{
								htmltext = "adventurer_penny_q10564_01.htm";
								break;
							}
							case 2:
							{
								if (player.getFactionLevel(Faction.ADVENTURE_GUILD) < 5)
								{
									htmltext = "adventurer_penny_q10564_03.htm";
									break;
								}
								htmltext = "adventurer_penny_q10564_04.htm";
								break;
							}
							case 3:
							{
								htmltext = "adventurer_penny_q10564_06.htm";
								break;
							}
						}
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
	
	@RegisterEvent(EventType.ON_NPC_MENU_SELECT)
	@RegisterType(ListenerRegisterType.NPC)
	@Id(HERPHAH)
	@Id(PENNY)
	public final void onNpcMenuSelect(OnNpcMenuSelect event)
	{
		final Player player = event.getTalker();
		final QuestState qs = getQuestState(player, false);
		final Npc npc = event.getNpc();
		final int ask = event.getAsk();
		final int reply = event.getReply();
		
		if (ask == 10564)
		{
			switch (reply)
			{
				case 1:
				{
					switch (npc.getId())
					{
						case HERPHAH:
						{
							showHtmlFile(player, "herphah_q10564_03.htm");
							break;
						}
						case PENNY:
						{
							qs.setCond(2);
							showHtmlFile(player, "adventurer_penny_q10564_02.htm");
							break;
						}
					}
					break;
				}
				case 2:
				{
					switch (npc.getId())
					{
						case HERPHAH:
						{
							showHtmlFile(player, "herphah_q10564_04.htm");
							break;
						}
						case PENNY:
						{
							qs.setCond(3);
							showHtmlFile(player, "adventurer_penny_q10564_05.htm");
							break;
						}
					}
					break;
				}
				case 9:
				{
					qs.exitQuest(false, true);
					addExpAndSp(player, 20325099190L, 18292589);
					giveItems(player, SOULSHOT_R_GRADE, 10000);
					giveItems(player, B_SPIRITSHOT_R_GRADE, 10000);
					giveItems(player, PA_ART_OF_DESUCTION, 20);
					giveItems(player, OLD_ELEMENTAL_SHIRT, 1);
					showHtmlFile(player, "herphah_q10564_09.htm");
					break;
				}
			}
		}
	}
}
