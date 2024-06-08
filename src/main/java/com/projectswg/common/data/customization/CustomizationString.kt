/***********************************************************************************
 * Copyright (c) 2023 /// Project SWG /// www.projectswg.com                       *
 * *
 * ProjectSWG is the first NGE emulator for Star Wars Galaxies founded on          *
 * July 7th, 2011 after SOE announced the official shutdown of Star Wars Galaxies. *
 * Our goal is to create an emulator which will provide a server for players to    *
 * continue playing a game similar to the one they used to play. We are basing     *
 * it on the final publish of the game prior to end-game events.                   *
 * *
 * This file is part of PSWGCommon.                                                *
 * *
 * --------------------------------------------------------------------------------*
 * *
 * PSWGCommon is free software: you can redistribute it and/or modify              *
 * it under the terms of the GNU Affero General Public License as                  *
 * published by the Free Software Foundation, either version 3 of the              *
 * License, or (at your option) any later version.                                 *
 * *
 * PSWGCommon is distributed in the hope that it will be useful,                   *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                  *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                   *
 * GNU Affero General Public License for more details.                             *
 * *
 * You should have received a copy of the GNU Affero General Public License        *
 * along with PSWGCommon.  If not, see <http:></http:>//www.gnu.org/licenses/>.             *
 */
package com.projectswg.common.data.customization

import com.projectswg.common.data.encodables.mongo.MongoData
import com.projectswg.common.data.encodables.mongo.MongoPersistable
import com.projectswg.common.encoding.Encodable
import com.projectswg.common.network.NetBuffer
import me.joshlarson.jlcommon.log.Log
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.util.*
import java.util.function.BiConsumer

/**
 * The Customization string is used to set special properties
 * on objects. This can be lightsaber color, vehicle speed,
 * facial hair, etc.
 */
class CustomizationString : Encodable, MongoPersistable {
	private val _variables: MutableMap<String, Int> = Collections.synchronizedMap(LinkedHashMap()) // Ordered and synchronized
	val variables: Map<String, Int> = Collections.unmodifiableMap(_variables)

	val isEmpty: Boolean
		get() = _variables.isEmpty()

	override fun saveMongo(data: MongoData) {
		data.putMap("variables", _variables)
	}

	override fun readMongo(data: MongoData) {
		_variables.clear()
		_variables.putAll(data.getMap("variables", String::class.java, Int::class.java))
	}

	fun put(name: String, value: Int): Int? {
		assert(name in VAR_NAME_TO_ID)
		return _variables.put(name, value)
	}

	fun remove(name: String?): Int? {
		return _variables.remove(name)
	}

	fun get(name: String?): Int? {
		return _variables[name]
	}

	fun forEach(consumer: BiConsumer<in String, in Int>) {
		_variables.forEach(consumer)
	}

	fun clear() {
		_variables.clear()
	}

	override fun toString(): String {
		val str = StringBuilder()
		var first = true
		for ((key, value) in _variables) {
			if (!first) str.append(", ")
			first = false
			str.append(key).append('=').append(value)
		}
		return str.toString()
	}

	override fun decode(data: NetBuffer) {
		val str = NetBuffer.wrap(data.array)
		if (str.size() == 0 || str.byte.toInt() != 0x02) return  // Start of Text

		val variableCount = getCustomizationStringByte(str)
		for (i in 0 until variableCount) {
			val combinedVariableId = str.byte
			val variableId = VAR_ID_TO_NAME[(combinedVariableId.toInt() and 0x7F).toByte()]
			var value = getCustomizationStringByte(str) and 0xFF
			if ((combinedVariableId.toInt() and 0x80) != 0) {
				value = value or ((getCustomizationStringByte(str) shl 8) and 0xFF00)
			}
			if (variableId == null) {
				Log.w("CustomizationString: Unparsed variableId %d with value %d", (combinedVariableId.toInt() and 0x7F), value)
			} else {
				_variables[variableId] = value and 0xFFFF
			}
		}

		// str.byte; 0xFF - Escape
		// str.byte; 0x03 - End of Text
	}

