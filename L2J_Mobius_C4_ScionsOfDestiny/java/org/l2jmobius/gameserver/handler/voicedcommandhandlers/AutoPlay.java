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
package org.l2jmobius.gameserver.handler.voicedcommandhandlers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.l2jmobius.Config;
import org.l2jmobius.gameserver.cache.HtmCache;
import org.l2jmobius.gameserver.data.ItemTable;
import org.l2jmobius.gameserver.handler.IVoicedCommandHandler;
import org.l2jmobius.gameserver.model.Skill;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.Summon;
import org.l2jmobius.gameserver.model.item.ItemTemplate;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.network.serverpackets.NpcHtmlMessage;
import org.l2jmobius.gameserver.taskmanager.AutoPlayTaskManager;
import org.l2jmobius.gameserver.taskmanager.AutoUseTaskManager;
import org.l2jmobius.gameserver.util.MathUtil;
import org.l2jmobius.gameserver.util.Util;

/**
 * @author Mobius
 */
public class AutoPlay implements IVoicedCommandHandler
{
	private static final int PAGE_LIMIT = 7;
	private static final Integer AUTO_ATTACK_ACTION = 2;
	private static final Set<Integer> HP_POTION_IDS = new HashSet<>();
	static
	{
		HP_POTION_IDS.add(1540);
		HP_POTION_IDS.add(1539);
		HP_POTION_IDS.add(1061);
		HP_POTION_IDS.add(1060);
	}
	
	private static final String[] VOICED_COMMANDS =
	{
		"play",
		"playskills",
		"playitems",
		"playpotion"
	};
	
