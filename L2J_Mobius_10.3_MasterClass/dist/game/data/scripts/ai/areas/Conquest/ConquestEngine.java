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
package ai.areas.Conquest;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;

import org.l2jmobius.Config;
import org.l2jmobius.commons.database.DatabaseFactory;
import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.gameserver.enums.TeleportWhereType;
import org.l2jmobius.gameserver.instancemanager.GlobalVariablesManager;
import org.l2jmobius.gameserver.instancemanager.ZoneManager;
import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.ListenerRegisterType;
import org.l2jmobius.gameserver.model.events.annotations.RegisterEvent;
import org.l2jmobius.gameserver.model.events.annotations.RegisterType;
import org.l2jmobius.gameserver.model.events.impl.creature.player.OnPlayerPvPKill;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.model.zone.ZoneId;
import org.l2jmobius.gameserver.model.zone.type.ConquestZone;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.SystemMessage;
import org.l2jmobius.gameserver.network.serverpackets.dethrone.ExDethroneSeasonInfo;
import org.l2jmobius.gameserver.util.Broadcast;

import ai.AbstractNpcAI;

/**
 * Conquest Engine.
 * @author CostyKiller
 */
public class ConquestEngine extends AbstractNpcAI
{
	private static final ConquestPointData CONQUEST_POINT_DATA = ConquestPointData.getInstance();
	private static final int BLOODY_COIN = 80306;
	private static final boolean DEBUG = false; // Set it true to display point related messages into server console.
	
	// Queries
	private static final String LOAD_CONQUEST_DATA = "SELECT current_cycle, conquest_season_end FROM conquest_data WHERE id = 0";
	private static final String SAVE_CONQUEST_DATA = "INSERT INTO conquest_data (id, current_cycle, conquest_season_end) VALUES (0,?,?) ON DUPLICATE KEY UPDATE current_cycle=?, conquest_season_end=?";
	private static final String CLEAR_CONQUEST_PREVIOUS_RANKLIST = "DELETE FROM conquest_prev_season_ranklist;";
	private static final String SAVE_CONQUEST_PREVIOUS_RANKLIST = "INSERT INTO conquest_prev_season_ranklist (charId, char_name, personal_points) SELECT a.charId, a.val, b.val FROM character_variables a, character_variables b WHERE a.var=\"CONQUEST_NAME\" AND b.var=\"CONQUEST_PERSONAL_POINTS\" AND a.charId=b.charId;";
	
	// Variables
	private static final String CONQUEST_AVAILABLE_VAR = "CONQUEST_AVAILABLE";
	private static final String CONQUEST_CONNECTED_VAR = "CONQUEST_CONNECTED";
	private static final String CONQUEST_SERVER_POINTS_VAR = "CONQUEST_SERVER_POINTS";
	private static final String CONQUEST_PREV_SEASON_SERVER_POINTS_VAR = "CONQUEST_PREV_SEASON_SERVER_POINTS";
	
	protected static boolean _isConquestAvailable = GlobalVariablesManager.getInstance().getBoolean(CONQUEST_AVAILABLE_VAR, false);
	protected int _currentCycle;
	protected long _conquestSeasonEnd;
	
	protected ScheduledFuture<?> _scheduledOpenAccessStartTask;
	protected ScheduledFuture<?> _scheduledOpenAccessEndTask;
	protected ScheduledFuture<?> _scheduledConquestSeasonEnd;
	
	public ConquestEngine()
	{
		if (Config.CONQUEST_SYSTEM_ENABLED)
		{
			init();
			load();
		}
		else
		{
			String s = "=[ Conquest ]";
			while (s.length() < 61)
			{
				s = "-" + s;
			}
			LOGGER.info(s);
			LOGGER.info(ConquestEngine.class.getSimpleName() + ": Conquest is disabled.");
		}
	}
	
