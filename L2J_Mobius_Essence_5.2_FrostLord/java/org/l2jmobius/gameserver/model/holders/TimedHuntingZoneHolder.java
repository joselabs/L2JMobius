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

import org.l2jmobius.gameserver.model.Location;

/**
 * @author Mobius
 */
public class TimedHuntingZoneHolder
{
	private final int _id;
	private final String _name;
	private final int _initialTime;
	private final int _maximumAddedTime;
	private final int _resetDelay;
	private final int _entryItemId;
	private final int _entryFee;
	private final int _minLevel;
	private final int _maxLevel;
	private final int _remainRefillTime;
	private final int _refillTimeMax;
	private final boolean _pvpZone;
	private final boolean _noPvpZone;
	private final int _instanceId;
	private final boolean _soloInstance;
	private final boolean _weekly;
	private final boolean _useWorldPrefix;
	private final boolean _zonePremiumUserOnly;
	private final Location _enterLocation;
	private final Location _exitLocation;
	
	public TimedHuntingZoneHolder(int id, String name, int initialTime, int maximumAddedTime, int resetDelay, int entryItemId, int entryFee, int minLevel, int maxLevel, int remainRefillTime, int refillTimeMax, boolean pvpZone, boolean noPvpZone, int instanceId, boolean soloInstance, boolean weekly, boolean useWorldPrefix, boolean zonePremiumUserOnly, Location enterLocation, Location exitLocation)
	{
		_id = id;
		_name = name;
		_initialTime = initialTime;
		_maximumAddedTime = maximumAddedTime;
		_resetDelay = resetDelay;
		_entryItemId = entryItemId;
		_entryFee = entryFee;
		_minLevel = minLevel;
		_maxLevel = maxLevel;
		_remainRefillTime = remainRefillTime;
		_refillTimeMax = refillTimeMax;
		_pvpZone = pvpZone;
		_noPvpZone = noPvpZone;
		_instanceId = instanceId;
		_soloInstance = soloInstance;
		_weekly = weekly;
		_useWorldPrefix = useWorldPrefix;
		_zonePremiumUserOnly = zonePremiumUserOnly;
		_enterLocation = enterLocation;
		_exitLocation = exitLocation;
	}
	
	public int getZoneId()
	{
		return _id;
	}
	
	public String getZoneName()
	{
		return _name;
	}
	
	public int getInitialTime()
	{
		return _initialTime;
	}
	
	public int getMaximumAddedTime()
	{
		return _maximumAddedTime;
	}
	
	public int getResetDelay()
	{
		return _resetDelay;
	}
	
	public int getEntryItemId()
	{
		return _entryItemId;
	}
	
	public int getEntryFee()
	{
		return _entryFee;
	}
	
	public int getMinLevel()
	{
		return _minLevel;
	}
	
	public int getMaxLevel()
	{
		return _maxLevel;
	}
	
	public int getRemainRefillTime()
	{
		return _remainRefillTime;
	}
	
	public int getRefillTimeMax()
	{
		return _refillTimeMax;
	}
	
	public boolean isPvpZone()
	{
		return _pvpZone;
	}
	
	public boolean isNoPvpZone()
	{
		return _noPvpZone;
	}
	
	public int getInstanceId()
	{
		return _instanceId;
	}
	
	public boolean isSoloInstance()
	{
		return _soloInstance;
	}
	
	public boolean isWeekly()
	{
		return _weekly;
	}
	
	public boolean useWorldPrefix()
	{
		return _useWorldPrefix;
	}
	
	public boolean zonePremiumUserOnly()
	{
		return _zonePremiumUserOnly;
	}
	
	public Location getEnterLocation()
	{
		return _enterLocation;
	}
	
	public Location getExitLocation()
	{
		return _exitLocation;
	}
}