	@Override
	public boolean useVoicedCommand(String command, Player player, String params)
	{
		if (!Config.ENABLE_AUTO_PLAY || (player == null))
		{
			return false;
		}
		
		switch (command)
		{
			case "play":
			{
				if (params != null)
				{
					final String[] paramArray = params.toLowerCase().split(" ");
					COMMAND: switch (paramArray[0])
					{
						case "attack":
						{
							if (player.getAutoUseSettings().getAutoActions().contains(AUTO_ATTACK_ACTION))
							{
								player.getAutoUseSettings().getAutoActions().remove(AUTO_ATTACK_ACTION);
							}
							else
							{
								player.getAutoUseSettings().getAutoActions().add(AUTO_ATTACK_ACTION);
							}
							break COMMAND;
						}
						case "loot":
						{
							player.getAutoPlaySettings().setPickup(!player.getAutoPlaySettings().doPickup());
							break COMMAND;
						}
						case "respect":
						{
							player.getAutoPlaySettings().setRespectfulHunting(!player.getAutoPlaySettings().isRespectfulHunting());
							break COMMAND;
						}
						case "range":
						{
							player.getAutoPlaySettings().setShortRange(!player.getAutoPlaySettings().isShortRange());
							break COMMAND;
						}
						case "mode0":
						{
							player.getAutoPlaySettings().setNextTargetMode(0);
							break COMMAND;
						}
						case "mode1":
						{
							player.getAutoPlaySettings().setNextTargetMode(1);
							break COMMAND;
						}
						case "mode2":
						{
							player.getAutoPlaySettings().setNextTargetMode(2);
							break COMMAND;
						}
						case "mode3":
						{
							player.getAutoPlaySettings().setNextTargetMode(3);
							break COMMAND;
						}
						case "percent":
						{
							if ((paramArray.length > 1) && Util.isDigit(paramArray[1]))
							{
								player.getAutoPlaySettings().setAutoPotionPercent(Math.max(0, Math.min(100, Integer.parseInt(paramArray[1]))));
							}
							break COMMAND;
						}
						case "start":
						{
							AutoPlayTaskManager.getInstance().startAutoPlay(player);
							AutoUseTaskManager.getInstance().startAutoUseTask(player);
							break COMMAND;
						}
						case "stop":
						{
							AutoPlayTaskManager.getInstance().stopAutoPlay(player);
							AutoUseTaskManager.getInstance().stopAutoUseTask(player);
							break COMMAND;
						}
					}
				}
				
				final NpcHtmlMessage html = new NpcHtmlMessage(5);
				String content = HtmCache.getInstance().getHtm("data/html/mods/AutoPlay/Main.htm");
				
				content = content.replace("%attack%", player.getAutoUseSettings().getAutoActions().contains(AUTO_ATTACK_ACTION) ? "L2UI.CheckBox_checked" : "L2UI.CheckBox");
				content = content.replace("%loot%", player.getAutoPlaySettings().doPickup() ? "L2UI.CheckBox_checked" : "L2UI.CheckBox");
				content = content.replace("%respect%", player.getAutoPlaySettings().isRespectfulHunting() ? "L2UI.CheckBox_checked" : "L2UI.CheckBox");
				content = content.replace("%range%", !player.getAutoPlaySettings().isShortRange() ? "L2UI.CheckBox_checked" : "L2UI.CheckBox");
				
				content = content.replace("%mode0%", player.getAutoPlaySettings().getNextTargetMode() == 0 ? "L2UI_CH3.radiobutton2" : "L2UI_CH3.radiobutton1");
				content = content.replace("%mode1%", player.getAutoPlaySettings().getNextTargetMode() == 1 ? "L2UI_CH3.radiobutton2" : "L2UI_CH3.radiobutton1");
				content = content.replace("%mode2%", player.getAutoPlaySettings().getNextTargetMode() == 2 ? "L2UI_CH3.radiobutton2" : "L2UI_CH3.radiobutton1");
				content = content.replace("%mode3%", player.getAutoPlaySettings().getNextTargetMode() == 3 ? "L2UI_CH3.radiobutton2" : "L2UI_CH3.radiobutton1");
				
				content = content.replace("%skill_button%", Config.ENABLE_AUTO_SKILL ? "<br><table width=295><tr><td height=31><center><button action=\"bypass -h voice .playskills\" value=\"Skills\" width=95 height=21 back=\"bigbutton_over\" fore=\"bigbutton\"></center></td></tr></table>" : "");
				content = content.replace("%item_button%", Config.ENABLE_AUTO_ITEM ? "<br><table width=295><tr><td height=31><center><button action=\"bypass -h voice .playitems\" value=\"Supply Items\" width=95 height=21 back=\"bigbutton_over\" fore=\"bigbutton\"></center></td></tr></table>" : "");
				content = content.replace("%potion_button%", Config.ENABLE_AUTO_POTION ? "<br><table><tr><td height=31><center><button action=\"bypass -h voice .playpotion\" value=\"Healing Potion\" width=95 height=21 back=\"bigbutton_over\" fore=\"bigbutton\"></center></td></tr></table><table width=295><tr><td height=31><center><table width=150><tr><td width=120><font color=\"CDB67F\">HP Percent (%percent%)</font></td><td><edit var=\"percentbox\" width=30 height=13></td><td><button value=\"Apply\" action=\"bypass -h voice .play percent $percentbox\" width=65 height=21 back=\"L2UI_ch3.smallbutton2_over\" fore=\"L2UI_ch3.smallbutton2\"></td></tr></table></center></td></tr></table>" : "");
				content = content.replace("%percent%", String.valueOf(player.getAutoPlaySettings().getAutoPotionPercent()));
				
				if (player.isAutoPlaying())
				{
					content = content.replace("%status_button%", "<table width=295><tr><td height=31><center><button action=\"bypass -h voice .play stop\" value=\"Stop\" width=95 height=21 back=\"bigbutton_over\" fore=\"bigbutton\"></center></td></tr></table>");
				}
				else
				{
					content = content.replace("%status_button%", "<table width=295><tr><td height=31><center><button action=\"bypass -h voice .play start\" value=\"Start\" width=95 height=21 back=\"bigbutton_over\" fore=\"bigbutton\"></center></td></tr></table>");
				}
				
				html.setHtml(content);
				player.sendPacket(html);
				break;
			}
			case "playskills":
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(5);
				final String content = HtmCache.getInstance().getHtm("data/html/mods/AutoPlay/Skills.htm");
				
				// Generate the skill list. Filter our some skills.
				List<Skill> skills = new ArrayList<>();
				for (Skill skill : player.getAllSkills())
				{
					if (!skill.isPassive() && !skill.isToggle() && !skills.contains(skill) && !Config.DISABLED_AUTO_SKILLS.contains(skill.getId()))
					{
						skills.add(skill);
					}
				}
				final Summon summon = player.getPet();
				if (summon != null)
				{
					for (Skill skill : summon.getAllSkills())
					{
						if (!skill.isPassive() && !skill.isToggle() && !skills.contains(skill) && !Config.DISABLED_AUTO_SKILLS.contains(skill.getId()))
						{
							skills.add(skill);
						}
					}
				}
				
				// Manage skill activation.
				final String[] paramArray = params == null ? new String[0] : params.split(" ");
				if (paramArray.length > 1)
				{
					final Integer skillId = Integer.parseInt(paramArray[1]);
					Skill knownSkill = player.getKnownSkill(skillId);
					if ((knownSkill == null) && (summon != null))
					{
						knownSkill = summon.getKnownSkill(skillId);
						if (knownSkill != null)
						{
							break;
						}
					}
					if (Config.ENABLE_AUTO_SKILL && (knownSkill != null) && skills.contains(knownSkill))
					{
						if (knownSkill.isOffensive())
						{
							if (player.getAutoUseSettings().getAutoSkills().contains(skillId))
							{
								player.getAutoUseSettings().getAutoSkills().remove(skillId);
							}
							else
							{
								player.getAutoUseSettings().getAutoSkills().add(skillId);
							}
						}
						else
						{
							if (player.getAutoUseSettings().getAutoBuffs().contains(skillId))
							{
								player.getAutoUseSettings().getAutoBuffs().remove(skillId);
							}
							else
							{
								player.getAutoUseSettings().getAutoBuffs().add(skillId);
							}
						}
					}
				}
				
				// Calculate page number.
				final int max = MathUtil.countPagesNumber(skills.size(), PAGE_LIMIT);
				int page = params == null ? 1 : Integer.parseInt(paramArray[0]);
				if (page > max)
				{
					page = max;
				}
				
				// Cut skills list up to page number.
				final StringBuilder sb = new StringBuilder();
				skills = skills.subList(Math.max(0, (page - 1) * PAGE_LIMIT), Math.min(page * PAGE_LIMIT, skills.size()));
				if (skills.isEmpty())
				{
					sb.append("<center><br>No skills found.<br></center>");
				}
				else
				{
					// Generate skill table.
					int row = 0;
					for (Skill skill : skills)
					{
						final int skillId = skill.getId();
						sb.append(((row % 2) == 0 ? "<table width=\"295\" bgcolor=\"000000\"><tr>" : "<table width=\"295\"><tr>"));
						if (player.getAutoUseSettings().getAutoBuffs().contains(skill.getId()) || player.getAutoUseSettings().getAutoSkills().contains(skill.getId()))
						{
							if (skillId < 100)
							{
								sb.append("<td height=40 width=40><img src=\"icon.skill00" + skillId + "\" width=32 height=32></td><td width=190>" + skill.getName() + "</td><td><button value=\" \" action=\"bypass -h voice .playskills " + page + " " + skillId + "\" width=32 height=32 back=\"L2UI_CH3.mapbutton_zoomout2\" fore=\"L2UI_CH3.mapbutton_zoomout1\"></td>");
							}
							else if (skillId < 1000)
							{
								sb.append("<td height=40 width=40><img src=\"icon.skill0" + skillId + "\" width=32 height=32></td><td width=190>" + skill.getName() + "</td><td><button value=\" \" action=\"bypass -h voice .playskills " + page + " " + skillId + "\" width=32 height=32 back=\"L2UI_CH3.mapbutton_zoomout2\" fore=\"L2UI_CH3.mapbutton_zoomout1\"></td>");
							}
							else
							{
								sb.append("<td height=40 width=40><img src=\"icon.skill" + skillId + "\" width=32 height=32></td><td width=190>" + skill.getName() + "</td><td><button value=\" \" action=\"bypass -h voice .playskills " + page + " " + skillId + "\" width=32 height=32 back=\"L2UI_CH3.mapbutton_zoomout2\" fore=\"L2UI_CH3.mapbutton_zoomout1\"></td>");
							}
						}
						else
						{
							if (skillId < 100)
							{
								sb.append("<td height=40 width=40><img src=\"icon.skill00" + skillId + "\" width=32 height=32></td><td width=190><font color=\"B09878\">" + skill.getName() + "</font></td><td><button value=\" \" action=\"bypass -h voice .playskills " + page + " " + skillId + "\" width=32 height=32 back=\"L2UI_CH3.mapbutton_zoomin2\" fore=\"L2UI_CH3.mapbutton_zoomin1\"></td>");
							}
							else if (skillId < 1000)
							{
								sb.append("<td height=40 width=40><img src=\"icon.skill0" + skillId + "\" width=32 height=32></td><td width=190><font color=\"B09878\">" + skill.getName() + "</font></td><td><button value=\" \" action=\"bypass -h voice .playskills " + page + " " + skillId + "\" width=32 height=32 back=\"L2UI_CH3.mapbutton_zoomin2\" fore=\"L2UI_CH3.mapbutton_zoomin1\"></td>");
							}
							else
							{
								sb.append("<td height=40 width=40><img src=\"icon.skill" + skillId + "\" width=32 height=32></td><td width=190><font color=\"B09878\">" + skill.getName() + "</font></td><td><button value=\" \" action=\"bypass -h voice .playskills " + page + " " + skillId + "\" width=32 height=32 back=\"L2UI_CH3.mapbutton_zoomin2\" fore=\"L2UI_CH3.mapbutton_zoomin1\"></td>");
							}
						}
						sb.append("</tr></table><img src=\"L2UI.SquareGray\" width=295 height=1>");
					}
					
					// Generate page footer.
					sb.append("<br><img src=\"L2UI.SquareGray\" width=295 height=1><table width=\"100%\" bgcolor=000000><tr>");
					if (page > 1)
					{
						sb.append("<td align=left width=70><a action=\"bypass -h voice .playskills " + (page - 1) + "\"><font color=\"CDB67F\">Previous</font></a></td>");
					}
					else
					{
						sb.append("<td align=left width=70><font color=\"B09878\">Previous</font></td>");
					}
					sb.append("<td align=center width=100>Page " + page + " of " + max + "</td>");
					if (page < max)
					{
						sb.append("<td align=right width=70><a action=\"bypass -h voice .playskills " + (page + 1) + "\"><font color=\"CDB67F\">Next</font></a></td>");
					}
					else
					{
						sb.append("<td align=right width=70><font color=\"B09878\">Next</font></td>");
					}
					sb.append("</tr></table><img src=\"L2UI.SquareGray\" width=295 height=1>");
				}
				
				// Replace and send the html.
				html.setHtml(content.replace("%skills%", sb.toString()));
				player.sendPacket(html);
				break;
			}
			case "playitems":
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(5);
				final String content = HtmCache.getInstance().getHtm("data/html/mods/AutoPlay/Items.htm");
				
				// Generate the item list. Filter our some items.
				List<ItemTemplate> items = new ArrayList<>();
				ITEM_SEARCH: for (Item item : player.getInventory().getItems())
				{
					final ItemTemplate template = item.getTemplate();
					if (!item.isEquipable() && template.hasSkills() && !HP_POTION_IDS.contains(item.getItemId()) && !Config.DISABLED_AUTO_ITEMS.contains(item.getItemId()))
					{
						for (Skill skill : template.getAllSkills())
						{
							if ((skill != null) && skill.isActive() && !items.contains(template))
							{
								items.add(template);
								continue ITEM_SEARCH;
							}
						}
					}
				}
				
				// Manage item activation.
				final String[] paramArray = params == null ? new String[0] : params.split(" ");
				if (paramArray.length > 1)
				{
					final int itemId = Integer.parseInt(paramArray[1]);
					if (Config.ENABLE_AUTO_ITEM && items.contains(ItemTable.getInstance().getTemplate(itemId)))
					{
						if (player.getAutoUseSettings().getAutoSupplyItems().contains(itemId))
						{
							player.getAutoUseSettings().getAutoSupplyItems().remove(itemId);
						}
						else
						{
							player.getAutoUseSettings().getAutoSupplyItems().add(itemId);
						}
					}
				}
				
				// Calculate page number.
				final int max = MathUtil.countPagesNumber(items.size(), PAGE_LIMIT);
				int page = params == null ? 1 : Integer.parseInt(paramArray[0]);
				if (page > max)
				{
					page = max;
				}
				
				// Cut items list up to page number.
				final StringBuilder sb = new StringBuilder();
				items = items.subList(Math.max(0, (page - 1) * PAGE_LIMIT), Math.min(page * PAGE_LIMIT, items.size()));
				if (items.isEmpty())
				{
					sb.append("<center><br>No items found.<br></center>");
				}
				else
				{
					// Generate item table.
					int row = 0;
					for (ItemTemplate template : items)
					{
						sb.append(((row % 2) == 0 ? "<table width=\"295\" bgcolor=\"000000\"><tr>" : "<table width=\"295\"><tr>"));
						if (player.getAutoUseSettings().getAutoSupplyItems().contains(template.getItemId()))
						{
							sb.append("<td height=40 width=40><img src=\"" + template.getIcon() + "\" width=32 height=32></td><td width=190>" + template.getName() + "</td><td><button value=\" \" action=\"bypass -h voice .playitems " + page + " " + template.getItemId() + "\" width=32 height=32 back=\"L2UI_CH3.mapbutton_zoomout2\" fore=\"L2UI_CH3.mapbutton_zoomout1\"></td>");
						}
						else
						{
							sb.append("<td height=40 width=40><img src=\"" + template.getIcon() + "\" width=32 height=32></td><td width=190><font color=\"B09878\">" + template.getName() + "</font></td><td><button value=\" \" action=\"bypass -h voice .playitems " + page + " " + template.getItemId() + "\" width=32 height=32 back=\"L2UI_CH3.mapbutton_zoomin2\" fore=\"L2UI_CH3.mapbutton_zoomin1\"></td>");
						}
						sb.append("</tr></table><img src=\"L2UI.SquareGray\" width=295 height=1>");
					}
					
					// Generate page footer.
					sb.append("<br><img src=\"L2UI.SquareGray\" width=295 height=1><table width=\"100%\" bgcolor=000000><tr>");
					if (page > 1)
					{
						sb.append("<td align=left width=70><a action=\"bypass -h voice .playitems " + (page - 1) + "\"><font color=\"CDB67F\">Previous</font></a></td>");
					}
					else
					{
						sb.append("<td align=left width=70><font color=\"B09878\">Previous</font></td>");
					}
					sb.append("<td align=center width=100>Page " + page + " of " + max + "</td>");
					if (page < max)
					{
						sb.append("<td align=right width=70><a action=\"bypass -h voice .playitems " + (page + 1) + "\"><font color=\"CDB67F\">Next</font></a></td>");
					}
					else
					{
						sb.append("<td align=right width=70><font color=\"B09878\">Next</font></td>");
					}
					sb.append("</tr></table><img src=\"L2UI.SquareGray\" width=295 height=1>");
				}
				
				// Replace and send the html.
				html.setHtml(content.replace("%items%", sb.toString()));
				player.sendPacket(html);
				break;
			}
			case "playpotion":
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(5);
				final String content = HtmCache.getInstance().getHtm("data/html/mods/AutoPlay/Potion.htm");
				
