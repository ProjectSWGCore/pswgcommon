/***********************************************************************************
 * Copyright (c) 2015 /// Project SWG /// www.projectswg.com                        *
 *                                                                                  *
 * ProjectSWG is the first NGE emulator for Star Wars Galaxies founded on           *
 * July 7th, 2011 after SOE announced the official shutdown of Star Wars Galaxies.  *
 * Our goal is to create an emulator which will provide a server for players to     *
 * continue playing a game similar to the one they used to play. We are basing      *
 * it on the final publish of the game prior to end-game events.                    *
 *                                                                                  *
 * This file is part of Holocore.                                                   *
 *                                                                                  *
 * -------------------------------------------------------------------------------- *
 *                                                                                  *
 * Holocore is free software: you can redistribute it and/or modify                 *
 * it under the terms of the GNU Affero General Public License as                   *
 * published by the Free Software Foundation, either version 3 of the               *
 * License, or (at your option) any later version.                                  *
 *                                                                                  *
 * Holocore is distributed in the hope that it will be useful,                      *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                   *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                    *
 * GNU Affero General Public License for more details.                              *
 *                                                                                  *
 * You should have received a copy of the GNU Affero General Public License         *
 * along with Holocore.  If not, see <http://www.gnu.org/licenses/>.                *
 *                                                                                  *
 ***********************************************************************************/
package com.projectswg.common.data;

import com.projectswg.common.data.encodables.mongo.MongoData;
import com.projectswg.common.data.encodables.mongo.MongoPersistable;
import com.projectswg.common.encoding.Encodable;
import com.projectswg.common.network.NetBuffer;

import java.util.Arrays;
import java.util.List;

public class Badges implements Encodable, MongoPersistable {
	
	private int bitmaskCount = 6;	// TODO determine programatically. Ceiling(Highest badge index % 32)
	
	private int[] bitmasks;
	private short badgeCount;
	private byte explorationBadgeCount;
	
	public Badges() {
		bitmasks = new int[bitmaskCount];
	}
	
	@Override
	public void decode(NetBuffer data) {
	
	}
	
	@Override
	public byte[] encode() {
		NetBuffer buffer = NetBuffer.allocate(getLength());
		
		buffer.addInt(15);	// Bitmask count. MUST be 15, otherwise client will display "No Badges"
		
		for(int bitmask : bitmasks) {
			buffer.addInt(bitmask);
		}
		
		return buffer.array();
	}
	
	@Override
	public int getLength() {
		return Integer.BYTES * (15 + 1);
	}
	
	public void set(int badgeIndex, boolean exploration, boolean add) {
		int bitmaskIndex = badgeIndex >> 5;
		
		int bit = badgeIndex % 32;
		int newValue = 1 << bit;
		
		if(add) {
			bitmasks[bitmaskIndex] |= newValue;
			badgeCount++;
			
			if(exploration)
				explorationBadgeCount++;
		} else {
			bitmasks[bitmaskIndex] -= newValue;
			badgeCount--;
			
			if(exploration)
				explorationBadgeCount--;
		}
	}
	
	public boolean hasBadge(int badgeIndex) {
		int bitmaskIndex = badgeIndex >> bitmaskCount - 1;
		
		int bit = badgeIndex % 32;
		int newValue = 1 << bit;
		
		return (bitmasks[bitmaskIndex] & newValue) != 0;
	}
	
	public short getBadgeCount() {
		return badgeCount;
	}
	
	public byte getExplorationBadgeCount() {
		return explorationBadgeCount;
	}
	
	@Override
	public void readMongo(MongoData data) {
		boolean badgesHaveBeenSavedPreviously = data.containsKey("bitmaksCount");
		
		if (badgesHaveBeenSavedPreviously) {
			bitmaskCount = data.getInteger("bitmaksCount");
			List<Integer> bitmaskList = data.getArray("bitmasks", Integer.class);
			bitmasks = bitmaskList.stream()
					.mapToInt(Integer::intValue)
					.toArray();
			badgeCount = data.getInteger("badgeCount").shortValue();
			explorationBadgeCount = data.getInteger("explorationBadgeCount").byteValue();
		}
	}
	
	@Override
	public void saveMongo(MongoData data) {
		data.putInteger("bitmaksCount", bitmaskCount);
		data.putArray("bitmasks", bitmasks);
		data.putInteger("badgeCount", badgeCount);
		data.putInteger("explorationBadgeCount", explorationBadgeCount);
	}
}
