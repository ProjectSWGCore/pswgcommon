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
package com.projectswg.common.network.packets.swg.zone.chat;

import com.projectswg.common.network.NetBuffer;
import com.projectswg.common.network.packets.SWGPacket;

public class ChatInstantMessageToClient extends SWGPacket {
	public static final int CRC = getCrc("ChatInstantMessageToClient");
	
	private String galaxy;
	private String character;
	private String message;
	private String outOfBand;
	
	public ChatInstantMessageToClient() {
		this("", "", "", "");
	}
	
	public ChatInstantMessageToClient(String galaxy, String character, String message) {
		this(galaxy, character, message, "");
	}
	
	public ChatInstantMessageToClient(String galaxy, String character, String message, String outOfBand) {
		this.galaxy = galaxy;
		this.character = character;
		this.message = message;
		this.outOfBand = outOfBand;
	}
	
	public ChatInstantMessageToClient(NetBuffer data) {
		decode(data);
	}
	
	public void decode(NetBuffer data) {
		if (!super.checkDecode(data, CRC))
			return;
		data.getAscii(); // "SWG"
		galaxy = data.getAscii();
		character = data.getAscii();
		message = data.getUnicode();
		outOfBand = data.getUnicode();
	}
	
	public NetBuffer encode() {
		NetBuffer data = NetBuffer.allocate(23 + galaxy.length() + character.length() + message.length()*2 + outOfBand.length()*2);
		data.addShort(2);
		data.addInt(CRC);
		data.addAscii("SWG");
		data.addAscii(galaxy);
		data.addAscii(character);
		data.addUnicode(message);
		data.addUnicode(outOfBand);
		return data;
	}
	
	public String getGalaxy() { return galaxy; }
	public String getCharacter() { return character; }
	public String getMessage() { return message; }
	public String getOutOfBand() { return outOfBand; }
	
	public void setGalaxy(String galaxy) { this.galaxy = galaxy; }
	public void setCharacter(String character) { this.character = character; }
	public void setMessage(String message) { this.message = message; }
	public void setOutOfBand(String outOfBand) { this.outOfBand = outOfBand; }
	
}
