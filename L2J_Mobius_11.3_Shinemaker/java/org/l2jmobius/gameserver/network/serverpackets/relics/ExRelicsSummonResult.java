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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.l2jmobius.Config;
import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.commons.util.Rnd;
import org.l2jmobius.gameserver.data.xml.RelicData;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.holders.PlayerRelicData;
import org.l2jmobius.gameserver.model.variables.AccountVariables;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;
import org.l2jmobius.gameserver.util.Broadcast;

/**
 * @author CostyKiller
 */
public class ExRelicsSummonResult extends ServerPacket
{
	private static final List<Integer> NO_GRADE_COMMON_RELICS = IntStream.rangeClosed(1, 26).boxed().collect(Collectors.toList());
	private static final List<Integer> D_GRADE_COMMON_RELICS = Arrays.asList(27, 29, 33, 34, 36, 37, 39, 40, 42, 43, 45, 47, 49, 50, 53, 54, 56, 57, 59, 60);
	private static final List<Integer> D_GRADE_SHINING_RELICS = Arrays.asList(28, 30, 31, 32, 35, 38, 41, 44, 46, 48, 51, 52, 55, 58, 61);
	private static final List<Integer> C_GRADE_COMMON_RELICS = Arrays.asList(62, 63, 64, 68, 70, 71, 74, 75, 78, 79, 82, 83, 84, 87, 91, 92, 95, 96, 98, 99, 101, 102, 103, 104);
	private static final List<Integer> C_GRADE_SHINING_RELICS = Arrays.asList(65, 66, 67, 69, 72, 73, 76, 77, 80, 81, 85, 86, 88, 89, 90, 93, 94, 97, 100, 105, 106, 107);
	private static final List<Integer> B_GRADE_COMMON_RELICS = IntStream.rangeClosed(108, 118).boxed().collect(Collectors.toList());
	private static final List<Integer> B_GRADE_SHINING_RELICS = IntStream.rangeClosed(119, 129).boxed().collect(Collectors.toList());
	private static final List<Integer> A_GRADE_COMMON_RELICS = IntStream.rangeClosed(130, 140).boxed().collect(Collectors.toList());
	
	private final Player _player;
	private final int _relicCouponItemId;
	private final int _relicSummonCount;
	
