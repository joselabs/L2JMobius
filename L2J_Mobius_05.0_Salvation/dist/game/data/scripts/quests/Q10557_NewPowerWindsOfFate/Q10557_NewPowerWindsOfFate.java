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
package quests.Q10557_NewPowerWindsOfFate;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.l2jmobius.gameserver.data.xml.SkillTreeData;
import org.l2jmobius.gameserver.enums.ClassId;
import org.l2jmobius.gameserver.enums.Race;
import org.l2jmobius.gameserver.enums.UserInfoType;
import org.l2jmobius.gameserver.instancemanager.QuestManager;
import org.l2jmobius.gameserver.model.SkillLearn;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.ListenerRegisterType;
import org.l2jmobius.gameserver.model.events.annotations.Id;
import org.l2jmobius.gameserver.model.events.annotations.RegisterEvent;
import org.l2jmobius.gameserver.model.events.annotations.RegisterType;
import org.l2jmobius.gameserver.model.events.impl.creature.npc.OnNpcMenuSelect;
import org.l2jmobius.gameserver.model.instancezone.Instance;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;
import org.l2jmobius.gameserver.model.skill.CommonSkill;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.network.NpcStringId;
import org.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2jmobius.gameserver.network.serverpackets.MagicSkillUse;
import org.l2jmobius.gameserver.network.serverpackets.SocialAction;
import org.l2jmobius.gameserver.network.serverpackets.UserInfo;
import org.l2jmobius.gameserver.network.serverpackets.ability.ExAcquireAPSkillList;

import instances.BalthusKnights.FaeronVillage.FaeronVillage;
import quests.Q10555_ChargeAtAntharas.Q10555_ChargeAtAntharas;

/**
 * Forgotten Power: Start of Fate (10556)
 * @author Kazumi
 */
public final class Q10557_NewPowerWindsOfFate extends Quest
{
	// NPCs
	private static final int STIG = 34361;
	private static final int SERENIA = 34394;
	// Items
	private static final int AGATHION_GRIFFIN = 37374;
	private static final int CHAOS_POMANDER = 37374;
	private static final int GRADUATION_GIFT = 46782;
	private static final int PAULINA_EQUIPMENT_SET_R = 46919;
	private static final int SAYHAS_BOX_EVISCERATOR = 40268;
	private static final int SAYHAS_BOX_SAYHAS_SEER = 40269;
	// Misc
	private static final Map<ClassId, Integer> AWAKE_POWER = new HashMap<>();
	static
	{
		AWAKE_POWER.put(ClassId.EVISCERATOR, SAYHAS_BOX_EVISCERATOR);
		AWAKE_POWER.put(ClassId.SAYHA_SEER, SAYHAS_BOX_SAYHAS_SEER);
	}
	
