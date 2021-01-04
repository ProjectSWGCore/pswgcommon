/***********************************************************************************
 * Copyright (c) 2021 /// Project SWG /// www.projectswg.com                       *
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
package com.projectswg.common.network.packets.swg.zone.object_controller.conversation;

import com.projectswg.common.data.encodables.oob.OutOfBandPackage;
import com.projectswg.common.network.NetBuffer;
import com.projectswg.common.network.packets.swg.zone.object_controller.ObjectController;

import java.util.Collection;

public class NpcConversationOptions extends ObjectController {
	
	public static final int CRC = 0x00E0;
	
	private Collection<OutOfBandPackage> playerReplies;
	
	public NpcConversationOptions(long objectId, Collection<OutOfBandPackage> playerReplies) {
		super(objectId, CRC);
		this.playerReplies = playerReplies;
	}
	
	public NpcConversationOptions(NetBuffer data) {
		super(CRC);
		decode(data);
	}
	
	@Override
	public void decode(NetBuffer data) {
		decodeHeader(data);
		byte replyCount = data.getByte();
		playerReplies = data.getList(OutOfBandPackage.class, replyCount);
	}
	
	@Override
	public NetBuffer encode() {
		int length = 0;
		
		length += HEADER_LENGTH;
		length += Byte.BYTES;
		
		for (OutOfBandPackage playerReply : playerReplies) {
			length += playerReply.getLength();
		}
		
		NetBuffer data = NetBuffer.allocate(length);
		encodeHeader(data);
		
		data.addByteSizedList(playerReplies);
		
		return data;
	}
	
	public Collection<OutOfBandPackage> getPlayerReplies() {
		return playerReplies;
	}
	
}
