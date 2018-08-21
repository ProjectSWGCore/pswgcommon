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

/**
 * @author Waverunner
 */
public class UpdateTransformWithParentMessage extends SWGPacket {
	public static final int CRC = getCrc("UpdateTransformWithParentMessage");

	private long objId;
	private long cellId;
	private int updateCounter;
	private short posX;
	private short posY;
	private short posZ;
	private byte speed;
	private byte direction;
	private boolean useLookDirection;
	private byte lookDirection;
	
	public UpdateTransformWithParentMessage() {
		this.objId = 0;
		this.cellId = 0;
		this.updateCounter = 0;
		this.posX = 0;
		this.posY = 0;
		this.posZ = 0;
		this.speed = 0;
		this.direction = 0;
		this.useLookDirection = false;
		this.lookDirection = 0;
	}
	
	public UpdateTransformWithParentMessage(long objId, long cellId, int updateCounter, Location location, byte speed) {
		this.objId = objId;
		this.cellId = cellId;
		this.updateCounter = updateCounter;
		this.speed = speed;
		this.useLookDirection = false;
		this.lookDirection = 0;
		setLocation(location);
	}
	
	@Override
	public void decode(NetBuffer data) {
		if (!super.checkDecode(data, CRC))
			return;
		cellId			= data.getLong();
		objId = data.getLong();
		posX = data.getShort();
		posY = data.getShort();
		posZ = data.getShort();
		updateCounter	= data.getInt();
		speed			= data.getByte();
		direction		= data.getByte();
		lookDirection	= data.getByte();
		useLookDirection= data.getBoolean();
	}

	@Override
	public NetBuffer encode() {
		NetBuffer data = NetBuffer.allocate(36);
		data.addShort(11);
		data.addInt(CRC);
		data.addLong(cellId);
		data.addLong(objId);
		data.addShort(posX);
		data.addShort(posY);
		data.addShort(posZ);
		data.addInt(updateCounter);
		data.addByte(speed);
		data.addByte(direction);
		data.addByte(lookDirection);
		data.addBoolean(useLookDirection);
		return data;
	}
	
	public void setCellId(long cellId) {
		this.cellId = cellId;
	}
	
	public void setObjectId(long objectId) {
		this.objId = objectId;
	}
	
	public void setUpdateCounter(int updateCounter) {
		this.updateCounter = updateCounter;
	}
	
	public void setSpeed(byte speed) {
		this.speed = speed;
	}
	
	public void setDirection(byte direction) {
		this.direction = direction;
	}
	
	public void setLookDirection(byte lookDirection) {
		this.lookDirection = lookDirection;
	}
	
	public void setUseLookDirection(boolean useLookDirection) {
		this.useLookDirection = useLookDirection;
	}
	
	public long getCellId() {
		return cellId;
	}
	
	public long getObjectId() {
		return objId;
	}
	
	public short getX() {
		return posX;
	}
	
	public short getY() {
		return posY;
	}
	
	public short getZ() {
		return posZ;
	}
	
	public int getUpdateCounter() {
		return updateCounter;
	}
	
	public byte getSpeed() {
		return speed;
	}
	
	public byte getDirection() {
		return direction;
	}
	
	public byte getLookDirection() {
		return lookDirection;
	}
	
	public boolean isUseLookDirection() {
		return useLookDirection;
	}
	
	public final void setLocation(Location location) {
		this.posX = (short) (location.getX() * 8 + 0.5);
		this.posY = (short) (location.getY() * 8 + 0.5);
		this.posZ = (short) (location.getZ() * 8 + 0.5);
		this.direction = getMovementAngle(location);
	}
	
	@Override
	protected String getPacketData() {
		return createPacketInformation(
				"objId", objId,
				"cellId", cellId,
				"posX", posX / 4,
				"posY", posY / 4,
				"posZ", posZ / 4,
				"dir", direction
		);
	}
	
	private byte getMovementAngle(Location requestedLocation) {
		byte movementAngle = (byte) 0.0f;
		double wOrient = requestedLocation.getOrientationW();
		double yOrient = requestedLocation.getOrientationY();
		double sq = Math.sqrt(1 - (wOrient*wOrient));
		
		if (sq != 0) {
			if (requestedLocation.getOrientationW() > 0 && requestedLocation.getOrientationY() < 0) {
				wOrient *= -1;
				yOrient *= -1;
			}
			movementAngle = (byte) ((yOrient / sq) * (2 * Math.acos(wOrient) / 0.06283f));
		}
		
		return movementAngle;
	}
	
}