				// Generate the item list. Filter our some items.
				List<ItemTemplate> items = new ArrayList<>();
				POTION_SEARCH: for (Item item : player.getInventory().getItems())
				{
					final ItemTemplate template = item.getTemplate();
					if (HP_POTION_IDS.contains(item.getItemId()) && !Config.DISABLED_AUTO_ITEMS.contains(item.getItemId()) && !items.contains(template))
					{
						items.add(template);
						continue POTION_SEARCH;
					}
				}
				
				// Manage item activation.
				final String[] paramArray = params == null ? new String[0] : params.split(" ");
				if (paramArray.length > 1)
				{
					final int itemId = Integer.parseInt(paramArray[1]);
					if (Config.ENABLE_AUTO_POTION && items.contains(ItemTable.getInstance().getTemplate(itemId)))
					{
						if (player.getAutoUseSettings().getAutoPotionItem() == itemId)
						{
							player.getAutoUseSettings().setAutoPotionItem(0);
						}
						else
						{
							player.getAutoUseSettings().setAutoPotionItem(itemId);
						}
					}
				}
				
				// Calculate page number.
				final int max = MathUtil.countPagesNumber(items.size(), PAGE_LIMIT);
				int page = params == null ? 1 : Integer.parseInt(paramArray[0]);
				if (page > max)
				{
					page = max;
				}
				
