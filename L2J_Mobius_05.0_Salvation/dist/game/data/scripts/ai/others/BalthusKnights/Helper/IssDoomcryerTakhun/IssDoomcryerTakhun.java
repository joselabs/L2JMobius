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
package ai.others.BalthusKnights.Helper.IssDoomcryerTakhun;

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
 * Iss Doomcryer Takhun AI
 * @author Kazumi
 */
public final class IssDoomcryerTakhun extends AbstractNpcAI
{
	// NPCs
	private static final int TAKHUN = 34376;
	// Monsters
	private static final int ANTHARAS = 24087;
	private static final int INVISIBLE_NPC = 18918;
	// Skills
	private static final SkillHolder EnsembleMelodySkill = new SkillHolder(32149, 1);
	// Misc
	private static final int p_TalkInterval = 15000;
	
	public IssDoomcryerTakhun()
	{
		addSpawnId(TAKHUN, INVISIBLE_NPC);
		addFirstTalkId(TAKHUN);
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		final Instance instance = npc.getInstanceWorld();
		
		switch (npc.getId())
		{
			case TAKHUN:
			{
				if ((instance != null) && (instance.getTemplateId() == 271))
				{
					npc.setInvul(true);
					npc.setTalkable(false);
					final Npc antharas = instance.getNpc(ANTHARAS);
					
					final Player player = instance.getFirstPlayer();
					if (player != null)
					{
						if (player.getClassId() == ClassId.ISS_ENCHANTER)
						{
							ThreadPool.schedule(new TalkTask(npc, instance), p_TalkInterval);
						}
						
						ThreadPool.schedule(() ->
						{
							npc.setTarget(player);
							addSkillCastDesire(npc, npc.getTarget(), EnsembleMelodySkill, 100000);
						}, 2000L); // 2 sec
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
				final Npc takhun = instance.getNpc(TAKHUN);
				if (takhun != null)
				{
					final Player player = instance.getFirstPlayer();
					if (player.getClassId() == ClassId.ISS_ENCHANTER)
					{
						takhun.setWalking();
						takhun.setTalkable(true);
						takhun.teleToLocation(171771, 190975, -11536, 25797, instance);
						// TODO: Check how NPC sit down
					}
					else
					{
						ThreadPool.schedule(() ->
						{
							takhun.decayMe();
						}, 5000L); // 5 sec
					}
				}
				break;
			}
		}
		return super.onSpawn(npc);
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
							_instance.broadcastPacket(new PlaySound(3, "Npcdialog1.takun_ep50_battle_1", 0, 0, 0, 0, 0));
							_npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.DON_T_LET_THE_DRAGON_SCARE_YOU_IT_S_JUST_ONE_OF_MANY_ENEMIES_WE_LL_COME_ACROSS);
							break;
						}
						case 2:
						{
							_instance.broadcastPacket(new PlaySound(3, "Npcdialog1.takun_ep50_battle_2", 0, 0, 0, 0, 0));
							_npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.YOUR_STRENGTH_MAKES_US_MORE_POWERFUL);
							break;
						}
						case 3:
						{
							_instance.broadcastPacket(new PlaySound(3, "Npcdialog1.takun_ep50_battle_3", 0, 0, 0, 0, 0));
							_npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.WHEN_THE_ENEMY_IS_ALMOST_DOWN_THAT_S_WHEN_WE_MUST_ATTACK_WITH_ALL_OUR_STRENGTH);
							break;
						}
						case 4:
						{
							_instance.broadcastPacket(new PlaySound(3, "Npcdialog1.takun_ep50_battle_4", 0, 0, 0, 0, 0));
							_npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.STAY_ON_YOUR_TOES_THIS_IS_WHEN_WE_SHOULD_MUSTER_OUR_STRENGTH);
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
		htmltext = "balthus_is001.htm";
		return htmltext;
	}
	
	public static void main(String[] args)
	{
		new IssDoomcryerTakhun();
	}
}