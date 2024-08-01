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
package instances.LurinSecretWorkshop;

import java.util.List;

import org.l2jmobius.commons.util.CommonUtil;
import org.l2jmobius.gameserver.ai.CtrlIntention;
import org.l2jmobius.gameserver.instancemanager.InstanceManager;
import org.l2jmobius.gameserver.instancemanager.WalkingManager;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.actor.Attackable;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.instance.Monster;
import org.l2jmobius.gameserver.model.holders.SkillHolder;
import org.l2jmobius.gameserver.model.instancezone.Instance;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.network.NpcStringId;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.ActionFailed;
import org.l2jmobius.gameserver.network.serverpackets.ExSendUIEvent;
import org.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2jmobius.gameserver.network.serverpackets.NpcHtmlMessage;
import org.l2jmobius.gameserver.network.serverpackets.SystemMessage;

import instances.AbstractInstance;

/**
 * @author CostyKiller
 */
public class LurinSecretWorkshop extends AbstractInstance
{
	// NPC
	private static final int HESED = 33780; // 8277 on wiki
	private static final int GRIMNIR = 8310;
	// Bosses
	private static final int[] BOSSES =
	{
		8306, // Lurin Archlord
		8307, // Lurin Archlord
		8308, // Lurin Archlord
		8309 // Lurin Archlord
	};
	// Additional Monsters Spawn Chance
	private static final int ADDITIONAL_MONSTER_SPAWN_CHANCE = 2;
	// Additional Monsters
	private static final int[] ADDITIONAL_MONSTERS =
	{
		8302, // Golem's Copy
		8303, // Golem's Copy
		8304, // Golem's Copy
		8304 // Golem's Copy
	};
	// Monsters 105
	private static final int[] MONSTERS_105 =
	{
		8278, // Artisan
		8282, // Scavenger
		8286, // Dwarven Defender
		8290, // Dwarven Hunter
		8294, // Dwarven Mage
		8298, // Dwarven Berserker
	};
	// Monsters 110
	private static final int[] MONSTERS_110 =
	{
		8279, // Artisan
		8283, // Scavenger
		8287, // Dwarven Defender
		8291, // Dwarven Hunter
		8295, // Dwarven Mage
		8299, // Dwarven Berserker
	};
	// Monsters 115
	private static final int[] MONSTERS_115 =
	{
		8280, // Artisan
		8284, // Scavenger
		8288, // Dwarven Defender
		8292, // Dwarven Hunter
		8296, // Dwarven Mage
		8300, // Dwarven Berserker
	};
	// Monsters 120
	private static final int[] MONSTERS_120 =
	{
		8281, // Artisan
		8285, // Scavenger
		8289, // Dwarven Defender
		8293, // Dwarven Hunter
		8297, // Dwarven Mage
		8301, // Dwarven Berserker
	};
	// Misc
	private static final int TEMPLATE_ID = 5000;
	private static final int ILLUSORY_POINTS_REWARD = 10; // 10 per boss kill
	private static final SkillHolder TRANSFORM_SKILL = new SkillHolder(29608, 1); // Grimnir's Siege Golem
	private static final SkillHolder UNTRANSFORM_SKILL = new SkillHolder(619, 1); // Dispel Transform
	// Instance Status
	private static final int SPAWNING_MONSTERS = 1;
	private static final int FIGHTING_MONSTERS = 2;
	private static final int FIGHTING_BOSS = 3;
	private int _instanceLevel = 0;
	