				// Cut items list up to page number.
				final StringBuilder sb = new StringBuilder();
				items = items.subList(Math.max(0, (page - 1) * PAGE_LIMIT), Math.min(page * PAGE_LIMIT, items.size()));
				if (items.isEmpty())
				{
					sb.append("<center><br>No potions found.<br></center>");
				}
				else
				{
					// Generate item table.
					int row = 0;
					for (ItemTemplate template : items)
					{
						sb.append(((row % 2) == 0 ? "<table width=\"295\" bgcolor=\"000000\"><tr>" : "<table width=\"295\"><tr>"));
						if (player.getAutoUseSettings().getAutoPotionItem() == template.getItemId())
						{
							sb.append("<td height=40 width=40><img src=\"" + template.getIcon() + "\" width=32 height=32></td><td width=190>" + template.getName() + "</td><td><button value=\" \" action=\"bypass -h voice .playpotion " + page + " " + template.getItemId() + "\" width=32 height=32 back=\"L2UI_CH3.mapbutton_zoomout2\" fore=\"L2UI_CH3.mapbutton_zoomout1\"></td>");
						}
						else
						{
							sb.append("<td height=40 width=40><img src=\"" + template.getIcon() + "\" width=32 height=32></td><td width=190><font color=\"B09878\">" + template.getName() + "</font></td><td><button value=\" \" action=\"bypass -h voice .playpotion " + page + " " + template.getItemId() + "\" width=32 height=32 back=\"L2UI_CH3.mapbutton_zoomin2\" fore=\"L2UI_CH3.mapbutton_zoomin1\"></td>");
						}
						sb.append("</tr></table><img src=\"L2UI.SquareGray\" width=295 height=1>");
					}
					
					// Generate page footer.
					sb.append("<br><img src=\"L2UI.SquareGray\" width=295 height=1><table width=\"100%\" bgcolor=000000><tr>");
					if (page > 1)
					{
						sb.append("<td align=left width=70><a action=\"bypass -h voice .playpotion " + (page - 1) + "\"><font color=\"CDB67F\">Previous</font></a></td>");
					}
					else
					{
						sb.append("<td align=left width=70><font color=\"B09878\">Previous</font></td>");
					}
					sb.append("<td align=center width=100>Page " + page + " of " + max + "</td>");
					if (page < max)
					{
						sb.append("<td align=right width=70><a action=\"bypass -h voice .playpotion " + (page + 1) + "\"><font color=\"CDB67F\">Next</font></a></td>");
					}
					else
					{
						sb.append("<td align=right width=70><font color=\"B09878\">Next</font></td>");
					}
					sb.append("</tr></table><img src=\"L2UI.SquareGray\" width=295 height=1>");
				}
				
				// Replace and send the html.
				html.setHtml(content.replace("%items%", sb.toString()));
				player.sendPacket(html);
				break;
			}
		}
		
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}
