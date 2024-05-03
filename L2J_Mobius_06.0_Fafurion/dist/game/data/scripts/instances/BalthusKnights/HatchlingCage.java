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
package instances.BalthusKnights;

import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.instancezone.Instance;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.network.NpcStringId;
import org.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2jmobius.gameserver.network.serverpackets.ExTutorialShowId;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

import instances.AbstractInstance;
import quests.Q10553_WhatMattersMoreThanAbility.Q10553_WhatMattersMoreThanAbility;

/**
 * Hatchling Cage instance zone.
 * @author Kazumi
 */
public final class HatchlingCage extends AbstractInstance
{
	// MOBs
	private static final int HATCHLING = 24089;
	private static final int GEM_DRAGON = 24097;
	// Misc
	private static final int TEMPLATE_ID = 270;
	
	public HatchlingCage()
	{
		super(TEMPLATE_ID);
		addKillId(HATCHLING, GEM_DRAGON);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		String htmltext = null;
		if (event.equals("enterInstance"))
		{
			final QuestState qs = player.getQuestState(Q10553_WhatMattersMoreThanAbility.class.getSimpleName());
			if ((qs != null) && qs.isStarted())
			{
				enterInstance(player, npc, TEMPLATE_ID);
			}
		}
		return htmltext;
	}
	
	@Override
	protected void onEnter(Player player, Instance instance, boolean firstEnter)
	{
		showOnScreenMsg(instance, NpcStringId.ATTACK_A_HATCHLING, ExShowScreenMessage.TOP_CENTER, 30000, false);
		super.onEnter(player, instance, firstEnter);
	}
	
	@Override
	public String onKill(Npc npc, Player player, boolean isSummon)
	{
		final Instance instance = player.getInstanceWorld();
		final QuestState qs = player.getQuestState(Q10553_WhatMattersMoreThanAbility.class.getSimpleName());
		
		if ((instance != null) && (instance.getTemplateId() == TEMPLATE_ID))
		{
			if (qs.isCond(2))
			{
				if (instance.getAliveNpcs(HATCHLING).isEmpty())
				{
					final ServerPacket packet = new ExTutorialShowId(15);
					player.sendPacket(packet);
					
					showOnScreenMsg(player, NpcStringId.PRESS_ALT_K_TO_OPEN_THE_SKILL_WINDOW_NYOU_CAN_ADD_THE_SKILLS_IN_THE_ACTIVE_TAB_TO_THE_SHORTCUTS, ExShowScreenMessage.TOP_CENTER, 8000, false);
					ThreadPool.schedule(() ->
					{
						instance.spawnGroup("balthus_cage_2523_01m2");
						showOnScreenMsg(player, NpcStringId.USE_A_SKILL_ON_THE_GEM_DRAGON, ExShowScreenMessage.TOP_CENTER, 5000, false);
					}, 5000); // 5 sec
				}
			}
			else if (qs.isCond(3))
			{
				if (instance.getAliveNpcs(GEM_DRAGON).isEmpty())
				{
					instance.finishInstance(0);
				}
			}
		}
		return super.onKill(npc, player, isSummon);
	}
	
	public static void main(String[] args)
	{
		new HatchlingCage();
	}
}