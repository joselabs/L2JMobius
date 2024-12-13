/*
 * Copyright (c) 2013 L2jMobius
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
 * IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package handlers.dailymissionhandlers;

import java.util.function.Consumer;

import org.l2jmobius.gameserver.enums.DailyMissionStatus;
import org.l2jmobius.gameserver.enums.ElementalType;
import org.l2jmobius.gameserver.handler.AbstractDailyMissionHandler;
import org.l2jmobius.gameserver.model.DailyMissionDataHolder;
import org.l2jmobius.gameserver.model.DailyMissionPlayerEntry;
import org.l2jmobius.gameserver.model.ElementalSpirit;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.events.Containers;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.impl.creature.OnElementalSpiritUpgrade;
import org.l2jmobius.gameserver.model.events.impl.creature.player.OnElementalSpiritLearn;
import org.l2jmobius.gameserver.model.events.listeners.ConsumerEventListener;

/**
 * @author JoeAlisson
 */
public class SpiritDailyMissionHandler extends AbstractDailyMissionHandler
{
	private final int _amount;
	private final ElementalType _type;
	
	public SpiritDailyMissionHandler(DailyMissionDataHolder holder)
	{
		super(holder);
		_type = getHolder().getParams().getEnum("element", ElementalType.class, ElementalType.NONE);
		_amount = holder.getRequiredCompletions();
	}
	
	@Override
	public void init()
	{
		final MissionKind kind = getHolder().getParams().getEnum("kind", MissionKind.class, null);
		if (MissionKind.EVOLVE == kind)
		{
			Containers.Players().addListener(new ConsumerEventListener(this, EventType.ON_ELEMENTAL_SPIRIT_UPGRADE, (Consumer<OnElementalSpiritUpgrade>) this::onElementalSpiritUpgrade, this));
		}
		else if (MissionKind.LEARN == kind)
		{
			Containers.Players().addListener(new ConsumerEventListener(this, EventType.ON_ELEMENTAL_SPIRIT_LEARN, (Consumer<OnElementalSpiritLearn>) this::onElementalSpiritLearn, this));
		}
	}
	
	@Override
	public boolean isAvailable(Player player)
	{
		final DailyMissionPlayerEntry entry = getPlayerEntry(player.getObjectId(), false);
		return (entry != null) && (entry.getStatus() == DailyMissionStatus.AVAILABLE);
	}
	
	private void onElementalSpiritLearn(OnElementalSpiritLearn event)
	{
		final DailyMissionPlayerEntry missionData = getPlayerEntry(event.getPlayer().getObjectId(), true);
		missionData.setProgress(1);
		missionData.setStatus(DailyMissionStatus.AVAILABLE);
		storePlayerEntry(missionData);
	}
	
	private void onElementalSpiritUpgrade(OnElementalSpiritUpgrade event)
	{
		final ElementalSpirit spirit = event.getSpirit();
		if (ElementalType.of(spirit.getType()) != _type)
		{
			return;
		}
		
		final DailyMissionPlayerEntry missionData = getPlayerEntry(event.getPlayer().getObjectId(), true);
		missionData.setProgress(spirit.getStage());
		if (missionData.getProgress() >= _amount)
		{
			missionData.setStatus(DailyMissionStatus.AVAILABLE);
		}
		storePlayerEntry(missionData);
	}
	
	private enum MissionKind
	{
		LEARN,
		EVOLVE
	}
}
