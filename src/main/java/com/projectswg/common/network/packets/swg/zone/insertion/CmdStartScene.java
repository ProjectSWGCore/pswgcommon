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
package com.projectswg.common.network.packets.swg.zone.insertion;

import com.projectswg.common.data.encodables.tangible.Race;
import com.projectswg.common.data.location.Location;
import com.projectswg.common.data.location.Terrain;
import com.projectswg.common.network.NetBuffer;
import com.projectswg.common.network.packets.SWGPacket;

public class CmdStartScene extends SWGPacket {
	public static final int CRC = getCrc("CmdStartScene");
	
	private boolean ignoreLayoutFiles;
	private long charId;
	private Race race;
	private Location l;
	private long galacticTime;
	private int serverEpoch;
	
	public CmdStartScene() {
		ignoreLayoutFiles = false;
		charId = 0;
		race = Race.HUMAN_MALE;
		l = new Location();
		galacticTime = 0;
		serverEpoch = 0;
	}
	
	public CmdStartScene(boolean ignoreLayoutFiles, long charId, Race race, Location l, long galacticTime, int serverEpoch) {
		this.ignoreLayoutFiles = ignoreLayoutFiles;
		this.charId = charId;
		this.race = race;
		this.l = l;
		this.galacticTime = galacticTime;
		this.serverEpoch = serverEpoch;
	}
	
	@Override
	public void decode(NetBuffer data) {
		if (!super.checkDecode(data, CRC))
			return;
		ignoreLayoutFiles = data.getBoolean();
		charId = data.getLong();
		String ter = data.getAscii();
		Terrain terrain = null;
		for (Terrain t : Terrain.values()) {
			if (t.getFile().equals(ter)) {
				terrain = t;
				break;
			}
		}
		l = Location.builder()
				.setTerrain(terrain)
				.setX(data.getFloat())
				.setY(data.getFloat())
				.setZ(data.getFloat())
				.setHeading(data.getFloat())
				.build();
		race = Race.getRaceByFile(data.getAscii());
		galacticTime = data.getLong();
		serverEpoch = data.getInt();
	}
	
	@Override
	public NetBuffer encode() {
		int length = 47 + l.getTerrain().getFile().length() + race.getFilename().length();
		NetBuffer data = NetBuffer.allocate(length);
		data.addShort(2);
		data.addInt(CRC);
		data.addBoolean(ignoreLayoutFiles);
		data.addLong(charId);
		data.addAscii(l.getTerrain().getFile());
		data.addFloat((float) l.getX());
		data.addFloat((float) l.getY());
		data.addFloat((float) l.getZ());
		data.addFloat((float) l.getYaw());
		data.addAscii(race.getFilename());
		data.addLong(galacticTime);
		data.addInt(serverEpoch);
		return data;
	}
	
	public long getCharacterId() { return charId; }
	public Location getLocation() { return l; }
	public Race getRace() { return race; }
	public long getGalacticTime() { return galacticTime; }
	public int getServerEpoch() { return serverEpoch; }
	
	public void setCharacterId(long id) { this.charId = id; }
	public void setLocation(Location l) { this.l = l; }
	public void setRace(Race r) { this.race = r; }
	public void setGalacticTime(long time) { this.galacticTime = time; }
	public void setServerEpoch(int epoch) { this.serverEpoch = epoch; }
	
}
