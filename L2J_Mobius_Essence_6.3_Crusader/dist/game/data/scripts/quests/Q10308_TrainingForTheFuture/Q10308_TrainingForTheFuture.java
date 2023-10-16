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
package quests.Q10308_TrainingForTheFuture;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.holders.ItemHolder;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.network.NpcStringId;
import org.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;

/**
 * @author Serenitty
 */
public class Q10308_TrainingForTheFuture extends Quest
{
	// NPCs
	private static final int ORVEN = 30857;
	private static final int TOKA = 34305;
	private static final int ERI = 34306;
	private static final int GROWN = 34307;
	private static final int TIND = 34308;
	// Monsters
	private static final int[] BOSS =
	{
		25952,
		25953,
		25954,
		25955,
		25963,
		25961,
		25962,
	};
	// Items
	private static final ItemHolder BOOST_ATK_SCROLL = new ItemHolder(94269, 5);
	private static final ItemHolder BOOST_DEF_SCROLL = new ItemHolder(94271, 5);
	private static final ItemHolder BERSERKER_SCROLL = new ItemHolder(94777, 5);
	private static final ItemHolder TRAINING_EXTRA_PASS = new ItemHolder(96941, 1);
	// Misc
	private static final int MIN_LEVEL = 76;
	
	public Q10308_TrainingForTheFuture()
	{
		super(10308);
		addStartNpc(ORVEN);
		addTalkId(ORVEN, GROWN, TIND, TOKA, ERI);
		addKillId(BOSS);
		addCondMinLevel(MIN_LEVEL, "30857-00.html");
		setQuestNameNpcStringId(NpcStringId.LV_76_TRAINING_FOR_THE_FUTURE_S_SAKE);
	}
	
	@Override
	public boolean checkPartyMember(Player member, Npc npc)
	{
		final QuestState qs = getQuestState(member, false);
		return ((qs != null) && qs.isStarted());
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		
		String htmltext = null;
		switch (event)
		{
			case "30857-01.htm":
			case "30857-02.htm":
			case "30857-03.htm":
			case "34305-01.html":
			case "34305-02.html":
			case "34306-01.html":
			case "34306-02.html":
			case "34307-01.html":
			case "34307-02.html":
			case "34308-01.html":
			case "34308-02.html":
			{
				htmltext = event;
				break;
			}
			case "30857-04.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "34307-03.html":
			{
				if (qs.isCond(1))
				{
					qs.setCond(2);
				}
				showOnScreenMsg(player, NpcStringId.HAVING_CHOSEN_THE_ZONE_FOR_YOUR_TRAINING_TALK_TO_TELEPORT_MENTOR_TIND, ExShowScreenMessage.TOP_CENTER, 10000);
				htmltext = event;
				break;
			}
			case "34308-03.html":
			{
				if (qs.isCond(2))
				{
					qs.setCond(3);
				}
				showOnScreenMsg(player, NpcStringId.TALK_TO_TELEPORT_MENTOR_TIND_AND_MOVE_TO_THE_TRAINING_ZONE, ExShowScreenMessage.TOP_CENTER, 10000);
				htmltext = event;
				break;
			}
			case "34305-03.html":
			{
				if (qs.isCond(3))
				{
					qs.setCond(4);
				}
				htmltext = event;
				break;
			}
			case "34306-03.html":
			{
				if (qs.isCond(4))
				{
					qs.setCond(5);
				}
				showOnScreenMsg(player, NpcStringId.HAVING_CHOSEN_THE_SUPPORT_MAGIC_YOU_NEED_DEFEAT_THE_MONSTERS_YOU_HAVE_DEFEAT_A_RAID_BOSS_WHICH_WILL_APPEAR_10_MIN_BEFORE_THE_TIME_RUNS_OUT, ExShowScreenMessage.TOP_CENTER, 10000);
				htmltext = event;
				break;
			}
			case "30857-06.html":
			{
				if (qs.isCond(6))
				{
					addExpAndSp(player, 100000000, 2700000);
					giveItems(player, BOOST_ATK_SCROLL);
					giveItems(player, BOOST_DEF_SCROLL);
					giveItems(player, BERSERKER_SCROLL);
					giveItems(player, TRAINING_EXTRA_PASS);
					qs.exitQuest(false, true);
					htmltext = event;
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
		
		if (qs.isCreated())
		{
			htmltext = "30857-01.htm";
		}
		else if (qs.isStarted())
		{
			switch (npc.getId())
			{
				case ORVEN:
				{
					if (qs.isCond(6))
					{
						htmltext = "30857-05.html";
					}
					break;
				}
				case GROWN:
				{
					if (qs.isCond(1))
					{
						htmltext = "34307-01.html";
					}
					break;
				}
				case TIND:
				{
					if (qs.isCond(2))
					{
						htmltext = "34308-01.html";
					}
					break;
				}
				case TOKA:
				{
					if (qs.isCond(3))
					{
						htmltext = "34305-01.html";
					}
					break;
				}
				case ERI:
				{
					if (qs.isCond(4))
					{
						htmltext = "34306-01.html";
					}
					else if (qs.isCond(5))
					{
						htmltext = "34306-04.html";
					}
					break;
				}
			}
		}
		else if (qs.isCompleted())
		{
			htmltext = getAlreadyCompletedMsg(player);
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isCond(5))
		{
			qs.setCond(6, true);
		}
		return super.onKill(npc, killer, isSummon);
	}
}
