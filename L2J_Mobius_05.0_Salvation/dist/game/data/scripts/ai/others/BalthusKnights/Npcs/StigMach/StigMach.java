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
package ai.others.BalthusKnights.Npcs.StigMach;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.ListenerRegisterType;
import org.l2jmobius.gameserver.model.events.annotations.Id;
import org.l2jmobius.gameserver.model.events.annotations.RegisterEvent;
import org.l2jmobius.gameserver.model.events.annotations.RegisterType;
import org.l2jmobius.gameserver.model.events.impl.creature.npc.OnNpcMenuSelect;
import org.l2jmobius.gameserver.model.holders.SkillHolder;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;

import ai.AbstractNpcAI;

/**
 * Stig Mach AI
 * @author Kazumi
 */
public final class StigMach extends AbstractNpcAI
{
	// NPCs
	private static final int STIG = 34361;
	// Skills
	private static final SkillHolder SKILL_BALTHUS_KNIGHT_MEMBER = new SkillHolder(32130, 1);
	
	public StigMach()
	{
		addFirstTalkId(STIG);
	}
	
	@Override
	public final String onFirstTalk(Npc npc, Player player)
	{
		String htmltext = null;
		if (player.getVariables().getInt(PlayerVariables.BALTHUS_PHASE, 1) == 1)
		{
			htmltext = "stig001.htm";
		}
		else
		{
			htmltext = "stig002.htm";
		}
		return htmltext;
	}
	
	@RegisterEvent(EventType.ON_NPC_MENU_SELECT)
	@RegisterType(ListenerRegisterType.NPC)
	@Id(STIG)
	public final void onNpcMenuSelect(OnNpcMenuSelect event)
	{
		final Player talker = event.getTalker();
		final Npc npc = event.getNpc();
		final int ask = event.getAsk();
		final int reply = event.getReply();
		if ((ask == -10552) && (reply == 1))
		{
			npc.setTarget(talker);
			npc.doCast(SKILL_BALTHUS_KNIGHT_MEMBER.getSkill());
		}
	}
	
	public static void main(String[] args)
	{
		new StigMach();
	}
}