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
package ai.areas.Gludio;

import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;

import ai.AbstractNpcAI;

/**
 * @author Notorion
 */
public class LizardmanTempleXPParty extends AbstractNpcAI
{
	// Monsters Lizardman Temple
	private static final int[] MONSTER_IDS =
	{
		24687,
		24688,
		24689,
		24690,
		24691,
		24692,
		24693,
		24694
	};
	// Distance radius for XP bonus Party
	private static final int BONUS_RADIUS = 1500;
	// Maximum XP Bonus (Level 128)
	private static final double MAX_BONUS_PERCENTAGE = 0.25; // Bonus 25%
	
	private LizardmanTempleXPParty()
	{
		addKillId(MONSTER_IDS);
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		if (npc.isMonster() && contains(MONSTER_IDS, npc.getId()))
		{
			final org.l2jmobius.gameserver.model.Party party = killer.getParty();
			if (party != null)
			{
				for (Player member : party.getMembers())
				{
					if (killer.isInsideRadius3D(member, BONUS_RADIUS) && (member.getLevel() >= 118) && (member.getLevel() <= 131))
					{
						final double bonusPercentage = calculateBonusPercentage(member.getLevel());
						final long bonusXp = (long) (npc.getExpReward(member.getLevel()) * bonusPercentage);
						
						// Adiciona a experiência total ao jogador após 1 segundo
						ThreadPool.schedule(() ->
						{
							member.addExpAndSp(bonusXp, 0);
						}, 1000); // 1000 milissegundos = 1 segundo
					}
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	private double calculateBonusPercentage(int level)
	{
		if ((level < 118) || (level > 131))
		{
			return 0; // No bonus for out of range levels
		}
		
		// Sets the percentage proportional to the level
		switch (level)
		{
			case 118:
				return MAX_BONUS_PERCENTAGE * 0.1;
			case 119:
				return MAX_BONUS_PERCENTAGE * 0.2;
			case 120:
				return MAX_BONUS_PERCENTAGE * 0.3;
			case 121:
				return MAX_BONUS_PERCENTAGE * 0.4;
			case 122:
				return MAX_BONUS_PERCENTAGE * 0.5;
			case 123:
				return MAX_BONUS_PERCENTAGE * 0.6;
			case 124:
			case 125:
				return MAX_BONUS_PERCENTAGE * 0.8;
			case 126:
				return MAX_BONUS_PERCENTAGE * 0.9;
			case 127:
				return MAX_BONUS_PERCENTAGE * 0.95;
			case 128:
			case 129:
			case 130:
			case 131:
				return MAX_BONUS_PERCENTAGE;
			default:
				return 0;
		}
	}
	
	private static boolean contains(int[] array, int value)
	{
		for (int i : array)
		{
			if (i == value)
			{
				return true;
			}
		}
		return false;
	}
	
	public static void main(String[] args)
	{
		new LizardmanTempleXPParty();
	}
}