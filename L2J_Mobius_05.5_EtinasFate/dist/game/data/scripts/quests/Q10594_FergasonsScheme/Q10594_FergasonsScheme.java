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
package quests.Q10594_FergasonsScheme;

import java.util.HashSet;
import java.util.Set;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.ListenerRegisterType;
import org.l2jmobius.gameserver.model.events.annotations.RegisterEvent;
import org.l2jmobius.gameserver.model.events.annotations.RegisterType;
import org.l2jmobius.gameserver.model.events.impl.creature.player.OnPlayerLogin;
import org.l2jmobius.gameserver.model.holders.NpcLogListHolder;
import org.l2jmobius.gameserver.model.itemcontainer.Inventory;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

/**
 * Fergason's Scheme (10594)
 * @author Kazumi
 */
public final class Q10594_FergasonsScheme extends Quest
{
	// NPCs
	private static final int FERGASON = 33681;
	private static final int SIZRAK = 33669;
	private static final int AKU = 33671;
	// Monsters
	private final int[] MONSTER_LIST =
	{
		23213, // Beggar Zofan
		23214, // Beggar Zofan
		23227, // Beggar Zofan
		23228, // Beggar Zofan
		23215, // Zofan
		23216, // Zofan
		23229, // Zofan
		23230, // Zofan
		23217, // Young Zofan
		23218, // Young Zofan
		23231, // Young Zofan
		23232, // Young Zofan
		23219, // Engineer Zofan
		23237, // Engineer Zofan
		23220, // Kunda Watchman
		23224, // Kunda Guardian
		23225, // Kunda Berserker
		23226, // Kunda Executor
		19265, // Kunda Lord
	};
	// Misc
	private static final int SEED_OF_HELLFIRE_MONSTER = 19701;
	private static final int MIN_LEVEL = 97;
	private static final int KILL_COUNT = 200;
	
	public Q10594_FergasonsScheme()
	{
		super(10594);
		addStartNpc(FERGASON);
		addTalkId(FERGASON, SIZRAK, AKU);
		addKillId(MONSTER_LIST);
		addCondMinLevel(MIN_LEVEL, "maestro_ferguson_q10594_02.htm");
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		
		String htmltext = event;
		
		switch (event)
		{
			case "maestro_ferguson_q10594_03.htm":
			case "maestro_ferguson_q10594_04.htm":
			{
				htmltext = event;
				break;
			}
			case "maestro_ferguson_q10594_05.htm":
			{
				qs.startQuest();
				break;
			}
			case "sofa_sizraku_q10594_02.htm":
			{
				if (qs.isCond(1))
				{
					qs.setCond(2);
				}
				break;
			}
			case "sofa_aku_q10594_02.htm":
			{
				if (qs.isCond(3))
				{
					qs.setCond(4);
				}
				break;
			}
			case "maestro_ferguson_q10594_11.htm":
			{
				if (qs.isCond(5))
				{
					if (player.getLevel() >= MIN_LEVEL)
					{
						qs.exitQuest(false, true);
						giveItems(player, Inventory.ADENA_ID, 2_344_896);
						addExpAndSp(player, 8076389850L, 8076330);
						break;
					}
					htmltext = getNoQuestLevelRewardMsg(player);
					break;
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
			case State.CREATED:
			{
				if (npc.getId() == FERGASON)
				{
					if (player.getLevel() >= MIN_LEVEL)
					{
						htmltext = "maestro_ferguson_q10594_01.htm";
						break;
					}
					htmltext = "maestro_ferguson_q10594_02.htm";
					break;
				}
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case FERGASON:
					{
						switch (qs.getCond())
						{
							case 1:
							{
								htmltext = "maestro_ferguson_q10594_06.htm";
								break;
							}
							case 2:
							{
								htmltext = "maestro_ferguson_q10594_07.htm";
								qs.setCond(3);
								break;
							}
							case 3:
							{
								htmltext = "maestro_ferguson_q10594_08.htm";
								break;
							}
							case 4:
							{
								htmltext = "maestro_ferguson_q10594_09.htm";
								break;
							}
							case 5:
							{
								htmltext = "maestro_ferguson_q10594_10.htm";
								break;
							}
						}
						break;
					}
					case SIZRAK:
					{
						switch (qs.getCond())
						{
							case 1:
							{
								htmltext = "sofa_sizraku_q10594_01.htm";
								break;
							}
							case 2:
							{
								htmltext = "sofa_sizraku_q10594_03.htm";
								break;
							}
							case 3:
							{
								htmltext = "sofa_sizraku_q10594_04.htm";
								break;
							}
							case 4:
							{
								htmltext = "sofa_sizraku_q10594_05.htm";
								break;
							}
							case 5:
							{
								htmltext = "sofa_sizraku_q10594_06.htm";
								break;
							}
						}
						break;
					}
					case AKU:
					{
						switch (qs.getCond())
						{
							case 1:
							case 2:
							{
								htmltext = "sofa_aku_q10594_03.htm";
								break;
							}
							case 3:
							{
								htmltext = "sofa_aku_q10594_01.htm";
								break;
							}
							case 4:
							{
								htmltext = "sofa_aku_q10594_04.htm";
								break;
							}
							case 5:
							{
								htmltext = "sofa_aku_q10594_05.htm";
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
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		final QuestState st = getQuestState(killer, false);
		
		if ((st != null) && st.isStarted() && st.isCond(4))
		{
			int killCount = st.getInt("KILLED_COUNT");
			
			if (killCount < KILL_COUNT)
			{
				killCount++;
				st.set("KILLED_COUNT", killCount);
				playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				sendNpcLogList(killer);
			}
			
			if (killCount == KILL_COUNT)
			{
				st.setCond(5, true);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public Set<NpcLogListHolder> getNpcLogList(Player activeChar)
	{
		final QuestState st = getQuestState(activeChar, false);
		if ((st != null) && st.isStarted() && st.isCond(4))
		{
			final Set<NpcLogListHolder> npcLogList = new HashSet<>(1);
			npcLogList.add(new NpcLogListHolder(SEED_OF_HELLFIRE_MONSTER, false, st.getInt("KILLED_COUNT")));
			return npcLogList;
		}
		return super.getNpcLogList(activeChar);
	}
	
	@RegisterEvent(EventType.ON_PLAYER_LOGIN)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public final void onLogin(OnPlayerLogin evt)
	{
		final Player player = evt.getPlayer();
		sendNpcLogList(player);
	}
}
