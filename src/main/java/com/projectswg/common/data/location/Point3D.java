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
package com.projectswg.common.data.location;

import com.projectswg.common.encoding.Encodable;
import com.projectswg.common.network.NetBuffer;
import com.projectswg.common.network.NetBufferStream;
import com.projectswg.common.persistable.Persistable;

public class Point3D implements Encodable, Persistable {
	
	private double x;
	private double y;
	private double z;
	
	public Point3D() {
		this(0, 0, 0);
	}

	public Point3D(Point3D p) {
		this(p.getX(), p.getY(), p.getZ());
	}
	
	public Point3D(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getZ() {
		return z;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	public void setZ(double z) {
		this.z = z;
	}
	
	public void set(double x, double y, double z) {
		setX(x);
		setY(y);
		setZ(z);
	}
	
	public void translate(Point3D p) {
		translate(p.getX(), p.getY(), p.getZ());
	}
	
	public void translate(double x, double y, double z) {
		this.x += x;
		this.y += y;
		this.z += z;
	}
	
	public void rotateAround(double x, double y, double z, Quaternion rot) {
		rot.rotatePoint(this);
		translate(x, y, z);
	}
	
	public double distanceTo(Point3D p) {
		return distanceTo(p.getX(), p.getY(), p.getZ());
	}
	
	public double distanceTo(double x, double y, double z) {
		double dist = 0, tmp;
		tmp = this.x - x;
		dist += tmp * tmp;
		tmp = this.y - y;
		dist += tmp * tmp;
		tmp = this.z - z;
		dist += tmp * tmp;
		return Math.sqrt(dist);
	}
	
	public double flatDistanceTo(Point3D p) {
		return flatDistanceTo(p.getX(), p.getZ());
	}
	
	public double flatDistanceTo(double x, double z) {
		double dist = 0, tmp;
		tmp = this.x - x;
		dist += tmp * tmp;
		tmp = this.z - z;
		dist += tmp * tmp;
		return Math.sqrt(dist);
	}
	
	public boolean isWithinDistance(Point3D p, double x, double y, double z) {
		if (Math.abs(getX() - p.getX()) > x)
			return false;
		if (Math.abs(getY() - p.getY()) > y)
			return false;
		if (Math.abs(getZ() - p.getZ()) > z)
			return false;
		return true;
	}
	
	public boolean isWithinDistance(Point3D p, double radius) {
		return isWithinDistance(p.getX(), p.getY(), p.getZ(), radius);
	}
	
	public boolean isWithinDistance(double x, double y, double z, double radius) {
		double dist = 0, tmp;
		tmp = this.x - x;
		dist += tmp * tmp;
		tmp = this.y - y;
		dist += tmp * tmp;
		tmp = this.z - z;
		dist += tmp * tmp;
		return dist <= radius * radius;
	}
	
	public boolean isWithinFlatDistance(Point3D target, double radius) {
		double dist = 0, tmp;
		tmp = target.x - x;
		dist += tmp * tmp;
		tmp = target.z - z;
		dist += tmp * tmp;
		return dist <= radius * radius;
	}
	
	public double getSpeed(Point3D p, double deltaTime) {
		double dist = 0, tmp;
		tmp = p.x - x;
		dist += tmp * tmp;
		tmp = p.z - z;
		dist += tmp * tmp;
		dist = Math.sqrt(dist);
		return dist / deltaTime;
	}

	@Override
	public byte[] encode() {
		NetBuffer buf = NetBuffer.allocate(12);
		buf.addFloat((float) x);
		buf.addFloat((float) y);
		buf.addFloat((float) z);
		return buf.array();
	}

	@Override
	public void decode(NetBuffer data) {
		x = data.getFloat();
		y = data.getFloat();
		z = data.getFloat();
	}
	
	@Override
	public int getLength() {
		return 12;
	}
	
	@Override
	public void save(NetBufferStream stream) {
		stream.addFloat((float) x);
		stream.addFloat((float) y);
		stream.addFloat((float) z);
	}
	
	@Override
	public void read(NetBufferStream stream) {
		x = stream.getFloat();
		y = stream.getFloat();
		z = stream.getFloat();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Point3D))
			return false;
		if (Math.abs(((Point3D) o).getX()-getX()) > 1E-7)
			return false;
		if (Math.abs(((Point3D) o).getY()-getY()) > 1E-7)
			return false;
		if (Math.abs(((Point3D) o).getZ()-getZ()) > 1E-7)
			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		return Double.hashCode(getX()) ^ Double.hashCode(getY()) ^ Double.hashCode(getZ());
	}

	@Override
	public String toString() {
		return String.format("Point3D[%.2f, %.2f, %.2f]", x, y, z);
	}
	
}
