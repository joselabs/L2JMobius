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
package org.l2jmobius.gameserver.data.xml;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import org.l2jmobius.Config;
import org.l2jmobius.commons.util.IXmlReader;
import org.l2jmobius.gameserver.model.StatSet;
import org.l2jmobius.gameserver.model.holders.RelicDataHolder;

/**
 * @author CostyKiller
 */
public class RelicData implements IXmlReader
{
	private static final Logger LOGGER = Logger.getLogger(RelicData.class.getName());
	
	private static final Map<Integer, RelicDataHolder> RELICS = new HashMap<>();
	
	protected RelicData()
	{
		if (Config.RELIC_SYSTEM_ENABLED)
		{
			load();
		}
	}
	
	@Override
	public void load()
	{
		RELICS.clear();
		parseDatapackFile("data/RelicData.xml");
		
		if (!RELICS.isEmpty())
		{
			LOGGER.info(getClass().getSimpleName() + ": Loaded " + RELICS.size() + " relics.");
		}
		else
		{
			LOGGER.info(getClass().getSimpleName() + ": System is disabled.");
		}
	}
	
	@Override
	public void parseDocument(Document doc, File f)
	{
		for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
		{
			if ("list".equalsIgnoreCase(n.getNodeName()))
			{
				for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
				{
					if ("relic".equalsIgnoreCase(d.getNodeName()))
					{
						NamedNodeMap attrs = d.getAttributes();
						Node att;
						final StatSet set = new StatSet();
						for (int i = 0; i < attrs.getLength(); i++)
						{
							att = attrs.item(i);
							set.set(att.getNodeName(), att.getNodeValue());
						}
						
						final int relicId = parseInteger(attrs, "id");
						final int grade = parseInteger(attrs, "grade");
						final int skillId = parseInteger(attrs, "skillId");
						int enchantLevel = 0;
						int skillLevel = 0;
						for (Node b = d.getFirstChild(); b != null; b = b.getNextSibling())
						{
							attrs = b.getAttributes();
							if ("relicStat".equalsIgnoreCase(b.getNodeName()))
							{
								enchantLevel = parseInteger(attrs, "enchantLevel");
								skillLevel = parseInteger(attrs, "skillLevel");
							}
						}
						final RelicDataHolder template = new RelicDataHolder(relicId, grade, skillId, enchantLevel, skillLevel);
						RELICS.put(relicId, template);
					}
				}
			}
		}
	}
	
	public RelicDataHolder getRelic(int id)
	{
		return RELICS.get(id);
	}
	
	public int getRelicSkillId(int id)
	{
		return RELICS.get(id).getSkillId();
	}
	
	public int getRelicSkillLevel(int id)
	{
		return RELICS.get(id).getSkillLevel();
	}
	
	public Collection<RelicDataHolder> getRelics()
	{
		return RELICS.values();
	}
	
	public static RelicData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final RelicData INSTANCE = new RelicData();
	}
}
