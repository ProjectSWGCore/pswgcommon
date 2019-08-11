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


public class LoginClientId extends SWGPacket {
	public static final int CRC = getCrc("LoginClientId");
	
	private String username;
	private String password;
	private String version;
	
	public LoginClientId() {
		this("", "", "");
	}
	
	public LoginClientId(NetBuffer data) {
		decode(data);
	}
	
	public LoginClientId(String username, String password, String version) {
		this.username = username;
		this.password = password;
		this.version  = version;
	}
	
	public void decode(NetBuffer data) {
		if (!super.checkDecode(data, CRC))
			return;
		username  = data.getAscii();
		password  = data.getAscii();
		version   = data.getAscii();
	}
	
	public NetBuffer encode() {
		int length = 6 + 6 + username.length() * 2 + password.length() * 2 + version.length() * 2;
		NetBuffer data = NetBuffer.allocate(length);
		data.addShort(4);
		data.addInt(CRC);
		data.addAscii(username);
		data.addAscii(password);
		data.addAscii(version);
		return data;
	}
	
	public String getUsername()  { return username; }
	public void setUsername(String str) { this.username = str; }
	public String getPassword()  { return password; }
	public void setPassword(String str) { this.password = str; }
	public String getVersion()  { return version; }
}
