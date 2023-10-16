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
package org.l2jmobius.gameserver.network.clientpackets;

import org.l2jmobius.Config;
import org.l2jmobius.commons.network.ReadablePacket;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.olympiad.Olympiad;
import org.l2jmobius.gameserver.model.zone.ZoneId;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.SystemMessageId;

/**
 * @author Dezmond_snz
 */
public class DlgAnswer implements ClientPacket
{
	private int _messageId;
	private int _answer;
	// private int _requesterId;
	
	@Override
	public void read(ReadablePacket packet)
	{
		_messageId = packet.readInt();
		_answer = packet.readInt();
		// _requesterId = packet.readInt();
	}
	
	@Override
	public void run(GameClient client)
	{
		final Player player = client.getPlayer();
		if (player == null)
		{
			return;
		}
		
		// final Long answerTime = player.getConfirmDlgRequestTime(_requesterId);
		// if ((_answer == 1) && (answerTime != null) && (System.currentTimeMillis() > answerTime))
		// {
		// _answer = 0;
		// }
		// player.removeConfirmDlgRequestTime(_requesterId);
		
		if (_messageId == SystemMessageId.S1_IS_MAKING_AN_ATTEMPT_AT_RESURRECTION_DO_YOU_WANT_TO_CONTINUE_WITH_THIS_RESURRECTION.getId())
		{
			player.reviveAnswer(_answer);
		}
		// else if (_messageId == SystemMessageId.S1_WISHES_TO_SUMMON_YOU_FROM_S2_DO_YOU_ACCEPT.getId())
		// {
		// player.teleportAnswer(_answer, _requesterId);
		// }
		else if (_messageId == SystemMessageId.WOULD_YOU_LIKE_TO_OPEN_THE_GATE.getId())
		{
			player.gatesAnswer(_answer, 1);
		}
		else if (_messageId == SystemMessageId.WOULD_YOU_LIKE_TO_CLOSE_THE_GATE.getId())
		{
			player.gatesAnswer(_answer, 0);
		}
		else if (_messageId == SystemMessageId.S1_S2.getId())
		{
			// Custom .offlineplay voiced command dialog.
			if (player.getVariables().getBoolean("ASK_AUTO_PLAY", false))
			{
				player.getVariables().remove("ASK_AUTO_PLAY");
				
				if ((_answer == 0) || !Config.ENABLE_OFFLINE_PLAY_COMMAND)
				{
					return;
				}
				
				if (!player.isAutoPlaying())
				{
					player.sendMessage("You need to enable auto play before exiting.");
					return;
				}
				
				if (player.isInBoat() || player.isInsideZone(ZoneId.PEACE))
				{
					player.sendPacket(SystemMessageId.YOU_MAY_NOT_LOG_OUT_FROM_THIS_LOCATION);
					return;
				}
				
				if (player.isRegisteredOnEvent())
				{
					player.sendMessage("Cannot use this command while registered on an event.");
					return;
				}
				
				// Unregister from olympiad.
				if (Olympiad.getInstance().isRegistered(player))
				{
					Olympiad.getInstance().unRegisterNoble(player);
				}
				
				player.startOfflinePlay();
			}
			else if (Config.ALLOW_WEDDING)
			{
				player.engageAnswer(_answer);
			}
		}
		else if (_messageId == SystemMessageId.S1.getId())
		{
			if (player.dialog != null)
			{
				player.dialog.onDlgAnswer(player);
				player.dialog = null;
			}
		}
	}
}