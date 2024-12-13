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

import org.l2jmobius.gameserver.enums.ChatType;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.holders.SkillHolder;
import org.l2jmobius.gameserver.model.instancezone.Instance;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.network.NpcStringId;
import org.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;

import instances.AbstractInstance;
import quests.Q10555_ChargeAtAntharas.Q10555_ChargeAtAntharas;

/**
 * Antharas Nest instance zone.
 * @author Kazumi
 */
public final class AntharasNest extends AbstractInstance
{
	// NPCs
	private static final int STIG_MACH_FRIEND = 34366;
	private static final int BALTHUS_KNIGHT = 34372;
	// Monsters
	private static final int HATCHLING = 24090;
	private static final int GEM_DRAGON_ANTHARAS = 24091;
	private static final int HATCHLING_BOMBER = 24098;
	private static final int CYCLONE = 24099;
	// Skills
	private static final SkillHolder PowerBomberSkill = new SkillHolder(32166, 1);
	// Misc
	private static final int TEMPLATE_ID = 271;
	
	public AntharasNest()
	{
		super(TEMPLATE_ID);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		String htmltext = null;
		if (event.equals("enterInstance"))
		{
			final QuestState qs = player.getQuestState(Q10555_ChargeAtAntharas.class.getSimpleName());
			if ((qs != null) && qs.isStarted())
			{
				qs.setCond(1, true);
				player.getVariables().set(PlayerVariables.BALTHUS_PHASE, 2);
				qs.set(Integer.toString(GEM_DRAGON_ANTHARAS), 0);
				enterInstance(player, npc, TEMPLATE_ID);
			}
		}
		else if (event.equals("startGemDragonsAttack"))
		{
			final Instance instance = player.getInstanceWorld();
			final Npc stig = instance.getNpc(STIG_MACH_FRIEND);
			instance.setStatus(1);
			if (stig != null)
			{
				if (getRandomBoolean())
				{
					addSpawn(GEM_DRAGON_ANTHARAS, 171654, 191155, -11536, 0, false, 0, false, instance.getId());
				}
				else
				{
					addSpawn(GEM_DRAGON_ANTHARAS, 171562, 190791, -11536, 0, false, 0, false, instance.getId());
				}
			}
		}
		else if (event.equals("continueGemDragonsAttack"))
		{
			final Instance instance = player.getInstanceWorld();
			final Npc stig = instance.getNpc(STIG_MACH_FRIEND);
			if ((stig != null) && instance.isStatus(1))
			{
				if (getRandomBoolean())
				{
					addSpawn(GEM_DRAGON_ANTHARAS, 171654, 191155, -11536, 0, false, 0, false, instance.getId());
				}
				else
				{
					addSpawn(GEM_DRAGON_ANTHARAS, 171562, 190791, -11536, 0, false, 0, false, instance.getId());
				}
			}
		}
		else if (event.equals("startAntharasProgress"))
		{
			final Instance instance = player.getInstanceWorld();
			final Npc stig = instance.getNpc(STIG_MACH_FRIEND);
			if (stig != null)
			{
				instance.setStatus(2);
				instance.getAliveNpcs(HATCHLING, GEM_DRAGON_ANTHARAS).forEach(dragon -> addSpawn(HATCHLING_BOMBER, dragon.getX(), dragon.getY(), dragon.getZ(), 0, false, 0, false, instance.getId()));
				instance.getAliveNpcs(HATCHLING, GEM_DRAGON_ANTHARAS).forEach(dragon -> addSpawn(CYCLONE, dragon.getX(), dragon.getY(), dragon.getZ(), 0, false, 0, false, instance.getId()));
				instance.getAliveNpcs(HATCHLING, GEM_DRAGON_ANTHARAS).forEach(dragon -> dragon.deleteMe());
				stig.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.DON_T_TAKE_US_LIGHTLY);
				getTimers().addTimer("ANTHARAS_INIT_TASK_1", 3000L, it1 ->
				{
					stig.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.WHAT_IN_THE_WORLD_ARE_YOU_DOING);
					instance.getAliveNpcs(HATCHLING_BOMBER).forEach(bomber -> bomber.doCast(PowerBomberSkill.getSkill()));
					instance.getAliveNpcs(HATCHLING_BOMBER).forEach(bomber -> bomber.decayMe());
					instance.getAliveNpcs(BALTHUS_KNIGHT).forEach(knight -> knight.decayMe());
					getTimers().addTimer("ANTHARAS_INIT_TASK_2", 1000L, it2 ->
					{
						instance.getAliveNpcs(CYCLONE).forEach(cyclone -> cyclone.deleteMe());
						getTimers().addTimer("ANTHARAS_INIT_TASK_3", 7000L, it3 ->
						{
							for (Npc antharas : instance.spawnGroup("balthus_anta_2523_01m1"))
							{
								antharas.setRandomWalking(false);
								antharas.asAttackable().setCanReturnToSpawnPoint(false);
								getTimers().addTimer("ANTHARAS_MOVE_TASK", 4000L, mt1 ->
								{
									final Location loc = instance.getTemplateParameters().getLocation("middlePointRoom");
									antharas.setRunning();
									addMoveToDesire(antharas, loc, 23);
								});
							}
							showOnScreenMsg(instance, NpcStringId.ANTHARAS_HAS_APPEARED, ExShowScreenMessage.TOP_CENTER, 10000, false);
							getTimers().addTimer("ANTHARAS_INIT_TASK_4", 4000L, it4 ->
							{
								getTimers().addTimer("ANTHARAS_INIT_TASK_5", 7000L, it5 ->
								{
									instance.setStatus(3);
									instance.spawnGroup("balthus_anta_2523_02m3");
									getTimers().addTimer("ANTHARAS_INIT_TASK_6", 1000L, it6 ->
									{
										stig.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.AT_LAST_ANTHARAS_IS_HERE);
									});
								});
							});
						});
					});
				});
			}
		}
		return htmltext;
	}
	
	@Override
	protected void onEnter(Player player, Instance instance, boolean firstEnter)
	{
		showOnScreenMsg(player, NpcStringId.GO_UP_THE_STAIRS_TO_THE_2ND_FLOOR_AND_NTALK_TO_BALTHUS_KNIGHT_CAPTAIN_STIG_MACH, ExShowScreenMessage.TOP_CENTER, 10000, false);
		super.onEnter(player, instance, firstEnter);
	}
	
	public static void main(String[] args)
	{
		new AntharasNest();
	}
}