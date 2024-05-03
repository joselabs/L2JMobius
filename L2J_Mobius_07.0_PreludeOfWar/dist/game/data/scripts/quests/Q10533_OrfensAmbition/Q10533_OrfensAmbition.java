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
package quests.Q10533_OrfensAmbition;

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
 * Orfen's Ambition (10533)
 * @author Kazumi
 */
public final class Q10533_OrfensAmbition extends Quest
{
	// NPCs
	private static final int BACON = 33846;
	private static final int JAMON = 34449;
	// Items
	private static final int SUPERIOR_GIANTS_CODEX_CHAPTER_1 = 46151;
	// Monster
	private static final int ORFEN = 29325;
	// Misc
	private static final int MIN_LEVEL = 106;
	
	public Q10533_OrfensAmbition()
	{
		super(10533);
		addStartNpc(BACON);
		addTalkId(BACON, JAMON);
		addKillId(ORFEN);
		addCondMinLevel(MIN_LEVEL, "disciple_bacon_q10533_02.htm");
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
			htmltext = "disciple_bacon_q10533_04.htm";
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
				if (npc.getId() == BACON)
				{
					htmltext = "disciple_bacon_q10533_01.htm";
					break;
				}
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case BACON:
					{
						htmltext = "disciple_bacon_q10533_05.htm";
						break;
					}
					case JAMON:
					{
						switch (qs.getCond())
						{
							case 1:
							{
								htmltext = "jamon_q10533_01.htm";
								break;
							}
							case 2:
							{
								htmltext = "jamon_q10533_04.htm";
								break;
							}
							case 3:
							{
								htmltext = "jamon_q10533_05.htm";
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
	@Id(BACON)
	@Id(JAMON)
	public final void onNpcMenuSelect(OnNpcMenuSelect event)
	{
		final Player player = event.getTalker();
		final QuestState qs = getQuestState(player, false);
		final Npc npc = event.getNpc();
		final int ask = event.getAsk();
		final int reply = event.getReply();
		
		if (ask == 10533)
		{
			switch (reply)
			{
				case 1:
				{
					switch (npc.getId())
					{
						case BACON:
						{
							showHtmlFile(player, "disciple_bacon_q10533_03.htm");
							break;
						}
						case JAMON:
						{
							showHtmlFile(player, "jamon_q10533_02.htm");
							break;
						}
					}
					break;
				}
				case 2:
				{
					qs.setCond(2);
					showHtmlFile(player, "jamon_q10533_03.htm");
					break;
				}
				case 10:
				{
					if (qs.getCond() == 3)
					{
						if (player.getLevel() >= MIN_LEVEL)
						{
							qs.exitQuest(false, true);
							addExpAndSp(player, 99527685300L, 99527580);
							giveItems(player, SUPERIOR_GIANTS_CODEX_CHAPTER_1, 1);
							showHtmlFile(player, "jamon_q10533_06.htm");
							break;
						}
						break;
					}
					break;
				}
			}
		}
	}
	
	@Override
	public String onKill(Npc npc, Player player, boolean isSummon)
	{
		final QuestState qs = getQuestState(player, true);
		
		if (qs != null)
		{
			if (qs.isCond(2))
			{
				qs.setCond(3, true);
			}
		}
		return super.onKill(npc, player, isSummon);
	}
}
