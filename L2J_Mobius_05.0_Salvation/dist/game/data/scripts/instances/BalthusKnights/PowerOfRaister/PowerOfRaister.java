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
package instances.BalthusKnights.PowerOfRaister;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.instancezone.Instance;
import org.l2jmobius.gameserver.model.quest.QuestState;

import instances.AbstractInstance;
import quests.Q10556_ForgottenPowerStartOfFate.Q10556_ForgottenPowerStartOfFate;

/**
 * Power of Raister instance zone.
 * @author Kazumi
 */
public final class PowerOfRaister extends AbstractInstance
{
	// NPCs
	private static final int BUFF_MASTER = 34391;
	// Misc
	private static final int TEMPLATE_ID = 277;
	
	public PowerOfRaister()
	{
		super(TEMPLATE_ID);
		addFirstTalkId(BUFF_MASTER);
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		final Instance instance = npc.getInstanceWorld();
		if (instance != null)
		{
			switch (player.getRace())
			{
				case HUMAN:
				{
					return "awakening_symbol6171.htm";
				}
				case ELF:
				{
					return "awakening_symbol6172.htm";
				}
				case DARK_ELF:
				{
					return "awakening_symbol6173.htm";
				}
				case ORC:
				{
					return "orc_select_dummy.htm";
				}
			}
		}
		return null;
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		String htmltext = null;
		
		switch (event)
		{
			case "awakening_symbol6174.htm":
			case "awakening_symbol6175.htm":
			{
				htmltext = event;
				break;
			}
			case "enterInstance":
			{
				final QuestState qs = player.getQuestState(Q10556_ForgottenPowerStartOfFate.class.getSimpleName());
				if ((qs != null) && qs.isStarted())
				{
					enterInstance(player, npc, TEMPLATE_ID);
				}
			}
		}
		return htmltext;
	}
	
	public static void main(String[] args)
	{
		new PowerOfRaister();
	}
}