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

/**
 * @author Waverunner
 */
public class ChatCreateRoom extends SWGPacket {
	public static final int CRC = getCrc("ChatCreateRoom");

	private boolean isPublic;
	private boolean isModerated;
	private String owner;
	private String roomName;
	private String roomTitle;
	private int sequence;

	public ChatCreateRoom() {}

	@Override
	public void decode(NetBuffer data) {
		if (!super.checkDecode(data, CRC))
			return;
		isPublic 	= data.getBoolean();
		isModerated	= data.getBoolean();
		owner		= data.getAscii();
		roomName	= data.getAscii();
		roomTitle	= data.getAscii();
		sequence	= data.getInt();
	}

	@Override
	public NetBuffer encode() {
		NetBuffer data = NetBuffer.allocate(18 + owner.length() + roomName.length() + roomTitle.length());
		data.addShort(7);
		data.addInt(CRC);
		data.addBoolean(isPublic);
		data.addBoolean(isModerated);
		data.addAscii(owner);
		data.addAscii(roomName);
		data.addAscii(roomTitle);
		data.addInt(sequence);
		return data;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public boolean isModerated() {
		return isModerated;
	}

	public String getOwner() {
		return owner;
	}

	public String getRoomName() {
		return roomName;
	}

	public String getRoomTitle() {
		return roomTitle;
	}

	public int getSequence() {
		return sequence;
	}

	@Override
	public String toString() {
		return "ChatCreateRoom[isPublic=" + isPublic + ", isModerated=" + isModerated +
				", owner='" + owner + "'," + "roomName='" + roomName + "'," + "roomTitle='" + roomTitle + '\'' +
				", sequence=" + sequence + "]";
	}
}
