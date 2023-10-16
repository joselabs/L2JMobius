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
package ai.others.MagicLampGenieLetter;

import java.util.Collection;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.ListenerRegisterType;
import org.l2jmobius.gameserver.model.events.annotations.RegisterEvent;
import org.l2jmobius.gameserver.model.events.annotations.RegisterType;
import org.l2jmobius.gameserver.model.events.impl.creature.player.OnPlayerBypass;
import org.l2jmobius.gameserver.model.events.impl.creature.player.OnPlayerLevelChanged;
import org.l2jmobius.gameserver.model.events.impl.creature.player.OnPlayerLogin;
import org.l2jmobius.gameserver.model.events.impl.creature.player.OnPlayerPressTutorialMark;
import org.l2jmobius.gameserver.model.holders.ItemHolder;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.network.serverpackets.TutorialShowQuestionMark;

import ai.AbstractNpcAI;

/**
 * @author Serenitty
 */
public class MagicLampGenieLetter extends AbstractNpcAI
{
	// NPC
	private static final int GENIE_LAMP_NPC = 34369;
	// Item
	private static final ItemHolder GENIE_LAMP = new ItemHolder(97943, 1);
	// Misc
	private static final int LEVEL_MIN = 52;
	private static final String TUTORIAL_BYPASS = "Quest MagicLampGenieLetter ";
	
	public MagicLampGenieLetter()
	{
		addFirstTalkId(GENIE_LAMP_NPC);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = null;
		switch (event)
		{
			case "34369-0.html":
			{
				htmltext = event;
				break;
			}
			case "giveLamp":
			{
				if (player.getInventory().getAllItemsByItemId(GENIE_LAMP.getId()).isEmpty())
				{
					giveItems(player, GENIE_LAMP);
					htmltext = "34369-0.html";
				}
				else
				{
					htmltext = event;
				}
				break;
			}
			case "close":
			{
				htmltext = event;
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		final Collection<Quest> questList;
		questList = player.getAllActiveQuests();
		for (Quest quest : questList)
		{
			if ((quest.getId() >= 401) && (quest.getId() <= 469))
			{
				return npc.getId() + "-2.html";
			}
		}
		return npc.getId() + "-1.html";
		
	}
	
	@RegisterEvent(EventType.ON_PLAYER_BYPASS)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onPlayerBypass(OnPlayerBypass event)
	{
		final Player player = event.getPlayer();
		if (event.getCommand().startsWith(TUTORIAL_BYPASS))
		{
			notifyEvent(event.getCommand().replace(TUTORIAL_BYPASS, ""), null, player);
		}
	}
	
	@RegisterEvent(EventType.ON_PLAYER_LEVEL_CHANGED)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onPlayerLevelChanged(OnPlayerLevelChanged event)
	{
		final Player player = event.getPlayer();
		if (player == null)
		{
			return;
		}
		
		if ((player.getLevel() < LEVEL_MIN))
		{
			return;
		}
		
		if (!player.getInventory().getAllItemsByItemId(GENIE_LAMP.getId()).isEmpty())
		{
			return;
		}
		
		player.sendPacket(new TutorialShowQuestionMark(220615, 1));
	}
	
	@RegisterEvent(EventType.ON_PLAYER_LOGIN)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onPlayerLogin(OnPlayerLogin event)
	{
		final Player player = event.getPlayer();
		if (player == null)
		{
			return;
		}
		
		if ((player.getLevel() < LEVEL_MIN))
		{
			return;
		}
		
		if (!player.getInventory().getAllItemsByItemId(GENIE_LAMP.getId()).isEmpty())
		{
			return;
		}
		
		player.sendPacket(new TutorialShowQuestionMark(220615, 1));
	}
	
	@RegisterEvent(EventType.ON_PLAYER_PRESS_TUTORIAL_MARK)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onPlayerPressTutorialMark(OnPlayerPressTutorialMark event)
	{
		if (event.getMarkId() != 220615)
		{
			return;
		}
		
		final Player player = event.getPlayer();
		final String html = getHtm(player, "34369-0.html");
		if (html != null)
		{
			showResult(event.getPlayer(), html);
		}
	}
	
	public static void main(String[] args)
	{
		new MagicLampGenieLetter();
	}
}
