/***********************************************************************************
* Copyright (c) 2015 /// Project SWG /// www.projectswg.com                        *
*                                                                                  *
* ProjectSWG is the first NGE emulator for Star Wars Galaxies founded on           *
* July 7th, 2011 after SOE announced the official shutdown of Star Wars Galaxies.  *
* Our goal is to create an emulator which will provide a server for players to     *
* continue playing a game similar to the one they used to play. We are basing      *
* it on the final publish of the game prior to end-game events.                    *
*                                                                                  *
* This file is part of Holocore.                                                   *
*                                                                                  *
* -------------------------------------------------------------------------------- *
*                                                                                  *
* Holocore is free software: you can redistribute it and/or modify                 *
* it under the terms of the GNU Affero General Public License as                   *
* published by the Free Software Foundation, either version 3 of the               *
* License, or (at your option) any later version.                                  *
*                                                                                  *
* Holocore is distributed in the hope that it will be useful,                      *
* but WITHOUT ANY WARRANTY; without even the implied warranty of                   *
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                    *
* GNU Affero General Public License for more details.                              *
*                                                                                  *
* You should have received a copy of the GNU Affero General Public License         *
* along with Holocore.  If not, see <http://www.gnu.org/licenses/>.                *
*                                                                                  *
***********************************************************************************/
package com.projectswg.common.data.location;

import java.nio.ByteBuffer;

import com.projectswg.common.encoding.Encodable;
import com.projectswg.common.network.NetBuffer;
import com.projectswg.common.network.NetBufferStream;
import com.projectswg.common.persistable.Persistable;

public class Location implements Encodable, Persistable {
	
	private final Point3D point;
	private final Quaternion orientation;
	private Terrain terrain;
	
	public Location() {
		this(Double.NaN, Double.NaN, Double.NaN, null);
	}
	
	public Location(Location l) { 
		this(l.getX(), l.getY(), l.getZ(), l.terrain);
		orientation.set(l.orientation);
	}
	
	public Location(double x, double y, double z, Terrain terrain) {
		this.orientation = new Quaternion(0, 0, 0, 1);
		this.point = new Point3D(x, y, z);
		this.terrain = terrain;
	}
	
	public void setTerrain(Terrain terrain) { this.terrain = terrain; }
	public void setX(double x) { point.setX(x); }
	public void setY(double y) { point.setY(y); }
	public void setZ(double z) { point.setZ(z); }
	public void setOrientationX(double oX) { orientation.setX(oX); }
	public void setOrientationY(double oY) { orientation.setY(oY); }
	public void setOrientationZ(double oZ) { orientation.setZ(oZ); }
	public void setOrientationW(double oW) { orientation.setW(oW); }
	public void setPosition(double x, double y, double z) {
		setX(x);
		setY(y);
		setZ(z);
	}
	public void setOrientation(double oX, double oY, double oZ, double oW) {
		setOrientationX(oX);
		setOrientationY(oY);
		setOrientationZ(oZ);
		setOrientationW(oW);
	}
	
	public Terrain getTerrain() { return terrain; }
	public double getX() { return point.getX(); }
	public double getY() { return point.getY(); }
	public double getZ() { return point.getZ(); }
	public Point3D getPosition() { return new Point3D(point); }
	public double getOrientationX() { return orientation.getX(); }
	public double getOrientationY() { return orientation.getY(); }
	public double getOrientationZ() { return orientation.getZ(); }
	public double getOrientationW() { return orientation.getW(); }
	public Quaternion getOrientation() { return new Quaternion(orientation); }
	
	public boolean isWithinDistance(Location l, double x, double y, double z) {
		if (getTerrain() != l.getTerrain())
			return false;
		double xD = Math.abs(getX() - l.getX());
		double yD = Math.abs(getY() - l.getY());
		double zD = Math.abs(getZ() - l.getZ());
		return xD <= x && yD <= y && zD <= z;
	}
	
	public boolean isWithinDistance(Location l, double radius) {
		return isWithinDistance(l.getTerrain(), l.getX(), l.getY(), l.getZ(), radius);
	}
	
	public boolean isWithinDistance(Terrain t, double x, double y, double z, double radius) {
		if (getTerrain() != t)
			return false;
		return square(getX()-x) + square(getY()-y) + square(getZ()-z) <= square(radius);
	}
	
	public boolean isWithinFlatDistance(Location l, double radius) {
		return isWithinFlatDistance(l.point, radius);
	}
	
	public boolean isWithinFlatDistance(Point3D target, double radius){
		return square(getX() - target.getX()) + square(getZ() - target.getZ()) <= square(radius);
	}
	
	public void translatePosition(double x, double y, double z) {
		setX(getX() + x);
		setY(getY() + y);
		setZ(getZ() + z);
	}
	
	public void translateLocation(Location l) {
        point.rotateAround(l.getX(), l.getY(), l.getZ(), l.orientation);
		orientation.rotateByQuaternion(l.orientation);
	}
	
	public Location translate(Location l) {
		Location ret = new Location(this);
		ret.translateLocation(l);
		return ret;
	}
	
	/**
	 * Sets the orientation to be facing the specified heading
	 * @param heading the heading to face, in degrees
	 */
	public void setHeading(double heading) {
		orientation.setHeading(heading);
	}
	