	public Q10557_NewPowerWindsOfFate()
	{
		super(10557);
		addStartNpc(STIG);
		addTalkId(STIG);
		addCondRace(Race.ERTHEIA, "");
		addCondCompletedQuest(Q10555_ChargeAtAntharas.class.getSimpleName(), "stig_q10557_02.htm");
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		String htmltext = null;
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		
		if (event.equals("quest_accept"))
		{
			qs.startQuest();
			switch (player.getClassId())
			{
				case EVISCERATOR_BALTHUS:
				{
					htmltext = "stig_q10557_04a.htm";
					break;
				}
				case SAYHA_SEER_BALTHUS:
				{
					htmltext = "stig_q10557_04b.htm";
					break;
				}
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		switch (qs.getState())
		{
			case State.CREATED:
			{
				htmltext = "stig_q10557_01.htm";
				break;
			}
			case State.STARTED:
			{
				switch (player.getClassId())
				{
					case EVISCERATOR_BALTHUS:
					{
						htmltext = "stig_q10557_05a.htm";
						break;
					}
					case SAYHA_SEER_BALTHUS:
					{
						htmltext = "stig_q10557_05b.htm";
						break;
					}
				}
				break;
			}
			case State.COMPLETED:
			{
				htmltext = getAlreadyCompletedMsg(player);
				break;
			}
		}
		return htmltext;
	}
	
	@RegisterEvent(EventType.ON_NPC_MENU_SELECT)
	@RegisterType(ListenerRegisterType.NPC)
	@Id(STIG)
	@Id(SERENIA)
	public final void onNpcMenuSelect(OnNpcMenuSelect event)
	{
		final Player player = event.getTalker();
		final QuestState qs = getQuestState(player, false);
		final Npc npc = event.getNpc();
		final int ask = event.getAsk();
		final int reply = event.getReply();
		
		if (ask == 10557)
		{
			switch (reply)
			{
				case 1:
				{
					showHtmlFile(player, "stig_q10557_03.htm");
					break;
				}
				case 280:
				{
					final Quest faeron = QuestManager.getInstance().getQuest(FaeronVillage.class.getSimpleName());
					if (faeron != null)
					{
						faeron.onEvent("enterInstance", npc, player);
						switch (player.getClassId())
						{
							case EVISCERATOR_BALTHUS:
							{
								qs.setCond(2);
								break;
							}
							case SAYHA_SEER_BALTHUS:
							{
								qs.setCond(3);
								break;
							}
						}
					}
					break;
				}
			}
		}
		else if (ask == 10338)
		{
			switch (reply)
			{
				case 3000:
				{
					qs.exitQuest(false, true);
					changeBalthusClass(player, 188);
					break;
				}
				case 3001:
				{
					qs.exitQuest(false, true);
					changeBalthusClass(player, 189);
					break;
				}
			}
		}
	}
	
	private void changeBalthusClass(Player player, int classId)
	{
		player.setClassId(classId);
		player.setBaseClass(player.getActiveClass());
		player.getVariables().remove(PlayerVariables.PLAYER_RACE);
		showOnScreenMsg(player, NpcStringId.TALK_TO_THE_MONK_OF_CHAOS_NYOU_CAN_LEARN_ABOUT_THE_REVELATION_SKILLS, ExShowScreenMessage.TOP_CENTER, 5000, true);
		player.broadcastPacket(new MagicSkillUse(player, CommonSkill.PRODUCTION_CLAN_TRANSFER.getId(), CommonSkill.PRODUCTION_CLAN_TRANSFER.getLevel(), 1000, 0));
		final UserInfo ui = new UserInfo(player, false);
		ui.addComponentType(UserInfoType.BASIC_INFO);
		ui.addComponentType(UserInfoType.MAX_HPCPMP);
		player.sendPacket(ui);
		player.broadcastInfo();
		
		player.broadcastPacket(new SocialAction(player.getObjectId(), 20));
		giveItems(player, AGATHION_GRIFFIN, 1);
		for (Entry<ClassId, Integer> ent : AWAKE_POWER.entrySet())
		{
			if (player.getClassId() == ent.getKey())
			{
				giveItems(player, ent.getValue(), 1);
				break;
			}
		}
		giveItems(player, CHAOS_POMANDER, 2);
		giveItems(player, GRADUATION_GIFT, 1);
		giveItems(player, PAULINA_EQUIPMENT_SET_R, 1);
		
		SkillTreeData.getInstance().cleanSkillUponAwakening(player);
		player.sendSkillList();
		
		// reset AP's
		for (SkillLearn sk : SkillTreeData.getInstance().getAbilitySkillTree().values())
		{
			final Skill skill = player.getKnownSkill(sk.getSkillId());
			if (skill != null)
			{
				player.removeSkill(skill);
			}
		}
		player.setAbilityPointsUsed(0);
		player.sendPacket(new ExAcquireAPSkillList(player));
		player.broadcastUserInfo();
		
		final Instance instance = player.getInstanceWorld();
		instance.finishInstance(1);
	}
}