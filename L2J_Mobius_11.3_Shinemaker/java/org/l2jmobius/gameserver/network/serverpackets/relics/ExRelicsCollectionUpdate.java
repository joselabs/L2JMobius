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
package org.l2jmobius.gameserver.network.serverpackets.relics;

import java.util.Collection;

import org.l2jmobius.Config;
import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.gameserver.data.xml.OptionData;
import org.l2jmobius.gameserver.data.xml.RelicCollectionData;
import org.l2jmobius.gameserver.data.xml.RelicData;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.holders.PlayerRelicCollectionData;
import org.l2jmobius.gameserver.model.holders.PlayerRelicData;
import org.l2jmobius.gameserver.model.holders.RelicCollectionDataHolder;
import org.l2jmobius.gameserver.model.holders.RelicDataHolder;
import org.l2jmobius.gameserver.model.options.Options;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * @author CostyKiller
 */
public class ExRelicsCollectionUpdate extends ServerPacket
{
	private final Player _player;
	private final int _relicId;
	private final int _relicLevel;
	
	public ExRelicsCollectionUpdate(Player player, int relicId, int relicLevel)
	{
		_player = player;
		_relicId = relicId;
		_relicLevel = relicLevel;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_RELICS_COLLECTION_UPDATE.writeId(this, buffer);
		
		final Collection<PlayerRelicData> storedRelics = _player.getRelics();
		PlayerRelicData existingRelic = null;
		// Check if the relic with the same ID exists.
		for (PlayerRelicData relic : storedRelics)
		{
			if ((relic.getRelicId() == _relicId) && (relic.getRelicIndex() == 0)) // Only relics with index 0 can be added to collection.
			{
				existingRelic = relic;
				break;
			}
		}
		
		// Check if obtained relic is required in some collection.
		if (existingRelic != null)
		{
			final int relicId = existingRelic.getRelicId();
			final int relicGrade = RelicData.getInstance().getRelic(_relicId).getGrade();
			int neededRelicCollectionId = 0;
			int neededRelicIndex = 0;
			int neededRelicLevel = 0;
			for (RelicCollectionDataHolder cRelicCollectionHolder : RelicCollectionData.getInstance().getRelicCollections())
			{
				// Find the relicId into collections.
				for (RelicDataHolder relicData : cRelicCollectionHolder.getRelics())
				{
					// Relic id found.
					if ((relicData.getRelicId() == relicId) && (!_player.isRelicRegisteredInCollection(relicId, cRelicCollectionHolder.getCollectionId())))
					{
						for (int i = 0; i < cRelicCollectionHolder.getRelics().size(); i++)
						{
							final RelicDataHolder relic = cRelicCollectionHolder.getRelic(i);
							if ((relic.getRelicId() == relicId))
							{
								neededRelicCollectionId = cRelicCollectionHolder.getCollectionId();
								neededRelicIndex = i; // Position found.
								neededRelicLevel = cRelicCollectionHolder.getRelic(i).getEnchantLevel();
								// Add relic to collection if matches the level needed.
								if (existingRelic.getRelicLevel() == neededRelicLevel)
								{
									if (Config.RELIC_SYSTEM_DEBUG_ENABLED)
									{
										_player.sendMessage("2.Relic id: " + existingRelic.getRelicId() + " with level: " + existingRelic.getRelicLevel() + " needed in collection: " + neededRelicCollectionId);
									}
									// Update Relic Collections from db.
									_player.getRelicCollections().add(new PlayerRelicCollectionData(neededRelicCollectionId, existingRelic.getRelicId(), existingRelic.getRelicLevel(), neededRelicIndex));
									_player.storeRelicCollections();
									_player.sendPacket(new ExRelicsCollectionInfo(_player));
									if (Config.RELIC_SYSTEM_DEBUG_ENABLED)
									{
										_player.sendMessage("Added Relic Id: " + existingRelic.getRelicId() + " into Collection Id: " + neededRelicCollectionId);
									}
								}
							}
						}
						
						if ((relicGrade == 4) || (relicGrade == 5))
						{
							return;
						}
						continue;
					}
				}
			}
			
			if ((neededRelicCollectionId != 0) && !_player.isRelicRegistered(existingRelic.getRelicId(), existingRelic.getRelicLevel()))
			{
				// Check if collection is complete and give skills.
				if (_player.isCompleteCollection(neededRelicCollectionId))
				{
					// Announce Collection Complete.
					_player.sendPacket(new ExRelicsCollectionCompleteAnnounce(neededRelicCollectionId));
					// Apply collection option if all requirements are met.
					final Options options = OptionData.getInstance().getOptions(RelicCollectionData.getInstance().getRelicCollection(neededRelicCollectionId).getOptionId());
					if (options != null)
					{
						options.apply(_player);
						if (Config.RELIC_SYSTEM_DEBUG_ENABLED)
						{
							_player.sendMessage("Added Skill for complete collection: " + options.getPassiveSkills());
						}
					}
					buffer.writeInt(1); // Collection array size.
					buffer.writeInt(neededRelicCollectionId); // Collection id.
					buffer.writeByte(_player.isCompleteCollection(neededRelicCollectionId)); // Collection is complete?
					buffer.writeInt(1); // Registered relics in collection size.
					buffer.writeInt(1); // Array position.
					buffer.writeInt(_relicId); // Relic id.
					buffer.writeInt(_relicLevel); // Relic level.
				}
			}
		}
	}
}
