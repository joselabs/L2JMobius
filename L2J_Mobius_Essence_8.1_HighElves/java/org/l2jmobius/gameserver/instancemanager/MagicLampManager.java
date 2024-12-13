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
package org.l2jmobius.gameserver.instancemanager;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.l2jmobius.Config;
import org.l2jmobius.commons.util.Rnd;
import org.l2jmobius.gameserver.data.xml.MagicLampData;
import org.l2jmobius.gameserver.enums.LampType;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.holders.MagicLampDataHolder;
import org.l2jmobius.gameserver.model.holders.MagicLampHolder;
import org.l2jmobius.gameserver.model.skill.CommonSkill;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.model.stats.Stat;
import org.l2jmobius.gameserver.network.serverpackets.magiclamp.ExMagicLampInfo;
import org.l2jmobius.gameserver.network.serverpackets.magiclamp.ExMagicLampResult;

/**
 * @author Serenitty
 */
public class MagicLampManager
{
	private static final List<MagicLampDataHolder> REWARDS = MagicLampData.getInstance().getLamps();
	private static final int REWARD_COUNT = 1;
	private final int FLAT_BONUS = 100000;
	
	public MagicLampManager()
	{
	}
	
	public void useMagicLamp(Player player)
	{
		if (REWARDS.isEmpty())
		{
			return;
		}
		
		final Map<LampType, MagicLampHolder> rewards = new EnumMap<>(LampType.class);
		int count = 0;
		while (count == 0) // There should be at least one Magic Lamp reward.
		{
			for (MagicLampDataHolder lamp : REWARDS)
			{
				if ((lamp.getFromLevel() <= player.getLevel()) && (player.getLevel() <= lamp.getToLevel()) && (Rnd.get(100d) < lamp.getChance()))
				{
					rewards.computeIfAbsent(lamp.getType(), list -> new MagicLampHolder(lamp)).inc();
					if (++count >= REWARD_COUNT)
					{
						break;
					}
				}
			}
		}
		
		rewards.values().forEach(lamp ->
		{
			final int exp = (int) lamp.getExp();
			final int sp = (int) lamp.getSp();
			player.addExpAndSp(exp, sp);
			
			final LampType lampType = lamp.getType();
			player.sendPacket(new ExMagicLampResult(exp, lampType.getGrade()));
			player.sendPacket(new ExMagicLampInfo(player));
			manageSkill(player, lampType);
		});
	}
	
	public void addLampExp(Player player, double exp, boolean rateModifiers)
	{
		if (Config.ENABLE_MAGIC_LAMP)
		{
			final int lampExp = (int) ((exp * player.getStat().getExpBonusMultiplier()) * (rateModifiers ? Config.MAGIC_LAMP_CHARGE_RATE * player.getStat().getMul(Stat.MAGIC_LAMP_EXP_RATE, 1) : 1));
			int calc = lampExp + player.getLampExp();
			if (player.getLevel() < 64)
			{
				calc = calc + FLAT_BONUS;
			}
			
			if (calc > Config.MAGIC_LAMP_MAX_LEVEL_EXP)
			{
				calc %= Config.MAGIC_LAMP_MAX_LEVEL_EXP;
				useMagicLamp(player);
			}
			player.setLampExp(calc);
			player.sendPacket(new ExMagicLampInfo(player));
		}
	}
	
	private void manageSkill(Player player, LampType lampType)
	{
		final Skill lampSkill;
		
		switch (lampType)
		{
			case RED:
			{
				lampSkill = CommonSkill.RED_LAMP.getSkill();
				break;
			}
			case PURPLE:
			{
				lampSkill = CommonSkill.PURPLE_LAMP.getSkill();
				break;
			}
			case BLUE:
			{
				lampSkill = CommonSkill.BLUE_LAMP.getSkill();
				break;
			}
			case GREEN:
			{
				lampSkill = CommonSkill.GREEN_LAMP.getSkill();
				break;
			}
			default:
			{
				lampSkill = null;
				break;
			}
		}
		
		if (lampSkill != null)
		{
			player.breakAttack(); // *TODO Stop Autohunt only for cast a skill?, nope.
			player.breakCast();
			
			player.doCast(lampSkill);
		}
	}
	
	public static MagicLampManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final MagicLampManager INSTANCE = new MagicLampManager();
	}
}
