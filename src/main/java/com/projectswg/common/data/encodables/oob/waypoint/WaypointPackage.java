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
package com.projectswg.common.data.encodables.oob.waypoint;

import com.projectswg.common.data.encodables.oob.OutOfBandData;
import com.projectswg.common.data.encodables.oob.OutOfBandPackage;
import com.projectswg.common.data.encodables.oob.OutOfBandPackage.Type;
import com.projectswg.common.data.location.Point3D;
import com.projectswg.common.data.location.Terrain;
import com.projectswg.common.network.NetBuffer;
import com.projectswg.common.network.NetBufferStream;

public class WaypointPackage implements OutOfBandData  {
	
	private Point3D position;
	
	private long objectId;
	private Terrain terrain;
	private long cellId;
	private String name;
	private WaypointColor color;
	private boolean active;
	
	public WaypointPackage() {
		this.position = new Point3D();
		this.objectId = 0;
		this.terrain = Terrain.GONE;
		this.cellId = 0;
		this.name = "New Waypoint";
		this.color = WaypointColor.BLUE;
		this.active = true;
	}
	
	public WaypointPackage(NetBuffer data) {
		this.position = new Point3D();
		decode(data);
	}
	
	public Point3D getPosition() {
		return position;
	}
	
	public long getObjectId() {
		return objectId;
	}
	
	public Terrain getTerrain() {
		return terrain;
	}
	
	public long getCellId() {
		return cellId;
	}
	
	public String getName() {
		return name;
	}
	
	public WaypointColor getColor() {
		return color;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public void setObjectId(long objectId) {
		this.objectId = objectId;
	}
	
	public void setTerrain(Terrain terrain) {
		this.terrain = terrain;
	}

	public void setPosition(Point3D position) {
		this.position = position;
	}
	
	public void setCellId(long cellId) {
		this.cellId = cellId;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setColor(WaypointColor color) {
		this.color = color;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}
	
	@Override
	public byte[] encode() {
		NetBuffer data = NetBuffer.allocate(getLength());
		data.addInt(0);
		data.addEncodable(position);
		data.addLong(cellId);
		data.addInt(terrain.getCrc());
		data.addUnicode(name);
		data.addLong(objectId);
		data.addByte(color.getValue());
		data.addBoolean(active);
		return data.array();
	}
	
	@Override
	public void decode(NetBuffer data) {
		data.getInt();
		position.decode(data);
		cellId = data.getLong();
		terrain = Terrain.getTerrainFromCrc(data.getInt());
		name = data.getUnicode();
		objectId = data.getLong();
		color = WaypointColor.valueOf(data.getByte());
		active = data.getBoolean();
	}
	
	@Override
	public int getLength() {
		return 42 + name.length() * 2;
	}

	@Override
	public void save(NetBufferStream stream) {
		stream.addByte(0);
		stream.addLong(objectId);
		stream.addLong(cellId);
		position.save(stream);
		stream.addAscii(terrain.name());
		stream.addUnicode(name);
		stream.addInt(color.getValue());
		stream.addBoolean(active);
	}
	
	@Override
	public void read(NetBufferStream stream) {
		stream.getByte();
		objectId = stream.getLong();
		cellId = stream.getLong();
		position.read(stream);
		terrain = Terrain.valueOf(stream.getAscii());
		name = stream.getUnicode();
		color = WaypointColor.valueOf(stream.getInt());
		active = stream.getBoolean();
	}
	
	@Override
	public Type getOobType() {
		return OutOfBandPackage.Type.WAYPOINT;
	}
	
	@Override
	public int getOobPosition() {
		return -3;
	}
	
	@Override
	public int hashCode() {
		return Long.hashCode(objectId);
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof WaypointPackage))
			return false;
		WaypointPackage wp = (WaypointPackage) o;
		return wp.getObjectId() == getObjectId();
	}
	
}
