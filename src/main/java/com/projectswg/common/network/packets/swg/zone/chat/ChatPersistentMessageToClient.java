/***********************************************************************************
 * Copyright (c) 2024 /// Project SWG /// www.projectswg.com                       *
 *                                                                                 *
 * ProjectSWG is an emulation project for Star Wars Galaxies founded on            *
 * July 7th, 2011 after SOE announced the official shutdown of Star Wars Galaxies. *
 * Our goal is to create one or more emulators which will provide servers for      *
 * players to continue playing a game similar to the one they used to play.        *
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

import com.projectswg.common.data.encodables.player.Mail;
import com.projectswg.common.network.NetBuffer;
import com.projectswg.common.network.packets.SWGPacket;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

public class ChatPersistentMessageToClient extends SWGPacket {
	
	public static final int CRC = getCrc("ChatPersistentMessageToClient");
	
	private Mail mail;
	private String galaxy;
	private boolean header;
	
	public ChatPersistentMessageToClient() {
		this(null, null, false);
	}
	
	public ChatPersistentMessageToClient(Mail mail, String galaxy, boolean header) {
		this.mail = mail;
		this.galaxy = galaxy;
		this.header = header;
	}
	
	@Override
	public void decode(@NotNull NetBuffer data) {
		if (!super.checkDecode(data, CRC))
			return;
		mail = new Mail("", "", "", 0);
		mail.setSender(data.getAscii());
		data.getAscii(); // SWG
		galaxy = data.getAscii();
		mail.setId(data.getInt());
		header = data.getBoolean();
		if (header)
			mail.decodeHeader(data);
		else
			mail.decode(data);
		mail.setStatus(data.getByte());
		mail.setTimestamp(Instant.ofEpochSecond(data.getInt()));
	}
	
	@Override
	public NetBuffer encode() {
		byte[] mailData = (header ? mail.encodeHeader() : mail.encode());
		
		NetBuffer data = NetBuffer.allocate(25 + galaxy.length() + mailData.length + mail.getSender().length());
		data.addShort(2);
		data.addInt(CRC);

		data.addAscii(mail.getSender());
		data.addAscii("SWG");
		data.addAscii(galaxy);
		data.addInt(mail.getId());
		data.addBoolean(header);
		data.addRawArray(mailData);
		data.addByte(mail.getStatus());
		data.addInt((int) mail.getTimestamp().getEpochSecond());
		return data;
	}

}
