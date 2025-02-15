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
package org.l2jmobius.gameserver.model.holders;

import org.l2jmobius.gameserver.enums.UniqueGachaRank;

public class GachaItemTimeStampHolder extends GachaItemHolder
{
	private final long _timeStamp;
	private boolean _storedInDatabase;
	
	public GachaItemTimeStampHolder(int itemId, long itemCount, int enchantLevel, UniqueGachaRank rank, long timeStamp, boolean stored)
	{
		super(itemId, itemCount, 0, enchantLevel, rank);
		_timeStamp = timeStamp;
		_storedInDatabase = stored;
	}
	
	public long getTimeStamp()
	{
		return _timeStamp;
	}
	
	public int getTimeStampFromNow()
	{
		final long timeOfReceive = _timeStamp / 1000L;
		final long currentTime = System.currentTimeMillis() / 1000L;
		return (int) (currentTime - timeOfReceive);
	}
	
	public boolean getStoredStatus()
	{
		if (_storedInDatabase)
		{
			return true;
		}
		
		_storedInDatabase = true;
		return false;
	}
}
