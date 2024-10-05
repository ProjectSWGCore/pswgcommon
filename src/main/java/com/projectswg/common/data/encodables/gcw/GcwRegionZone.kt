/***********************************************************************************
 * Copyright (c) 2018 /// Project SWG /// www.projectswg.com                       *
 * *
 * ProjectSWG is the first NGE emulator for Star Wars Galaxies founded on          *
 * July 7th, 2011 after SOE announced the official shutdown of Star Wars Galaxies. *
 * Our goal is to create an emulator which will provide a server for players to    *
 * continue playing a game similar to the one they used to play. We are basing     *
 * it on the final publish of the game prior to end-game events.                   *
 * *
 * This file is part of Holocore.                                                  *
 * *
 * --------------------------------------------------------------------------------*
 * *
 * Holocore is free software: you can redistribute it and/or modify                *
 * it under the terms of the GNU Affero General Public License as                  *
 * published by the Free Software Foundation, either version 3 of the              *
 * License, or (at your option) any later version.                                 *
 * *
 * Holocore is distributed in the hope that it will be useful,                     *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                  *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                   *
 * GNU Affero General Public License for more details.                             *
 * *
 * You should have received a copy of the GNU Affero General Public License        *
 * along with Holocore.  If not, see <http:></http:>//www.gnu.org/licenses/>.               *
 */
package com.projectswg.common.data.encodables.gcw

import com.projectswg.common.encoding.Encodable
import com.projectswg.common.network.NetBuffer

class GcwRegionZone(private val name: String, private val x: Float, private val z: Float, private val radius: Float) : Encodable {
	override fun decode(data: NetBuffer) {
	}

	override fun encode(): ByteArray {
		val buffer = NetBuffer.allocate(length)

		buffer.addAscii(name)
		buffer.addFloat(x)
		buffer.addFloat(z)
		buffer.addFloat(radius)

		return buffer.array()
	}

	override val length: Int
		get() = 2 + name.length + java.lang.Float.BYTES * 3
}
