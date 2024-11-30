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
package com.projectswg.common.data.customization

import com.projectswg.common.data.encodables.mongo.MongoData
import com.projectswg.common.data.encodables.mongo.MongoPersistable
import com.projectswg.common.encoding.Encodable
import com.projectswg.common.network.NetBuffer
import me.joshlarson.jlcommon.log.Log
import java.io.*
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
		val stream = CustomizationStringInputStream(data.array)
		stream.read() // version - should be 0x02
		val variableCount = stream.read()
		for (i in 0 until variableCount) {
			val (key, value) = stream.readKeyValue()
			val variableId = VAR_ID_TO_NAME[(key and 0x7F).toByte()]
			if (variableId == null) {
				Log.w("CustomizationString: Unparsed variableId %d with value %d", key, value)
			} else {
				_variables[variableId] = value
			}
		}
		stream.read() // 0xFF
		stream.read() // 0x03
	}
	
	override fun encode(): ByteArray {
		if (isEmpty) {
			// No need to send more than an empty array in this case
			return ByteArray(Short.SIZE_BYTES)
		}
		
		val out = CustomizationStringOutputStream(length - Short.SIZE_BYTES)
		out.write(2) // Customization String Version
		out.write(_variables.size)
		_variables.forEach { (variableName: String, variableValue: Int) ->
			out.writeKeyValue(VAR_NAME_TO_ID[variableName]!!.toInt(), variableValue)
		}
		out.writeEndOfText()
		
		val data = NetBuffer.allocate(out.length + Short.SIZE_BYTES)
		data.addArray(out.toByteArray())
		return data.array()
	}
	
	override val length: Int
		get() {
			var length = Short.SIZE_BYTES // Array length
			
			length += 4 // encoded version, escape, and end of text
			length += getValueLength(_variables.size) // variable count
			for (entry in _variables.entries) {
				val variableId = VAR_NAME_TO_ID[entry.key]?.toInt() ?: 0
				val value = entry.value
				val isSignedShort = value < 0 || value >= 256
				length += getValueLength(variableId or (if (isSignedShort) 0x80 else 0x00))
				length += getValueLength(value and 0xFF)
				if (isSignedShort) {
					length += getValueLength((value shr 8) and 0xFF)
				}
			}
			
			return length
		}
	
	private class CustomizationStringInputStream(val buffer: ByteArray) : InputStream() {
		
		private var index = 0
		
		override fun read(): Int {
			val nextByte = readUTF8Byte()
			if (nextByte == 0xFF) {
				// If the next byte is end of text, don't strip the 0xFF
				if (buffer[index] == 3.toByte()) return nextByte
				return when (val escapedByte = readUTF8Byte()) {
					0x01 -> 0x00
					0x02 -> 0xFF
					else -> escapedByte // Technically invalid, but here we are
				}
			}
			return nextByte
		}
		
		fun readKeyValue(): Pair<Int, Int> {
			val keyEncoded = read()
			var value = read()
			if ((keyEncoded and 0x80) != 0) {
				value = 0xFFFF0000.toInt() or value or (read() shl 8)
			}
			return Pair(keyEncoded and 0x7F, value)
		}
		
		private fun readUTF8Byte(): Int {
			// This assumes a max character width of 2
			val firstByte = buffer[index++].toInt()
			
			// One byte
			if ((firstByte and 0b10000000) == 0) return firstByte
			
			// Two bytes
			val secondByte = buffer[index++].toInt()
			return ((firstByte shl 6) or (secondByte and 0b00111111)) and 0xFF
		}
		
	}
	
	private class CustomizationStringOutputStream(val length: Int) : OutputStream() {
		
		private val buffer = ByteArray(length)
		private var index = 0
		
		override fun write(b: Int) {
			assert(b in 0..0xFF)
			when (b) {
				0x00 -> {
					writeUTF8Byte(0xFF) // Escape
					writeUTF8Byte(0x01)
				}
				
				0xFF -> {
					writeUTF8Byte(0xFF) // Escape
					writeUTF8Byte(0x02)
				}
				
				else -> writeUTF8Byte(b)
			}
		}
		
		fun writeEndOfText() {
			writeUTF8Byte(0xFF)
			writeUTF8Byte(0x03)
		}
		
		fun writeKeyValue(key: Int, value: Int) {
			val isSignedShort = value < 0 || value >= 256
			write(key or (if (isSignedShort) 0x80 else 0x00))
			write(value and 0xFF)
			if (isSignedShort) {
				write((value shr 8) and 0xFF)
			}
		}
		
		fun toByteArray(): ByteArray {
			assert(index == buffer.size)
			return buffer
		}
		
		private fun writeUTF8Byte(c: Int) {
			assert(c in 0..0xFF) // Guaranteed one or two byte UTF-8
			if ((c and 0b10000000) == 0) {
				buffer[index++] = c.toByte()
			} else {
				buffer[index++] = (0b11000000 or ((c shr 6) and 0b11)).toByte()
				buffer[index++] = (0b10000000 or (c and 0b00111111)).toByte()
			}
		}
		
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
			if (c == 0x00 || c == 0xFF) return 3 // One extra byte for UTF-8 encoding of 0xFF
			if (c >= 0x80) return 2 // One extra byte for UTF-8 encoding of values larger than 0x7F
			return 1
		}
		
	}
}