	/**
	 * Rotates the orientation by the specified angle along the Y-axis
	 * @param angle the angle to rotate by in degrees
	 */
	public void rotateHeading(double angle) {
		orientation.rotateHeading(angle);
	}
	
	/**
	 * Rotates the orientation by the specified angle along the specified axises
	 * @param angle the angle to rotate by in degrees
	 * @param axisX the amount of rotation about the x-axis
	 * @param axisY the amount of rotation about the x-axis
	 * @param axisZ the amount of rotation about the x-axis
	 */
	public void rotate(double angle, double axisX, double axisY, double axisZ) {
		orientation.rotateDegrees(angle, axisX, axisY, axisZ);
	}
	
	public void mergeWith(Location l) {
		if (terrain == null || terrain != l.getTerrain())
			terrain = l.getTerrain();
		mergeLocation(l.getX(), l.getY(), l.getZ());
		mergeOrientation(l);
	}
	
	public void mergeLocation(double lX, double lY, double lZ) {
		if (!isEqual(getX(), lX))
			point.setX(lX);
		if (!isEqual(getY(), lY))
			point.setY(lY);
		if (!isEqual(getZ(), lZ))
			point.setZ(lZ);
	}
	
	private void mergeOrientation(Location l) {
		if (!isEqual(getOrientationX(), l.getOrientationX()))
			orientation.setX(l.getOrientationX());
		if (!isEqual(getOrientationY(), l.getOrientationY()))
			orientation.setY(l.getOrientationY());
		if (!isEqual(getOrientationZ(), l.getOrientationZ()))
			orientation.setZ(l.getOrientationZ());
		if (!isEqual(getOrientationW(),  l.getOrientationW()))
			orientation.setW(l.getOrientationW());
	}
	
	public double getSpeed(Location l, double deltaTime) {
		double dist = Math.sqrt(square(getX()-l.getX()) + square(getY()-l.getY()) + square(getZ()-l.getZ()));
		return dist / deltaTime;
	}
	
	public double getYaw() {
		return orientation.getYaw();
	}
	
	private double square(double x) {
		return x*x;
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof Location))
			return false;
		return equals((Location) o);
	}
	
	public boolean equals(Location l) {
		if (terrain != l.terrain)
			return false;
		if (!isEqual(l.getX(), getX()))
			return false;
		if (!isEqual(l.getY(), getY()))
			return false;
		if (!isEqual(l.getZ(), getZ()))
			return false;
		if (!isEqual(l.getOrientationX(), getOrientationX()))
			return false;
		if (!isEqual(l.getOrientationY(), getOrientationY()))
			return false;
		if (!isEqual(l.getOrientationZ(), getOrientationZ()))
			return false;
		if (!isEqual(l.getOrientationW(), getOrientationW()))
			return false;
		return true;
	}
	
	public int hashCode() {
		return hash(getX())*13 + hash(getY())*17 + hash(getZ())*19 + hash(getOrientationX())*23 + hash(getOrientationY())*29 + hash(getOrientationZ())*31 + hash(getOrientationW())*37;
	}
	
	private int hash(double x) {
		long v = Double.doubleToLongBits(x);
		return (int)(v^(v>>>32));
	}
	
	private boolean isEqual(double x, double y) {
		if (Double.isNaN(x))
			return Double.isNaN(y);
		if (Double.isNaN(y))
			return false;
		return Math.abs(x - y) <= 1E-7;
	}

	@Override
	public byte[] encode() {
		NetBuffer buf = NetBuffer.allocate(28);
		buf.addFloat(safeEncodeDouble(orientation.getX()));
		buf.addFloat(safeEncodeDouble(orientation.getY()));
		buf.addFloat(safeEncodeDouble(orientation.getZ()));
		buf.addFloat(safeEncodeDouble(orientation.getW()));
		buf.addFloat(safeEncodeDouble(point.getX()));
		buf.addFloat(safeEncodeDouble(point.getY()));
		buf.addFloat(safeEncodeDouble(point.getZ()));
		return buf.array();
	}

	@Override
	public void decode(ByteBuffer data) {
		orientation.decode(data);
		point.decode(data);
	}
	
	@Override
	public void save(NetBufferStream stream) {
		stream.addByte(0);
		orientation.save(stream);
		point.save(stream);
		stream.addBoolean(terrain != null);
		if (terrain != null)
			stream.addAscii(terrain.name());
	}
	
	@Override
	public void read(NetBufferStream stream) {
		stream.getByte();
		orientation.read(stream);
		point.read(stream);
		if (stream.getBoolean())
			terrain = Terrain.valueOf(stream.getAscii());
	}

	@Override
	public String toString() {
		return String.format("Location[TRN=%s, %s %s]", terrain, point, orientation);
	}
	
	/**
	 * @param destination to get the distance for
	 * @return the distance between {@code this} and destination, which is ALWAYS positive.
	 */
	public double distanceTo(Location destination) {
		return distanceTo(destination.getX(), destination.getY(), destination.getZ());
	}
	
	public double distanceTo(double dstX, double dstY, double dstZ) {
		return Math.sqrt(square(dstX - getX()) + square(dstY - getY()) + square(dstZ - getZ()));
	}
	
	private float safeEncodeDouble(double d) {
		return (float) (Double.isNaN(d) ? 0 : d);
	}
	
}
