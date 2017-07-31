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
package ai.others.NewbieGuide;

import com.l2jmobius.Config;
import com.l2jmobius.gameserver.enums.Race;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.holders.ItemHolder;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jmobius.gameserver.network.serverpackets.PlaySound;

import ai.AbstractNpcAI;

/**
 * @author Mobius
 */
public class NewbieGuide extends AbstractNpcAI
{
	// NPCs
	private static final int[] NEWBIE_GUIDES =
	{
		30598,
		30599,
		30600,
		30601,
		30602,
	};
	// Items
	private final static ItemHolder SOULSHOT_REWARD = new ItemHolder(5789, 200);
	private final static ItemHolder SPIRITSHOT_REWARD = new ItemHolder(5790, 100);
	// Other
	private final static String TUTORIAL_QUEST = "Q00255_Tutorial";
	private final static String TUTORIAL_SHOT_VAR = "TUTORIAL_SHOT_REWARDED";
	private final static String SUPPORT_MAGIC_STRING = "<Button ALIGN=LEFT ICON=\"NORMAL\" action=\"bypass -h Link default/SupportMagic.htm\">Receive help from beneficial magic.</Button>";
	
	private NewbieGuide()
	{
		addStartNpc(NEWBIE_GUIDES);
		addTalkId(NEWBIE_GUIDES);
		addFirstTalkId(NEWBIE_GUIDES);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = null;
		if (event.equals("0"))
		{
			if (Config.MAX_NEWBIE_BUFF_LEVEL > 0)
			{
				htmltext = npc.getId() + ".htm";
			}
			else
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
				html.setFile(player.getHtmlPrefix(), "data/scripts/ai/others/NewbieGuide/" + npc.getId() + ".htm");
				html.replace(SUPPORT_MAGIC_STRING, "");
				player.sendPacket(html);
				return htmltext;
			}
		}
		else
		{
			htmltext = npc.getId() + "-" + event + ".htm";
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		if (npc.getRace() != player.getTemplate().getRace())
		{
			return npc.getId() + "-no.htm";
		}
		final QuestState qs = player.getQuestState(TUTORIAL_QUEST);
		if ((qs != null) && qs.isCompleted() && !Config.DISABLE_TUTORIAL && !player.getVariables().getBoolean(TUTORIAL_SHOT_VAR, true))
		{
			player.getVariables().set(TUTORIAL_SHOT_VAR, true);
			if (player.isMageClass() && (player.getRace() != Race.ORC))
			{
				giveItems(player, SPIRITSHOT_REWARD);
				playTutorialVoice(player, "tutorial_voice_027");
			}
			else
			{
				giveItems(player, SOULSHOT_REWARD);
				playTutorialVoice(player, "tutorial_voice_026");
			}
		}
		if (Config.MAX_NEWBIE_BUFF_LEVEL > 0)
		{
			return npc.getId() + ".htm";
		}
		final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
		html.setFile(player.getHtmlPrefix(), "data/scripts/ai/others/NewbieGuide/" + npc.getId() + ".htm");
		html.replace(SUPPORT_MAGIC_STRING, "");
		player.sendPacket(html);
		return null;
	}
	
	public void playTutorialVoice(L2PcInstance player, String voice)
	{
		player.sendPacket(new PlaySound(2, voice, 0, 0, player.getX(), player.getY(), player.getZ()));
	}
	
	public static void main(String[] args)
	{
		new NewbieGuide();
	}
}
