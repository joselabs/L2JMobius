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
package ai.zones.Gracia.npcs.ZealotOfShilen;

import com.l2jmobius.gameserver.model.actor.L2Attackable;
import com.l2jmobius.gameserver.model.actor.L2Character;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;

import ai.AbstractNpcAI;

/**
 * Zealot of Shilen AI.
 * @author nonom
 */
public final class ZealotOfShilen extends AbstractNpcAI
{
	// NPCs
	private static final int ZEALOT = 18782;
	private static final int[] GUARDS =
	{
		32628,
		32629
	};
	
	private ZealotOfShilen()
	{
		super(ZealotOfShilen.class.getSimpleName(), "ai/zones/Gracia/npcs");
		addSpawnId(ZEALOT);
		addSpawnId(GUARDS);
		addFirstTalkId(GUARDS);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (npc == null)
		{
			return null;
		}
		
		startQuestTimer("WATCHING", 10000, npc, null, true);
		if (event.equalsIgnoreCase("WATCHING") && !npc.isAttackingNow())
		{
			for (L2Character character : npc.getKnownList().getKnownCharacters())
			{
				if (character.isMonster() && !character.isDead() && !((L2Attackable) character).isDecayed())
				{
					addAttackDesire(npc, character);
				}
			}
		}
		return null;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		return npc.isAttackingNow() ? "32628-01.html" : npc.getId() + ".html";
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		if (npc.getId() == ZEALOT)
		{
			npc.setIsNoRndWalk(true);
		}
		else
		{
			npc.setIsInvul(true);
			((L2Attackable) npc).setCanReturnToSpawnPoint(false);
			startQuestTimer("WATCHING", 10000, npc, null, true);
		}
		return super.onSpawn(npc);
	}
	
	public static void main(String[] args)
	{
		new ZealotOfShilen();
	}
}