	public ExRelicsSummonResult(Player player, int relicCouponItemId, int relicSummonCount)
	{
		_player = player;
		_relicCouponItemId = relicCouponItemId;
		_relicSummonCount = relicSummonCount;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_RELICS_SUMMON_RESULT.writeId(this, buffer);
		
		buffer.writeByte(true); // Only works with true.
		buffer.writeInt(_relicCouponItemId); // Summon item id.
		buffer.writeInt(_relicSummonCount); // Array size of obtained relics.
		
		// Obtained relics by scroll type.
		int obtainedRelicId = 0;
		for (int i = 1; i <= _relicSummonCount; i++)
		{
			if (Config.RELIC_SYSTEM_DEBUG_ENABLED)
			{
				_player.sendMessage("I = " + i);
			}
			// Relic Summon Coupon (relic of No-grade/ D-grade/ C-grade).
			if ((_relicCouponItemId == 83006) || (_relicCouponItemId == 83007))
			{
				final int relicChance = Rnd.get(100);
				final int shiningRelicChance = Rnd.get(100);
				if (relicChance <= Config.RELIC_SUMMON_COMMON_COUPON_CHANCE_C_GRADE)
				{
					if (shiningRelicChance <= Config.RELIC_SUMMON_CHANCE_SHINING_C_GRADE)
					{
						obtainedRelicId = C_GRADE_SHINING_RELICS.get(Rnd.get(C_GRADE_SHINING_RELICS.size()));
					}
					else
					{
						obtainedRelicId = C_GRADE_COMMON_RELICS.get(Rnd.get(C_GRADE_COMMON_RELICS.size()));
					}
				}
				else if (relicChance <= Config.RELIC_SUMMON_COMMON_COUPON_CHANCE_D_GRADE)
				{
					if (shiningRelicChance <= Config.RELIC_SUMMON_CHANCE_SHINING_D_GRADE)
					{
						obtainedRelicId = D_GRADE_SHINING_RELICS.get(Rnd.get(D_GRADE_SHINING_RELICS.size()));
					}
					else
					{
						obtainedRelicId = D_GRADE_COMMON_RELICS.get(Rnd.get(D_GRADE_COMMON_RELICS.size()));
					}
				}
				else
				{
					obtainedRelicId = NO_GRADE_COMMON_RELICS.get(Rnd.get(NO_GRADE_COMMON_RELICS.size()));
				}
			}
			// Shining Relic Summon Coupon (relics of No-grade/ D-grade/ C-grade/ B-grade).
			else if ((_relicCouponItemId == 83004) || (_relicCouponItemId == 83005))
			{
				final int relicChance = Rnd.get(100);
				final int shiningRelicChance = Rnd.get(100);
				if (relicChance <= Config.RELIC_SUMMON_SHINING_COUPON_CHANCE_B_GRADE)
				{
					if (shiningRelicChance <= Config.RELIC_SUMMON_CHANCE_SHINING_B_GRADE)
					{
						obtainedRelicId = B_GRADE_SHINING_RELICS.get(Rnd.get(B_GRADE_SHINING_RELICS.size()));
					}
					else
					{
						obtainedRelicId = B_GRADE_COMMON_RELICS.get(Rnd.get(B_GRADE_COMMON_RELICS.size()));
					}
				}
				else if (relicChance <= Config.RELIC_SUMMON_SHINING_COUPON_CHANCE_C_GRADE)
				{
					if (shiningRelicChance <= Config.RELIC_SUMMON_CHANCE_SHINING_C_GRADE)
					{
						obtainedRelicId = C_GRADE_SHINING_RELICS.get(Rnd.get(C_GRADE_SHINING_RELICS.size()));
					}
					else
					{
						obtainedRelicId = C_GRADE_COMMON_RELICS.get(Rnd.get(C_GRADE_COMMON_RELICS.size()));
					}
				}
				else if (relicChance <= Config.RELIC_SUMMON_SHINING_COUPON_CHANCE_D_GRADE)
				{
					if (shiningRelicChance <= Config.RELIC_SUMMON_CHANCE_SHINING_D_GRADE)
					{
						obtainedRelicId = D_GRADE_SHINING_RELICS.get(Rnd.get(D_GRADE_SHINING_RELICS.size()));
					}
					else
					{
						obtainedRelicId = D_GRADE_COMMON_RELICS.get(Rnd.get(D_GRADE_COMMON_RELICS.size()));
					}
				}
				else
				{
					obtainedRelicId = NO_GRADE_COMMON_RELICS.get(Rnd.get(NO_GRADE_COMMON_RELICS.size()));
				}
			}
			// C-grade Relic Summon Coupon (relics of C-grade).
			else if (_relicCouponItemId == 82931)
			{
				final int shiningRelicChance = Rnd.get(100);
				if (shiningRelicChance <= Config.RELIC_SUMMON_CHANCE_SHINING_C_GRADE)
				{
					obtainedRelicId = C_GRADE_SHINING_RELICS.get(Rnd.get(C_GRADE_SHINING_RELICS.size()));
				}
				else
				{
					obtainedRelicId = C_GRADE_COMMON_RELICS.get(Rnd.get(C_GRADE_COMMON_RELICS.size()));
				}
			}
			// B-grade Relic Summon Coupon (relics of B-grade).
			else if (_relicCouponItemId == 82932)
			{
				final int shiningRelicChance = Rnd.get(100);
				if (shiningRelicChance <= Config.RELIC_SUMMON_CHANCE_SHINING_B_GRADE)
				{
					obtainedRelicId = B_GRADE_SHINING_RELICS.get(Rnd.get(B_GRADE_SHINING_RELICS.size()));
				}
				else
				{
					obtainedRelicId = B_GRADE_COMMON_RELICS.get(Rnd.get(B_GRADE_COMMON_RELICS.size()));
				}
			}
			// A-grade Relic Summon Coupon (relics of A-grade).
			else if (_relicCouponItemId == 82933)
			{
				obtainedRelicId = A_GRADE_COMMON_RELICS.get(Rnd.get(A_GRADE_COMMON_RELICS.size()));
			}
			// C-grade Relic Ticket (relics of D-grade/C-grade).
			else if (_relicCouponItemId == 83008)
			{
				final int relicChance = Rnd.get(100);
				final int shiningRelicChance = Rnd.get(100);
				if (relicChance < Config.RELIC_SUMMON_C_TICKET_CHANCE_C_GRADE)
				{
					obtainedRelicId = C_GRADE_COMMON_RELICS.get(Rnd.get(C_GRADE_COMMON_RELICS.size()));
				}
				else
				{
					if (shiningRelicChance <= Config.RELIC_SUMMON_CHANCE_SHINING_D_GRADE)
					{
						obtainedRelicId = D_GRADE_SHINING_RELICS.get(Rnd.get(D_GRADE_SHINING_RELICS.size()));
					}
					else
					{
						obtainedRelicId = D_GRADE_COMMON_RELICS.get(Rnd.get(D_GRADE_COMMON_RELICS.size()));
					}
				}
			}
			// B-grade Relic Ticket (relics of C-grade/B-grade).
			else if (_relicCouponItemId == 83009)
			{
				final int relicChance = Rnd.get(100);
				final int shiningRelicChance = Rnd.get(100);
				if (relicChance < Config.RELIC_SUMMON_B_TICKET_CHANCE_B_GRADE)
				{
					if (shiningRelicChance <= Config.RELIC_SUMMON_CHANCE_SHINING_B_GRADE)
					{
						obtainedRelicId = B_GRADE_SHINING_RELICS.get(Rnd.get(B_GRADE_SHINING_RELICS.size()));
					}
					else
					{
						obtainedRelicId = B_GRADE_COMMON_RELICS.get(Rnd.get(B_GRADE_COMMON_RELICS.size()));
					}
				}
				else
				{
					if (shiningRelicChance <= Config.RELIC_SUMMON_CHANCE_SHINING_C_GRADE)
					{
						obtainedRelicId = C_GRADE_SHINING_RELICS.get(Rnd.get(C_GRADE_SHINING_RELICS.size()));
					}
					else
					{
						obtainedRelicId = C_GRADE_COMMON_RELICS.get(Rnd.get(C_GRADE_COMMON_RELICS.size()));
					}
				}
			}
			// A-grade Relic Ticket (relics of B-grade/A-grade).
			else if (_relicCouponItemId == 83010)
			{
				final int relicChance = Rnd.get(100);
				final int shiningRelicChance = Rnd.get(100);
				if (relicChance < Config.RELIC_SUMMON_A_TICKET_CHANCE_A_GRADE)
				{
					obtainedRelicId = A_GRADE_COMMON_RELICS.get(Rnd.get(A_GRADE_COMMON_RELICS.size()));
				}
				else
				{
					if (shiningRelicChance < Config.RELIC_SUMMON_CHANCE_SHINING_B_GRADE)
					{
						obtainedRelicId = B_GRADE_SHINING_RELICS.get(Rnd.get(B_GRADE_SHINING_RELICS.size()));
					}
					else
					{
						obtainedRelicId = B_GRADE_COMMON_RELICS.get(Rnd.get(B_GRADE_COMMON_RELICS.size()));
					}
				}
			}
			
			buffer.writeInt(obtainedRelicId);
			
			// Add to database table the obtained relics.
			Collection<PlayerRelicData> storedRelics = _player.getRelics();
			
			// Check if the relic with the same ID exists.
			PlayerRelicData existingRelic = null;
			for (PlayerRelicData relic : storedRelics)
			{
				if (relic.getRelicId() == obtainedRelicId)
				{
					existingRelic = relic;
					break;
				}
			}
			
			PlayerRelicData newRelic = new PlayerRelicData(obtainedRelicId, 0, 0, 0, 0);
			final int obtainedRelicGrade = RelicData.getInstance().getRelic(obtainedRelicId).getGrade();
			if (existingRelic != null)
			{
				// A/B Grade relics need to be added to confirmation list first.
				if ((obtainedRelicGrade == 4) || (obtainedRelicGrade == 5))
				{
					// Check indexes of relics with same id to avoid duplicate 300+ index.
					final List<Integer> unconfirmedRelics = new ArrayList<>();
					final Collection<PlayerRelicData> storedRelics2 = _player.getRelics();
					for (PlayerRelicData relic2 : storedRelics2)
					{
						if ((relic2.getRelicIndex() >= 300) && (relic2.getRelicId() == existingRelic.getRelicId())) // Unconfirmed relics are set on summon to index 300.
						{
							unconfirmedRelics.add(relic2.getRelicIndex());
						}
					}
					if (Config.RELIC_SYSTEM_DEBUG_ENABLED)
					{
						_player.sendMessage("0.Duplicate relic indexes list: " + unconfirmedRelics);
					}
					newRelic.setRelicCount(1);
					newRelic.setRelicIndex(300 + unconfirmedRelics.size());
					newRelic.setRelicSummonTime(System.currentTimeMillis());
					storedRelics.add(newRelic);
					// Increase the unconfirmed relics variable count.
					_player.getAccountVariables().set(AccountVariables.UNCONFIRMED_RELICS_COUNT, _player.getAccountVariables().getInt(AccountVariables.UNCONFIRMED_RELICS_COUNT, 0) + 1);
					_player.getAccountVariables().storeMe();
					_player.storeRelics();
					_player.sendPacket(new ExRelicsExchangeList(_player)); // Update relic exchange/confirm list.
					if (Config.RELIC_SYSTEM_DEBUG_ENABLED)
					{
						_player.sendMessage("1.Duplicate relic id: " + newRelic.getRelicId() + " was added to confirmation list.");
					}
					if (Config.RELIC_SUMMON_ANNOUNCE)
					{
						// Announce new the obtained relic.
						Broadcast.toAllOnlinePlayers(new ExRelicsAnnounce(_player, newRelic.getRelicId()));
					}
				}
				// Update existing relics if not A/B Grade relics.
				else if (!((obtainedRelicGrade == 4) || (obtainedRelicGrade == 5)))
				{
					existingRelic.setRelicCount(existingRelic.getRelicCount() + 1);
					_player.storeRelics();
					_player.sendPacket(new ExRelicsUpdateList(1, existingRelic.getRelicId(), 0, 1)); // Update confirmed relic list with new relic.
					if (Config.RELIC_SYSTEM_DEBUG_ENABLED)
					{
						_player.sendMessage("2.Existing relic id: " + existingRelic.getRelicId() + " count was updated.");
					}
					// Announce the existing obtained relic.
					if (Config.RELIC_SUMMON_ANNOUNCE && !Config.RELIC_ANNOUNCE_ONLY_A_B_GRADE)
					{
						Broadcast.toAllOnlinePlayers(new ExRelicsAnnounce(_player, existingRelic.getRelicId()));
					}
					// Check if relic is already registered in some collection.
					if (!_player.isRelicRegistered(existingRelic.getRelicId(), existingRelic.getRelicLevel()))
					{
						// Auto-Add to relic collections on summon.
						_player.sendPacket(new ExRelicsCollectionUpdate(_player, existingRelic.getRelicId(), existingRelic.getRelicLevel())); // Update collection list.
					}
				}
			}
			else
			{
				// A/B Grade relics need to be confirmed before add them to relics list.
				if ((obtainedRelicGrade == 4) || (obtainedRelicGrade == 5))
				{
					// Set Relic Index to 300 to be able to get the list of confirmation relics later.
					newRelic.setRelicCount(1);
					newRelic.setRelicIndex(300);
					newRelic.setRelicSummonTime(System.currentTimeMillis());
					storedRelics.add(newRelic);
					_player.storeRelics();
					_player.getAccountVariables().set(AccountVariables.UNCONFIRMED_RELICS_COUNT, _player.getAccountVariables().getInt(AccountVariables.UNCONFIRMED_RELICS_COUNT, 0) + 1);
					_player.getAccountVariables().storeMe();
					_player.sendPacket(new ExRelicsExchangeList(_player)); // Update relic exchange/confirm list.
					if (Config.RELIC_SYSTEM_DEBUG_ENABLED)
					{
						_player.sendMessage("1.New relic id: " + newRelic.getRelicId() + " was added to confirmation list.");
					}
					if (Config.RELIC_SUMMON_ANNOUNCE)
					{
						// Announce the new obtained relic.
						Broadcast.toAllOnlinePlayers(new ExRelicsAnnounce(_player, newRelic.getRelicId()));
					}
				}
				else // Add new relics if not A/B Grade relics.
				{
					storedRelics.add(newRelic);
					_player.storeRelics();
					_player.sendPacket(new ExRelicsUpdateList(1, newRelic.getRelicId(), 0, 0)); // Update confirmed relic list with new relic.
					if (Config.RELIC_SYSTEM_DEBUG_ENABLED)
					{
						_player.sendMessage("2.New relic id: " + newRelic.getRelicId() + " was added to relic list.");
					}
					if (Config.RELIC_SUMMON_ANNOUNCE && !Config.RELIC_ANNOUNCE_ONLY_A_B_GRADE)
					{
						// Announce the new obtained relic
						Broadcast.toAllOnlinePlayers(new ExRelicsAnnounce(_player, newRelic.getRelicId()));
					}
					if (!_player.isRelicRegistered(newRelic.getRelicId(), newRelic.getRelicLevel()))
					{
						// Auto-Add to relic collections on summon.
						_player.sendPacket(new ExRelicsCollectionUpdate(_player, newRelic.getRelicId(), newRelic.getRelicLevel())); // Update collection list.
					}
				}
			}
			_player.storeRelics();
			_player.sendPacket(new ExRelicsList(_player)); // Update confirmed relic list relics count.
			_player.sendPacket(new ExRelicsExchangeList(_player)); // Update relic exchange/confirm list.
			
		}
	}
}
