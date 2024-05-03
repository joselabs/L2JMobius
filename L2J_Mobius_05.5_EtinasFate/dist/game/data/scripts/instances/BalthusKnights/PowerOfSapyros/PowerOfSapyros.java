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
package instances.BalthusKnights.PowerOfSapyros;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.instancezone.Instance;
import org.l2jmobius.gameserver.model.quest.QuestState;

import instances.AbstractInstance;
import quests.Q10556_ForgottenPowerStartOfFate.Q10556_ForgottenPowerStartOfFate;

/**
 * Power of Sapyros instance zone.
 * @author Kazumi
 */
public final class PowerOfSapyros extends AbstractInstance
{
	// NPCs
	private static final int MEELE_DAMAGE_DEALER = 34387;
	// Misc
	private static final int TEMPLATE_ID = 273;
	
	public PowerOfSapyros()
	{
		super(TEMPLATE_ID);
		addFirstTalkId(MEELE_DAMAGE_DEALER);
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
					return "human_select_dummy.htm";
				}
				case ORC:
				{
					return "orc_select_dummy.htm";
				}
				case DWARF:
				{
					return "awakening_symbol2156.htm";
				}
				case KAMAEL:
				{
					return "awakening_symbol2157.htm";
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
			case "awakening_symbol2152.htm":
			case "awakening_symbol2153.htm":
			case "awakening_symbol2154.htm":
			case "awakening_symbol2155.htm":
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
		new PowerOfSapyros();
	}
}