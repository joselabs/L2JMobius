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
package instances.BalthusKnights;

import java.util.List;

import org.l2jmobius.gameserver.data.xml.SkillData;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.instancezone.Instance;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.network.NpcStringId;
import org.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;

import instances.AbstractInstance;
import quests.Q10552_ChallengeBalthusKnight.Q10552_ChallengeBalthusKnight;

/**
 * Hatchling Nest instance zone.
 * @author Kazumi
 */
public final class HatchlingNest extends AbstractInstance
{
	// MOBs
	private static final int HATCHLING = 24089;
	// Misc
	private static final int TEMPLATE_ID = 269;
	
	public HatchlingNest()
	{
		super(TEMPLATE_ID);
		addKillId(HATCHLING);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		String htmltext = null;
		if (event.equals("enterInstance"))
		{
			final QuestState qs = player.getQuestState(Q10552_ChallengeBalthusKnight.class.getSimpleName());
			if ((qs != null) && qs.isStarted())
			{
				enterInstance(player, npc, TEMPLATE_ID);
				player.addSkill(SkillData.getInstance().getSkill(239, 5), true); // Expertise S
				player.refreshExpertisePenalty();
			}
		}
		return htmltext;
	}
	
	@Override
	protected void onEnter(Player player, Instance instance, boolean firstEnter)
	{
		showOnScreenMsg(instance, NpcStringId.ATTACK_A_HATCHLING, ExShowScreenMessage.TOP_CENTER, 30000, false);
		super.onEnter(player, instance, firstEnter);
	}
	
	@Override
	public String onKill(Npc npc, Player player, boolean isSummon)
	{
		final Instance instance = player.getInstanceWorld();
		if ((instance != null) && (instance.getTemplateId() == TEMPLATE_ID))
		{
			if (instance.getAliveNpcs(HATCHLING).isEmpty())
			{
				moveMonsters(instance.spawnGroup("balthus_start_2523_03m1"));
				showOnScreenMsg(player, NpcStringId.ATTACK_A_HATCHLING, ExShowScreenMessage.TOP_CENTER, 30000, false);
			}
		}
		return super.onKill(npc, player, isSummon);
	}
	
	private void moveMonsters(List<Npc> monsterList)
	{
		for (Npc monster : monsterList)
		{
			final Instance instance = monster.getInstanceWorld();
			if (monster.isAttackable() && (instance != null) && !monster.isDead())
			{
				monster.setRandomWalking(false);
				final Location loc = instance.getTemplateParameters().getLocation("middlePointRoom");
				final Location moveTo = new Location(loc.getX() + getRandom(-100, 100), loc.getY() + getRandom(-100, 100), loc.getZ());
				monster.setRunning();
				addMoveToDesire(monster, moveTo, 23);
				monster.asAttackable().setCanReturnToSpawnPoint(false);
			}
		}
	}
	
	public static void main(String[] args)
	{
		new HatchlingNest();
	}
}