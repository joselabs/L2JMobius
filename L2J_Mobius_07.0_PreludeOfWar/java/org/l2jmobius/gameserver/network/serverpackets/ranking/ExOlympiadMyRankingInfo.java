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
package org.l2jmobius.gameserver.network.serverpackets.ranking;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.l2jmobius.commons.database.DatabaseFactory;
import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.gameserver.instancemanager.RankManager;
import org.l2jmobius.gameserver.model.StatSet;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.olympiad.Hero;
import org.l2jmobius.gameserver.model.olympiad.Olympiad;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.PacketLogger;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * @author NviX
 */
public class ExOlympiadMyRankingInfo extends ServerPacket
{
	// TODO: Move query and store data at RankManager.
	private static final String GET_CURRENT_CYCLE_DATA = "SELECT charId, olympiad_points, competitions_won, competitions_lost FROM olympiad_nobles WHERE class_id = ? ORDER BY olympiad_points DESC, competitions_won DESC LIMIT " + RankManager.PLAYER_LIMIT;
	private static final String GET_PREVIOUS_CYCLE_DATA = "SELECT charId, olympiad_points, competitions_won, competitions_lost FROM olympiad_nobles_eom WHERE class_id = ? ORDER BY olympiad_points DESC, competitions_won DESC LIMIT " + RankManager.PLAYER_LIMIT;
	
	private final Player _player;
	
	public ExOlympiadMyRankingInfo(Player player)
	{
		_player = player;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_OLYMPIAD_MY_RANKING_INFO.writeId(this, buffer);
		final Date date = new Date();
		final Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		int year = calendar.get(Calendar.YEAR);
		// Add one to month {0 - 11}
		int month = calendar.get(Calendar.MONTH) + 1;
		
		int currentPlace = 0;
		int currentWins = 0;
		int currentLoses = 0;
		int currentPoints = 0;
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement(GET_CURRENT_CYCLE_DATA))
		{
			statement.setInt(1, _player.getBaseClass());
			try (ResultSet rset = statement.executeQuery())
			{
				int i = 1;
				while (rset.next())
				{
					if (rset.getInt("charId") == _player.getObjectId())
					{
						currentPlace = i;
						currentWins = rset.getInt("competitions_won");
						currentLoses = rset.getInt("competitions_lost");
						currentPoints = rset.getInt("olympiad_points");
					}
					i++;
				}
			}
		}
		catch (SQLException e)
		{
			PacketLogger.warning("Olympiad my ranking: Could not load data: " + e.getMessage());
		}
		int previousPlace = 0;
		int previousWins = 0;
		int previousLoses = 0;
		int previousPoints = 0;
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement(GET_PREVIOUS_CYCLE_DATA))
		{
			statement.setInt(1, _player.getBaseClass());
			try (ResultSet rset = statement.executeQuery())
			{
				int i = 1;
				while (rset.next())
				{
					if (rset.getInt("charId") == _player.getObjectId())
					{
						previousPlace = i;
						previousWins = rset.getInt("competitions_won");
						previousLoses = rset.getInt("competitions_lost");
						previousPoints = rset.getInt("olympiad_points");
					}
					i++;
				}
			}
		}
		catch (SQLException e)
		{
			PacketLogger.warning("Olympiad my ranking: Could not load data: " + e.getMessage());
		}
		int heroCount = 0;
		int legendCount = 0;
		if (Hero.getInstance().getCompleteHeroes().containsKey(_player.getObjectId()))
		{
			final StatSet hero = Hero.getInstance().getCompleteHeroes().get(_player.getObjectId());
			heroCount = hero.getInt("count", 0);
			legendCount = hero.getInt("legend_count", 0);
		}
		buffer.writeInt(year); // Year
		buffer.writeInt(month); // Month
		buffer.writeInt(Math.min(Olympiad.getInstance().getCurrentCycle() - 1, 0)); // cycle ?
		buffer.writeInt(currentPlace); // Place on current cycle ?
		buffer.writeInt(currentWins); // Wins
		buffer.writeInt(currentLoses); // Loses
		buffer.writeInt(currentPoints); // Points
		buffer.writeInt(previousPlace); // Place on previous cycle
		buffer.writeInt(previousWins); // win count & lose count previous cycle? lol
		buffer.writeInt(previousLoses); // ??
		buffer.writeInt(previousPoints); // Points on previous cycle
		buffer.writeInt(heroCount); // Hero counts
		buffer.writeInt(legendCount); // Legend counts
		buffer.writeInt(0); // change to 1 causes shows nothing
	}
}
