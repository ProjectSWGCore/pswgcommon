/***********************************************************************************
 * Copyright (c) 2018 /// Project SWG /// www.projectswg.com                       *
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
package com.projectswg.common.encoding

import com.projectswg.common.data.GameVersion
import com.projectswg.common.network.NetBuffer
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1

interface Encodable {
	fun decode(data: NetBuffer)
	fun encode(): ByteArray
	val length: Int
}

abstract class Encodable2<T: Encodable2<T>>(private val builder: EncodableBuilder<T>) : Encodable {
	
	override fun decode(data: NetBuffer) = builder.decode(this as T, GameVersion.NGE, data)
	override fun encode(): ByteArray = builder.encode(this as T, GameVersion.NGE)
	override val length: Int
		get() = builder.length(this as T, GameVersion.NGE)
	
	fun decode(data: NetBuffer, version: GameVersion) = builder.decode(this as T, version, data)
	fun encode(version: GameVersion): ByteArray = builder.encode(this as T, version)
	fun getLength(version: GameVersion): Int = builder.length(this as T, version)
	
}

data class EncoderOperation<K>(val encode: (K, GameVersion, NetBuffer) -> Unit, val decode: (K, GameVersion, NetBuffer) -> Unit, val length: (K, GameVersion) -> Int)
data class EncoderOperationFundamental<T>(val encode: (T, GameVersion, NetBuffer) -> Unit, val decode: (GameVersion, NetBuffer) -> T, val length: (T, GameVersion) -> Int)

class EncodableBuilder<T: Encodable2<T>>(private val variables: Array<EncoderOperation<T>>) {
	
	fun decode(instance: T, version: GameVersion, data: NetBuffer) {
		for (variable in variables) {
			variable.decode(instance, version, data)
		}
	}
	
	fun encode(instance: T, version: GameVersion): ByteArray {
		val ret = NetBuffer.allocate(length(instance, version))
		for (variable in variables) {
			variable.encode(instance, version, ret)
		}
		return ret.array()
	}
	
	fun length(instance: T, version: GameVersion): Int {
		var length = 0
		for (variable in variables) {
			length += variable.length(instance, version)
		}
		return length
	}
	
	companion object {
		fun <T, U> conditional(prop: KMutableProperty1<T, U>, cu: ((KMutableProperty1<T, U>) -> EncoderOperation<T>)? = null, nge: ((KMutableProperty1<T, U>) -> EncoderOperation<T>)? = null): EncoderOperation<T> {
			if (cu == null && nge != null) {
				val op = nge(prop)
				return EncoderOperation(
						{ inst, ver, buf -> if (ver == GameVersion.NGE) op.encode(inst, ver, buf) },
						{ inst, ver, buf -> if (ver == GameVersion.NGE) op.decode(inst, ver, buf) },
						{ inst, ver -> if (ver == GameVersion.NGE) op.length(inst, ver) else 0 })
			} else if (cu != null && nge == null) {
				val op = cu(prop)
				return EncoderOperation(
						{ inst, ver, buf -> if (ver == GameVersion.CU) op.encode(inst, ver, buf) },
						{ inst, ver, buf -> if (ver == GameVersion.CU) op.decode(inst, ver, buf) },
						{ inst, ver -> if (ver == GameVersion.CU) op.length(inst, ver) else 0 })
			} else if (cu != null && nge != null) {
				val cuOp = cu(prop)
				val ngeOp = nge(prop)
				return EncoderOperation(
						{ inst, ver, buf -> if (ver == GameVersion.CU) cuOp.encode(inst, ver, buf) else if (ver == GameVersion.NGE) ngeOp.encode(inst, ver, buf) },
						{ inst, ver, buf -> if (ver == GameVersion.CU) cuOp.decode(inst, ver, buf) else if (ver == GameVersion.NGE) ngeOp.decode(inst, ver, buf) },
						{ inst, ver -> if (ver == GameVersion.CU) cuOp.length(inst, ver) else if (ver == GameVersion.NGE) ngeOp.length(inst, ver) else 0 })
			}
			
			throw IllegalArgumentException("CU or NGE must be defined")
		}
		
		inline fun <T, reified U: Encodable2<U>> list(prop: KProperty1<T, MutableCollection<U>>) = list(prop, ::encodable)
		fun <T, U, V: MutableCollection<U>> list(prop: KProperty1<T, V>, encoder: () -> EncoderOperationFundamental<U>): EncoderOperation<T> {
			val op = encoder()
			return EncoderOperation(
					encode = { inst, ver, buf ->
						val list = prop.get(inst)
						buf.addInt(list.size)
						for (item in list)
							op.encode(item, ver, buf)
					},
					decode = { inst, ver, buf ->
						val length = buf.int
						val list = prop.get(inst)
						list.clear()
						for (i in 0 until length) {
							list.add(op.decode(ver, buf))
						}
					},
					length = { inst, ver -> 
						var length = 4
						for (item in prop.get(inst))
							length += op.length(item, ver)
						length
					}
			)
		}
		
		inline fun <T, reified K:Encodable2<K>> encodable(prop: KMutableProperty1<T, K>) = EncoderOperation<T>({ inst, _, buf -> buf.addEncodable(prop.get(inst)) }, { inst, _, buf -> prop.set(inst, buf.getEncodable((K::class.java))) }, { inst, _ -> prop.get(inst).length })
		fun <T> ascii(prop: KMutableProperty1<T, String>) = EncoderOperation<T>({ inst, _, buf -> buf.addAscii(prop.get(inst)) }, { inst, _, buf -> prop.set(inst, buf.ascii) }, { inst, _ -> 2 + prop.get(inst).length })
		fun <T> unicode(prop: KMutableProperty1<T, String>) = EncoderOperation<T>({ inst, _, buf -> buf.addUnicode(prop.get(inst)) }, { inst, _, buf -> prop.set(inst, buf.unicode) }, { inst, _ -> 4 + prop.get(inst).length*2 })
		fun <T> float32(prop: KMutableProperty1<T, Double>) = EncoderOperation<T>({ inst, _, buf -> buf.addFloat(prop.get(inst).toFloat()) }, { inst, _, buf -> prop.set(inst, buf.float.toDouble()) }, { _, _ -> 4 })
		fun <T> int8(prop: KMutableProperty1<T, Int>) = EncoderOperation<T>({ inst, _, buf -> buf.addByte(prop.get(inst)) }, { inst, _, buf -> prop.set(inst, buf.byte.toInt()) }, { _, _ -> 1 })
		fun <T> int16(prop: KMutableProperty1<T, Int>) = EncoderOperation<T>({ inst, _, buf -> buf.addShort(prop.get(inst)) }, { inst, _, buf -> prop.set(inst, buf.short.toInt()) }, { _, _ -> 2 })
		fun <T> int32(prop: KMutableProperty1<T, Int>) = EncoderOperation<T>({ inst, _, buf -> buf.addInt(prop.get(inst)) }, { inst, _, buf -> prop.set(inst, buf.int) }, { _, _ -> 4 })
		fun <T> int64(prop: KMutableProperty1<T, Long>) = EncoderOperation<T>({ inst, _, buf -> buf.addLong(prop.get(inst)) }, { inst, _, buf -> prop.set(inst, buf.long) }, { _, _ -> 8 })
		
		inline fun <reified K:Encodable2<K>> encodable() = EncoderOperationFundamental<K>({ prop, _, buf -> buf.addEncodable(prop) }, { _, buf -> buf.getEncodable(K::class.java) }, Encodable2<K>::getLength)
		fun ascii() = EncoderOperationFundamental<String>({ prop, _, buf -> buf.addAscii(prop) }, { _, buf -> buf.ascii }, { str, _ -> 2 + str.length })
		fun unicode() = EncoderOperationFundamental<String>({ prop, _, buf -> buf.addUnicode(prop) }, { _, buf -> buf.unicode }, { str, _ -> 4 + str.length*2 })
		fun float32() = EncoderOperationFundamental<Double>({ prop, _, buf -> buf.addFloat(prop.toFloat()) }, { _, buf -> buf.float.toDouble() }, { _, _ -> 4 })
		fun int8() = EncoderOperationFundamental<Int>({ prop, _, buf -> buf.addByte(prop) }, { _, buf -> buf.byte.toInt() }, { _, _ -> 1 })
		fun int16() = EncoderOperationFundamental<Int>({ prop, _, buf -> buf.addShort(prop) }, { _, buf -> buf.short.toInt() }, { _, _ -> 2 })
		fun int32() = EncoderOperationFundamental<Int>({ prop, _, buf -> buf.addInt(prop) }, { _, buf -> buf.int }, { _, _ -> 4 })
		fun int64() = EncoderOperationFundamental<Long>({ prop, _, buf -> buf.addLong(prop) }, { _, buf -> buf.long }, { _, _ -> 8 })
		
	}
	
}
