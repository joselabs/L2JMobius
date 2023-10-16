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
package org.l2jmobius.gameserver.handler.voicedcommandhandlers;

import org.l2jmobius.Config;
import org.l2jmobius.gameserver.handler.IVoicedCommandHandler;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.zone.ZoneId;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.ActionFailed;
import org.l2jmobius.gameserver.network.serverpackets.ConfirmDlg;
import org.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;

/**
 * @author Mobius
 */
public class OfflinePlay implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"offlineplay"
	};
	
	@Override
	public boolean useVoicedCommand(String command, Player player, String target)
	{
		if (command.equals("offlineplay") && Config.ENABLE_OFFLINE_PLAY_COMMAND)
		{
			if (!player.isAutoPlaying())
			{
				player.sendPacket(new ExShowScreenMessage("You need to enable auto play before exiting.", 5000));
				player.sendMessage("You need to enable auto play before exiting.");
				player.sendPacket(ActionFailed.STATIC_PACKET);
				return false;
			}
			
			if (player.isInBoat() || player.isInsideZone(ZoneId.PEACE))
			{
				player.sendPacket(new ExShowScreenMessage("You may not log out from this location.", 5000));
				player.sendPacket(SystemMessageId.YOU_MAY_NOT_LOG_OUT_FROM_THIS_LOCATION);
				player.sendPacket(ActionFailed.STATIC_PACKET);
				return false;
			}
			
			if (player.isRegisteredOnEvent())
			{
				player.sendPacket(new ExShowScreenMessage("Cannot use this command while registered on an event.", 5000));
				player.sendMessage("Cannot use this command while registered on an event.");
				player.sendPacket(ActionFailed.STATIC_PACKET);
				return false;
			}
			
			player.getVariables().set("ASK_AUTO_PLAY", true);
			player.sendPacket(new ConfirmDlg("Do you wish to exit and continue auto play?"));
		}
		
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}