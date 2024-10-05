/***********************************************************************************
 * Copyright (c) 2024 /// Project SWG /// www.projectswg.com                       *
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
package com.projectswg.common.data.encodables.oob

import com.projectswg.common.data.EnumLookup
import com.projectswg.common.data.encodables.mongo.MongoData
import com.projectswg.common.data.encodables.mongo.MongoPersistable
import com.projectswg.common.data.encodables.oob.waypoint.WaypointPackage
import com.projectswg.common.encoding.Encodable
import com.projectswg.common.network.NetBuffer
import me.joshlarson.jlcommon.log.Log
import java.util.*

class OutOfBandPackage() : Encodable, MongoPersistable {
	private val _packages: MutableList<OutOfBandData> = ArrayList()
	var isConversation: Boolean = false
	val packages: List<OutOfBandData> = _packages

	constructor(vararg outOfBandData: OutOfBandData) : this() {
		Collections.addAll(_packages, *outOfBandData)
	}

	override fun encode(): ByteArray {
		if (_packages.isEmpty()) return ByteArray(4)

		val length = length
		val data = NetBuffer.allocate(length)
		data.addInt((length - 4) / 2) // Client treats this like a unicode string, so it's half the actual size of the array

		if (isConversation) {
			// Unfortunately conversations specifically require this to be present. If not, texts aren't displayed properly.
			data.addShort(0)
		}

		for (oob in _packages) {
			data.addRawArray(packOutOfBandData(oob))
		}
		return data.array()
	}

	override fun decode(data: NetBuffer) {
		var remaining = data.int * 2
		while (remaining > 0) {
			val start = data.position()
			val padding = data.short.toInt()
			val type = Type.getTypeForByte(data.byte)
			unpackOutOfBandData(data, type)
			data.seek(padding)
			remaining -= data.position() - start
		}
	}

	override val length: Int
		get() {
			var size = 4

			if (isConversation) {
				size += java.lang.Short.BYTES
			}

			for (oob in _packages) {
				size += getOOBLength(oob)
			}
			return size
		}

	override fun readMongo(data: MongoData) {
		_packages.clear()
		_packages.addAll(data.getArray("packages", OutOfBandFactory::create))
	}

	override fun saveMongo(data: MongoData) {
		data.putArray("packages", _packages) { obj: OutOfBandData -> OutOfBandFactory.save(obj) }
	}

	private fun unpackOutOfBandData(data: NetBuffer, type: Type) {
		// Position doesn't seem to be reflective of it's spot in the package list, not sure if this can be automated
		// as the client will send -3 for multiple waypoints in a mail, so could be static for each OutOfBandData
		// If that's the case, then we can move the position variable to the Type enum instead of a method return statement
		data.int
		// position
		val oob = when (type) {
			Type.PROSE_PACKAGE -> data.getEncodable(ProsePackage::class.java)
			Type.WAYPOINT      -> data.getEncodable(WaypointPackage::class.java)
			Type.STRING_ID     -> data.getEncodable(StringId::class.java)
			else               -> {
				Log.e("Tried to decode an unsupported OutOfBandData Type: $type")
				return
			}
		}
		_packages.add(oob)
	}

	private fun packOutOfBandData(oob: OutOfBandData): ByteArray {
		// Type and position is included in the padding size
		val paddingSize = getOOBPaddingSize(oob)
		val data = NetBuffer.allocate(getOOBLength(oob))
		data.addShort(paddingSize) // Number of bytes for decoding to skip over when reading
		data.addByte(oob.oobType.type.toInt())
		data.addInt(oob.oobPosition)
		data.addEncodable(oob)
		for (i in 0 until paddingSize) {
			data.addByte(0)
		}

		return data.array()
	}

	private fun getOOBLength(oob: OutOfBandData?): Int {
		return 7 + oob!!.length + getOOBPaddingSize(oob)
	}

	private fun getOOBPaddingSize(oob: OutOfBandData?): Int {
		return (oob!!.length + 5) % 2
	}

	override fun toString(): String {
		return "OutOfBandPackage[packages=$_packages]"
	}

	enum class Type(type: Int) {
		UNDEFINED(Byte.MIN_VALUE.toInt()),
		OBJECT(0),
		PROSE_PACKAGE(1),
		AUCTION_TOKEN(2),
		OBJECT_ATTRIBUTES(3),
		WAYPOINT(4),
		STRING_ID(5),
		STRING(6),
		NUM_TYPES(7);

		val type: Byte = type.toByte()

		companion object {
			private val LOOKUP = EnumLookup(Type::class.java) { t: Type -> t.type }

			fun getTypeForByte(typeByte: Byte): Type {
				return LOOKUP.getEnum(typeByte, UNDEFINED)
			}
		}
	}
}
