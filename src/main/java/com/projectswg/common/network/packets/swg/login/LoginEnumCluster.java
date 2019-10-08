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
package com.projectswg.common.network.packets.swg.login;

import java.time.ZoneOffset;
import java.util.List;
import java.util.Vector;

import com.projectswg.common.data.encodables.galaxy.Galaxy;
import com.projectswg.common.network.NetBuffer;
import com.projectswg.common.network.packets.SWGPacket;


public class LoginEnumCluster extends SWGPacket {
	
	public static final int CRC = 0xC11C63B9;
	
	private Vector <Galaxy> galaxies;
	private int maxCharacters;
	
	public LoginEnumCluster() {
		galaxies = new Vector<Galaxy>();
	}
	
	public LoginEnumCluster(List<Galaxy> galaxies, int maxCharacters) {
		this.galaxies = new Vector<Galaxy>(galaxies);
		this.maxCharacters = maxCharacters;
	}
	
	public LoginEnumCluster(int maxCharacters) {
		galaxies = new Vector<Galaxy>();
		this.maxCharacters = maxCharacters;
	}
	
	public void decode(NetBuffer data) {
		if (!super.checkDecode(data, CRC))
			return;
		int serverCount = data.getInt();
		for (int i = 0; i < serverCount; i++) {
			Galaxy g = new Galaxy();
			g.setId(data.getInt());
			g.setName(data.getAscii());
			g.setZoneOffset(ZoneOffset.ofTotalSeconds(data.getInt()));
			galaxies.add(g);
		}
		maxCharacters = data.getInt();
	}
	
	public NetBuffer encode() {
		int length = 14;
		for (Galaxy g : galaxies)
			length += 10 + g.getName().length();
		NetBuffer data = NetBuffer.allocate(length);
		data.addShort(3);
		data.addInt(CRC);
		data.addInt(galaxies.size());
		for (Galaxy g : galaxies) {
			data.addInt(g.getId());
			data.addAscii(g.getName());
			data.addInt(g.getDistance());
		}
		data.addInt(maxCharacters);
		return data;
	}
	
	public void addGalaxy(Galaxy g) {
		galaxies.add(g);
	}
	
	public void setMaxCharacters(int max) {
		this.maxCharacters = max;
	}
	
	public int getMaxCharacters() {
		return maxCharacters;
	}
	
	public List <Galaxy> getGalaxies() {
		return galaxies;
	}
}
