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

import com.projectswg.common.data.location.Location;
import com.projectswg.common.network.NetBuffer;
import com.projectswg.common.network.packets.SWGPacket;

public class SceneCreateObjectByCrc extends SWGPacket {
	
	public static final int CRC = getCrc("SceneCreateObjectByCrc");
	
	private Location location;
	private boolean hyperspace;
	private long objId;
	private int objCrc;
	
	public SceneCreateObjectByCrc() {
		
	}
	
	public SceneCreateObjectByCrc(long objId, Location l, int objCrc, boolean hyperspace) {
		setObjectId(objId);
		setLocation(l);
		setObjectCrc(objCrc);
		setHyperspace(hyperspace);
	}
	
	@Override
	public void decode(NetBuffer data) {
		if (!super.checkDecode(data, CRC))
			return;
		objId = data.getLong();
		location = data.getEncodable(Location.class);
		objCrc = data.getInt();
		hyperspace = data.getBoolean();
		verifyInternals();
	}
	
	@Override
	public NetBuffer encode() {
		verifyInternals();
		NetBuffer data = NetBuffer.allocate(47);
		data.addShort(5);
		data.addInt(CRC);
		data.addLong(objId);
		data.addEncodable(location);
		data.addInt(objCrc);
		data.addBoolean(hyperspace);
		return data;
	}
	
	public void setObjectId(long objId) {
		if (objId == 0)
			throw new IllegalArgumentException("Object ID cannot be 0!");
		this.objId = objId;
	}
	
	public void setLocation(Location l) {
		this.location = new Location(l);
		verifyInternals();
	}
	
	public void setObjectCrc(int objCrc) {
		this.objCrc = objCrc;
	}
	
	public void setHyperspace(boolean hyperspace) {
		this.hyperspace = hyperspace;
	}
	
	public long getObjectId() {
		return objId;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public int getObjectCrc() {
		return objCrc;
	}
	
	public boolean isHyperspace() {
		return hyperspace;
	}
	
	@Override
	protected String getPacketData() {
		return createPacketInformation(
				"objId", objId,
				"location", location,
				"template", com.projectswg.common.data.CRC.getString(objCrc),
				"hyperspace", hyperspace
		);
	}
	
	private void verifyInternals() {
		packetAssert(objId != 0, "Object ID cannot be 0!");
		packetAssert(location != null, "location cannot be null");
		packetAssert(!Double.isNaN(location.getX()), "X Coordinate is NaN!");
		packetAssert(!Double.isNaN(location.getY()), "Y Coordinate is NaN!");
		packetAssert(!Double.isNaN(location.getZ()), "Z Coordinate is NaN!");
		packetAssert(!Double.isNaN(location.getOrientationX()), "X Orientation is NaN!");
		packetAssert(!Double.isNaN(location.getOrientationY()), "Y Orientation is NaN!");
		packetAssert(!Double.isNaN(location.getOrientationZ()), "Z Orientation is NaN!");
		packetAssert(!Double.isNaN(location.getOrientationW()), "W Orientation is NaN!");
	}
	
}