	private void load()
	{
		boolean loaded = false;
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement(LOAD_CONQUEST_DATA);
			ResultSet rset = statement.executeQuery())
		{
			while (rset.next())
			{
				_currentCycle = rset.getInt("current_cycle");
				_conquestSeasonEnd = rset.getLong("conquest_season_end");
				loaded = true;
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, ConquestEngine.class.getSimpleName() + ": Error loading conquest data from database: ", e);
		}
		if (!loaded)
		{
			LOGGER.info(ConquestEngine.class.getSimpleName() + ": Failed to load data from database, trying to load from file.");
			
			final Properties conquestProperties = new Properties();
			try (InputStream is = new FileInputStream(Config.CONQUEST_CONFIG_FILE))
			{
				conquestProperties.load(is);
			}
			catch (Exception e)
			{
				LOGGER.log(Level.SEVERE, ConquestEngine.class.getSimpleName() + ": Error loading conquest properties: ", e);
				return;
			}
			_currentCycle = Integer.parseInt(conquestProperties.getProperty("ConquestCurrentCycle", "1"));
			_conquestSeasonEnd = Long.parseLong(conquestProperties.getProperty("ConquestSeasonEnd", "0"));
		}
		
		if ((_conquestSeasonEnd == 0) || (_conquestSeasonEnd < System.currentTimeMillis()))
		{
			setNewConquestSeasonEnd();
		}
		
