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
package com.projectswg.common.data.encodables.oob

import com.projectswg.common.data.encodables.mongo.MongoData
import com.projectswg.common.data.encodables.mongo.MongoPersistable
import com.projectswg.common.network.NetBuffer

class StringId(file: String, key: String? = null) : OutOfBandData, MongoPersistable {
	var key: String
		private set
	var file: String
		private set
	
	constructor() : this("", "")
	
	init {
		if (key == null) {
			var stf = file
			if (!stf.contains(":"))
				throw IllegalArgumentException("Invalid stf format! Expected a colon in '$stf'")

			if (stf.startsWith("@")) stf = stf.substring(1)

			val split = stf.split(":".toRegex(), limit = 2).toTypedArray()
			this.file = split[0]
			this.key = if ((split.size >= 2)) split[1] else ""
		} else {
			this.file = file.intern()
			this.key = key.intern()
		}
	}

	override fun encode(): ByteArray {
		val buffer = NetBuffer.allocate(length)
		buffer.addAscii(file)
		buffer.addInt(0)
		buffer.addAscii(key)
		return buffer.array()
	}

	override fun decode(data: NetBuffer) {
		file = data.ascii
		data.int
		key = data.ascii
	}

	override val length: Int
		get() = 8 + key.length + file.length

	override fun readMongo(data: MongoData) {
		file = data.getString("file") ?: ""
		key = data.getString("key") ?: ""
	}

	override fun saveMongo(data: MongoData) {
		data.putString("file", file)
		data.putString("key", key)
	}

	override val oobType: OutOfBandPackage.Type
		get() = OutOfBandPackage.Type.STRING_ID

	override val oobPosition: Int
		get() = -1

	override fun toString(): String {
		return "@$file:$key"
	}

	override fun equals(other: Any?): Boolean {
		if (other !is StringId) return false
		return other.key == this.key && (other.file == this.file)
	}

	override fun hashCode(): Int {
		return key.hashCode() * 67 + file.hashCode()
	}

	companion object {

		val EMPTY: StringId
			get() { return StringId("", "") }

	}

}
