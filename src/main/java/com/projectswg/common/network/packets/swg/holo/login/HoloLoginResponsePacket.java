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

package com.projectswg.common.network.packets.swg.holo.login;

import com.projectswg.common.data.encodables.galaxy.Galaxy;
import com.projectswg.common.network.NetBuffer;
import com.projectswg.common.network.packets.swg.holo.HoloPacket;
import com.projectswg.common.network.packets.swg.login.EnumerateCharacterId.SWGCharacter;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class HoloLoginResponsePacket extends HoloPacket {
	
	public static final int CRC = getCrc("HoloLoginResponsePacket");
	
	private final List<Galaxy> galaxies;
	private final List<SWGCharacter> characters;
	
	private boolean success;
	private String error;
	
	public HoloLoginResponsePacket() {
		this.galaxies = new ArrayList<>();
		this.characters = new ArrayList<>();
		this.success = false;
		this.error = "";
	}
	
	public HoloLoginResponsePacket(NetBuffer data) {
		this();
		decode(data);
	}
	
	public HoloLoginResponsePacket(boolean success) {
		this();
		this.success = success;
	}
	
	public HoloLoginResponsePacket(boolean success, String error) {
		this();
		this.success = success;
		this.error = error;
	}
	
	public HoloLoginResponsePacket(boolean success, String error, Collection<Galaxy> galaxies, Collection<SWGCharacter> characters) {
		this(success, error);
		this.galaxies.addAll(galaxies);
		this.characters.addAll(characters);
	}
	
	@Override
	public final void decode(NetBuffer data) {
		if (!super.checkDecode(data, CRC))
			return;
		
		this.success = data.getBoolean();
		this.error = data.getAscii();
		
		{ // galaxies
			int count = data.getInt();
			for (int i = 0; i < count; i++) {
				Galaxy g = new Galaxy();
				g.setId(data.getInt());
				g.setAddress(data.getAscii());
				g.setZonePort(data.getShort());
				g.setPingPort(data.getShort());
				g.setPopulation(data.getInt());
				data.getInt(); // population status
				g.setMaxCharacters(data.getInt());
				g.setZoneOffset(ZoneOffset.ofTotalSeconds(data.getInt()));
				g.setStatus(data.getInt());
				g.setRecommended(data.getBoolean());
				g.setOnlinePlayerLimit(data.getInt());
				g.setOnlineFreeTrialLimit(data.getInt());
				galaxies.add(g);
			}
		}
		
		characters.clear();
		characters.addAll(data.getList(SWGCharacter.class));
	}
	
	@Override
	public final NetBuffer encode() {
		int galaxySize = galaxies.stream().mapToInt(g -> 39 + g.getAddress().length()).sum();
		int characterSize = characters.stream().mapToInt(SWGCharacter::getLength).sum();
		NetBuffer data = NetBuffer.allocate(17 + error.length() + galaxySize + characterSize);
		data.addShort(5);
		data.addInt(CRC);
		
		data.addBoolean(success);
		data.addAscii(error);
		
		data.addInt(galaxies.size());
		for (Galaxy g : galaxies) {
			data.addInt(g.getId());
			data.addAscii(g.getAddress());
			data.addShort(g.getZonePort());
			data.addShort(g.getPingPort());
			data.addInt(g.getPopulation());
			data.addInt(g.getPopulationStatus());
			data.addInt(g.getMaxCharacters());
			data.addInt(g.getDistance());
			data.addInt(g.getStatus().getStatus());
			data.addBoolean(g.isRecommended());
			data.addInt(g.getOnlinePlayerLimit());
			data.addInt(g.getOnlineFreeTrialLimit());
		}
		
		data.addList(characters);
		
		return data;
	}
	
	public List<Galaxy> getGalaxies() {
		return Collections.unmodifiableList(galaxies);
	}
	
	public List<SWGCharacter> getCharacters() {
		return Collections.unmodifiableList(characters);
	}
	
	public boolean isSuccess() {
		return success;
	}
	
	public String getError() {
		return error;
	}
	
	public void addGalaxy(Galaxy galaxy) {
		this.galaxies.add(galaxy);
	}
	
	public void addCharacter(SWGCharacter character) {
		this.characters.add(character);
	}
	
	public void setSuccess(boolean success) {
		this.success = success;
	}
	
	public void setError(String error) {
		this.error = error;
	}
}