	public LurinSecretWorkshop()
	{
		super(TEMPLATE_ID);
		addStartNpc(HESED);
		// addFirstTalkId(HESED);
		addFirstTalkId(GRIMNIR);
		addTalkId(HESED, GRIMNIR);
		addKillId(MONSTERS_105);
		addKillId(MONSTERS_110);
		addKillId(MONSTERS_115);
		addKillId(MONSTERS_120);
		addKillId(BOSSES);
		addSpawnId(MONSTERS_105);
		addSpawnId(MONSTERS_110);
		addSpawnId(MONSTERS_115);
		addSpawnId(MONSTERS_120);
		addSpawnId(ADDITIONAL_MONSTERS);
		addSpawnId(BOSSES);
		addCreatureSeeId(MONSTERS_105);
		addCreatureSeeId(MONSTERS_110);
		addCreatureSeeId(MONSTERS_115);
		addCreatureSeeId(MONSTERS_120);
		addCreatureSeeId(ADDITIONAL_MONSTERS);
		addCreatureSeeId(BOSSES);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		String htmltext = null;
		final long currentTime = System.currentTimeMillis();
		switch (event)
		{
			case "33780.html":
			case "33780-1.html":
			{
				htmltext = event;
				return htmltext;
			}
			case "showPoints":
			{
				final NpcHtmlMessage html = getNpcHtmlMessage(player, npc, "points.html");
				html.replace("%points%", player.getVariables().getInt(PlayerVariables.ILLUSORY_POINTS_ACQUIRED, 0));
				player.sendPacket(html);
				break;
			}
			case "enterInstance":
			{
				// Temp restriction until fully working.
				if (!player.isGM())
				{
					player.sendPacket(new SystemMessage(SystemMessageId.C1_CANNOT_ENTER_YET).addString(player.getName()));
					htmltext = "condNoEnter.html";
					return htmltext;
				}
				else if (currentTime < InstanceManager.getInstance().getInstanceTime(player, TEMPLATE_ID))
				{
					player.sendPacket(new SystemMessage(SystemMessageId.C1_CANNOT_ENTER_YET).addString(player.getName()));
					htmltext = "condNoEnter.html";
					return htmltext;
				}
				enterInstance(player, npc, TEMPLATE_ID);
				break;
			}
			case "exitInstance":
			{
				final Instance world = player.getInstanceWorld();
				if (!isInInstance(world))
				{
					return null;
				}
				player.sendPacket(new ExSendUIEvent(player, true, false, 3600, 0, NpcStringId.TIME_LEFT));
				world.ejectPlayer(player);
				break;
			}
			case "startInstance":
			{
				final Instance world = player.getInstanceWorld();
				
				if (!isInInstance(world))
				{
					return null;
				}
				if ((player.getLevel() >= 105) && (player.getLevel() <= 109))
				{
					_instanceLevel = 105;
				}
				else if ((player.getLevel() >= 110) && (player.getLevel() <= 114))
				{
					_instanceLevel = 110;
				}
				else if ((player.getLevel() >= 115) && (player.getLevel() <= 119))
				{
					_instanceLevel = 115;
				}
				else if (player.getLevel() >= 120)
				{
					_instanceLevel = 120;
				}
				world.setStatus(SPAWNING_MONSTERS);
				if (!player.isTransformed())
				{
					player.doCast(TRANSFORM_SKILL.getSkill());
				}
				startQuestTimer("check_status", 10000, null, player);
				break;
			}
			case "check_status":
			{
				final Instance world = player.getInstanceWorld();
				if (!isInInstance(world))
				{
					return null;
				}
				
				switch (world.getStatus())
				{
					case SPAWNING_MONSTERS: // Spawning monsters.
					{
						if (!world.getParameters().getBoolean("MONSTERS_SPAWNED", false))
						{
							world.despawnGroup("GRIMNIR_DOOR");
							world.setStatus(FIGHTING_MONSTERS);
							world.getParameters().set("MONSTERS_SPAWNED", true);
							player.sendPacket(new ExSendUIEvent(player, false, false, 600, 0, NpcStringId.TIME_LEFT));
							startQuestTimer("spawn_boss", 420 * 1000, null, player); // Boss Lurin appears in 7 minutes.
							startQuestTimer("end_fight", 600 * 1000, null, player); // End fight timer.
							startQuestTimer("check_status", 10000, null, player);
							switch (_instanceLevel)
							{
								case 105:
								{
									moveMonsters(world.spawnGroup("MONSTERS_105_GROUP_1"));
									moveMonsters(world.spawnGroup("MONSTERS_105_GROUP_2"));
									moveMonsters(world.spawnGroup("MONSTERS_105_GROUP_3"));
									moveMonsters(world.spawnGroup("MONSTERS_105_GROUP_4"));
									break;
								}
								case 110:
								{
									moveMonsters(world.spawnGroup("MONSTERS_110_GROUP_1"));
									moveMonsters(world.spawnGroup("MONSTERS_110_GROUP_2"));
									moveMonsters(world.spawnGroup("MONSTERS_110_GROUP_3"));
									moveMonsters(world.spawnGroup("MONSTERS_110_GROUP_4"));
									break;
								}
								case 115:
								{
									moveMonsters(world.spawnGroup("MONSTERS_115_GROUP_1"));
									moveMonsters(world.spawnGroup("MONSTERS_115_GROUP_2"));
									moveMonsters(world.spawnGroup("MONSTERS_115_GROUP_3"));
									moveMonsters(world.spawnGroup("MONSTERS_115_GROUP_4"));
									break;
								}
								case 120:
								{
									moveMonsters(world.spawnGroup("MONSTERS_120_GROUP_1"));
									moveMonsters(world.spawnGroup("MONSTERS_120_GROUP_2"));
									moveMonsters(world.spawnGroup("MONSTERS_120_GROUP_3"));
									moveMonsters(world.spawnGroup("MONSTERS_120_GROUP_4"));
									break;
								}
							}
						}
						break;
					}
					case FIGHTING_BOSS: // Spawn Grimnir exit
					{
						if (world.getAliveNpcCount(BOSSES) == 0)
						{
							world.spawnGroup("GRIMNIR_CENTER");
							world.finishInstance(5);
						}
						else
						{
							startQuestTimer("check_status", 10000, null, player);
						}
						break;
					}
				}
				break;
			}
			case "spawn_boss":
			{
				final Instance world = player.getInstanceWorld();
				switch (_instanceLevel)
				{
					case 105:
					{
						moveMonsters(world.spawnGroup("BOSS_105"));
						break;
					}
					case 110:
					{
						moveMonsters(world.spawnGroup("BOSS_110"));
						break;
					}
					case 115:
					{
						moveMonsters(world.spawnGroup("BOSS_115"));
						break;
					}
					case 120:
					{
						moveMonsters(world.spawnGroup("BOSS_120"));
						break;
					}
				}
				world.setStatus(FIGHTING_BOSS);
				showOnScreenMsg(world, NpcStringId.YOU_MADE_ME_ANGRY, ExShowScreenMessage.TOP_CENTER, 10000, true);
				break;
			}
			case "end_fight":
			{
				// after 10 min instance timer ends player is untransformed
				if (player.isTransformed())
				{
					player.doCast(UNTRANSFORM_SKILL.getSkill());
				}
				break;
			}
			case "move_to_spawn":
			{
				final Instance world = npc.getInstanceWorld();
				final Location loc = world.getTemplateParameters().getLocation("spawnpoint");
				final Location moveTo = new Location(loc.getX() + getRandom(-100, 100), loc.getY() + getRandom(-100, 100), loc.getZ());
				npc.setRunning();
				addMoveToDesire(npc, moveTo, 4);
				startQuestTimer("start_moving", 5000, npc, null);
				break;
			}
			case "start_moving":
			{
				final Instance world = npc.getInstanceWorld();
				final String selectedRoute = "routetospawn";
				WalkingManager.getInstance().startMoving(npc, world.getTemplateParameters().getString(selectedRoute));
				break;
			}
		}
		return super.onEvent(event, npc, player);
	}
	
