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
package org.l2jmobius.gameserver.network.clientpackets.dethrone;

import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.network.clientpackets.ClientPacket;
import org.l2jmobius.gameserver.network.serverpackets.dethrone.ExDethroneChangeName;

/**
 * @author Negrito8
 */
public class RequestExDethroneChangeName extends ClientPacket
{
	private String _conquestName;
	
	@Override
	protected void readImpl()
	{
		_conquestName = readString();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (player.getVariables().hasVariable(PlayerVariables.CONQUEST_NAME))
		{
			final boolean success = player.destroyItemByItemId("ConquestNameChange", 81979, 1, player, true);
			player.sendPacket(new ExDethroneChangeName(_conquestName, success));
			if (!success)
			{
				return;
			}
		}
		else
		{
			player.sendPacket(new ExDethroneChangeName(_conquestName, true));
		}
		player.getVariables().set(PlayerVariables.CONQUEST_NAME, _conquestName);
		player.getVariables().storeMe();
	}
}
