/*
 * Copyright (c) 2013 L2jMobius
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
 * IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.l2jmobius.gameserver.data.xml;

import static org.l2jmobius.gameserver.model.itemcontainer.Inventory.ADENA_ID;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.l2jmobius.Config;
import org.l2jmobius.commons.database.DatabaseFactory;
import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.commons.util.StringUtil;
import org.l2jmobius.commons.util.file.filter.XMLFilter;
import org.l2jmobius.gameserver.enums.ItemLocation;
import org.l2jmobius.gameserver.instancemanager.IdManager;
import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.WorldObject;
import org.l2jmobius.gameserver.model.actor.Attackable;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.instance.EventMonster;
import org.l2jmobius.gameserver.model.events.EventDispatcher;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.impl.item.OnItemCreate;
import org.l2jmobius.gameserver.model.holders.ItemSkillHolder;
import org.l2jmobius.gameserver.model.item.Armor;
import org.l2jmobius.gameserver.model.item.EtcItem;
import org.l2jmobius.gameserver.model.item.ItemTemplate;
import org.l2jmobius.gameserver.model.item.Weapon;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.model.item.type.EtcItemType;
import org.l2jmobius.gameserver.model.skill.AmmunitionSkillList;
import org.l2jmobius.gameserver.util.DocumentItem;
import org.l2jmobius.gameserver.util.GMAudit;

/**
 * This class serves as a container for all item templates in the game.
 */
public class ItemData
{
	private static final Logger LOGGER = Logger.getLogger(ItemData.class.getName());
	private static final Logger LOGGER_ITEMS = Logger.getLogger("item");
	
	private ItemTemplate[] _allTemplates;
	private final Map<Integer, EtcItem> _etcItems = new HashMap<>();
	private final Map<Integer, Armor> _armors = new HashMap<>();
	private final Map<Integer, Weapon> _weapons = new HashMap<>();
	private final List<File> _itemFiles = new ArrayList<>();
	
	public static final Map<String, Long> SLOTS = new HashMap<>();
	static
	{
		SLOTS.put("shirt", (long) ItemTemplate.SLOT_UNDERWEAR);
		SLOTS.put("lbracelet", (long) ItemTemplate.SLOT_L_BRACELET);
		SLOTS.put("rbracelet", (long) ItemTemplate.SLOT_R_BRACELET);
		SLOTS.put("talisman", (long) ItemTemplate.SLOT_DECO);
		SLOTS.put("chest", (long) ItemTemplate.SLOT_CHEST);
		SLOTS.put("fullarmor", (long) ItemTemplate.SLOT_FULL_ARMOR);
		SLOTS.put("head", (long) ItemTemplate.SLOT_HEAD);
		SLOTS.put("hair", (long) ItemTemplate.SLOT_HAIR);
		SLOTS.put("hairall", (long) ItemTemplate.SLOT_HAIRALL);
		SLOTS.put("underwear", (long) ItemTemplate.SLOT_UNDERWEAR);
		SLOTS.put("back", (long) ItemTemplate.SLOT_BACK);
		SLOTS.put("neck", (long) ItemTemplate.SLOT_NECK);
		SLOTS.put("legs", (long) ItemTemplate.SLOT_LEGS);
		SLOTS.put("feet", (long) ItemTemplate.SLOT_FEET);
		SLOTS.put("gloves", (long) ItemTemplate.SLOT_GLOVES);
		SLOTS.put("chest,legs", (long) ItemTemplate.SLOT_CHEST | ItemTemplate.SLOT_LEGS);
		SLOTS.put("belt", (long) ItemTemplate.SLOT_BELT);
		SLOTS.put("rhand", (long) ItemTemplate.SLOT_R_HAND);
		SLOTS.put("lhand", (long) ItemTemplate.SLOT_L_HAND);
		SLOTS.put("lrhand", (long) ItemTemplate.SLOT_LR_HAND);
		SLOTS.put("rear;lear", (long) ItemTemplate.SLOT_R_EAR | ItemTemplate.SLOT_L_EAR);
		SLOTS.put("rfinger;lfinger", (long) ItemTemplate.SLOT_R_FINGER | ItemTemplate.SLOT_L_FINGER);
		SLOTS.put("wolf", (long) ItemTemplate.SLOT_WOLF);
		SLOTS.put("greatwolf", (long) ItemTemplate.SLOT_GREATWOLF);
		SLOTS.put("hatchling", (long) ItemTemplate.SLOT_HATCHLING);
		SLOTS.put("strider", (long) ItemTemplate.SLOT_STRIDER);
		SLOTS.put("babypet", (long) ItemTemplate.SLOT_BABYPET);
		SLOTS.put("brooch", (long) ItemTemplate.SLOT_BROOCH);
		SLOTS.put("brooch_jewel", (long) ItemTemplate.SLOT_BROOCH_JEWEL);
		SLOTS.put("agathion", ItemTemplate.SLOT_AGATHION);
		SLOTS.put("artifactbook", ItemTemplate.SLOT_ARTIFACT_BOOK);
		SLOTS.put("artifact", ItemTemplate.SLOT_ARTIFACT);
		SLOTS.put("none", (long) ItemTemplate.SLOT_NONE);
		
		// retail compatibility
		SLOTS.put("onepiece", (long) ItemTemplate.SLOT_FULL_ARMOR);
		SLOTS.put("hair2", (long) ItemTemplate.SLOT_HAIR2);
		SLOTS.put("dhair", (long) ItemTemplate.SLOT_HAIRALL);
		SLOTS.put("alldress", (long) ItemTemplate.SLOT_ALLDRESS);
		SLOTS.put("deco1", (long) ItemTemplate.SLOT_DECO);
		SLOTS.put("waist", (long) ItemTemplate.SLOT_BELT);
	}
	
