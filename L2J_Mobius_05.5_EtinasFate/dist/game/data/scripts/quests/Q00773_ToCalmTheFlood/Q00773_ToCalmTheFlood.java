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
package quests.Q00773_ToCalmTheFlood;

import java.util.HashSet;
import java.util.Set;

import org.l2jmobius.commons.util.CommonUtil;
import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.enums.QuestType;
import org.l2jmobius.gameserver.instancemanager.QuestManager;
import org.l2jmobius.gameserver.model.Party;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.ListenerRegisterType;
import org.l2jmobius.gameserver.model.events.annotations.RegisterEvent;
import org.l2jmobius.gameserver.model.events.annotations.RegisterType;
import org.l2jmobius.gameserver.model.events.impl.creature.player.OnPlayerLogin;
import org.l2jmobius.gameserver.model.holders.NpcLogListHolder;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

import quests.Q00565_BasicMissionFairySettlementWest.Q00565_BasicMissionFairySettlementWest;
import quests.Q00749_TiesWithTheGuardians.Q00749_TiesWithTheGuardians;

/**
 * To Calm the Flood (773)
 * @author Kazumi
 */
public class Q00773_ToCalmTheFlood extends Quest
{
	// NPCs
	private static final int FAIRY = 32921;
	// Monster
	private static final int[] FAIRY_MONSTERS =
	{
		22863, // Fairy Warrior
		22867, // Fairy Warrior - Violent
		22868, // Fairy Warrior - Brutal
		22871, // Fairy Rogue
		22875, // Fairy Rogue - Violent
		22876, // Fairy Rogue - Brutal
		22879, // Fairy Knight
		22883, // Fairy Knight - Violent
		22884, // Fairy Knight - Brutal
	};
	private static final int[] SATIRE_MONSTERS =
	{
		22887, // Satyr Wizard
		22891, // Satyr Wizard - Violent
		22892, // Satyr Wizard - Brutal
		22895, // Satyr Summoner
		22899, // Satyr Summoner - Violent
		22900, // Satyr Summoner - Brutal
		22903, // Satyr Witch
		22907, // Satyr Witch - Violent
		22908, // Satyr Witch - Brutal
	};
	// Misc
	private static final int MIN_LEVEL = 88;
	private static final int MAX_LEVEL = 98;
	private static final int KILL_COUNT = 200;
	private static final int ID_FAIRY = 19705;
	private static final int ID_SATYR = 19706;
	
	public Q00773_ToCalmTheFlood()
	{
		super(773);
		addStartNpc(FAIRY);
		addTalkId(FAIRY);
		addKillId(FAIRY_MONSTERS);
		addKillId(SATIRE_MONSTERS);
		addCondMinLevel(MIN_LEVEL, "fairy_civilian_quest_q0773_02.htm");
		addCondMaxLevel(MAX_LEVEL, "fairy_civilian_quest_q0773_02.htm");
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
			case "fairy_civilian_quest_q0773_03.htm":
			case "fairy_civilian_quest_q0773_04.htm":
			{
				htmltext = event;
				break;
			}
			case "fairy_civilian_quest_q0773_05.htm":
			{
				qs.startQuest();
				break;
			}
			case "fairy_civilian_quest_q0773_10.htm":
			{
				if (qs.isCond(2))
				{
					if (player.getLevel() >= MIN_LEVEL)
					{
						qs.exitQuest(QuestType.DAILY, true);
						giveAdena(player, 1_448_604, true);
						addExpAndSp(player, 429526470, 429510);
						final Quest qs749 = QuestManager.getInstance().getQuest(Q00749_TiesWithTheGuardians.class.getSimpleName());
						if (qs749 != null)
						{
							qs749.notifyEvent("NOTIFY_Q749", npc, player);
						}
						
						final Quest qs565 = QuestManager.getInstance().getQuest(Q00565_BasicMissionFairySettlementWest.class.getSimpleName());
						if (qs565 != null)
						{
							qs565.notifyEvent("NOTIFY_Q773", npc, player);
						}
						break;
					}
					htmltext = getNoQuestLevelRewardMsg(player);
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
				// fallthrough
			}
			case State.CREATED:
			{
				htmltext = "fairy_civilian_quest_q0773_01.htm";
				break;
			}
			case State.STARTED:
			{
				switch (qs.getCond())
				{
					case 1:
					{
						htmltext = "fairy_civilian_quest_q0773_06.htm";
						break;
					}
					case 2:
					{
						htmltext = "fairy_civilian_quest_q0773_07.htm";
						break;
					}
				}
				break;
			}
		}
		return htmltext;
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
			int killedFairy = qs.getInt("killed_" + ID_FAIRY);
			int killedSatyr = qs.getInt("killed_" + ID_SATYR);
			
			if (CommonUtil.contains(FAIRY_MONSTERS, npc.getId()))
			{
				if (killedFairy < KILL_COUNT)
				{
					qs.set("killed_" + ID_FAIRY, ++killedFairy);
					playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
				sendNpcLogList(killer);
			}
			
			if (CommonUtil.contains(SATIRE_MONSTERS, npc.getId()))
			{
				if (killedSatyr < KILL_COUNT)
				{
					qs.set("killed_" + ID_SATYR, ++killedSatyr);
					playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
				sendNpcLogList(killer);
			}
			
			if ((killedFairy >= KILL_COUNT) && (killedSatyr >= KILL_COUNT))
			{
				qs.setCond(2, true);
			}
		}
	}
	
	@Override
	public Set<NpcLogListHolder> getNpcLogList(Player player)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isCond(1))
		{
			final Set<NpcLogListHolder> holder = new HashSet<>();
			holder.add(new NpcLogListHolder(ID_FAIRY, false, qs.getInt("killed_" + ID_FAIRY)));
			holder.add(new NpcLogListHolder(ID_SATYR, false, qs.getInt("killed_" + ID_SATYR)));
			return holder;
		}
		return super.getNpcLogList(player);
	}
	
	@RegisterEvent(EventType.ON_PLAYER_LOGIN)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public final void onLogin(OnPlayerLogin evt)
	{
		final Player player = evt.getPlayer();
		sendNpcLogList(player);
	}
}
