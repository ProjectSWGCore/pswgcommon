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
package com.projectswg.common.network.packets.swg.login.creation;

import java.util.Locale;

import com.projectswg.common.network.NetBuffer;
import com.projectswg.common.network.packets.SWGPacket;


public class ClientVerifyAndLockNameResponse extends SWGPacket {
	public static final int CRC = getCrc("ClientVerifyAndLockNameResponse");
	
	private String name = "";
	private ErrorMessage error = ErrorMessage.NAME_APPROVED;
	
	public ClientVerifyAndLockNameResponse() {
		
	}
	
	public ClientVerifyAndLockNameResponse(String name, ErrorMessage error) {
		this.name = name;
		this.error = error;
	}
	
	public void decode(NetBuffer data) {
		if (!super.checkDecode(data, CRC))
			return;
		name = data.getUnicode();
		data.getAscii(); // ui
		data.getInt();
		error = ErrorMessage.valueOf(data.getAscii().toUpperCase(Locale.US));
	}
	
	public NetBuffer encode() {
		NetBuffer data = NetBuffer.allocate(20 + error.name().length() + name.length() * 2);
		data.addShort(9);
		data.addInt(CRC);
		data.addUnicode(name);
		data.addAscii("ui");
		data.addInt(0);
		data.addAscii(error.name().toLowerCase(Locale.US));
		return data;
	}
	
	public enum ErrorMessage {
		NAME_APPROVED,
		NAME_APPROVED_MODIFIED,
		NAME_DECLINED_SYNTAX,
		NAME_DECLINED_EMPTY,
		NAME_DECLINED_RACIALLY_INAPPROPRIATE,
		NAME_DECLINED_FICTIONALLY_INAPPROPRIATE,
		NAME_DECLINED_PROFANE,
		NAME_DECLINED_IN_USE,
		NAME_DECLINED_RESERVED,
		NAME_DECLINED_NO_TEMPLATE,
		NAME_DECLINED_NOT_CREATURE_TEMPLATE,
		NAME_DECLINED_NO_NAME_GENERATOR,
		NAME_DECLINED_CANT_CREATE_AVATAR,
		NAME_DECLINED_INTERNAL_ERROR,
		NAME_DECLINED_RETRY,
		NAME_DECLINED_TOO_FAST,
		NAME_DECLINED_NOT_AUTHORIZED_FOR_SPECIES,
		NAME_DECLINED_FICTIONALLY_RESERVED,
		SERVER_CHARACTER_CREATION_MAX_CHARS;
	}
	
}