	override fun encode(): ByteArray {
		if (isEmpty) {
			// No need to send more than an empty array in this case
			return ByteArray(java.lang.Short.BYTES)
		}

		val encodableLength = length
		val out = ByteArrayOutputStream(encodableLength - java.lang.Short.BYTES)

		try {
			out.write(2) // Marks start of text
			addCustomizationStringByte(out, _variables.size)

			_variables.forEach { (variableName: String, variableValue: Int) ->
				var variableId = VAR_NAME_TO_ID[variableName]!!.toInt()
				val variableValueOneByte = variableValue in 0..127
				if (!variableValueOneByte) variableId = variableId or 0x80
				try {
					out.write(variableId)
					if (variableValueOneByte) {
						addCustomizationStringByte(out, variableValue)
					} else {
						addCustomizationStringByte(out, variableValue and 0xFF)
						addCustomizationStringByte(out, (variableValue shr 8) and 0xFF)
					}
				} catch (e: Exception) {
					Log.e(e)
				}
			}

			out.write(0xFF) // Escape
			out.write(3) // Marks end of text
			out.flush()

			val result = out.toByteArray()
			val data = NetBuffer.allocate(encodableLength)

			data.addArray(result) // This will add the array length in little endian order

			return data.array()
		} catch (e: IOException) {
			Log.e(e)
			return NetBuffer.allocate(java.lang.Short.SIZE).array() // Returns an array of 0x00, 0x00 indicating that it's empty
		}
	}

	@Throws(IOException::class)
	private fun addCustomizationStringByte(out: ByteArrayOutputStream, c: Int) {
		assert(c in 0..0xFF)
		when (c) {
			0x00 -> {
				out.write(0xFF) // Escape
				out.write(0x01) // Put variable value
			}

			0xFF -> {
				out.write(0xFF) // Escape
				out.write(0x02) // Put variable value
			}

			else -> out.write(c)
		}
	}

	private fun getCustomizationStringByte(out: NetBuffer): Int {
		val c = out.byte

		if (c == 0xFF.toByte()) {
			return when (out.byte) {
				0x01.toByte() -> 0x00
				0x02.toByte() -> 0xFF
				else -> 0x00
			}
		}

		return c.toInt()
	}

	override val length: Int
		get() {
			var length = java.lang.Short.BYTES // Array size declaration field

			length += 3 // UTF-8 start of text, escape, UTF-8 end of text
			length += getValueLength(_variables.size) // variable count
			for (i in _variables.values) {
				length += 1 // variableId
				val firstByte = i and 0xFF
				val secondByte = (i shr 8) and 0xFF
				length += getValueLength(firstByte)
				if (i < 0 || i >= 128) { // signed short
					length += getValueLength(secondByte)
				}
			}

			return length
		}

	companion object {
		private val VAR_NAME_TO_ID: MutableMap<String, Byte> = HashMap()
		private val VAR_ID_TO_NAME: MutableMap<Byte, String> = HashMap()

		init {
			try {
				BufferedReader(InputStreamReader(Objects.requireNonNull(CustomizationString::class.java.getResourceAsStream("customization_variables.sdb")))).use { reader ->
					reader.lines().forEach { line: String ->
						val tab = line.indexOf('\t')
						val key = line.substring(0, tab)
						val value = line.substring(tab + 1).toByte()
						VAR_NAME_TO_ID[key] = value
						VAR_ID_TO_NAME[value] = key
					}
				}
			} catch (e: IOException) {
				throw RuntimeException("could not load customization variables from resources", e)
			}
		}

		private fun getValueLength(c: Int): Int {
			if (c == 0x00 || c == 0xFF) return 2
			return 1
		}
	}
}
