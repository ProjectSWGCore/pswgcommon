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

import com.projectswg.common.network.NetBuffer;
import com.projectswg.common.network.packets.SWGPacket;


public class ClientIdMsg extends SWGPacket {
	
	public static final int CRC = getCrc("ClientIdMsg");

	private int gameBitsToClear;
	private byte [] sessionToken;
	private String version;
	
	public ClientIdMsg() {
		this(0, new byte[0], "");
	}
	
	public ClientIdMsg(NetBuffer data) {
		decode(data);
	}
	
	public ClientIdMsg(int gameBitsToClear, byte [] sessionKey, String version) {
		this.gameBitsToClear = gameBitsToClear;
		this.sessionToken = sessionKey;
		this.version = version;
	}
	
	@Override
	public void decode(NetBuffer data) {
		if (!super.checkDecode(data, CRC))
			return;
		gameBitsToClear = data.getInt();
		sessionToken = data.getArrayLarge();
		version = data.getAscii();
	}
	
	@Override
	public NetBuffer encode() {
		NetBuffer data = NetBuffer.allocate(16 + sessionToken.length + version.length());
		data.addShort(4);
		data.addInt(CRC);
		data.addInt(gameBitsToClear);
		data.addArrayLarge(sessionToken);
		data.addAscii(version);
		return data;
	}
	
	public int getGameBitsToClear() { return gameBitsToClear; }
	public byte [] getSessionToken() { return sessionToken; }
	public String getVersion() { return version; }
}