		addKillId(CONQUEST_POINT_DATA.getPointsInfo().keySet());
		LOGGER.info(ConquestEngine.class.getSimpleName() + ": Conquest Server Points: " + getServerPoints());
		LOGGER.info(ConquestEngine.class.getSimpleName() + ": Conquest " + getZoneNameById(1) + " Zone points: " + getZonePoints(1));
		LOGGER.info(ConquestEngine.class.getSimpleName() + ": Conquest " + getZoneNameById(2) + " Zone points: " + getZonePoints(2));
		LOGGER.info(ConquestEngine.class.getSimpleName() + ": Conquest " + getZoneNameById(3) + " Zone points: " + getZonePoints(3));
	}
	
	protected void setNewConquestSeasonEnd()
	{
		final SystemMessage sm = new SystemMessage(SystemMessageId.THE_CONQUEST_CYCLE_S1_HAS_BEGUN);
		sm.addInt(_currentCycle);
		Broadcast.toAllOnlinePlayers(sm);
		LOGGER.info(ConquestEngine.class.getSimpleName() + ": The Conquest cycle: " + _currentCycle + " has begun.");
		
		_conquestSeasonEnd = System.currentTimeMillis() + getMillisToConquestSeasonEnd();
		
		// Update db
		saveConquestData();
	}
	
	private long getMillisToConquestSeasonEnd()
	{
		long timeDiff = 0;
		final Calendar calendar = Calendar.getInstance();
		final Date currentDate = calendar.getTime();
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		final Date lastDayOfMonth = calendar.getTime();
		timeDiff = lastDayOfMonth.getTime() - currentDate.getTime();
		return Math.max(0, timeDiff);
	}
	
	/**
	 * @return time in millis until open access starts.
	 */
	private long getMillisUntilOpenAccessStart()
	{
		// Get the current date and time.
		LocalDateTime now = LocalDateTime.now();
		
		// Find the closest start time of the next available window.
		long millisUntilOpenAccessStart = 0;
		if (Config.CONQUEST_AVAILABLE_DAYS1.contains(now.getDayOfWeek().getValue()))
		{
			final LocalDateTime startTime1 = LocalDateTime.of(now.toLocalDate(), LocalTime.parse(Config.CONQUEST_START_HOUR1));
			final LocalDateTime endTime1 = LocalDateTime.of(now.toLocalDate(), LocalTime.parse(Config.CONQUEST_END_HOUR1));
			final LocalDateTime startTime2 = LocalDateTime.of(now.toLocalDate(), LocalTime.parse(Config.CONQUEST_START_HOUR2));
			// Added 1 day to endTime2 for proper millis calculation
			final LocalDateTime endTime2 = LocalDateTime.of(now.toLocalDate().plusDays(1), LocalTime.parse(Config.CONQUEST_END_HOUR2));
			
			if ((now.isAfter(startTime1) && now.isBefore(endTime1)) || (now.isAfter(startTime2) && now.isBefore(endTime2)))
			{
				millisUntilOpenAccessStart = 0;
			}
			else if (now.isAfter(endTime1) && now.isBefore(startTime2))
			{
				millisUntilOpenAccessStart = ChronoUnit.MILLIS.between(now, startTime2);
			}
			else if (now.isBefore(startTime1))
			{
				millisUntilOpenAccessStart = ChronoUnit.MILLIS.between(now, startTime1);
			}
		}
		else if (Config.CONQUEST_AVAILABLE_DAYS2.contains(now.getDayOfWeek().getValue()))
		{
			final LocalDateTime startTime3 = LocalDateTime.of(now.toLocalDate(), LocalTime.parse(Config.CONQUEST_START_HOUR3));
			// Added 1 day to endTime3 for proper millis calculation
			final LocalDateTime endTime3 = LocalDateTime.of(now.toLocalDate().plusDays(1), LocalTime.parse(Config.CONQUEST_END_HOUR3));
			if (now.isBefore(startTime3))
			{
				millisUntilOpenAccessStart = ChronoUnit.MILLIS.between(now, startTime3);
			}
			else if (now.isAfter(startTime3) && now.isBefore(endTime3))
			{
				millisUntilOpenAccessStart = 0;
			}
		}
		return millisUntilOpenAccessStart;
	}
	
	/**
	 * @return time in millis until open access ends.
	 */
	private long getMillisUntilOpenAccessEnd()
	{
		// Get the current date and time.
		LocalDateTime now = LocalDateTime.now();
		
		// Find the closest end time of the next available window.
		long millisUntilOpenAccessEnd = 0;
		if (Config.CONQUEST_AVAILABLE_DAYS1.contains(now.getDayOfWeek().getValue()))
		{
			final LocalDateTime startTime1 = LocalDateTime.of(now.toLocalDate(), LocalTime.parse(Config.CONQUEST_START_HOUR1));
			final LocalDateTime endTime1 = LocalDateTime.of(now.toLocalDate(), LocalTime.parse(Config.CONQUEST_END_HOUR1));
			final LocalDateTime startTime2 = LocalDateTime.of(now.toLocalDate(), LocalTime.parse(Config.CONQUEST_START_HOUR2));
			final LocalDateTime endTime2 = LocalDateTime.of(now.toLocalDate().plusDays(1), LocalTime.parse(Config.CONQUEST_END_HOUR2));
			
			if (now.isAfter(startTime1) && now.isBefore(endTime1))
			{
				millisUntilOpenAccessEnd = ChronoUnit.MILLIS.between(now, endTime1);
			}
			else if (now.isAfter(startTime2) && now.isBefore(endTime2))
			{
				millisUntilOpenAccessEnd = ChronoUnit.MILLIS.between(now, endTime2);
			}
			else if ((now.isAfter(endTime1) && now.isBefore(startTime2)) || (now.isBefore(startTime1)))
			{
				millisUntilOpenAccessEnd = 0;
			}
		}
		else if (Config.CONQUEST_AVAILABLE_DAYS2.contains(now.getDayOfWeek().getValue()))
		{
			final LocalDateTime startTime3 = LocalDateTime.of(now.toLocalDate(), LocalTime.parse(Config.CONQUEST_START_HOUR3));
			final LocalDateTime endTime3 = LocalDateTime.of(now.toLocalDate().plusDays(1), LocalTime.parse(Config.CONQUEST_END_HOUR3));
			if (now.isBefore(startTime3))
			{
				millisUntilOpenAccessEnd = 0;
			}
			else if (now.isAfter(startTime3) && now.isBefore(endTime3))
			{
				millisUntilOpenAccessEnd = ChronoUnit.MILLIS.between(now, endTime3);
			}
		}
		return millisUntilOpenAccessEnd;
	}
	
	protected void init()
	{
		// Schedule not enabled means conquest is available all time
		if (!Config.CONQUEST_SCHEDULE_ENABLED)
		{
			_isConquestAvailable = true;
			GlobalVariablesManager.getInstance().set(CONQUEST_AVAILABLE_VAR, true);
			if (!GlobalVariablesManager.getInstance().hasVariable(CONQUEST_CONNECTED_VAR))
			{
				GlobalVariablesManager.getInstance().set(CONQUEST_CONNECTED_VAR, false);
			}
			return;
		}
		
		// Set conquest end if current day is first day of month
		if (Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == 1)
		{
			setNewConquestSeasonEnd();
			if (_scheduledConquestSeasonEnd != null)
			{
				_scheduledConquestSeasonEnd.cancel(true);
			}
			_scheduledConquestSeasonEnd = ThreadPool.schedule(new ConquestSeasonEndTask(), getMillisToConquestSeasonEnd());
		}
		
		// Start / Close Access
		updateOpenAccessStatus();
	}
	
	private synchronized void updateOpenAccessStatus()
	{
		long millisToEnd;
		if (_isConquestAvailable)
		{
			millisToEnd = getMillisUntilOpenAccessEnd();
		}
		else
		{
			millisToEnd = getMillisUntilOpenAccessStart();
		}
		
		final long numDays = millisToEnd / 86400000;
		millisToEnd %= 86400000;
		final long numHours = millisToEnd / 3600000;
		millisToEnd %= 3600000;
		final long numMins = millisToEnd / 60000;
		
		if (_isConquestAvailable)
		{
			LOGGER.info(ConquestEngine.class.getSimpleName() + ": Conquest Access: is Open. Closes in: " + numDays + " days, " + numHours + " hours and " + numMins + " mins.");
		}
		else
		{
			LOGGER.info(ConquestEngine.class.getSimpleName() + ": Conquest Access: is Closed. Opens in: " + numDays + " days, " + numHours + " hours and " + numMins + " mins.");
		}
		LOGGER.info(ConquestEngine.class.getSimpleName() + ": Conquest Season End: " + (new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")).format(System.currentTimeMillis() + getMillisToConquestSeasonEnd()));
		
		_scheduledOpenAccessStartTask = ThreadPool.schedule(() ->
		{
			if (!Config.CONQUEST_SYSTEM_ENABLED)
			{
				return;
			}
			
			_isConquestAvailable = true;
			GlobalVariablesManager.getInstance().set(CONQUEST_AVAILABLE_VAR, true);
			
			for (Player player : World.getInstance().getPlayers())
			{
				player.sendPacket(new ExDethroneSeasonInfo(true));
			}
			
			Broadcast.toAllOnlinePlayers(new SystemMessage(SystemMessageId.THE_PATH_TO_THE_CONQUEST_WORLD_IS_OPEN_YOU_CAN_GET_THERE_ON_MONDAYS_TUESDAYS_WEDNESDAYS_AND_THURSDAYS_FROM_10_00_TILL_14_00_AND_FROM_22_00_TILL_00_00_AND_ON_FRIDAYS_SATURDAYS_AND_SUNDAYS_FROM_20_00_TILL_01_00_OF_THE_FOLLOWING_DAY_SERVER_TIME_PVP_IS_DISABLED_FROM_20_00_TILL_22_00_FOR_2_HOURS_BECAUSE_THE_NEW_WORLD_EXPLORATION_IS_UNDER_WAY));
			LOGGER.info(ConquestEngine.class.getSimpleName() + ": Conquest access is now Open.");
			
			_scheduledOpenAccessEndTask = ThreadPool.schedule(() ->
			{
				_isConquestAvailable = false;
				GlobalVariablesManager.getInstance().set(CONQUEST_AVAILABLE_VAR, false);
				if (!GlobalVariablesManager.getInstance().hasVariable(CONQUEST_CONNECTED_VAR))
				{
					GlobalVariablesManager.getInstance().set(CONQUEST_CONNECTED_VAR, false);
				}
				
				for (Player player : World.getInstance().getPlayers())
				{
					player.sendPacket(new ExDethroneSeasonInfo(false));
				}
				
				Broadcast.toAllOnlinePlayers(new SystemMessage(SystemMessageId.THE_PATH_TO_THE_CONQUEST_WORLD_IS_CLOSED_YOU_CAN_GET_THERE_ON_MONDAYS_TUESDAYS_WEDNESDAYS_AND_THURSDAYS_FROM_10_00_TILL_14_00_AND_FROM_22_00_TILL_00_00_AND_ON_FRIDAYS_SATURDAYS_AND_SUNDAYS_FROM_20_00_TILL_01_00_OF_THE_FOLLOWING_DAY_SERVER_TIME_PVP_IS_DISABLED_FROM_20_00_TILL_22_00_FOR_2_HOURS_BECAUSE_THE_NEW_WORLD_EXPLORATION_IS_UNDER_WAY));
				LOGGER.info(ConquestEngine.class.getSimpleName() + ": Conquest access is now Closed.");
				
				// Teleport players to town if Open Access ends.
				ConquestZone conquestZone = (ConquestZone) ZoneManager.getInstance().getZoneByName("conquest");
				for (Player player : conquestZone.getPlayersInside())
				{
					if (!player.isGM())
					{
						player.teleToLocation(TeleportWhereType.TOWN);
					}
				}
				init();
			}, getMillisUntilOpenAccessEnd());
		}, getMillisUntilOpenAccessStart());
	}
	
	protected class ConquestSeasonEndTask implements Runnable
	{
		@SuppressWarnings("synthetic-access")
		@Override
		public void run()
		{
			final SystemMessage sm = new SystemMessage(SystemMessageId.THE_CONQUEST_CYCLE_S1_IS_OVER);
			sm.addInt(_currentCycle);
			Broadcast.toAllOnlinePlayers(sm);
			
			LOGGER.info(ConquestEngine.class.getSimpleName() + ": The Conquest cycle: " + _currentCycle + " is over.");
			
			if (_scheduledOpenAccessEndTask != null)
			{
				_scheduledOpenAccessEndTask.cancel(true);
			}
			
			saveConquestData();
			saveConquestPrevSeasonRanklistData();
			saveConquestPrevSeasonServerPoints();
			_currentCycle++;
			setNewConquestSeasonEnd();
			init();
		}
	}
	
	/**
	 * Save conquest data to database
	 */
	public void saveConquestData()
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement(SAVE_CONQUEST_DATA))
		{
			statement.setInt(1, _currentCycle);
			statement.setLong(2, _conquestSeasonEnd);
			// On duplicate
			statement.setInt(3, _currentCycle);
			statement.setLong(4, _conquestSeasonEnd);
			statement.execute();
		}
		catch (SQLException e)
		{
			LOGGER.log(Level.SEVERE, ConquestEngine.class.getSimpleName() + ": Failed to save conquest data to database: ", e);
		}
	}
	
	/**
	 * Save conquest previous season rank list data to database
	 */
	public void saveConquestPrevSeasonRanklistData()
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps1 = con.prepareStatement(CLEAR_CONQUEST_PREVIOUS_RANKLIST);
			PreparedStatement ps2 = con.prepareStatement(SAVE_CONQUEST_PREVIOUS_RANKLIST))
		{
			ps1.execute();
			ps2.execute();
		}
		catch (SQLException e)
		{
			LOGGER.log(Level.SEVERE, ConquestEngine.class.getSimpleName() + ": Failed to save previous season rank list data to database: ", e);
		}
	}
	
	/**
	 * Save conquest previous season points to database
	 */
	public void saveConquestPrevSeasonServerPoints()
	{
		GlobalVariablesManager.getInstance().set(CONQUEST_PREV_SEASON_SERVER_POINTS_VAR, GlobalVariablesManager.getInstance().getLong(CONQUEST_SERVER_POINTS_VAR));
		GlobalVariablesManager.getInstance().set(CONQUEST_SERVER_POINTS_VAR, 0);
		// Maybe add here soul orbs as well.
	}
	
	/**
	 * Sets the Attack Points.
	 * @param player the player to set points for
	 * @param attackPoints the attack points amount to set
	 */
	public void setAttackPoints(Player player, int attackPoints)
	{
		if (DEBUG)
		{
			LOGGER.info(ConquestEngine.class.getSimpleName() + ": Attack Points updated for player: " + player.getName() + " from " + getAttackPoints(player) + " to " + (getAttackPoints(player) + attackPoints) + ".");
		}
		player.getVariables().set("CONQUEST_ATTACK_POINTS", getAttackPoints(player) + attackPoints);
	}
	
	/**
	 * Sets the Life Points.
	 * @param player the player to set points for
	 * @param lifePoints the life points amount to set
	 */
	public void setLifePoints(Player player, int lifePoints)
	{
		if (DEBUG)
		{
			LOGGER.info(ConquestEngine.class.getSimpleName() + ": Life Points updated for player: " + player.getName() + " from " + getLifePoints(player) + " to " + (getLifePoints(player) + lifePoints) + ".");
		}
		player.getVariables().set("CONQUEST_LIFE_POINTS", getLifePoints(player) + lifePoints);
	}
	
	/**
	 * Sets the Conquest Personal Points.
	 * @param player the activeChar
	 * @param personalPoints the personal points amount to set
	 */
	public void setPersonalPoints(Player player, long personalPoints)
	{
		if (DEBUG)
		{
			LOGGER.info(ConquestEngine.class.getSimpleName() + ": Personal Points updated from " + getPersonalPoints(player) + " to " + (getPersonalPoints(player) + personalPoints) + ".");
		}
		player.getVariables().set(PlayerVariables.CONQUEST_PERSONAL_POINTS, getPersonalPoints(player) + personalPoints);
	}
	
	/**
	 * Sets the Conquest Server Points.
	 * @param player the activeChar
	 * @param serverPoints the server points amount to set
	 */
	public void setServerPoints(Player player, long serverPoints)
	{
		if (DEBUG)
		{
			LOGGER.info(ConquestEngine.class.getSimpleName() + ": Global Server Points updated from " + getServerPoints() + " to " + (getServerPoints() + serverPoints) + ".");
		}
		GlobalVariablesManager.getInstance().set(CONQUEST_SERVER_POINTS_VAR, getServerPoints() + serverPoints);
	}
	
	/**
	 * Gets the Conquest Zone name by id.
	 * @param zoneId the zone id
	 * @return
	 */
	public String getZoneNameById(int zoneId)
	{
		switch (zoneId)
		{
			case 1:
			{
				return "ASA";
			}
			case 2:
			{
				return "ANIMA";
			}
			case 3:
			{
				return "NOX";
			}
		}
		return "NONE";
	}
	
	/**
	 * Sets the Conquest Zone Points.
	 * @param zonePoints the zone points amount to set
	 * @param zoneId the zone id to set points for
	 */
	public void setZonePoints(int zoneId, int zonePoints)
	{
		// exclusion for monsters with no zone id
		if (!getZoneNameById(zoneId).equals("NONE"))
		{
			GlobalVariablesManager.getInstance().set("CONQUEST_ZONE_" + getZoneNameById(zoneId) + "_POINTS", getZonePoints(zoneId) + zonePoints);
			if (DEBUG)
			{
				LOGGER.info(ConquestEngine.class.getSimpleName() + ": Zone Points updated for zone: " + getZoneNameById(zoneId) + " from " + getZonePoints(zoneId) + " to " + (getZonePoints(zoneId) + zonePoints) + ".");
			}
		}
	}
	
	/**
	 * Updates the conquest points.
	 * @param player the activeChar
	 * @param personalPoints the personal points
	 * @param serverPoints the server points
	 * @param zonePoints the zone points
	 * @param zoneId the zone id
	 * @param useRates if {@code true} it will use Conquest rates multipliers
	 */
	public synchronized void updatePoints(Player player, int personalPoints, int serverPoints, int zonePoints, int zoneId, boolean useRates)
	{
		if (!_isConquestAvailable)
		{
			return;
		}
		
		long finalPersonalPoints = personalPoints;
		long finalServerPoints = serverPoints;
		int finalZonePoints = zonePoints;
		if (useRates)
		{
			finalPersonalPoints = personalPoints * Config.CONQUEST_RATE_PERSONAL_POINTS;
			finalServerPoints = serverPoints * Config.CONQUEST_RATE_SERVER_POINTS;
			finalZonePoints = zonePoints * Config.CONQUEST_RATE_ZONE_POINTS;
		}
		setPersonalPoints(player, finalPersonalPoints);
		setServerPoints(player, finalServerPoints);
		setZonePoints(zoneId, finalZonePoints);
	}
	
	/**
	 * Gets the Conquest attack points.
	 * @param player the player to get points for
	 * @return the attack points amount
	 */
	public int getAttackPoints(Player player)
	{
		return player.getVariables().getInt("CONQUEST_ATTACK_POINTS", Config.CONQUEST_ATTACK_POINTS);
	}
	
	/**
	 * Gets the Conquest life points.
	 * @param player the player to get points for
	 * @return the life points amount
	 */
	public int getLifePoints(Player player)
	{
		return player.getVariables().getInt("CONQUEST_LIFE_POINTS", Config.CONQUEST_LIFE_POINTS);
	}
	
	/**
	 * Gets the Conquest personal points.
	 * @param player the player to get points for
	 * @return the personal points amount
	 */
	public long getPersonalPoints(Player player)
	{
		return player.getVariables().getLong(PlayerVariables.CONQUEST_PERSONAL_POINTS, 0);
	}
	
	/**
	 * Gets the Conquest server points.
	 * @return the server points amount
	 */
	public long getServerPoints()
	{
		return GlobalVariablesManager.getInstance().getLong(CONQUEST_SERVER_POINTS_VAR, 0);
	}
	
	/**
	 * Gets the Conquest zone points.
	 * @param zoneId the zone id to get point for
	 * @return the server points amount
	 */
	public int getZonePoints(int zoneId)
	{
		return GlobalVariablesManager.getInstance().getInt("CONQUEST_ZONE_" + getZoneNameById(zoneId) + "_POINTS", 0);
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		if (_isConquestAvailable)
		{
			final int npcId = npc.getId();
			updatePoints(killer, CONQUEST_POINT_DATA.getPersonalPointsAmount(npcId), CONQUEST_POINT_DATA.getServerPointsAmount(npcId), CONQUEST_POINT_DATA.getZonePointsAmount(npcId), CONQUEST_POINT_DATA.getZoneId(npcId), true);
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@RegisterEvent(EventType.ON_PLAYER_PVP_KILL)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onPlayerPvPKill(OnPlayerPvPKill event)
	{
		if (_isConquestAvailable && event.getPlayer().isInsideZone(ZoneId.CONQUEST) && event.getTarget().isInsideZone(ZoneId.CONQUEST))
		{
			final Player attackerPlayer = event.getPlayer();
			final Player targetPlayer = event.getTarget();
			if (CONQUEST_POINT_DATA.getPvpPointsInfo().containsKey(targetPlayer.getLevel()))
			{
				updatePoints(attackerPlayer, CONQUEST_POINT_DATA.getPvpPersonalPointsAmount(targetPlayer.getLevel()), CONQUEST_POINT_DATA.getPvpServerPointsAmount(targetPlayer.getLevel()), 0, 0, true);
				if ((getAttackPoints(attackerPlayer) >= 1) && (getLifePoints(targetPlayer) >= 1))
				{
					attackerPlayer.addItem("ConquestCoins", BLOODY_COIN, CONQUEST_POINT_DATA.getCoinsAmount(targetPlayer.getLevel()), attackerPlayer, true);
					setAttackPoints(attackerPlayer, -1);
					setLifePoints(targetPlayer, -1);
					
					// Looser Message
					SystemMessage sm1 = new SystemMessage(SystemMessageId.C1_HAS_DEFEATED_YOU_YOU_HAVE_LOST_1_VITALITY_POINT_CHARACTERS_GET_BLOODY_COINS_FOR_A_VICTORY_ONLY_IF_THEY_HAVE_AT_LEAST_1_VITALITY_POINT_TO_CHECK_THEIR_CURRENT_AMOUNT_ENTER_BLOODYCOIN_IN_YOUR_CHAT_WINDOW);
					sm1.addString(attackerPlayer.getName());
					targetPlayer.sendPacket(sm1);
					
					// Winner Message
					SystemMessage sm2 = new SystemMessage(SystemMessageId.YOU_HAVE_DEFEATED_C1_AND_GOT_BLOODY_COINS_X_S2_PERSONAL_CONQUEST_POINTS_X_S3_SERVER_CONQUEST_POINTS_X_S4_ATTACK_POINTS_1_TO_CHECK_THEIR_CURRENT_AMOUNT_ENTER_BLOODYCOIN_IN_YOUR_CHAT_WINDOW);
					sm2.addString(targetPlayer.getName());
					sm2.addString(Integer.toString(CONQUEST_POINT_DATA.getCoinsAmount(targetPlayer.getLevel())));
					sm2.addString(Long.toString(CONQUEST_POINT_DATA.getPvpPersonalPointsAmount(targetPlayer.getLevel())));
					sm2.addString(Long.toString(CONQUEST_POINT_DATA.getPvpServerPointsAmount(targetPlayer.getLevel())));
					attackerPlayer.sendPacket(sm2);
				}
			}
		}
	}
	
	public static void main(String[] args)
	{
		new ConquestEngine();
	}
}
