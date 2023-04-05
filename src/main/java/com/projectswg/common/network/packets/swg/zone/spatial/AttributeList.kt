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
package com.projectswg.common.network.packets.swg.zone.spatial

import com.projectswg.common.encoding.Encodable
import com.projectswg.common.network.NetBuffer
import java.text.NumberFormat
import java.util.*
import kotlin.collections.LinkedHashMap

class AttributeList : Encodable {

	private val map: MutableMap<String, String> = LinkedHashMap()
	
	fun asMap() = map.toMap()
	
	fun putPercent(attribute: String, value: Double) {
		val nf = NumberFormat.getInstance(Locale.US)
		nf.maximumFractionDigits = 2
		val formattedValue = nf.format(value)
		
		map[attribute] = formattedValue
	}
	
	fun putText(attribute: String, value: String) {
		map[attribute] = value
	}
	
	fun putNumber(attribute: String, value: Number) {
		putNumber(attribute, value, "")
	}

	fun putNumber(attribute: String, value: Number, suffix: String) {
		if (value == 0) {
			map[attribute] = "0$suffix"
			return
		}
		
		val nf = NumberFormat.getInstance(Locale.US)
		nf.maximumFractionDigits = 2
		nf.isGroupingUsed = false
		val formattedValue = nf.format(value)

		map[attribute] = formattedValue + suffix
	}
	
	override fun decode(data: NetBuffer) {
		val size = data.int
		
		for (i in 0..size) {
			map[data.ascii] = data.unicode
		}
	}

	override fun encode(): ByteArray {
		val data = NetBuffer.allocate(length)

		data.addInt(map.size)
		for (entry in map) {
			data.addAscii(entry.key)
			data.addUnicode(entry.value)
		}
		
		return data.array()
	}

	override val length: Int
		get() {
			var size = 4
			for (entry in map) {
				size += 2 + entry.key.length
				size += 4 + entry.value.length * 2
			}

			return size
		}
}