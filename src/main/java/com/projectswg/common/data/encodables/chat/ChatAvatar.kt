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
package com.projectswg.common.data.encodables.chat

import com.projectswg.common.encoding.Encodable
import com.projectswg.common.network.NetBuffer
import java.util.concurrent.atomic.AtomicReference

class ChatAvatar(name: String) : Encodable {
	var name: String = name
		get() {
			assert(field.isNotEmpty())
			return field
		}

	val galaxy: String
		get() = GALAXY.get()
	
	constructor() : this("unknown")

	override val length: Int
		get() = 9 + name.length + GALAXY.get().length

	override fun encode(): ByteArray {
		val buffer = NetBuffer.allocate(length)
		buffer.addAscii("SWG")
		buffer.addAscii(GALAXY.get())
		buffer.addAscii(name)
		return buffer.array()
	}

	override fun decode(data: NetBuffer) {
		data.ascii // SWG
		data.ascii
		name = data.ascii.lowercase()
	}

	override fun toString(): String {
		return String.format("ChatAvatar[name='%s']", name)
	}

	override fun equals(other: Any?): Boolean {
		if (other !is ChatAvatar) return false
		return other.name == this.name
	}

	override fun hashCode(): Int {
		return name.hashCode()
	}

	companion object {
		private val GALAXY = AtomicReference("")
		val systemAvatar: ChatAvatar = ChatAvatar("system")

		fun setGalaxy(galaxy: String) {
			GALAXY.set(galaxy)
		}
	}
}
