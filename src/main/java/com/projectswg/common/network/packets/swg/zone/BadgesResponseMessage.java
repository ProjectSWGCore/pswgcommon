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
package com.projectswg.common.network.packets.swg.zone;

import com.projectswg.common.data.Badges;
import com.projectswg.common.network.NetBuffer;
import com.projectswg.common.network.packets.SWGPacket;

public class BadgesResponseMessage extends SWGPacket {
	public static final int CRC = getCrc("BadgesResponseMessage");
	
	private long creatureObjectId;	// Ziggy: You'd think this would be the object ID for the PlayerObject...
	private Badges badges;
	
	public BadgesResponseMessage() {
	}
	
	public BadgesResponseMessage(long creatureObjectId, Badges badges) {
		this.creatureObjectId = creatureObjectId;
		this.badges = badges;
	}
	
	@Override
	public void decode(NetBuffer data) {
		if (!super.checkDecode(data, CRC))
			return;
		creatureObjectId = data.getLong();
		badges = data.getEncodable(Badges.class);
	}
	
	@Override
	public NetBuffer encode() {
		byte[] encodedBadges = badges.encode();
		NetBuffer data = NetBuffer.allocate(Short.BYTES + Integer.BYTES + Long.BYTES + encodedBadges.length);
		
		data.addShort(3);
		data.addInt(CRC);
		data.addLong(creatureObjectId);
		data.addRawArray(encodedBadges);
		
		return data;
	}
	
}
