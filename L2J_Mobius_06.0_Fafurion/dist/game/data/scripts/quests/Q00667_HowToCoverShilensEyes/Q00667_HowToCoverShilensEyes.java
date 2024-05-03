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
package quests.Q00667_HowToCoverShilensEyes;

import org.l2jmobius.gameserver.enums.Faction;
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

/**
 * How to Cover Shilen's Eyes (667)
 * @author Kazumi
 */
public final class Q00667_HowToCoverShilensEyes extends Quest
{
	// NPCs
	private static final int ARCTURUS = 34267;
	private static final int COLIN = 30703;
	// Monster
	private static final int[] MONSTERS =
	{
		25915, // Anakim
		25919, // Lilith
	};
	// Misc
	private static final int MIN_LEVEL = 85;
	private static final int REWARD = 150;
	private static final int FACTION_AMITY_TOKEN = 48030;
	
	public Q00667_HowToCoverShilensEyes()
	{
		super(667);
		addStartNpc(ARCTURUS, COLIN);
		addTalkId(ARCTURUS, COLIN);
		addKillId(MONSTERS);
		addCondMinLevel(MIN_LEVEL, "");
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		
		String htmltext = null;
		
		if ("quest_accept".equals(event))
		{
			qs.startQuest();
			switch (npc.getId())
			{
				case ARCTURUS:
				{
					htmltext = "hunter_leader_arcturus_q0667_05.htm";
					break;
				}
				case COLIN:
				{
					htmltext = "union_member_colin_q0667_05.htm";
					break;
				}
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
				if (player.getLevel() >= MIN_LEVEL)
				{
					if (player.getFactionLevel(Faction.HUNTERS_GUILD) >= 1)
					{
						htmltext = npc.getId() == ARCTURUS ? "hunter_leader_arcturus_q0667_01.htm" : "union_member_colin_q0667_01.htm";
						break;
					}
					htmltext = npc.getId() == ARCTURUS ? "hunter_leader_arcturus_q0667_02.htm" : "union_member_colin_q0667_02.htm";
					break;
				}
				htmltext = npc.getId() == ARCTURUS ? "hunter_leader_arcturus_q0667_02.htm" : "union_member_colin_q0667_02.htm";
				break;
			}
			case State.STARTED:
			{
				switch (qs.getCond())
				{
					case 1:
					{
						htmltext = npc.getId() == ARCTURUS ? "hunter_leader_arcturus_q0667_06.htm" : "union_member_colin_q0667_06.htm";
						break;
					}
					case 2:
					{
						htmltext = npc.getId() == ARCTURUS ? "hunter_leader_arcturus_q0667_07.htm" : "union_member_colin_q0667_07.htm";
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
	@Id(ARCTURUS)
	@Id(COLIN)
	public final void onNpcMenuSelect(OnNpcMenuSelect event)
	{
		final Player player = event.getTalker();
		final QuestState qs = getQuestState(player, false);
		final Npc npc = event.getNpc();
		final int ask = event.getAsk();
		final int reply = event.getReply();
		
		if (ask == 667)
		{
			switch (reply)
			{
				case 1:
				{
					showHtmlFile(player, npc.getId() == ARCTURUS ? "hunter_leader_arcturus_q0667_03.htm" : "union_member_colin_q0667_03.htm");
					break;
				}
				case 2:
				{
					showHtmlFile(player, npc.getId() == ARCTURUS ? "hunter_leader_arcturus_q0667_04.htm" : "union_member_colin_q0667_04.htm");
					break;
				}
				case 1001:
				{
					if (qs.getCond() == 2)
					{
						qs.exitQuest(QuestType.DAILY, true);
						addFactionPoints(player, Faction.HUNTERS_GUILD, REWARD);
						showHtmlFile(player, npc.getId() == ARCTURUS ? "hunter_leader_arcturus_q0667_08.htm" : "union_member_colin_q0667_08.htm");
					}
					break;
				}
				case 1002:
				{
					if (qs.getCond() == 2)
					{
						if (hasQuestItems(player, FACTION_AMITY_TOKEN))
						{
							qs.exitQuest(QuestType.DAILY, true);
							takeItems(player, FACTION_AMITY_TOKEN, 1);
							addFactionPoints(player, Faction.HUNTERS_GUILD, REWARD * 2);
							showHtmlFile(player, npc.getId() == ARCTURUS ? "hunter_leader_arcturus_q0667_08.htm" : "union_member_colin_q0667_08.htm");
							break;
						}
						showHtmlFile(player, npc.getId() == ARCTURUS ? "hunter_leader_arcturus_q0667_09.htm" : "union_member_colin_q0667_09.htm");
					}
					break;
				}
			}
		}
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		final Party party = killer.getParty();
		if (party != null)
		{
			party.getMembers().forEach(p -> onKill(npc, p));
		}
		else
		{
			onKill(npc, killer);
		}
		
		return super.onKill(npc, killer, isSummon);
	}
	
	public void onKill(Npc npc, Player killer)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isCond(1) && (npc.calculateDistance3D(killer) <= 1000))
		{
			qs.setCond(2, true);
		}
	}
}