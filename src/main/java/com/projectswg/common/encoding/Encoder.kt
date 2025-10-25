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
package com.projectswg.common.encoding

import me.joshlarson.jlcommon.log.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets

object Encoder {
	@JvmStatic
	@JvmOverloads
	fun encode(`object`: Any, strType: StringType = StringType.UNSPECIFIED): ByteArray? {
		return when {
			strType == StringType.ASCII && `object` is String   -> encodeAscii(`object`)
			strType == StringType.UNICODE && `object` is String -> encodeUnicode(`object`)
			`object` is Encodable                               -> encodeObject(`object`)
			`object` is Int                                     -> encodeInteger(`object`)
			`object` is Long                                    -> encodeLong(`object`)
			`object` is Short                                   -> encodeShort(`object`)
			`object` is Byte                                    -> encodeByte(`object`)
			`object` is Boolean                                 -> encodeBoolean(`object`)
			`object` is Float || `object` is Double             -> encodeFloat(`object`)
			`object` is String                                  -> throw UnsupportedOperationException("You must specify a String type!")
			else                                                -> {
				Log.e("Do not know how to encode instance type " + `object`.javaClass.getName())
				null
			}
		}
	}

	fun encodeObject(encodable: Encodable): ByteArray {
		return encodable.encode()
	}

	private fun encodeFloat(`object`: Any): ByteArray {
		val buffer = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)
		buffer.putFloat((if (`object` is Float) `object` else (`object` as Double).toFloat()))
		return buffer.array()
	}

	private fun encodeByte(`object`: Byte): ByteArray {
		return byteArrayOf((`object`))
	}

	private fun encodeBoolean(`object`: Boolean): ByteArray {
		return encodeByte((if (`object`) 1 else 0).toByte())
	}

	fun encodeShort(`object`: Short): ByteArray {
		val buffer = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN)
		buffer.putShort(`object`)
		return buffer.array()
	}

	fun encodeInteger(integer: Int): ByteArray {
		val buffer = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)
		buffer.putInt(integer)
		return buffer.array()
	}

	fun encodeLong(l: Long): ByteArray {
		val buffer = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN)
		buffer.putLong(l)
		return buffer.array()
	}

	fun encodeAscii(string: String): ByteArray {
		val buffer = ByteBuffer.allocate(2 + string.length).order(ByteOrder.LITTLE_ENDIAN)
		buffer.putShort(string.length.toShort())
		buffer.put(string.toByteArray(StandardCharsets.UTF_8))
		return buffer.array()
	}

	fun encodeUnicode(string: String): ByteArray {
		val buffer = ByteBuffer.allocate(4 + (string.length * 2)).order(ByteOrder.LITTLE_ENDIAN)
		buffer.putInt(string.length)
		buffer.put(string.toByteArray(StandardCharsets.UTF_16LE))
		return buffer.array()
	}
}
