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
package com.projectswg.common.network.packets.swg.zone.object_controller;


import com.projectswg.common.data.location.Location;
import com.projectswg.common.network.NetBuffer;

public class DataTransformWithParent extends ObjectController {
	
	public static final int CRC = 0x00F1;
	
	private int counter;
	private long cellId;
	private Location l;
	private float speed;
	
	public DataTransformWithParent(long objectId) {
		super(objectId, CRC);
	}
	
	public DataTransformWithParent(long objectId, int timestamp, int counter, long cellId, Location l, float speed) {
		super(objectId, CRC);
		this.timestamp = timestamp;
		this.counter = counter;
		this.cellId = cellId;
		this.l = l;
		this.speed = speed;
	}
	
	public DataTransformWithParent(NetBuffer data) {
		super(CRC);
		decode(data);
	}
	
	@Override
	public void decode(NetBuffer data) {
		decodeHeader(data);
		counter = data.getInt();
		cellId = data.getLong();
		l = data.getEncodable(Location.class);
		speed = data.getFloat();
	}
	
	@Override
	public NetBuffer encode() {
		NetBuffer data = NetBuffer.allocate(HEADER_LENGTH + 44);
		encodeHeader(data);
		data.addInt(counter);
		data.addLong(cellId);
		data.addEncodable(l);
		data.addFloat(speed);
		return data;
	}
	
	public void setUpdateCounter(int counter) {
		this.counter = counter;
	}
	
	public void setCellId(long cellId) {
		this.cellId = cellId;
	}
	
	public void setLocation(Location l) {
		this.l = l;
	}
	
	public void setSpeed(float speed) {
		this.speed = speed;
	}
	
	public int getUpdateCounter() {
		return counter;
	}
	
	public long getCellId() {
		return cellId;
	}
	
	public Location getLocation() {
		return l;
	}
	
	public float getSpeed() {
		return speed;
	}
	
	public byte getMovementAngle() {
		byte movementAngle = (byte) 0.0f;
		double wOrient = l.getOrientationW();
		double yOrient = l.getOrientationY();
		double sq = Math.sqrt(1 - (wOrient * wOrient));
		
		if (sq != 0) {
			if (l.getOrientationW() > 0 && l.getOrientationY() < 0) {
				wOrient *= -1;
				yOrient *= -1;
			}
			movementAngle = (byte) ((yOrient / sq) * (2 * Math.acos(wOrient) / 0.06283f));
		}
		
		return movementAngle;
	}
	
	@Override
	protected String getPacketData() {
		return createPacketInformation(
				"objId", getObjectId(),
				"cellId", cellId,
				"timestamp", timestamp,
				"counter", counter,
				"location", l,
				"speed", speed
		);
	}
	
}
