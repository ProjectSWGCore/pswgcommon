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

import com.projectswg.common.data.encodables.mongo.MongoData;
import com.projectswg.common.data.encodables.mongo.MongoPersistable;
import com.projectswg.common.encoding.Encodable;
import com.projectswg.common.network.NetBuffer;
import com.projectswg.common.network.NetBufferStream;
import com.projectswg.common.persistable.Persistable;

public class Location implements Encodable, Persistable, MongoPersistable {
	
	private static final Location ZERO = new Location(0, 0, 0, Terrain.GONE);
	
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
	
	private Location(Point3D point, Quaternion orientation, Terrain terrain) {
		this.point = point;
		this.orientation = orientation;
		this.terrain = terrain;
	}
	
	public Terrain getTerrain() {
		return terrain;
	}
	
	public double getX() {
		return point.getX();
	}
	
	public double getY() {
		return point.getY();
	}
	
	public double getZ() {
		return point.getZ();
	}
	
	public Point3D getPosition() {
		return new Point3D(point);
	}
	
	public double getOrientationX() {
		return orientation.getX();
	}
	
	public double getOrientationY() {
		return orientation.getY();
	}
	
	public double getOrientationZ() {
		return orientation.getZ();
	}
	
	public double getOrientationW() {
		return orientation.getW();
	}
	
	public Quaternion getOrientation() {
		return new Quaternion(orientation);
	}
	
	public boolean isWithinDistance(Location l, double x, double y, double z) {
		if (getTerrain() != l.getTerrain())
			return false;
		if (Math.abs(getX() - l.getX()) > x)
			return false;
		if (Math.abs(getY() - l.getY()) > y)
			return false;
		if (Math.abs(getZ() - l.getZ()) > z)
			return false;
		return true;
	}
	
	public boolean isWithinDistance(Location l, double radius) {
		return isWithinDistance(l.getTerrain(), l.getX(), l.getY(), l.getZ(), radius);
	}
	
	public boolean isWithinDistance(Terrain t, double x, double y, double z, double radius) {
		if (getTerrain() != t)
			return false;
		return square(getX() - x) + square(getY() - y) + square(getZ() - z) <= square(radius);
	}
	
	public boolean isWithinFlatDistance(Location l, double radius) {
		return isWithinFlatDistance(l.point, radius);
	}
	
	public boolean isWithinFlatDistance(Point3D target, double radius) {
		double mX = getX(), mZ = getZ();
		double tX = target.getX(), tZ = target.getZ();
		if (Math.abs(mX - tX) >= radius || Math.abs(mZ - tZ) >= radius)
			return false;
		return square(mX - tX) + square(mZ - tZ) <= square(radius);
	}
	
	public double getSpeed(Location l, double deltaTime) {
		double dist = Math.sqrt(square(getX() - l.getX()) + square(getY() - l.getY()) + square(getZ() - l.getZ()));
		return dist / deltaTime;
	}
	
	public double getYaw() {
		return orientation.getHeading();
	}
	
	public double getHeadingTo(Location target) {
		return ((360 - Math.toDegrees(Math.atan2(target.getX()-getX(), target.getZ()-getZ())) + 360) % 360);
	}
	
	public double getHeadingTo(Point3D target) {
		return (Math.toDegrees(Math.atan2(target.getX()-getX(), target.getZ()-getZ())) + 360) % 360;
	}
	
	private static double square(double x) {
		return x * x;
	}
	
	@Override
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
	
	@Override
	public int hashCode() {
		return hash(getX()) * 13 + hash(getY()) * 17 + hash(getZ()) * 19 + hash(getOrientationX()) * 23 + hash(getOrientationY()) * 29 + hash(getOrientationZ()) * 31 + hash(getOrientationW()) * 37;
	}
	
	private int hash(double x) {
		long v = Double.doubleToLongBits(x);
		return (int) (v ^ (v >>> 32));
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
		buf.addFloat((float) orientation.getX());
		buf.addFloat((float) orientation.getY());
		buf.addFloat((float) orientation.getZ());
		buf.addFloat((float) orientation.getW());
		buf.addFloat((float) point.getX());
		buf.addFloat((float) point.getY());
		buf.addFloat((float) point.getZ());
		return buf.array();
	}
	
	@Override
	public void decode(NetBuffer data) {
		orientation.decode(data);
		point.decode(data);
	}
	
