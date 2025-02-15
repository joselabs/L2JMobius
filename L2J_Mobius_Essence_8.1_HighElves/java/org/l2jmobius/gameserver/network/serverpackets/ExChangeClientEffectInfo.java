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
 * @author UnAfraid, Mobius
 */
public class ExChangeClientEffectInfo extends ServerPacket
{
	public static final ExChangeClientEffectInfo STATIC_FREYA_DEFAULT = new ExChangeClientEffectInfo(0, 0, 1);
	public static final ExChangeClientEffectInfo STATIC_FREYA_DESTROYED = new ExChangeClientEffectInfo(0, 0, 2);
	public static final ExChangeClientEffectInfo GIRAN_NORMAL = new ExChangeClientEffectInfo(0, 0, 1);
	public static final ExChangeClientEffectInfo GIRAN_PETALS = new ExChangeClientEffectInfo(0, 0, 2);
	public static final ExChangeClientEffectInfo GIRAN_SNOW = new ExChangeClientEffectInfo(0, 0, 3);
	public static final ExChangeClientEffectInfo GIRAN_FLOWERS = new ExChangeClientEffectInfo(0, 0, 4);
	
	private final int _type;
	private final int _key;
	private final int _value;
	
	/**
	 * @param type
	 *            <ul>
	 *            <li>0 - ChangeZoneState</li>
	 *            <li>1 - SetL2Fog</li>
	 *            <li>2 - postEffectData</li>
	 *            </ul>
	 * @param key
	 * @param value
	 */
	public ExChangeClientEffectInfo(int type, int key, int value)
	{
		_type = type;
		_key = key;
		_value = value;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_CHANGE_CLIENT_EFFECT_INFO.writeId(this, buffer);
		buffer.writeInt(_type);
		buffer.writeInt(_key);
		buffer.writeInt(_value);
	}
}
