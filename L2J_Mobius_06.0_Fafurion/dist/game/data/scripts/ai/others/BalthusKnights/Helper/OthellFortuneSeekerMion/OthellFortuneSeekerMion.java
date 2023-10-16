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
package ai.others.BalthusKnights.Helper.OthellFortuneSeekerMion;

import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.commons.util.Rnd;
import org.l2jmobius.gameserver.enums.ChatType;
import org.l2jmobius.gameserver.enums.ClassId;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.holders.SkillHolder;
import org.l2jmobius.gameserver.model.instancezone.Instance;
import org.l2jmobius.gameserver.network.NpcStringId;
import org.l2jmobius.gameserver.network.serverpackets.PlaySound;

import ai.AbstractNpcAI;

/**
 * Othell Fortune Seeker Mion AI
 * @author Kazumi
 */
public final class OthellFortuneSeekerMion extends AbstractNpcAI
{
	// NPCs
	private static final int MION = 34375;
	// Monsters
	private static final int ANTHARAS = 24087;
	private static final int INVISIBLE_NPC = 18918;
	// Skills
	private static final SkillHolder ShadowIllusionSkill = new SkillHolder(32141, 1);
	private static final SkillHolder AngelOfDeathSkill = new SkillHolder(32142, 1);
	// Misc
	private static final int p_CheckInterval = 3000;
	private static final int p_TalkInterval = 15000;
	
	public OthellFortuneSeekerMion()
	{
		addSpawnId(MION, INVISIBLE_NPC);
		addFirstTalkId(MION);
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		final Instance instance = npc.getInstanceWorld();
		
		switch (npc.getId())
		{
			case MION:
			{
				if ((instance != null) && (instance.getTemplateId() == 271))
				{
					npc.setInvul(true);
					npc.setTalkable(false);
					final Npc antharas = instance.getNpc(ANTHARAS);
					ThreadPool.schedule(new CheckTask(npc, instance), p_CheckInterval);
					if (instance.getFirstPlayer().getClassId() == ClassId.OTHELL_ROGUE)
					{
						ThreadPool.schedule(new TalkTask(npc, instance), p_TalkInterval);
					}
					
					ThreadPool.schedule(() ->
					{
						npc.setRandomWalking(false);
						npc.setTarget(antharas);
						npc.setRunning();
						addAttackDesire(npc, antharas);
					}, 7000L); // 7 sec
				}
				break;
			}
			case INVISIBLE_NPC:
			{
				final Npc mion = instance.getNpc(MION);
				if (mion != null)
				{
					final Player player = instance.getFirstPlayer();
					if (player.getClassId() == ClassId.OTHELL_ROGUE)
					{
						mion.setWalking();
						mion.setTalkable(true);
						mion.teleToLocation(171771, 190975, -11536, 25797, instance);
						// TODO: Check how NPC sit down
					}
					else
					{
						ThreadPool.schedule(() ->
						{
							mion.decayMe();
						}, 5000L); // 5 sec
					}
				}
				break;
			}
		}
		return super.onSpawn(npc);
	}
	
	private class CheckTask implements Runnable
	{
		private final Npc _npc;
		private final Instance _instance;
		
		public CheckTask(Npc npc, Instance instance)
		{
			_npc = npc;
			_instance = instance;
		}
		
		@Override
		public void run()
		{
			if ((_instance != null) && (_instance.getStatus() < 4))
			{
				if ((_npc != null) && !_npc.isDead() && !_npc.isDecayed())
				{
					ThreadPool.schedule(new CheckTask(_npc, _instance), p_CheckInterval);
					if (Rnd.get(1000) <= 333)
					{
						final Npc antharas = _instance.getNpc(ANTHARAS);
						_npc.setTarget(antharas);
						switch (Rnd.get(2))
						{
							case 1:
							{
								addSkillCastDesire(_npc, _npc.getTarget(), ShadowIllusionSkill, 20000);
								break;
							}
							default:
							{
								addSkillCastDesire(_npc, _npc.getTarget(), AngelOfDeathSkill, 20000);
								break;
							}
						}
					}
					else
					{
						final Npc antharas = _instance.getNpc(ANTHARAS);
						addAttackDesire(_npc, antharas);
					}
				}
			}
		}
	}
	
	private class TalkTask implements Runnable
	{
		private final Npc _npc;
		private final Instance _instance;
		
		public TalkTask(Npc npc, Instance instance)
		{
			_npc = npc;
			_instance = instance;
		}
		
		@Override
		public void run()
		{
			
			if ((_instance != null) && (_instance.getStatus() < 4))
			{
				if ((_npc != null) && !_npc.isDead() && !_npc.isDecayed())
				{
					ThreadPool.schedule(new TalkTask(_npc, _instance), p_TalkInterval);
					switch (Rnd.get(1, 4))
					{
						case 1:
						{
							_instance.broadcastPacket(new PlaySound(3, "Npcdialog1.mion_ep50_battle_1", 0, 0, 0, 0, 0));
							_npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.FIND_THE_ENEMY_S_WEAKNESS_AND_EXPLOIT_IT_THAT_CAN_BE_CRUCIAL);
							break;
						}
						case 2:
						{
							_instance.broadcastPacket(new PlaySound(3, "Npcdialog1.mion_ep50_battle_2", 0, 0, 0, 0, 0));
							_npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.THIS_IS_THE_CHANCE_TO_FIRE_OUR_MOST_POWERFUL_ATTACK_AT_THE_ENEMY);
							break;
						}
						case 3:
						{
							_instance.broadcastPacket(new PlaySound(3, "Npcdialog1.mion_ep50_battle_3", 0, 0, 0, 0, 0));
							_npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.ATTACKING_THE_ENEMY_FROM_BEHIND_CAN_BE_QUITE_EFFECTIVE);
							break;
						}
						case 4:
						{
							_instance.broadcastPacket(new PlaySound(3, "Npcdialog1.mion_ep50_battle_4", 0, 0, 0, 0, 0));
							_npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.IN_MOMENTS_OF_DANGER_WE_CAN_HIDE);
							break;
						}
					}
				}
			}
		}
	}
	
	@Override
	public final String onFirstTalk(Npc npc, Player player)
	{
		String htmltext = null;
		htmltext = "balthus_othel001.htm";
		return htmltext;
	}
	
	public static void main(String[] args)
	{
		new OthellFortuneSeekerMion();
	}
}