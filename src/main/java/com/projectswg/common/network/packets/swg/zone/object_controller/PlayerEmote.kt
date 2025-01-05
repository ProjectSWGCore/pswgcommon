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
package com.projectswg.common.network.packets.swg.zone.object_controller

import com.projectswg.common.network.NetBuffer

class PlayerEmote : ObjectController {
	
	private var sourceId: Long = 0
	private var targetId: Long = 0
	private var emoteId = 0
	private var emoteFlags: Byte = 0

	constructor(objectId: Long) : super(objectId, CRC)

	constructor(data: NetBuffer) : super(CRC) {
		decode(data)
	}

	constructor(objectId: Long, sourceId: Long, targetId: Long, emoteId: Int, emoteFlags: Byte) : super(objectId, CRC) {
		this.sourceId = sourceId
		this.targetId = targetId
		this.emoteId = emoteId
		this.emoteFlags = emoteFlags
	}

	constructor(objectId: Long, emote: PlayerEmote) : super(objectId, CRC) {
		this.sourceId = emote.sourceId
		this.targetId = emote.targetId
		this.emoteId = emote.emoteId
		this.emoteFlags = emote.emoteFlags
	}

	override fun decode(data: NetBuffer) {
		decodeHeader(data)
		sourceId = data.long
		targetId = data.long
		emoteId = data.int
		emoteFlags = data.byte
	}

	override fun encode(): NetBuffer {
		val data = NetBuffer.allocate(HEADER_LENGTH + 21)
		encodeHeader(data)
		data.addLong(sourceId)
		data.addLong(targetId)
		data.addInt(emoteId)
		data.addByte(emoteFlags.toInt())
		return data
	}

	companion object {
		const val CRC: Int = 0x012E
	}
}