	protected ItemData()
	{
		processDirectory("data/stats/items", _itemFiles);
		if (Config.CUSTOM_ITEMS_LOAD)
		{
			processDirectory("data/stats/items/custom", _itemFiles);
		}
		
		load();
	}
	
	private void processDirectory(String dirName, List<File> list)
	{
		final File dir = new File(Config.DATAPACK_ROOT, dirName);
		if (!dir.exists())
		{
			LOGGER.warning("Dir " + dir.getAbsolutePath() + " does not exist.");
			return;
		}
		final File[] files = dir.listFiles(new XMLFilter());
		for (File file : files)
		{
			list.add(file);
		}
	}
	
	private Collection<ItemTemplate> loadItems()
	{
		final Collection<ItemTemplate> list = ConcurrentHashMap.newKeySet();
		if (Config.THREADS_FOR_LOADING)
		{
			final Collection<ScheduledFuture<?>> jobs = ConcurrentHashMap.newKeySet();
			for (File file : _itemFiles)
			{
				jobs.add(ThreadPool.schedule(() ->
				{
					final DocumentItem document = new DocumentItem(file);
					document.parse();
					list.addAll(document.getItemList());
				}, 0));
			}
			while (!jobs.isEmpty())
			{
				for (ScheduledFuture<?> job : jobs)
				{
					if ((job == null) || job.isDone() || job.isCancelled())
					{
						jobs.remove(job);
					}
				}
			}
		}
		else
		{
			for (File file : _itemFiles)
			{
				final DocumentItem document = new DocumentItem(file);
				document.parse();
				list.addAll(document.getItemList());
			}
		}
		return list;
	}
	
