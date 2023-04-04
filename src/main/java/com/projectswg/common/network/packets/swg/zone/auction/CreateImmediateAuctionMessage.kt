/***********************************************************************************
 * Copyright (c) 2023 /// Project SWG /// www.projectswg.com                       *
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
package com.projectswg.common.network.packets.swg.zone.auction

import com.projectswg.common.network.NetBuffer
import com.projectswg.common.network.packets.SWGPacket

data class CreateImmediateAuctionMessage(
	var objectId: Long = 0L,
	var vendorId: Long = 0L,
	var price: Int = 0,
	var duration: Int = 0,
	var description: String = "",
	var premium: Boolean = false,
) : SWGPacket() {

	companion object {
		val crc = getCrc("CreateImmediateAuctionMessage")
	}

	override fun decode(data: NetBuffer) {
		if (!super.checkDecode(data, crc)) return
		objectId = data.long
		vendorId = data.long
		price = data.int
		duration = data.int
		description = data.unicode
		premium = data.boolean
	}

	override fun encode(): NetBuffer {
		val data = NetBuffer.allocate(35 + (description.length * 2))

		data.addShort(6)
		data.addInt(crc)
		data.addLong(objectId)
		data.addLong(vendorId)
		data.addInt(price)
		data.addInt(duration)
		data.addUnicode(description)
		data.addBoolean(premium)

		return data
	}
}