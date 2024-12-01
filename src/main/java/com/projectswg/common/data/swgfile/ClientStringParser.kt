/***********************************************************************************
 * Copyright (c) 2024 /// Project SWG /// www.projectswg.com                       *
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
package com.projectswg.common.data.swgfile

import com.projectswg.common.data.encodables.oob.StringId
import com.projectswg.common.network.NetBuffer
import me.joshlarson.jlcommon.log.Log
import java.io.File
import java.nio.channels.FileChannel

class ClientStringParser private constructor(private val buffer: NetBuffer, private val swgFileName: String) {
	
	val strings = HashMap<StringId, String>()
	
	private fun parseVersion0() {
		assert(false) // No .stf's I'm aware of use this version
	}
	
	private fun parseVersion1() {
		buffer.int // nextUniqueId
		val entryCount = buffer.int
		val values = HashMap<Int, String>(entryCount)
		for (i in 0 until entryCount) {
			val id = buffer.int
			buffer.int // source CRC used to generate the string--unused for us
			values[id] = buffer.unicode
		}
		for (i in 0 until entryCount) {
			val id = buffer.int
			val key = String(buffer.arrayLarge, Charsets.US_ASCII)
			if (!values.contains(id))
				Log.w("Unknown string value for key: %d (key text: %s)", id, key)
			strings[StringId(swgFileName, key)] = values[id] ?: continue
		}
	}
	
	companion object {
		
		fun parse(filepath: File, swgFileName: String): ClientStringParser {
			FileChannel.open(filepath.toPath()).use { channel ->
				val buffer = NetBuffer.wrap(channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size()))
				val magic = buffer.int
				val version = buffer.byte.toInt()
				check(magic == 0xABCD) { "invalid magic bytes at start of file" }

				val parser = ClientStringParser(buffer, swgFileName)
				when (version) {
					0 -> parser.parseVersion0()
					1 -> parser.parseVersion1()
					else -> error("invalid version $version")
				}
				return parser
			}
		}
		
	}
	
}
