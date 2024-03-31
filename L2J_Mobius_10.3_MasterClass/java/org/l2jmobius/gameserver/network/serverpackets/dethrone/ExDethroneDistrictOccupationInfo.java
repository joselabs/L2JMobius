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
package org.l2jmobius.gameserver.network.serverpackets.dethrone;

import org.l2jmobius.Config;
import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.gameserver.instancemanager.GlobalVariablesManager;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * @author CostyKiller
 */
public class ExDethroneDistrictOccupationInfo extends ServerPacket
{
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_DETHRONE_DISTRICT_OCCUPATION_INFO.writeId(this, buffer);
		buffer.writeInt(3); // array size for conquest regions (currently 3)
		
		// Zone 1 ASA
		buffer.writeInt(1); // zone name id for ASA
		buffer.writeInt(Config.SERVER_ID); // server id for ASA ZONE CONQUERER
		buffer.writeInt(1); // array size for ASA servers (only one server atm)
		
		buffer.writeInt(1); // server rank 1
		buffer.writeInt(Config.SERVER_ID); // rank 1 server id
		buffer.writeInt(GlobalVariablesManager.getInstance().getInt("CONQUEST_ZONE_ASA_POINTS", 0)); // rank 1 server points
		buffer.writeInt(0);
		
		// buffer.writeInt(2); // server rank 2
		// buffer.writeInt(0); // rank 2 server id
		// buffer.writeInt(0); // rank 2 server points
		// buffer.writeInt(0);
		
		// Zone 2 ANIMA
		buffer.writeInt(2); // zone name id for ANIMA
		buffer.writeInt(Config.SERVER_ID); // server id for ANIMA ZONE CONQUERER
		buffer.writeInt(1); // array size for ANIMA servers (only one server atm)
		
		buffer.writeInt(1); // server rank 1
		buffer.writeInt(Config.SERVER_ID); // rank 1 server id
		buffer.writeInt(GlobalVariablesManager.getInstance().getInt("CONQUEST_ZONE_ANIMA_POINTS", 0)); // rank 1 server points
		buffer.writeInt(0);
		
		// buffer.writeInt(2); // server rank 2
		// buffer.writeInt(0); // rank 2 server id
		// buffer.writeInt(0); // rank 2 server points
		// buffer.writeInt(0);
		
		// Zone 3 NOX
		buffer.writeInt(3); // zone name id for NOX
		buffer.writeInt(Config.SERVER_ID); // server id for NOX ZONE CONQUERER
		buffer.writeInt(1); // array size for NOX servers (only one server atm)
		
		buffer.writeInt(1); // server rank 1
		buffer.writeInt(Config.SERVER_ID); // rank 1 server id
		buffer.writeInt(GlobalVariablesManager.getInstance().getInt("CONQUEST_ZONE_NOX_POINTS", 0)); // rank 1 server points
		buffer.writeInt(0);
		
		// buffer.writeInt(2); // server rank 2
		// buffer.writeInt(0); // rank 2 server id
		// buffer.writeInt(0); // rank 2 server points
		// buffer.writeInt(0);
	}
}