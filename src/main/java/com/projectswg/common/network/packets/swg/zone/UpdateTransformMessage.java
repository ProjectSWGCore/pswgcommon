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

import com.projectswg.common.network.NetBuffer;
import com.projectswg.common.network.packets.SWGPacket;

public class UpdateTransformMessage extends SWGPacket {
	public static final int CRC = getCrc("UpdateTransformMessage");

	private long objId;
	private short posX;
	private short posY;
	private short posZ;
	private int updateCounter;
	private byte direction;
	private float speed;
	private byte lookAtYaw;
	private boolean useLookAtYaw;

	public UpdateTransformMessage() {
		this.objId = 0;
		this.posX = 0;
		this.posY = 0;
		this.posZ = 0;
		this.updateCounter = 0;
		this.direction = 0;
		this.speed = 0;
	}
	
	public UpdateTransformMessage(NetBuffer data) {
		decode(data);
	}
	
	public void decode(NetBuffer data) {
		if (!super.checkDecode(data, CRC))
			return;
		objId = data.getLong();
		posX = data.getShort();
		posY = data.getShort();
		posZ = data.getShort();
		updateCounter = data.getInt();
		speed = data.getByte();
		direction = data.getByte();
		lookAtYaw = data.getByte();
		useLookAtYaw = data.getBoolean();
	}
	
	public NetBuffer encode() {
		NetBuffer data = NetBuffer.allocate(28);
		data.addShort(10);
		data.addInt(CRC);
		data.addLong(objId);
		data.addShort(posX);
		data.addShort(posY);
		data.addShort(posZ);
		data.addInt(updateCounter);
		data.addByte((byte) speed);
		data.addByte(direction);
		data.addByte(lookAtYaw); // lookAtYaw
		data.addBoolean(useLookAtYaw); // useLookAtYaw
		return data;
	}
	
	public void setObjectId(long objId) { this.objId = objId; }
	public void setX(short x) { this.posX = x; }
	public void setY(short y) { this.posY = y; }
	public void setZ(short z) { this.posZ = z; }
	public void setUpdateCounter(int count) { this.updateCounter = count; }
	public void setDirection(byte d) { this.direction = d; }
	public void setSpeed(float speed) { this.speed = speed; }
	
	public long getObjectId() { return objId; }
	public short getX() { return posX; }
	public short getY() { return posY; }
	public short getZ() { return posZ; }
	public int getUpdateCounter() { return updateCounter; }
	public byte getDirection() { return direction; }
	public float getSpeed() { return speed; }

	public boolean isUseLookAtYaw() {
		return useLookAtYaw;
	}

	public void setUseLookAtYaw(boolean useLookAtYaw) {
		this.useLookAtYaw = useLookAtYaw;
	}

	public byte getLookAtYaw() {
		return lookAtYaw;
	}

	public void setLookAtYaw(byte lookAtYaw) {
		this.lookAtYaw = lookAtYaw;
	}
	
	@Override
	public String toString() {
		return String.format("UpdateTransformMessage[objId=%d posX=%d posY=%d posZ=%d direction=%d]", objId, posX, posY, posZ, direction);
	}
	
}
