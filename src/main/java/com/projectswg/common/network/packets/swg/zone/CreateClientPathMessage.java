/***********************************************************************************
 * Copyright (c) 2018 /// Project SWG /// www.projectswg.com                       *
 *                                                                                 *
 * ProjectSWG is the first NGE emulator for Star Wars Galaxies founded on          *
 * July 7th, 2011 after SOE announced the official shutdown of Star Wars Galaxies. *
 * Our goal is to create an emulator which will provide a server for players to    *
 * continue playing a game similar to the one they used to play. We are basing     *
 * it on the final publish of the game prior to end-game events.                   *
 *                                                                                 *
 * This file is part of PSWGCommon.                                                *
 *                                                                                 *
 * --------------------------------------------------------------------------------*
 *                                                                                 *
 * PSWGCommon is free software: you can redistribute it and/or modify              *
 * it under the terms of the GNU Affero General Public License as                  *
 * published by the Free Software Foundation, either version 3 of the              *
 * License, or (at your option) any later version.                                 *
 *                                                                                 *
 * PSWGCommon is distributed in the hope that it will be useful,                   *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                  *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                   *
 * GNU Affero General Public License for more details.                             *
 *                                                                                 *
 * You should have received a copy of the GNU Affero General Public License        *
 * along with PSWGCommon.  If not, see <http://www.gnu.org/licenses/>.             *
 ***********************************************************************************/
package com.projectswg.common.network.packets.swg.zone;

import com.projectswg.common.data.location.Point3D;
import com.projectswg.common.network.NetBuffer;
import com.projectswg.common.network.packets.SWGPacket;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Makes the client construct a client path based on the given points
 */
public class CreateClientPathMessage extends SWGPacket {
	
	public static final int CRC = getCrc("CreateClientPathMessage");
	
	private Collection<Point3D> points;
	
	public CreateClientPathMessage(Collection<Point3D> points) {
		this.points = points;
	}
	
	public CreateClientPathMessage(NetBuffer data) {
		points = new LinkedList<>();
		decode(data);
	}
	
	@Override
	public void decode(NetBuffer data) {
		if (!super.checkDecode(data, CRC))
			return;
		
		points = data.getList(Point3D.class);
	}
	
	@Override
	public NetBuffer encode() {
		int pointCount = points.size();
		int pointSize = pointCount * 3 * Integer.BYTES;	// Three int coordinates, 3 * 4 = 12
		NetBuffer data = NetBuffer.allocate(10 + pointSize);
		data.addShort(5);
		data.addInt(CRC);
		data.addList(points);
		
		return data;
	}
	
	@Override
	protected String getPacketData() {
		return createPacketInformation(
				"points", points
		);
	}
}
