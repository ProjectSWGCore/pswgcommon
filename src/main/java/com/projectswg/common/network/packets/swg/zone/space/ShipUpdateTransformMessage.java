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
package com.projectswg.common.network.packets.swg.zone.space;

import com.projectswg.common.data.location.Location;
import com.projectswg.common.data.location.Point3D;
import com.projectswg.common.network.NetBuffer;
import com.projectswg.common.network.packets.SWGPacket;
import org.jetbrains.annotations.NotNull;

public class ShipUpdateTransformMessage extends SWGPacket {
	public static final int CRC = getCrc("ShipUpdateTransformMessage");

	private short shipId;
	private Location l;
	private Point3D velocity;
	private float yawRate;
	private float pitchRate;
	private float rollRate;
	private int syncStamp;

	public ShipUpdateTransformMessage(short shipId, Location l, Point3D velocity, float yawRate, float pitchRate, float rollRate, int syncStamp) {
		this.shipId = shipId;
		this.l = l;
		this.velocity = velocity;
		this.yawRate = yawRate;
		this.pitchRate = pitchRate;
		this.rollRate = rollRate;
		this.syncStamp = syncStamp;
	}

	public ShipUpdateTransformMessage() {
		this((short) 0, Location.builder().build(), new Point3D(), 0.0f, 0.0f, 0.0f, 0);
	}
	
	public void decode(@NotNull NetBuffer data) {
		if (!super.checkDecode(data, CRC))
			return;
		this.shipId = data.getShort();
		Location.LocationBuilder location = Location.builder();
		location.setOrientationW(data.getByte());
		location.setOrientationX(data.getByte());
		location.setOrientationY(data.getByte());
		location.setOrientationZ(data.getByte());
		location.getOrientation().normalize();
		{
			final float MULTIPLIER = 8000 / 32767f;
			location.setX(data.getShort() * MULTIPLIER);
			location.setY(data.getShort() * MULTIPLIER);
			location.setZ(data.getShort() * MULTIPLIER);
		}
		this.l = location.build();
		decodePackedVelocity(data);
		{
			final float MULTIPLIER = 3.1415926535f / 254;
			yawRate = data.getByte() * MULTIPLIER;
			pitchRate = data.getByte() * MULTIPLIER;
			rollRate = data.getByte() * MULTIPLIER;
		}
		syncStamp = data.getInt();
	}
	
	public NetBuffer encode() {
		NetBuffer data = NetBuffer.allocate(64);
		data.addShort(7);
		data.addInt(CRC);
		data.addShort(shipId);
		data.addByte((byte) (l.getOrientationW() * 127));
		data.addByte((byte) (l.getOrientationX() * 127));
		data.addByte((byte) (l.getOrientationY() * 127));
		data.addByte((byte) (l.getOrientationZ() * 127));
		data.addShort((short) (l.getX() * 32767 / 8000));
		data.addShort((short) (l.getY() * 32767 / 8000));
		data.addShort((short) (l.getZ() * 32767 / 8000));
		encodePackedVelocity(data);
		float ROT_RATE_MULTIPLIER = 254 / 3.1415926535f;
		data.addByte(((int) (yawRate * ROT_RATE_MULTIPLIER)) & 0xFF);
		data.addByte(((int) (pitchRate * ROT_RATE_MULTIPLIER)) & 0xFF);
		data.addByte(((int) (rollRate * ROT_RATE_MULTIPLIER)) & 0xFF);
		data.addInt(syncStamp);
		return data;
	}
	
	public void setShipId(short shipId) { this.shipId = shipId; }
	public void setLocation(Location l) { this.l = l; }
	public void setVelocity(Point3D velocity) { this.velocity = velocity; }
	public void setYawRate(float yawRate) { this.yawRate = yawRate; }
	public void setPitchRate(float pitchRate) { this.pitchRate = pitchRate; }
	public void setRollRate(float rollRate) { this.rollRate = rollRate; }
	public void setSyncStamp(int syncStamp) { this.syncStamp = syncStamp; }
	
	public short getShipId() { return shipId; }
	public Location getLocation() { return l; }
	public Point3D getVelocity() { return velocity; }
	public float getYawRate() { return yawRate; }
	public float getPitchRate() { return pitchRate; }
	public float getRollRate() { return rollRate; }
	public int getSyncStamp() { return syncStamp; }
	
	@Override
	protected String getPacketData() {
		return createPacketInformation(
				"shipId", shipId,
				"location", l,
				"velocity", velocity,
				"yawRate", yawRate,
				"pitchRate", pitchRate,
				"rollRate", rollRate,
				"syncStamp", syncStamp
		);
	}
	
	// *sigh* why SOE -- was 4 fewer bytes really worth so many bitwise operations?
	private void decodePackedVelocity(@NotNull NetBuffer buffer) {
		final double speed = (buffer.getShort() & 0xFFFF) * 512 / 32767f;
		final int directionPacked = buffer.getShort() & 0xFFFF;
		double x = ((directionPacked >> 6) & 0x3F) * (((directionPacked & 0x8000) != 0) ? -1 : 1);
		double y = (directionPacked & 0x3F) * (((directionPacked & 0x4000) != 0) ? -1 : 1);
		double z = (0x3F - x - y) * (((directionPacked & 0x2000) != 0) ? -1 : 1);
		double mag = Math.sqrt(x*x + y*y + z*z);
		velocity = new Point3D(x * speed / mag, y * speed / mag, z * speed / mag);
	}
	
	private void encodePackedVelocity(@NotNull NetBuffer buffer) {
		final double speed = Math.sqrt(velocity.getX()*velocity.getX() + velocity.getY()*velocity.getY() + velocity.getZ()*velocity.getZ());
		buffer.addShort((short) (speed * 32767 / 512));
		int directionPacked = 0;
		if (velocity.getX() < 0)
			directionPacked |= 0x8000;
		if (velocity.getY() < 0)
			directionPacked |= 0x4000;
		if (velocity.getZ() < 0)
			directionPacked |= 0x2000;
		double magic = ((double) 0x3E) / (velocity.getX() + velocity.getY() + velocity.getZ());
		directionPacked |= (((int) (velocity.getX() * magic)) & 0x3F) << 6;
		directionPacked |= ((int) (velocity.getY() * magic)) & 0x3F;
		buffer.addShort(directionPacked);
	}

}
