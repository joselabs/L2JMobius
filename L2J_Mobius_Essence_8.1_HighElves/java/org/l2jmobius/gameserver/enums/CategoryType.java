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
package org.l2jmobius.gameserver.enums;

/**
 * This class defines all category types.
 * @author xban1x
 */
public enum CategoryType
{
	FIGHTER_GROUP,
	MAGE_GROUP,
	WIZARD_GROUP,
	CLERIC_GROUP,
	ATTACKER_GROUP,
	FIRST_CLASS_GROUP,
	SECOND_CLASS_GROUP,
	THIRD_CLASS_GROUP,
	FOURTH_CLASS_GROUP,
	BOUNTY_HUNTER_GROUP,
	WARSMITH_GROUP,
	STRIDER,
	WOLF_GROUP,
	WYVERN_GROUP,
	PET_GROUP,
	SUBJOB_GROUP_KNIGHT,
	HUMAN_FALL_CLASS,
	HUMAN_MALL_CLASS,
	HUMAN_CALL_CLASS,
	ELF_FALL_CLASS,
	ELF_MALL_CLASS,
	ELF_CALL_CLASS,
	ORC_FALL_CLASS,
	ORC_MALL_CLASS,
	BEGINNER_MAGE,
	SUB_GROUP_ROGUE,
	SUB_GROUP_KNIGHT,
	SUB_GROUP_HEC,
	SUB_GROUP_HEW,
	SUB_GROUP_HEF,
	SUB_GROUP_ORC,
	SUB_GROUP_WARE,
	SUB_GROUP_BLACK,
	SUB_GROUP_DE,
	DEATH_KNIGHT_ALL_CLASS,
	SYLPH_ALL_CLASS,
	VANGUARD_ALL_CLASS,
	ASSASSIN_ALL_CLASS,
	HIGH_ELF_WEAVER,
	HIGH_ELF_TEMPLAR,
	HIGH_ELF_ALL_CLASS;
	
	/**
	 * Finds category by it's name
	 * @param categoryName
	 * @return A {@code CategoryType} if category was found, {@code null} if category was not found
	 */
	public static CategoryType findByName(String categoryName)
	{
		for (CategoryType type : values())
		{
			if (type.name().equalsIgnoreCase(categoryName))
			{
				return type;
			}
		}
		return null;
	}
}
