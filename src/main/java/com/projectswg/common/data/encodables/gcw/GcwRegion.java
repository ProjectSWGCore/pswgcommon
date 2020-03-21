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
package com.projectswg.common.data.encodables.gcw;

import com.projectswg.common.encoding.Encodable;
import com.projectswg.common.network.NetBuffer;

import java.util.Collection;

public class GcwRegion implements Encodable {
	
	private final String planetName;
	private final Collection<GcwRegionZone> zones;
	
	public GcwRegion(String planetName, Collection<GcwRegionZone> zones) {
		this.planetName = planetName;
		this.zones = zones;
	}
	
	@Override
	public void decode(NetBuffer data) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public byte[] encode() {
		NetBuffer buffer = NetBuffer.allocate(getLength());
		
		buffer.addAscii(planetName);
		buffer.addList(zones);
		
		return buffer.array();
	}
	
	@Override
	public int getLength() {
		int nameLength = 2 + planetName.length();
		int zonesLength = 4 + zones.stream().mapToInt(GcwRegionZone::getLength).sum();
		
		return nameLength + zonesLength;
	}
}
