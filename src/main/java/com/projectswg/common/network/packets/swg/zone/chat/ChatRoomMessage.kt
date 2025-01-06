/***********************************************************************************
 * Copyright (c) 2025 /// Project SWG /// www.projectswg.com                       *
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
package com.projectswg.common.network.packets.swg.zone.chat

import com.projectswg.common.data.encodables.chat.ChatAvatar
import com.projectswg.common.data.encodables.oob.OutOfBandPackage
import com.projectswg.common.network.NetBuffer
import com.projectswg.common.network.packets.SWGPacket

class ChatRoomMessage(
	var avatar: ChatAvatar = ChatAvatar(),
	var roomId: Int = 0,
	var message: String = "",
	var outOfBandPackage: OutOfBandPackage = OutOfBandPackage(),
) : SWGPacket() {

	override fun decode(data: NetBuffer) {
		if (!super.checkDecode(data, CRC)) return
		avatar = data.getEncodable(ChatAvatar::class.java)
		roomId = data.int
		message = data.unicode
		outOfBandPackage = data.getEncodable(OutOfBandPackage::class.java)
	}

	override fun encode(): NetBuffer {
		val oob = outOfBandPackage.encode()
		val length = 14 + avatar.length + oob.size + message.length * 2
		val data = NetBuffer.allocate(length)
		data.addShort(5)
		data.addInt(CRC)
		data.addEncodable(avatar)
		data.addInt(roomId)
		data.addUnicode(message)
		data.addRawArray(oob)
		return data
	}

	companion object {
		val CRC: Int = getCrc("ChatRoomMessage")
	}
}
