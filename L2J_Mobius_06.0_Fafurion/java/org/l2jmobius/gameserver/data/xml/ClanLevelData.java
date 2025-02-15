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
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import org.l2jmobius.commons.util.IXmlReader;

/**
 * @author Mobius
 */
public class ClanLevelData implements IXmlReader
{
	private static final Logger LOGGER = Logger.getLogger(ClanLevelData.class.getName());
	
	private static final int EXPECTED_CLAN_LEVEL_DATA = 16; // Level 0 included.
	
	private int[] CLAN_EXP;
	private int[] CLAN_COMMON_MEMBERS;
	private int[] CLAN_ELITE_MEMBERS;
	private int MAX_CLAN_LEVEL = 0;
	
	protected ClanLevelData()
	{
		load();
	}
	
	@Override
	public synchronized void load()
	{
		CLAN_EXP = new int[EXPECTED_CLAN_LEVEL_DATA];
		CLAN_COMMON_MEMBERS = new int[EXPECTED_CLAN_LEVEL_DATA];
		CLAN_ELITE_MEMBERS = new int[EXPECTED_CLAN_LEVEL_DATA];
		MAX_CLAN_LEVEL = 0;
		
		parseDatapackFile("data/ClanLevelData.xml");
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + (EXPECTED_CLAN_LEVEL_DATA - 1) /* level 0 excluded */ + " clan level data.");
	}
	
	@Override
	public void parseDocument(Document doc, File f)
	{
		for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
		{
			if ("list".equals(n.getNodeName()))
			{
				for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
				{
					if ("clan".equals(d.getNodeName()))
					{
						NamedNodeMap attrs = d.getAttributes();
						
						final int level = parseInteger(attrs, "level");
						final int exp = parseInteger(attrs, "exp");
						final int commonMembers = parseInteger(attrs, "commonMembers");
						final int eliteMembers = parseInteger(attrs, "eliteMembers");
						
						if (MAX_CLAN_LEVEL < level)
						{
							MAX_CLAN_LEVEL = level;
						}
						
						CLAN_EXP[level] = exp;
						CLAN_COMMON_MEMBERS[level] = commonMembers;
						CLAN_ELITE_MEMBERS[level] = eliteMembers;
					}
				}
			}
		}
	}
	
	public int getLevelExp(int clanLevel)
	{
		return CLAN_EXP[clanLevel];
	}
	
	public int getCommonMemberLimit(int clanLevel)
	{
		return CLAN_COMMON_MEMBERS[clanLevel];
	}
	
	public int getEliteMemberLimit(int clanLevel)
	{
		return CLAN_ELITE_MEMBERS[clanLevel];
	}
	
	public int getMaxLevel()
	{
		return MAX_CLAN_LEVEL;
	}
	
	public static ClanLevelData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ClanLevelData INSTANCE = new ClanLevelData();
	}
}
