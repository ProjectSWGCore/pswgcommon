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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import com.projectswg.common.data.encodables.chat.ChatRoom;
import com.projectswg.common.network.NetBuffer;
import com.projectswg.common.network.packets.SWGPacket;

public class ChatRoomList extends SWGPacket {
	
	public static final int CRC = getCrc("ChatRoomList");
	
	private Collection<ChatRoom> rooms;
	
	public ChatRoomList() {
		this.rooms = new ArrayList<>();
	}
	
	public ChatRoomList(ChatRoom... rooms) {
		this.rooms = Arrays.asList(rooms);
	}
	
	public ChatRoomList(Collection<ChatRoom> rooms) {
		this.rooms = rooms;
	}
	
	@Override
	public void decode(NetBuffer data) {
		if (!super.checkDecode(data, CRC))
			return;
		rooms = data.getList(ChatRoom.class);
	}
	
	@Override
	public NetBuffer encode() {
		int length = 10;
		for (ChatRoom r : rooms) {
			length += r.getLength();
		}
		NetBuffer data = NetBuffer.allocate(length);
		data.addShort(2);
		data.addInt(CRC);
		data.addList(rooms);
		return data;
	}
	
	public Collection<ChatRoom> getRooms() {
		return rooms;
	}
	
	@Override
	public String toString() {
		return "ChatRoomList[roomSize=" + rooms.size() + "]";
	}
	
}
