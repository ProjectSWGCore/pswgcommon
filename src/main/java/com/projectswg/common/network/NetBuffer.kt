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
package com.projectswg.common.network

import com.projectswg.common.encoding.Encodable
import com.projectswg.common.encoding.StringType
import me.joshlarson.jlcommon.log.Log
import java.lang.reflect.InvocationTargetException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

class NetBuffer private constructor(val buffer: ByteBuffer) {
	val ASCII: Charset = StandardCharsets.UTF_8
	val UNICODE: Charset = StandardCharsets.UTF_16LE

	private val size: Int = buffer.capacity()

	fun hasRemaining(): Boolean {
		return buffer.hasRemaining()
	}

	fun remaining(): Int {
		return buffer.remaining()
	}

	fun position(): Int {
		return buffer.position()
	}

	fun limit(): Int {
		return buffer.limit()
	}

	fun capacity(): Int {
		return buffer.capacity()
	}

	fun position(position: Int) {
		buffer.position(position)
	}

	fun seek(relative: Int) {
		buffer.position(buffer.position() + relative)
	}

	fun clear() {
		buffer.clear()
	}

	fun flip() {
		buffer.flip()
	}

	fun mark() {
		buffer.mark()
	}

	fun rewind() {
		buffer.rewind()
	}

	fun compact() {
		buffer.compact()
	}

	fun add(buffer: NetBuffer) {
		add(buffer.buffer)
	}

	fun add(buffer: ByteBuffer) {
		this.buffer.put(buffer)
	}

	fun addBoolean(b: Boolean) {
		buffer.put(if (b) 1.toByte() else 0.toByte())
	}

	fun addAscii(s: String) {
		addArray(s.toByteArray(ASCII))
	}

	fun addUnicode(s: String) {
		addInt(s.length)
		buffer.put(s.toByteArray(UNICODE))
	}

	fun addLong(l: Long) {
		buffer.order(ByteOrder.LITTLE_ENDIAN).putLong(l)
	}

	fun addInt(i: Int) {
		buffer.order(ByteOrder.LITTLE_ENDIAN).putInt(i)
	}

	fun addFloat(f: Float) {
		buffer.order(ByteOrder.LITTLE_ENDIAN).putFloat(f)
	}

	fun addDouble(d: Double) {
		buffer.order(ByteOrder.LITTLE_ENDIAN).putDouble(d)
	}

	fun addShort(i: Int) {
		buffer.order(ByteOrder.LITTLE_ENDIAN).putShort(i.toShort())
	}

	fun addNetLong(l: Long) {
		buffer.order(ByteOrder.BIG_ENDIAN).putLong(l)
	}

	fun addNetInt(i: Int) {
		buffer.order(ByteOrder.BIG_ENDIAN).putInt(i)
	}

	fun addNetShort(i: Int) {
		buffer.order(ByteOrder.BIG_ENDIAN).putShort(i.toShort())
	}

	fun addByte(b: Int) {
		buffer.put(b.toByte())
	}

	fun addArray(b: ByteArray) {
		addShort(b.size)
		buffer.put(b)
	}

	fun addArrayLarge(b: ByteArray) {
		addInt(b.size)
		buffer.put(b)
	}

	fun addRawArray(b: ByteArray) {
		buffer.put(b)
	}

	fun addByteSizedList(list: MutableCollection<out Encodable>?) {
		if (list == null) {
			addByte(0)
			return
		}

		addByte(list.size)
		for (encodable in list) {
			addEncodable(encodable)
		}
	}

	fun addList(list: Collection<Encodable>?) {
		if (list == null) {
			addInt(0)
			return
		}

		addInt(list.size)
		for (encodable in list) {
			addEncodable(encodable)
		}
	}

	fun addList(list: Collection<String>, type: StringType) {
		addInt(list.size)

		when (type) {
			StringType.ASCII   -> for (s in list) {
				addAscii(s)
			}

			StringType.UNICODE -> for (s in list) {
				addUnicode(s)
			}

			else               -> Log.e("Cannot encode StringType " + type)
		}
	}

	fun addEncodable(e: Encodable) {
		buffer.put(e.encode())
	}

	val boolean: Boolean
		get() = this.byte.toInt() == 1

	val ascii: String
		get() = String(this.array, ASCII)

	val unicode: String
		get() = String(getArray(this.int * 2), UNICODE)

	fun getString(type: StringType?): String {
		if (type == StringType.ASCII) return this.ascii
		if (type == StringType.UNICODE) return this.unicode
		throw IllegalArgumentException("Unknown StringType: " + type)
	}

	val byte: Byte
		get() = buffer.get()

	val short: Short
		get() = buffer.order(ByteOrder.LITTLE_ENDIAN).getShort()

	val int: Int
		get() = buffer.order(ByteOrder.LITTLE_ENDIAN).getInt()

	val float: Float
		get() = buffer.getFloat()

	val double: Double
		get() = buffer.getDouble()

	val long: Long
		get() = buffer.order(ByteOrder.LITTLE_ENDIAN).getLong()

	val netShort: Short
		get() = buffer.order(ByteOrder.BIG_ENDIAN).getShort()

	val netInt: Int
		get() = buffer.order(ByteOrder.BIG_ENDIAN).getInt()

