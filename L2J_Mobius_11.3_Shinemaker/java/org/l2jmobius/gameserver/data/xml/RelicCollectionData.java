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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import org.l2jmobius.commons.util.IXmlReader;
import org.l2jmobius.gameserver.model.StatSet;
import org.l2jmobius.gameserver.model.holders.RelicCollectionDataHolder;
import org.l2jmobius.gameserver.model.holders.RelicDataHolder;

/**
 * @author CostyKiller
 */
public class RelicCollectionData implements IXmlReader
{
	private static final Logger LOGGER = Logger.getLogger(RelicCollectionData.class.getName());
	
	private static final Map<Integer, RelicCollectionDataHolder> RELIC_COLLECTIONS = new HashMap<>();
	private static final Map<Integer, List<RelicCollectionDataHolder>> RELIC_COLLECTION_CATEGORIES = new HashMap<>();
	
	protected RelicCollectionData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		RELIC_COLLECTIONS.clear();
		parseDatapackFile("data/RelicCollectionData.xml");
		
		if (!RELIC_COLLECTIONS.isEmpty())
		{
			LOGGER.info(getClass().getSimpleName() + ": Loaded " + RELIC_COLLECTIONS.size() + " relic collections.");
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
					if ("relicCollection".equalsIgnoreCase(d.getNodeName()))
					{
						NamedNodeMap attrs = d.getAttributes();
						Node att;
						final StatSet set = new StatSet();
						for (int i = 0; i < attrs.getLength(); i++)
						{
							att = attrs.item(i);
							set.set(att.getNodeName(), att.getNodeValue());
						}
						
						final int id = parseInteger(attrs, "id");
						final int optionId = parseInteger(attrs, "optionId");
						final int category = parseInteger(attrs, "category");
						final int completeCount = parseInteger(attrs, "completeCount");
						final List<RelicDataHolder> relics = new ArrayList<>();
						for (Node b = d.getFirstChild(); b != null; b = b.getNextSibling())
						{
							attrs = b.getAttributes();
							if ("relic".equalsIgnoreCase(b.getNodeName()))
							{
								final int relicId = parseInteger(attrs, "id");
								final int relicLevel = parseInteger(attrs, "enchantLevel", 0);
								final RelicDataHolder relic = RelicData.getInstance().getRelic(relicId);
								if (relic == null)
								{
									LOGGER.severe(getClass().getSimpleName() + ": Relic null for relicId: " + relicId + " relics collection item: " + id);
									continue;
								}
								relics.add(new RelicDataHolder(relicId, relic.getGrade(), relic.getSkillId(), relicLevel, relic.getSkillLevel()));
							}
						}
						
						final RelicCollectionDataHolder template = new RelicCollectionDataHolder(id, optionId, category, completeCount, relics);
						RELIC_COLLECTIONS.put(id, template);
						RELIC_COLLECTION_CATEGORIES.computeIfAbsent(template.getCategory(), list -> new ArrayList<>()).add(template);
					}
				}
			}
		}
	}
	
	public RelicCollectionDataHolder getRelicCollection(int id)
	{
		return RELIC_COLLECTIONS.get(id);
	}
	
	public List<RelicCollectionDataHolder> getRelicCategory(int tabId)
	{
		if (RELIC_COLLECTION_CATEGORIES.containsKey(tabId))
		{
			return RELIC_COLLECTION_CATEGORIES.get(tabId);
		}
		return Collections.emptyList();
	}
	
	public Collection<RelicCollectionDataHolder> getRelicCollections()
	{
		return RELIC_COLLECTIONS.values();
	}
	
	public static RelicCollectionData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final RelicCollectionData INSTANCE = new RelicCollectionData();
	}
}