	private void moveMonsters(List<Npc> monsterList)
	{
		int delay = 500;
		for (Npc monster : monsterList)
		{
			final Instance world = monster.getInstanceWorld();
			if (monster.isAttackable() && (world != null))
			{
				monster.setRandomWalking(false);
				startQuestTimer("move_to_spawn", delay, monster, null);
				((Attackable) monster).setCanReturnToSpawnPoint(false);
			}
		}
	}
	
	@Override
	public String onCreatureSee(Npc npc, Creature player)
	{
		final Instance world = player.getInstanceWorld();
		if ((world != null) && (player.isPlayer()))
		{
			final double distance = npc.calculateDistance2D(player);
			if ((distance < 900))
			{
				WalkingManager.getInstance().cancelMoving(npc);
				((Monster) npc).addDamageHate(player, 0, 1000);
				npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
				addAttackDesire(npc, player);
			}
		}
		return super.onCreatureSee(npc, player);
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isPet)
	{
		final Instance world = npc.getInstanceWorld();
		if (isInInstance(world))
		{
			if (getRandom(10000) < ADDITIONAL_MONSTER_SPAWN_CHANCE)
			{
				if (CommonUtil.contains(MONSTERS_105, npc.getId()))
				{
					moveMonsters(world.spawnGroup("ADDITIONAL_MONSTER_105"));
				}
				else if (CommonUtil.contains(MONSTERS_110, npc.getId()))
				{
					moveMonsters(world.spawnGroup("ADDITIONAL_MONSTER_110"));
				}
				else if (CommonUtil.contains(MONSTERS_115, npc.getId()))
				{
					moveMonsters(world.spawnGroup("ADDITIONAL_MONSTER_115"));
				}
				else if (CommonUtil.contains(MONSTERS_120, npc.getId()))
				{
					moveMonsters(world.spawnGroup("ADDITIONAL_MONSTER_120"));
				}
			}
			if (CommonUtil.contains(BOSSES, npc.getId()))
			{
				// despawn monsters if boss is killed
				switch (_instanceLevel)
				{
					case 105:
					{
						world.despawnGroup("MONSTERS_105_GROUP_1");
						world.despawnGroup("MONSTERS_105_GROUP_2");
						world.despawnGroup("MONSTERS_105_GROUP_3");
						world.despawnGroup("MONSTERS_105_GROUP_4");
						break;
					}
					case 110:
					{
						world.despawnGroup("MONSTERS_110_GROUP_1");
						world.despawnGroup("MONSTERS_110_GROUP_2");
						world.despawnGroup("MONSTERS_110_GROUP_3");
						world.despawnGroup("MONSTERS_110_GROUP_4");
						break;
					}
					case 115:
					{
						world.despawnGroup("MONSTERS_115_GROUP_1");
						world.despawnGroup("MONSTERS_115_GROUP_2");
						world.despawnGroup("MONSTERS_115_GROUP_3");
						world.despawnGroup("MONSTERS_115_GROUP_4");
						break;
					}
					case 120:
					{
						world.despawnGroup("MONSTERS_120_GROUP_1");
						world.despawnGroup("MONSTERS_120_GROUP_2");
						world.despawnGroup("MONSTERS_120_GROUP_3");
						world.despawnGroup("MONSTERS_120_GROUP_4");
						break;
					}
				}
				// Spawn Grimnir npc exit.
				world.spawnGroup("GRIMNIR_CENTER");
				world.finishInstance(5);
				// Untransform.
				if (killer.isTransformed())
				{
					killer.doCast(UNTRANSFORM_SKILL.getSkill());
				}
				// Give player illussory equipment points.
				killer.getVariables().set(PlayerVariables.ILLUSORY_POINTS_ACQUIRED, killer.getVariables().getInt(PlayerVariables.ILLUSORY_POINTS_ACQUIRED, 0) + ILLUSORY_POINTS_REWARD);
				killer.sendMessage("You received " + ILLUSORY_POINTS_REWARD + " Illusory equipement points.");
			}
		}
		return super.onKill(npc, killer, isPet);
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		final Instance world = npc.getInstanceWorld();
		if (isInInstance(world))
		{
			npc.setRandomAnimation(false);
			addAttackDesire(npc, world.getFirstPlayer());
		}
		return super.onSpawn(npc);
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		final Instance world = npc.getInstanceWorld();
		if (world != null)
		{
			switch (npc.getId())
			{
				case HESED:
				{
					return "33780.html";
				}
				case GRIMNIR:
				{
					if (world.getStatus() == FIGHTING_BOSS)
					{
						return "8310-exit.html";
					}
					return "8310-enter.html";
				}
			}
		}
		player.sendPacket(ActionFailed.STATIC_PACKET);
		return null;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		switch (npc.getId())
		{
			case HESED:
			{
				return "33780.html";
			}
			case GRIMNIR:
			{
				return "8310-enter.html";
			}
		}
		
		player.sendPacket(ActionFailed.STATIC_PACKET);
		return null;
	}
	
	@Override
	public void onInstanceLeave(Player player, Instance instance)
	{
		player.sendPacket(new ExSendUIEvent(player, true, false, 3600, 0, NpcStringId.TIME_LEFT));
	}
	
	private NpcHtmlMessage getNpcHtmlMessage(Player player, Npc npc, String fileName)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
		final String text = getHtm(player, fileName);
		if (text == null)
		{
			LOGGER.info("Cannot find HTML file for " + getClass().getSimpleName() + " Instance: " + fileName);
			return null;
		}
		
		html.setHtml(text);
		return html;
	}
	
	public static void main(String[] args)
	{
		new LurinSecretWorkshop();
	}
}
