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
package org.l2jmobius.gameserver.network.serverpackets;

import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;

/**
 * @author Sdw
 */
public class ExMagicAttackInfo extends ServerPacket
{
	// TODO: Enum
	public static final int CRITICAL = 1;
	public static final int CRITICAL_HEAL = 2;
	public static final int OVERHIT = 3;
	public static final int EVADED = 4;
	public static final int BLOCKED = 5;
	public static final int RESISTED = 6;
	public static final int IMMUNE = 7;
	public static final int IMMUNE2 = 8;
	public static final int PERFECTION = 9;
	public static final int P_CRITICAL = 10;
	public static final int M_CRITICAL = 11;
	
	private final int _caster;
	private final int _target;
	private final int _type;
	
	public ExMagicAttackInfo(int caster, int target, int type)
	{
		_caster = caster;
		_target = target;
		_type = type;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_MAGIC_ATTACK_INFO.writeId(this, buffer);
		buffer.writeInt(_caster);
		buffer.writeInt(_target);
		buffer.writeInt(_type);
	}
}