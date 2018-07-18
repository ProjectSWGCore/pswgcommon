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

import java.util.ArrayList;
import java.util.List;

import com.projectswg.common.data.location.Point3D;
import com.projectswg.common.encoding.StringType;
import com.projectswg.common.network.NetBuffer;
import com.projectswg.common.network.packets.SWGPacket;

public class PlanetTravelPointListResponse extends SWGPacket {
	
	public static final int CRC = getCrc("PlanetTravelPointListResponse");
	
	private final List<PlanetTravelPoint> travelPoints;
	private String planetName;
	
	public PlanetTravelPointListResponse() {
		this("", new ArrayList<>());
	}
	
	public PlanetTravelPointListResponse(String planetName, List<PlanetTravelPoint> travelPoints) {
		this.planetName = planetName;
		this.travelPoints = travelPoints;
	}
	
	@Override
	public NetBuffer encode() {
		NetBuffer data = NetBuffer.allocate(getLength());
		
		data.addShort(6); // Operand count
		data.addInt(CRC); // CRC
		data.addAscii(planetName);	// ASCII planet name
		
		data.addInt(travelPoints.size()); // List size
		for (PlanetTravelPoint tp : travelPoints) // Point names
			data.addAscii(tp.getName());
		
		data.addInt(travelPoints.size()); // List size
		for (PlanetTravelPoint tp : travelPoints) { // Point coordinates
			data.addFloat((float) tp.getLocation().getX());
			data.addFloat((float) tp.getLocation().getY());
			data.addFloat((float) tp.getLocation().getZ());
		}
		
		data.addInt(travelPoints.size()); // List size
		for (PlanetTravelPoint tp : travelPoints) { // additional costs
			data.addInt(tp.getAdditionalCost());
		}
		
		data.addInt(travelPoints.size()); // List size
		for (PlanetTravelPoint tp : travelPoints) { // reachable
			data.addBoolean(tp.isReachable());
		}
		
		return data;
	}
	
	@Override
	public void decode(NetBuffer data) {
		if (!super.checkDecode(data, CRC))
			return;
		planetName = data.getAscii();
		List<String> pointNames = data.getList(StringType.ASCII);
		List<Point3D> points = data.getList(Point3D.class);
		int[] additionalCosts = data.getIntArray();
		boolean [] reachable = data.getBooleanArray(); // reachable
		
		for (int i = 0; i < pointNames.size(); i++) {
			travelPoints.add(new PlanetTravelPoint(pointNames.get(i), points.get(i), additionalCosts[i]*2, reachable[i]));
		}
	}
	
	public int getLength() {
		int size = 24 + planetName.length();
		
		for (PlanetTravelPoint tp : travelPoints) {
			size += 19 + tp.getName().length();
		}
		
		return size;
	}
	
	public static class PlanetTravelPoint {
		
		private final String name;
		private final Point3D location;
		private final int additionalCost;
		private final boolean reachable;
		
		public PlanetTravelPoint(String name, Point3D location, int additionalCost, boolean reachable) {
			this.name = name;
			this.location = new Point3D();
			this.additionalCost = additionalCost;
			this.reachable = reachable;
			
			this.location.set(location.getX(), location.getY(), location.getZ());
		}
		
		public String getName() {
			return name;
		}
		
		public Point3D getLocation() {
			return location;
		}
		
		public int getAdditionalCost() {
			return additionalCost;
		}
		
		public boolean isReachable() {
			return reachable;
		}
		
	}
	
}
