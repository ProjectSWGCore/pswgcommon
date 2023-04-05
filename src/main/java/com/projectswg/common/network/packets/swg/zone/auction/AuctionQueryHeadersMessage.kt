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

class AuctionQueryHeadersMessage : SWGPacket {
	var region = 0
	var updateCounter = 0
	var windowType = 0
	var subcategory = 0.toByte()
	var cat1 = 0.toByte()
	var cat2 = 0.toByte()
	var cat3 = 0.toByte()
	var itemType = 0
	var searchString = ""
	var searchMinPrice = 0
	var searchMaxPrice = 0
	var includeEntranceFee = false
	var vendorId = 0L
	var vendor = false
	var firstAuctionToShow = 0.toShort()

	companion object {
		val crc = getCrc("AuctionQueryHeadersMessage")
	}
	
	constructor()
	constructor(data: NetBuffer) {
		decode(data)
	}

	override fun decode(data: NetBuffer) {
		if (!super.checkDecode(data, crc)) return
		region = data.int
		updateCounter = data.int
		windowType = data.int
		subcategory = data.byte
		cat1 = data.byte
		cat2 = data.byte
		cat3 = data.byte
		itemType = data.int
		searchString = data.unicode
		data.int
		searchMinPrice = data.int
		searchMaxPrice = data.int
		includeEntranceFee = data.boolean
		vendorId = data.long
		vendor = data.boolean
		firstAuctionToShow = data.short
	}

	override fun encode(): NetBuffer {
		val data = NetBuffer.allocate(54)
		data.addShort(2)
		data.addInt(crc)
		data.addInt(region)
		data.addInt(updateCounter)
		data.addInt(windowType)
		data.addByte(subcategory.toInt())
		data.addByte(cat1.toInt())
		data.addByte(cat2.toInt())
		data.addByte(cat3.toInt())
		data.addInt(itemType)
		data.addUnicode(searchString)
		data.addInt(0)
		data.addInt(searchMinPrice)
		data.addInt(searchMaxPrice)
		data.addBoolean(includeEntranceFee)
		data.addLong(vendorId)
		data.addBoolean(vendor)
		data.addShort(firstAuctionToShow.toInt())
		
		return data
	}

}
