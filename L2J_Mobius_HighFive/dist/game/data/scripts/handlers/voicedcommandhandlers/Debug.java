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
package handlers.voicedcommandhandlers;

import com.l2jmobius.gameserver.data.xml.impl.AdminData;
import com.l2jmobius.gameserver.handler.IVoicedCommandHandler;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;

public class Debug implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"debug"
	};
	
	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String params)
	{
		if (!AdminData.getInstance().hasAccess(command, activeChar.getAccessLevel()))
		{
			return false;
		}
		
		if (VOICED_COMMANDS[0].equalsIgnoreCase(command))
		{
			if (activeChar.isDebug())
			{
				activeChar.setDebug(null);
				activeChar.sendMessage("Debugging disabled.");
			}
			else
			{
				activeChar.setDebug(activeChar);
				activeChar.sendMessage("Debugging enabled.");
			}
		}
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}