	@Override
	public int getLength() {
		return 28;
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
	public void readMongo(MongoData data) {
		data.getDocument("orientation", orientation);
		data.getDocument("point", point);
		terrain = data.containsKey("terrain") ? Terrain.valueOf(data.getString("terrain")) : null;
	}
	
	@Override
	public void saveMongo(MongoData data) {
		data.putDocument("orientation", orientation);
		data.putDocument("point", point);
		if (terrain != null)
			data.putString("terrain", terrain.name());
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
	
	public double flatDistanceTo(Location destination) {
		return flatDistanceTo(destination.getX(), destination.getZ());
	}
	
	public double flatDistanceTo(double dstX, double dstZ) {
		return Math.sqrt(square(dstX - getX()) + square(dstZ - getZ()));
	}
	
	public static LocationBuilder builder() {
		return new LocationBuilder();
	}
	
	public static LocationBuilder builder(Location location) {
		return new LocationBuilder(location);
	}
	
	public static Location zero() {
		return ZERO;
	}
	
	public static class LocationBuilder {
		
		private final Point3D point;
		private final Quaternion orientation;
		private Terrain terrain;
		
		public LocationBuilder() {
			this.point = new Point3D(Double.NaN, Double.NaN, Double.NaN);
			this.orientation = new Quaternion(0, 0, 0, 1);
			this.terrain = null;
		}
		
		public LocationBuilder(Location location) {
			this.point = location.getPosition();
			this.orientation = location.getOrientation();
			this.terrain = location.getTerrain();
		}
		
		public Terrain getTerrain() {
			return terrain;
		}
		
		public double getX() {
			return point.getX();
		}
		
		public double getY() {
			return point.getY();
		}
		
		public double getZ() {
			return point.getZ();
		}
		
		public double getOrientationX() {
			return orientation.getX();
		}
		
		public double getOrientationY() {
			return orientation.getY();
		}
		
		public double getOrientationZ() {
			return orientation.getZ();
		}
		
		public double getOrientationW() {
			return orientation.getW();
		}
		
		public double getYaw() {
			return orientation.getHeading();
		}
		
		public boolean isWithinDistance(Location l, double x, double y, double z) {
			if (getTerrain() != l.getTerrain())
				return false;
			if (Math.abs(getX() - l.getX()) > x)
				return false;
			if (Math.abs(getY() - l.getY()) > y)
				return false;
			if (Math.abs(getZ() - l.getZ()) > z)
				return false;
			return true;
		}
		
		public boolean isWithinDistance(Location l, double radius) {
			return isWithinDistance(l.getTerrain(), l.getX(), l.getY(), l.getZ(), radius);
		}
		
		public boolean isWithinDistance(Terrain t, double x, double y, double z, double radius) {
			if (getTerrain() != t)
				return false;
			return square(getX() - x) + square(getY() - y) + square(getZ() - z) <= square(radius);
		}
		
		public boolean isWithinFlatDistance(Location l, double radius) {
			return isWithinFlatDistance(l.point, radius);
		}
		
		public boolean isWithinFlatDistance(Point3D target, double radius) {
			return square(getX() - target.getX()) + square(getZ() - target.getZ()) <= square(radius);
		}
		
		public double getSpeed(Location l, double deltaTime) {
			double dist = Math.sqrt(square(getX() - l.getX()) + square(getY() - l.getY()) + square(getZ() - l.getZ()));
			return dist / deltaTime;
		}
		
		public LocationBuilder setTerrain(Terrain terrain) {
			this.terrain = terrain;
			return this;
		}
		
		public LocationBuilder setX(double x) {
			point.setX(x);
			return this;
		}
		
		public LocationBuilder setY(double y) {
			point.setY(y);
			return this;
		}
		
		public LocationBuilder setZ(double z) {
			point.setZ(z);
			return this;
		}
		
		public LocationBuilder setOrientationX(double oX) {
			orientation.setX(oX);
			return this;
		}
		
		public LocationBuilder setOrientationY(double oY) {
			orientation.setY(oY);
			return this;
		}
		
		public LocationBuilder setOrientationZ(double oZ) {
			orientation.setZ(oZ);
			return this;
		}
		
		public LocationBuilder setOrientationW(double oW) {
			orientation.setW(oW);
			return this;
		}
		
		public LocationBuilder setPosition(double x, double y, double z) {
			setX(x);
			setY(y);
			setZ(z);
			return this;
		}
		
		public LocationBuilder setOrientation(double oX, double oY, double oZ, double oW) {
			setOrientationX(oX);
			setOrientationY(oY);
			setOrientationZ(oZ);
			setOrientationW(oW);
			return this;
		}
		
		public LocationBuilder translatePosition(double x, double y, double z) {
			setX(point.getX() + x);
			setY(point.getY() + y);
			setZ(point.getZ() + z);
			return this;
		}
		
		public LocationBuilder translateLocation(Location l) {
			point.rotateAround(l.getX(), l.getY(), l.getZ(), l.orientation);
			orientation.rotateByQuaternion(l.orientation);
			return this;
		}
		
		/**
		 * Sets the orientation to be facing the specified heading
		 * 
		 * @param heading the heading to face, in degrees
		 */
		public LocationBuilder setHeading(double heading) {
			orientation.setHeading(heading);
			return this;
		}
		
		/**
		 * Rotates the orientation by the specified angle along the Y-axis
		 * 
		 * @param angle the angle to rotate by in degrees
		 */
		public LocationBuilder rotateHeading(double angle) {
			orientation.rotateHeading(angle);
			return this;
		}
		
		/**
		 * Rotates the orientation by the specified angle along the specified axises
		 * 
		 * @param angle the angle to rotate by in degrees
		 * @param axisX the amount of rotation about the x-axis
		 * @param axisY the amount of rotation about the x-axis
		 * @param axisZ the amount of rotation about the x-axis
		 */
		public LocationBuilder rotate(double angle, double axisX, double axisY, double axisZ) {
			orientation.rotateDegrees(angle, axisX, axisY, axisZ);
			return this;
		}
		
		public Location build() {
			return new Location(new Point3D(point), new Quaternion(orientation), terrain);
		}
		
	}
	
}
