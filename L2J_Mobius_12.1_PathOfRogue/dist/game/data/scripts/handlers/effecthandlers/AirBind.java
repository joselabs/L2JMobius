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
package handlers.effecthandlers;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.l2jmobius.gameserver.ai.CtrlEvent;
import org.l2jmobius.gameserver.enums.CategoryType;
import org.l2jmobius.gameserver.enums.ClassId;
import org.l2jmobius.gameserver.enums.Race;
import org.l2jmobius.gameserver.model.StatSet;
import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.effects.AbstractEffect;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.network.serverpackets.ExAlterSkillRequest;

/**
 * @author Mobius
 */
public class AirBind extends AbstractEffect
{
	private static final Set<Creature> ACTIVE_AIRBINDS = ConcurrentHashMap.newKeySet();
	private static final Map<ClassId, Integer> AIRBIND_SKILLS = new EnumMap<>(ClassId.class);
	static
	{
		AIRBIND_SKILLS.put(ClassId.SIGEL_PHOENIX_KNIGHT, 10249); // Heavy Hit
		AIRBIND_SKILLS.put(ClassId.SIGEL_HELL_KNIGHT, 10249); // Heavy Hit
		AIRBIND_SKILLS.put(ClassId.SIGEL_EVA_TEMPLAR, 10249); // Heavy Hit
		AIRBIND_SKILLS.put(ClassId.SIGEL_SHILLIEN_TEMPLAR, 10249); // Heavy Hit
		AIRBIND_SKILLS.put(ClassId.SIGEL_DEATH_KNIGHT, 10249); // Heavy Hit
		AIRBIND_SKILLS.put(ClassId.TYRR_DUELIST, 10499); // Heavy Hit
		AIRBIND_SKILLS.put(ClassId.TYRR_DREADNOUGHT, 10499); // Heavy Hit
		AIRBIND_SKILLS.put(ClassId.TYRR_TITAN, 10499); // Heavy Hit
		AIRBIND_SKILLS.put(ClassId.TYRR_GRAND_KHAVATARI, 10499); // Heavy Hit
		AIRBIND_SKILLS.put(ClassId.TYRR_MAESTRO, 10499); // Heavy Hit
		AIRBIND_SKILLS.put(ClassId.TYRR_DOOMBRINGER, 10499); // Heavy Hit
		AIRBIND_SKILLS.put(ClassId.OTHELL_ADVENTURER, 10749); // Heavy Hit
		AIRBIND_SKILLS.put(ClassId.OTHELL_WIND_RIDER, 10749); // Heavy Hit
		AIRBIND_SKILLS.put(ClassId.OTHELL_GHOST_HUNTER, 10749); // Heavy Hit
		AIRBIND_SKILLS.put(ClassId.OTHELL_FORTUNE_SEEKER, 10749); // Heavy Hit
		AIRBIND_SKILLS.put(ClassId.YUL_SAGITTARIUS, 10999); // Heavy Hit
		AIRBIND_SKILLS.put(ClassId.YUL_MOONLIGHT_SENTINEL, 10999); // Heavy Hit
		AIRBIND_SKILLS.put(ClassId.YUL_GHOST_SENTINEL, 10999); // Heavy Hit
		AIRBIND_SKILLS.put(ClassId.YUL_TRICKSTER, 10999); // Heavy Hit
		AIRBIND_SKILLS.put(ClassId.FEOH_ARCHMAGE, 11249); // Heavy Hit
		AIRBIND_SKILLS.put(ClassId.FEOH_SOULTAKER, 11249); // Heavy Hit
		AIRBIND_SKILLS.put(ClassId.FEOH_MYSTIC_MUSE, 11249); // Heavy Hit
		AIRBIND_SKILLS.put(ClassId.FEOH_STORM_SCREAMER, 11249); // Heavy Hit
		AIRBIND_SKILLS.put(ClassId.FEOH_SOUL_HOUND, 11249); // Heavy Hit
		AIRBIND_SKILLS.put(ClassId.ISS_HIEROPHANT, 11749); // Heavy Hit
		AIRBIND_SKILLS.put(ClassId.ISS_SWORD_MUSE, 11749); // Heavy Hit
		AIRBIND_SKILLS.put(ClassId.ISS_SPECTRAL_DANCER, 11749); // Heavy Hit
		AIRBIND_SKILLS.put(ClassId.ISS_DOMINATOR, 11749); // Heavy Hit
		AIRBIND_SKILLS.put(ClassId.ISS_DOOMCRYER, 11749); // Heavy Hit
		AIRBIND_SKILLS.put(ClassId.WYNN_ARCANA_LORD, 11499); // Heavy Hit
		AIRBIND_SKILLS.put(ClassId.WYNN_ELEMENTAL_MASTER, 11499); // Heavy Hit
		AIRBIND_SKILLS.put(ClassId.WYNN_SPECTRAL_MASTER, 11499); // Heavy Hit
		AIRBIND_SKILLS.put(ClassId.AEORE_CARDINAL, 11999); // Heavy Hit
		AIRBIND_SKILLS.put(ClassId.AEORE_EVA_SAINT, 11999); // Heavy Hit
		AIRBIND_SKILLS.put(ClassId.AEORE_SHILLIEN_SAINT, 11999); // Heavy Hit
		AIRBIND_SKILLS.put(ClassId.SHINE_MAKER, 11999); // Heavy Hit
	}
	
	public AirBind(StatSet params)
	{
	}
	
	@Override
	public boolean isInstant()
	{
		return false;
	}
	
	@Override
	public void continuousInstant(Creature effector, Creature effected, Skill skill, Item item)
	{
		if (!ACTIVE_AIRBINDS.contains(effected))
		{
			ACTIVE_AIRBINDS.add(effected);
			
			World.getInstance().forEachVisibleObjectInRange(effected, Player.class, 1200, nearby ->
			{
				if ((nearby.getRace() != Race.ERTHEIA) && (nearby.getTarget() == effected) && nearby.isInCategory(CategoryType.SIXTH_CLASS_GROUP) && !nearby.isAlterSkillActive())
				{
					final int chainSkill = AIRBIND_SKILLS.get(nearby.getClassId()).intValue();
					if (nearby.getSkillRemainingReuseTime(chainSkill) == -1)
					{
						nearby.sendPacket(new ExAlterSkillRequest(nearby, chainSkill, chainSkill, 5));
					}
				}
			});
		}
	}
	
	@Override
	public void onExit(Creature effector, Creature effected, Skill skill)
	{
		ACTIVE_AIRBINDS.remove(effected);
		
		if (!effected.isPlayer())
		{
			effected.getAI().notifyEvent(CtrlEvent.EVT_THINK);
		}
	}
}
