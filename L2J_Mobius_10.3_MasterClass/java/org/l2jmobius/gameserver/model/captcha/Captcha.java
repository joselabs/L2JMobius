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
package org.l2jmobius.gameserver.model.captcha;

/**
 * @author JoeAlisson
 */
public class Captcha
{
	private final int _code;
	private final byte[] _data;
	private final int _id;
	
	public Captcha(int id, int code, byte[] data)
	{
		_id = id;
		_code = code;
		_data = data;
	}
	
	public int getCode()
	{
		return _code;
	}
	
	public byte[] getData()
	{
		return _data;
	}
	
	public int getId()
	{
		return _id;
	}
}
