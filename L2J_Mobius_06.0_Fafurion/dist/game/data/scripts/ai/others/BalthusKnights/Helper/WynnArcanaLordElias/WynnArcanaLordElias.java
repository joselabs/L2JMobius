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
package ai.others.BalthusKnights.Helper.WynnArcanaLordElias;

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
 * Wynn Arcana Lord Elias AI
 * @author Kazumi
 */
public final class WynnArcanaLordElias extends AbstractNpcAI
{
	// NPCs
	private static final int ELIAS = 34378;
	// Monsters
	private static final int ANTHARAS = 24087;
	private static final int INVISIBLE_NPC = 18918;
	// Skills
	private static final SkillHolder ArcaneRageSkill = new SkillHolder(32145, 1);
	private static final SkillHolder InvokeSkill = new SkillHolder(32146, 1);
	private static final SkillHolder ArcanasCallSkill = new SkillHolder(32147, 1);
	// Misc
	private static final int p_CheckInterval = 3000;
	private static final int p_TalkInterval = 15000;
	
	public WynnArcanaLordElias()
	{
		addSpawnId(ELIAS, INVISIBLE_NPC);
		addFirstTalkId(ELIAS);
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		final Instance instance = npc.getInstanceWorld();
		
		switch (npc.getId())
		{
			case ELIAS:
			{
				if ((instance != null) && (instance.getTemplateId() == 271))
				{
					npc.setInvul(true);
					npc.setTalkable(false);
					final Npc antharas = instance.getNpc(ANTHARAS);
					if (instance.getFirstPlayer().getClassId() == ClassId.WYNN_SUMMONER)
					{
						ThreadPool.schedule(new TalkTask(npc, instance), p_TalkInterval);
					}
					ThreadPool.schedule(() ->
					{
						ThreadPool.schedule(new CheckTask(npc, instance), p_CheckInterval);
						npc.setRandomWalking(false);
						npc.setTargetable(false);
						npc.setTarget(antharas);
						npc.setRunning();
						addAttackDesire(npc, antharas);
					}, 14000L); // 14 sec
				}
				break;
			}
			case INVISIBLE_NPC:
			{
				final Npc elias = instance.getNpc(ELIAS);
				if (elias != null)
				{
					final Player player = instance.getFirstPlayer();
					if (player.getClassId() == ClassId.WYNN_SUMMONER)
					{
						elias.setWalking();
						elias.setTalkable(true);
						elias.setTargetable(true);
						elias.teleToLocation(171771, 190975, -11536, 25797, instance);
						// TODO: Check how NPC sit down
					}
					else
					{
						ThreadPool.schedule(() ->
						{
							elias.decayMe();
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
						switch (Rnd.get(3))
						{
							case 1:
							{
								addSkillCastDesire(_npc, _npc.getTarget(), ArcaneRageSkill, 20000);
								break;
							}
							case 2:
							{
								addSkillCastDesire(_npc, _npc.getTarget(), ArcanasCallSkill, 20000);
								break;
							}
							default:
							{
								addSkillCastDesire(_npc, _npc.getTarget(), InvokeSkill, 20000);
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
							_instance.broadcastPacket(new PlaySound(3, "Npcdialog1.elias_ep50_battle_1", 0, 0, 0, 0, 0));
							_npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.YOU_RE_IN_MY_WAY_DON_T_STAY_TOO_CLOSE);
							break;
						}
						case 2:
						{
							_instance.broadcastPacket(new PlaySound(3, "Npcdialog1.elias_ep50_battle_2", 0, 0, 0, 0, 0));
							_npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.YOU_NEED_TO_CONCENTRATE_DURING_BATTLE_SHOULDN_T_YOU_TAKE_CARE_OF_YOUR_SERVITOR);
							break;
						}
						case 3:
						{
							_instance.broadcastPacket(new PlaySound(3, "Npcdialog1.elias_ep50_battle_3", 0, 0, 0, 0, 0));
							_npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.YOU_AND_YOUR_SERVITOR_ARE_ONE_DON_T_LET_DOWN_YOUR_GUARD_BECAUSE_YOU_RE_NOT_HURT);
							break;
						}
						case 4:
						{
							_instance.broadcastPacket(new PlaySound(3, "Npcdialog1.elias_ep50_battle_4", 0, 0, 0, 0, 0));
							_npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.IDIOT_NOW_I_M_GETTING_PISSED_OFF);
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
		htmltext = "balthus_wynn001.htm";
		return htmltext;
	}
	
	public static void main(String[] args)
	{
		new WynnArcanaLordElias();
	}
}