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
package quests.Q00759_TheDwarvenNightmareContinues;

import org.l2jmobius.gameserver.enums.QuestType;
import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;
import org.l2jmobius.gameserver.util.Util;

/**
 * The Dwarven Nightmare Continues (759)
 * @author Kazumi
 */
public class Q00759_TheDwarvenNightmareContinues extends Quest
{
	// NPCs
	private static final int DAICHIR = 30537;
	private static final int TRASKEN = 29195;
	// Misc
	private static final int MIN_LEVEL = 98;
	private static final int[] REWARDS =
	{
		17623, // Earth Wyrm Heart Ring
		35389, // Blessed Amaranthine Shaper Fragment
		35390, // Blessed Amaranthine Cutter Fragment
		35391, // Blessed Amaranthine Slasher Fragment
		35392, // Blessed Amaranthine Avenger Fragment
		35393, // Blessed Amaranthine Fighter Fragment
		35394, // Blessed Amaranthine Stormer Fragment
		35395, // Blessed Amaranthine Thrower Fragment
		35396, // Blessed Amaranthine Shooter Fragment
		35397, // Blessed Amaranthine Buster Fragment
		35398, // Blessed Amaranthine Caster Fragment
		35399, // Blessed Amaranthine Retributer Fragment
		9552, // Fire Crystal
		9553, // Water Crystal
		9554, // Earth Crystal
		9555, // Wind Crystal
		9556, // Dark Crystal
		9557 // Holy Crystal
	};
	
	public Q00759_TheDwarvenNightmareContinues()
	{
		super(759);
		addStartNpc(DAICHIR);
		addTalkId(DAICHIR);
		addKillId(TRASKEN);
		addCondMinLevel(MIN_LEVEL, "daichir_priest_of_earth_q0759_02.htm");
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return getNoQuestMsg(player);
		}
		
		String htmltext = null;
		switch (event)
		{
			case "daichir_priest_of_earth_q0759_05.htm":
			case "daichir_priest_of_earth_q0759_06.htm":
			case "daichir_priest_of_earth_q0759_10.htm":
			case "daichir_priest_of_earth_q0759_11.htm":
			case "daichir_priest_of_earth_q0759_14.htm":
			{
				htmltext = event;
				break;
			}
			case "daichir_priest_of_earth_q0759_08.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "daichir_priest_of_earth_q0759_16.htm":
			{
				if (qs.isCond(2))
				{
					if (player.getLevel() >= MIN_LEVEL)
					{
						final int reward = REWARDS[getRandom(REWARDS.length)];
						qs.exitQuest(QuestType.DAILY, true);
						giveItems(player, reward, 1);
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
		String htmltext = getNoQuestMsg(player);
		final QuestState qs = getQuestState(player, true);
		switch (qs.getState())
		{
			case State.COMPLETED:
			{
				if (!qs.isNowAvailable())
				{
					htmltext = "daichir_priest_of_earth_q0759_03.htm";
					break;
				}
				qs.setState(State.CREATED);
				// fallthrough
			}
			case State.CREATED:
			{
				htmltext = "daichir_priest_of_earth_q0759_01.htm";
				break;
			}
			case State.STARTED:
			{
				switch (qs.getCond())
				{
					case 1:
					{
						htmltext = "daichir_priest_of_earth_q0759_09.htm";
						break;
					}
					case 2:
					{
						htmltext = "daichir_priest_of_earth_q0759_15.htm";
						break;
					}
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player player, boolean isSummon)
	{
		for (Player pl : World.getInstance().getVisibleObjects(npc, Player.class))
		{
			final QuestState qs = getQuestState(pl, false);
			if ((qs != null) && qs.isCond(1) && Util.checkIfInRange(1500, npc, qs.getPlayer(), false))
			{
				qs.setCond(2, true);
			}
		}
		return super.onKill(npc, player, isSummon);
	}
}