	val netLong: Long
		get() = buffer.order(ByteOrder.BIG_ENDIAN).getLong()

	val array: ByteArray
		get() = getArray(this.short.toInt())

	val arrayLarge: ByteArray
		get() = getArray(this.int)

	fun getArray(size: Int): ByteArray {
		val bData = ByteArray(size)
		buffer.get(bData)
		return bData
	}

	val intArray: IntArray
		get() {
			val ints = IntArray(this.int)
			for (i in ints.indices) ints[i] = this.int
			return ints
		}

	fun getIntArray(size: Int): IntArray {
		val ints = IntArray(size)
		for (i in ints.indices) ints[i] = this.int
		return ints
	}

	val booleanArray: BooleanArray
		get() {
			val booleans = BooleanArray(this.int)
			for (i in booleans.indices) booleans[i] = this.boolean
			return booleans
		}

	fun getGeneric(type: Class<*>): Any? {
		if (Encodable::class.java.isAssignableFrom(type)) {
			try {
				val instance = type.getConstructor().newInstance()
				if (!(instance as Encodable).decode(this))
					return null
				return instance
			} catch (e: NoSuchMethodException) {
				Log.e(e)
			} catch (e: InvocationTargetException) {
				Log.e(e)
			} catch (e: InstantiationException) {
				Log.e(e)
			} catch (e: IllegalAccessException) {
				Log.e(e)
			}

			return null
		}
		return when {
			Integer::class.java.isAssignableFrom(type) || Int::class.java.isAssignableFrom(type) || Integer.TYPE.isAssignableFrom(type) -> this.int
			Long::class.java.isAssignableFrom(type) || java.lang.Long.TYPE.isAssignableFrom(type) -> this.long
			Float::class.java.isAssignableFrom(type) || java.lang.Float.TYPE.isAssignableFrom(type) -> this.float
			StringType.ASCII.javaClass.isAssignableFrom(type) -> this.ascii
			StringType.UNICODE.javaClass.isAssignableFrom(type) -> this.unicode
			else -> throw IllegalArgumentException()
		}
	}

	fun <T : Encodable> getList(type: Class<T>, size: Int): MutableList<T>? {
		if (size < 0) {
			Log.e("Read list with size less than zero!")
			return null
		} else if (size == 0) {
			return ArrayList()
		}

		val list: MutableList<T> = ArrayList()
		try {
			for (i in 0..<size) {
				val instance = type.getConstructor().newInstance() ?: return null
				if (!instance.decode(this))
					return null
				list.add(instance)
			}
		} catch (e: NoSuchMethodException) {
			Log.e(e)
		} catch (e: InvocationTargetException) {
			Log.e(e)
		} catch (e: InstantiationException) {
			Log.e(e)
		} catch (e: IllegalAccessException) {
			Log.e(e)
		}

		if (size != list.size) Log.e("Expected list size %d but only have %d elements in the list", size, list.size)
		return list
	}

	fun <T : Encodable> getList(type: Class<T>): MutableList<T>? {
		val size = this.int

		return getList(type, size)
	}

	fun getList(type: StringType): MutableList<String>? {
		val size = this.int

		if (size < 0) {
			Log.e("Read list with size less than zero!")
			return null
		} else if (size == 0) {
			return ArrayList()
		}

		val list: MutableList<String> = ArrayList()

		when (type) {
			StringType.ASCII   -> {
				var i = 0
				while (i < size) {
					list.add(this.ascii)
					i++
				}
			}

			StringType.UNICODE -> {
				var i = 0
				while (i < size) {
					list.add(this.unicode)
					i++
				}
			}

			else               -> Log.e("Do not know how to read list of StringType $type")
		}

		return list
	}

	fun <T : Encodable> getEncodable(type: Class<T>): T? {
		try {
			val instance = type.getConstructor().newInstance() ?: return null
			if (!instance.decode(this))
				return null
			return instance
		} catch (e: NoSuchMethodException) {
			Log.e(e)
		} catch (e: InvocationTargetException) {
			Log.e(e)
		} catch (e: InstantiationException) {
			Log.e(e)
		} catch (e: IllegalAccessException) {
			Log.e(e)
		}

		return null
	}

	fun array(): ByteArray {
		return buffer.array()
	}

	fun size(): Int {
		return size
	}

	@JvmOverloads
	fun copyArray(offset: Int = 0, length: Int = size): ByteArray {
		require(length >= 0) { "Length cannot be less than 0!" }
		require(offset + length <= size) { "Length extends past the end of the array!" }
		val ret = ByteArray(length)
		System.arraycopy(array(), offset, ret, 0, length)
		return ret
	}
	
	companion object {
		@JvmStatic
		fun allocate(size: Int): NetBuffer {
			return NetBuffer(ByteBuffer.allocate(size))
		}

		@JvmStatic
		fun allocateDirect(size: Int): NetBuffer {
			return NetBuffer(ByteBuffer.allocateDirect(size))
		}

		@JvmStatic
		fun wrap(data: ByteArray): NetBuffer {
			return NetBuffer(ByteBuffer.wrap(data))
		}

		@JvmStatic
		fun wrap(data: ByteBuffer): NetBuffer {
			return NetBuffer(data)
		}
	}
}