	private void load()
	{
		int highest = 0;
		_armors.clear();
		_etcItems.clear();
		_weapons.clear();
		for (ItemTemplate item : loadItems())
		{
			if (highest < item.getId())
			{
				highest = item.getId();
			}
			if (item instanceof EtcItem)
			{
				_etcItems.put(item.getId(), (EtcItem) item);
				
				if ((item.getItemType() == EtcItemType.ARROW) || (item.getItemType() == EtcItemType.BOLT) || (item.getItemType() == EtcItemType.ELEMENTAL_ORB))
				{
					final List<ItemSkillHolder> skills = item.getAllSkills();
					if (skills != null)
					{
						AmmunitionSkillList.add(skills);
					}
				}
			}
			else if (item instanceof Armor)
			{
				_armors.put(item.getId(), (Armor) item);
			}
			else
			{
				_weapons.put(item.getId(), (Weapon) item);
			}
		}
		buildFastLookupTable(highest);
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + _etcItems.size() + " Etc Items");
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + _armors.size() + " Armor Items");
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + _weapons.size() + " Weapon Items");
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + (_etcItems.size() + _armors.size() + _weapons.size()) + " Items in total.");
	}
	
	/**
	 * Builds a variable in which all items are putting in in function of their ID.
	 * @param size
	 */
	private void buildFastLookupTable(int size)
	{
		// Create a FastLookUp Table called _allTemplates of size : value of the highest item ID
		LOGGER.info(getClass().getSimpleName() + ": Highest item id used: " + size);
		_allTemplates = new ItemTemplate[size + 1];
		
		// Insert armor item in Fast Look Up Table
		for (Armor item : _armors.values())
		{
			_allTemplates[item.getId()] = item;
		}
		
		// Insert weapon item in Fast Look Up Table
		for (Weapon item : _weapons.values())
		{
			_allTemplates[item.getId()] = item;
		}
		
		// Insert etcItem item in Fast Look Up Table
		for (EtcItem item : _etcItems.values())
		{
			_allTemplates[item.getId()] = item;
		}
	}
	
	/**
	 * Returns the item corresponding to the item ID
	 * @param id : int designating the item
	 * @return Item
	 */
	public ItemTemplate getTemplate(int id)
	{
		if ((id >= _allTemplates.length) || (id < 0))
		{
			return null;
		}
		return _allTemplates[id];
	}
	
	/**
	 * Create the Item corresponding to the Item Identifier and quantitiy add logs the activity. <b><u>Actions</u>:</b>
	 * <li>Create and Init the Item corresponding to the Item Identifier and quantity</li>
	 * <li>Add the Item object to _allObjects of L2world</li>
	 * <li>Logs Item creation according to log settings</li><br>
	 * @param process : String Identifier of process triggering this action
	 * @param itemId : int Item Identifier of the item to be created
	 * @param count : int Quantity of items to be created for stackable items
	 * @param actor : Creature requesting the item creation
	 * @param reference : Object Object referencing current action like NPC selling item or previous item in transformation
	 * @return Item corresponding to the new item
	 */
	public Item createItem(String process, int itemId, long count, Creature actor, Object reference)
	{
		// Create and Init the Item corresponding to the Item Identifier
		final Item item = new Item(IdManager.getInstance().getNextId(), itemId);
		if (process.equalsIgnoreCase("loot") && !Config.AUTO_LOOT_ITEM_IDS.contains(itemId))
		{
			ScheduledFuture<?> itemLootShedule;
			if ((reference instanceof Attackable) && ((Attackable) reference).isRaid()) // loot privilege for raids
			{
				final Attackable raid = (Attackable) reference;
				// if in CommandChannel and was killing a World/RaidBoss
				if ((raid.getFirstCommandChannelAttacked() != null) && !Config.AUTO_LOOT_RAIDS)
				{
					item.setOwnerId(raid.getFirstCommandChannelAttacked().getLeaderObjectId());
					itemLootShedule = ThreadPool.schedule(new ResetOwner(item), Config.LOOT_RAIDS_PRIVILEGE_INTERVAL);
					item.setItemLootShedule(itemLootShedule);
				}
			}
			else if (!Config.AUTO_LOOT || ((reference instanceof EventMonster) && ((EventMonster) reference).eventDropOnGround()))
			{
				item.setOwnerId(actor.getObjectId());
				itemLootShedule = ThreadPool.schedule(new ResetOwner(item), 15000);
				item.setItemLootShedule(itemLootShedule);
			}
		}
		
		// Add the Item object to _allObjects of L2world
		World.getInstance().addObject(item);
		
		// Set Item parameters
		if (item.isStackable() && (count > 1))
		{
			item.setCount(count);
		}
		
		if ((Config.LOG_ITEMS && !process.equals("Reset") && ((!Config.LOG_ITEMS_SMALL_LOG) && (!Config.LOG_ITEMS_IDS_ONLY))) || (Config.LOG_ITEMS_SMALL_LOG && (item.isEquipable() || (item.getId() == ADENA_ID))) || (Config.LOG_ITEMS_IDS_ONLY && Config.LOG_ITEMS_IDS_LIST.contains(item.getId())))
		{
			if (item.getEnchantLevel() > 0)
			{
				final StringBuilder sb = new StringBuilder();
				sb.append("CREATE:");
				sb.append(process);
				sb.append(", item ");
				sb.append(item.getObjectId());
				sb.append(":+");
				sb.append(item.getEnchantLevel());
				sb.append(" ");
				sb.append(item.getTemplate().getName());
				sb.append("(");
				sb.append(item.getCount());
				sb.append("), ");
				sb.append(actor);
				sb.append(", ");
				sb.append(reference);
				LOGGER_ITEMS.info(sb.toString());
			}
			else
			{
				final StringBuilder sb = new StringBuilder();
				sb.append("CREATE:");
				sb.append(process);
				sb.append(", item ");
				sb.append(item.getObjectId());
				sb.append(":");
				sb.append(item.getTemplate().getName());
				sb.append("(");
				sb.append(item.getCount());
				sb.append("), ");
				sb.append(actor);
				sb.append(", ");
				sb.append(reference);
				LOGGER_ITEMS.info(sb.toString());
			}
		}
		
		if ((actor != null) && actor.isGM() && Config.GMAUDIT)
		{
			final StringBuilder sb = new StringBuilder();
			sb.append(process);
			sb.append("(id: ");
			sb.append(itemId);
			sb.append(" count: ");
			sb.append(count);
			sb.append(" name: ");
			sb.append(item.getItemName());
			sb.append(" objId: ");
			sb.append(item.getObjectId());
			sb.append(")");
			
			final String targetName = (actor.getTarget() != null ? actor.getTarget().getName() : "no-target");
			
			String referenceName = "no-reference";
			if (reference instanceof WorldObject)
			{
				referenceName = (((WorldObject) reference).getName() != null ? ((WorldObject) reference).getName() : "no-name");
			}
			else if (reference instanceof String)
			{
				referenceName = (String) reference;
			}
			
			GMAudit.auditGMAction(actor.toString(), sb.toString(), targetName, StringUtil.concat("Object referencing this action is: ", referenceName));
		}
		
		// Notify to scripts
		if (EventDispatcher.getInstance().hasListener(EventType.ON_ITEM_CREATE, item.getTemplate()))
		{
			EventDispatcher.getInstance().notifyEventAsync(new OnItemCreate(process, item, actor, reference), item.getTemplate());
		}
		
		return item;
	}
	
	public Item createItem(String process, int itemId, long count, Player actor)
	{
		return createItem(process, itemId, count, actor, null);
	}
	
	/**
	 * Destroys the Item.<br>
	 * <br>
	 * <b><u>Actions</u>:</b>
	 * <ul>
	 * <li>Sets Item parameters to be unusable</li>
	 * <li>Removes the Item object to _allObjects of L2world</li>
	 * <li>Logs Item deletion according to log settings</li>
	 * </ul>
	 * @param process a string identifier of process triggering this action.
	 * @param item the item instance to be destroyed.
	 * @param actor the player requesting the item destroy.
	 * @param reference the object referencing current action like NPC selling item or previous item in transformation.
	 */
	public void destroyItem(String process, Item item, Player actor, Object reference)
	{
		synchronized (item)
		{
			final long old = item.getCount();
			item.setCount(0);
			item.setOwnerId(0);
			item.setItemLocation(ItemLocation.VOID);
			item.setLastChange(Item.REMOVED);
			
			World.getInstance().removeObject(item);
			IdManager.getInstance().releaseId(item.getObjectId());
			
			if ((Config.LOG_ITEMS && ((!Config.LOG_ITEMS_SMALL_LOG) && (!Config.LOG_ITEMS_IDS_ONLY))) || (Config.LOG_ITEMS_SMALL_LOG && (item.isEquipable() || (item.getId() == ADENA_ID))) || (Config.LOG_ITEMS_IDS_ONLY && Config.LOG_ITEMS_IDS_LIST.contains(item.getId())))
			{
				if (item.getEnchantLevel() > 0)
				{
					final StringBuilder sb = new StringBuilder();
					sb.append("DELETE:");
					sb.append(process);
					sb.append(", item ");
					sb.append(item.getObjectId());
					sb.append(":+");
					sb.append(item.getEnchantLevel());
					sb.append(" ");
					sb.append(item.getTemplate().getName());
					sb.append("(");
					sb.append(item.getCount());
					sb.append("), PrevCount(");
					sb.append(old);
					sb.append("), ");
					sb.append(actor);
					sb.append(", ");
					sb.append(reference);
					LOGGER_ITEMS.info(sb.toString());
				}
				else
				{
					final StringBuilder sb = new StringBuilder();
					sb.append("DELETE:");
					sb.append(process);
					sb.append(", item ");
					sb.append(item.getObjectId());
					sb.append(":");
					sb.append(item.getTemplate().getName());
					sb.append("(");
					sb.append(item.getCount());
					sb.append("), PrevCount(");
					sb.append(old);
					sb.append("), ");
					sb.append(actor);
					sb.append(", ");
					sb.append(reference);
					LOGGER_ITEMS.info(sb.toString());
				}
			}
			
			if ((actor != null) && actor.isGM() && Config.GMAUDIT)
			{
				final StringBuilder sb = new StringBuilder();
				sb.append(process);
				sb.append("(id: ");
				sb.append(item.getId());
				sb.append(" count: ");
				sb.append(item.getCount());
				sb.append(" itemObjId: ");
				sb.append(item.getObjectId());
				sb.append(")");
				
				final String targetName = (actor.getTarget() != null ? actor.getTarget().getName() : "no-target");
				
				String referenceName = "no-reference";
				if (reference instanceof WorldObject)
				{
					referenceName = (((WorldObject) reference).getName() != null ? ((WorldObject) reference).getName() : "no-name");
				}
				else if (reference instanceof String)
				{
					referenceName = (String) reference;
				}
				
				GMAudit.auditGMAction(actor.toString(), sb.toString(), targetName, StringUtil.concat("Object referencing this action is: ", referenceName));
			}
			
			// if it's a pet control item, delete the pet as well
			if (item.getTemplate().isPetItem())
			{
				try (Connection con = DatabaseFactory.getConnection();
					PreparedStatement statement = con.prepareStatement("DELETE FROM pets WHERE item_obj_id=?"))
				{
					// Delete the pet in db
					statement.setInt(1, item.getObjectId());
					statement.execute();
				}
				catch (Exception e)
				{
					LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Could not delete pet objectid:", e);
				}
			}
		}
	}
	
	public void reload()
	{
		load();
		EnchantItemHPBonusData.getInstance().load();
	}
	
	public Set<Integer> getAllArmorsId()
	{
		return _armors.keySet();
	}
	
	public Collection<Armor> getAllArmors()
	{
		return _armors.values();
	}
	
	public Set<Integer> getAllWeaponsId()
	{
		return _weapons.keySet();
	}
	
	public Collection<Weapon> getAllWeapons()
	{
		return _weapons.values();
	}
	
	public Set<Integer> getAllEtcItemsId()
	{
		return _etcItems.keySet();
	}
	
	public Collection<EtcItem> getAllEtcItems()
	{
		return _etcItems.values();
	}
	
	public ItemTemplate[] getAllItems()
	{
		return _allTemplates;
	}
	
	public int getArraySize()
	{
		return _allTemplates.length;
	}
	
	protected static class ResetOwner implements Runnable
	{
		Item _item;
		
		public ResetOwner(Item item)
		{
			_item = item;
		}
		
		@Override
		public void run()
		{
			// Set owner id to 0 only when location is VOID.
			if (_item.getItemLocation() == ItemLocation.VOID)
			{
				_item.setOwnerId(0);
			}
			_item.setItemLootShedule(null);
		}
	}
	
	public static ItemData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ItemData INSTANCE = new ItemData();
	}
}
