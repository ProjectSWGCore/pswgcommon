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

import com.projectswg.common.network.NetBuffer
import com.projectswg.common.network.packets.SWGPacket

class ChatOnSendRoomMessage @JvmOverloads constructor(var result: Int = 0, var sequence: Int = 0) : SWGPacket() {

	override fun decode(data: NetBuffer) {
		if (!super.checkDecode(data, CRC)) return
		result = data.int
		sequence = data.int
	}

	override fun encode(): NetBuffer {
		val data = NetBuffer.allocate(14)
		data.addShort(3)
		data.addInt(CRC)
		data.addInt(result)
		data.addInt(sequence)
		return data
	}

	companion object {
		val CRC: Int = getCrc("ChatOnSendRoomMessage")
	}
}
