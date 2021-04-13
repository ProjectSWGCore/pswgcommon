/***********************************************************************************
 * Copyright (c) 2018 /// Project SWG /// www.projectswg.com                       *
 *                                                                                 *
 * ProjectSWG is the first NGE emulator for Star Wars Galaxies founded on          *
 * July 7th, 2011 after SOE announced the official shutdown of Star Wars Galaxies. *
 * Our goal is to create an emulator which will provide a server for players to    *
 * continue playing a game similar to the one they used to play. We are basing     *
 * it on the final publish of the game prior to end-game events.                   *
 *                                                                                 *
 * This file is part of Holocore.                                                  *
 *                                                                                 *
 * --------------------------------------------------------------------------------*
 *                                                                                 *
 * Holocore is free software: you can redistribute it and/or modify                *
 * it under the terms of the GNU Affero General Public License as                  *
 * published by the Free Software Foundation, either version 3 of the              *
 * License, or (at your option) any later version.                                 *
 *                                                                                 *
 * Holocore is distributed in the hope that it will be useful,                     *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                  *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                   *
 * GNU Affero General Public License for more details.                             *
 *                                                                                 *
 * You should have received a copy of the GNU Affero General Public License        *
 * along with Holocore.  If not, see <http://www.gnu.org/licenses/>.               *
 ***********************************************************************************/
package com.projectswg.common.network.packets.swg.zone;

import com.projectswg.common.data.encodables.gcw.GcwGroup;
import com.projectswg.common.network.NetBuffer;
import com.projectswg.common.network.packets.SWGPacket;

import java.util.Collection;

/**
 * Shows given locations as Galactic Civil War Contested Zones on the map of the planetary map
 */
public class GcwGroupsRsp extends SWGPacket {
	
	public static final int CRC = getCrc("GcwGroupsRsp");
	
	private final Collection<GcwGroup> groups;
	
	public GcwGroupsRsp(Collection<GcwGroup> groups) {
		this.groups = groups;
	}
	
	@Override
	public void decode(NetBuffer data) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public NetBuffer encode() {
		int regionsLength = groups.stream().mapToInt(GcwGroup::getLength).sum();
		NetBuffer buffer = NetBuffer.allocate(Short.BYTES + Integer.BYTES + 4 + regionsLength);	// 4 is list size variable
		
		buffer.addShort(2);
		buffer.addInt(CRC);
		buffer.addList(groups);
		
		return buffer;
	}
}
