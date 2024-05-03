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
package quests.Q00529_RegularBarrierMaintenance;

import org.l2jmobius.gameserver.enums.QuestType;
import org.l2jmobius.gameserver.model.Party;
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

import quests.Q10529_IvoryTowersResearchSeaOfSporesJournal.Q10529_IvoryTowersResearchSeaOfSporesJournal;

/**
 * Regular Barrier Maintenance (529)
 * @author Kazumi
 */
public final class Q00529_RegularBarrierMaintenance extends Quest
{
	// NPCs
	private static final int CHORINA = 33846;
	// Monster
	private static final int[] MONSTERS =
	{
		24227, // Keros
		24228, // Falena
		24229, // Atrofe
		24230, // Nuba
		24231, // Torfedo
		29329, // Harane
		24234, // Lesatanas
		24235, // Arbor
		24236, // Tergus
		24237, // Skeletus
		24238, // Atrofine
		29327, // Arminus
		29326, // Arima
	};
	// Items
	private static final int SEIZED_ENERGY_OF_THE_SEA_OF_SPORES = 48838;
	// Misc
	private static final int MIN_LEVEL = 106;
	private static final int ITEM_COUNT = 200;
	
	public Q00529_RegularBarrierMaintenance()
	{
		super(529);
		addStartNpc(CHORINA);
		addTalkId(CHORINA);
		addKillId(MONSTERS);
		addCondMinLevel(MIN_LEVEL, "chorina_q0529_02.htm");
		addCondCompletedQuest(Q10529_IvoryTowersResearchSeaOfSporesJournal.class.getSimpleName(), "chorina_q0529_02.htm");
		registerQuestItems(SEIZED_ENERGY_OF_THE_SEA_OF_SPORES);
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
			htmltext = "chorina_q0529_05.htm";
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
				htmltext = "chorina_q0529_01.htm";
				break;
			}
			case State.STARTED:
			{
				switch (qs.getCond())
				{
					case 1:
					{
						htmltext = "chorina_q0529_06.htm";
						break;
					}
					case 2:
					{
						htmltext = "chorina_q0529_07.htm";
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
	@Id(CHORINA)
	public final void onNpcMenuSelect(OnNpcMenuSelect event)
	{
		final Player player = event.getTalker();
		final QuestState qs = getQuestState(player, false);
		final int ask = event.getAsk();
		final int reply = event.getReply();
		
		if (ask == 529)
		{
			switch (reply)
			{
				case 1:
				{
					showHtmlFile(player, "chorina_q0529_03.htm");
					break;
				}
				case 2:
				{
					showHtmlFile(player, "chorina_q0529_04.htm");
					break;
				}
				case 10:
				{
					if (qs.isCond(2))
					{
						if (player.getLevel() >= MIN_LEVEL)
						{
							qs.exitQuest(QuestType.DAILY, true);
							addExpAndSp(player, 49763842650L, 49763790);
							giveItems(player, 57, 3_225_882);
							showHtmlFile(player, "chorina_q0529_08.htm");
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
		final Party party = player.getParty();
		if (party != null)
		{
			party.getMembers().forEach(p -> onKill(npc, p));
		}
		else
		{
			onKill(npc, player);
		}
		return super.onKill(npc, player, isSummon);
	}
	
	public void onKill(Npc npc, Player player)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isCond(1))
		{
			giveItemRandomly(player, npc, SEIZED_ENERGY_OF_THE_SEA_OF_SPORES, 1, ITEM_COUNT, 0.5, true);
			if ((getQuestItemsCount(player, SEIZED_ENERGY_OF_THE_SEA_OF_SPORES) >= ITEM_COUNT))
			{
				qs.setCond(2, true);
			}
		}
	}
}