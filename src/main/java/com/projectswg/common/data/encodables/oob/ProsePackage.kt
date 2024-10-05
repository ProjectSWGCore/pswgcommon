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

import com.projectswg.common.data.encodables.mongo.MongoData
import com.projectswg.common.data.encodables.mongo.MongoPersistable
import com.projectswg.common.encoding.Encodable
import com.projectswg.common.network.NetBuffer
import me.joshlarson.jlcommon.log.Log
import java.math.BigInteger
import java.util.*

class ProsePackage() : OutOfBandData {
	var base: StringId
		private set
	private var actor: Prose
	private var target: Prose
	private var other: Prose
	private var di: Int // decimal integer
	private var df: Float // decimal float
	private var grammarFlag: Boolean

	init {
		this.base = StringId.EMPTY
		this.actor = Prose()
		this.target = Prose()
		this.other = Prose()
		this.di = 0
		this.df = 0f
		this.grammarFlag = false
	}

	/**
	 * Creates a new ProsePackage that only specifies a StringId
	 *
	 * @param table Base stf table for this ProsePackage
	 * @param key The key for the provided table to use
	 */
	constructor(table: String, key: String) : this() {
		setStringId(StringId(table, key))
	}

	/**
	 * Creates a new ProsePackage that contains only 1 parameter for the specified StringId object <br></br>
	 * <br></br>
	 * Example: <br></br>
	 * &nbsp&nbsp&nbsp&nbsp ProsePackage("@base_player:prose_deposit_success", "DI", 500)
	 *
	 * @param stringId The base stringId for this ProsePackage
	 * @param proseKey The key in the message, can either be TU, TT, TO, or DI.
	 * @param prose Value to set for this key, instance depends on the key.
	 */
	constructor(stringId: StringId, proseKey: String, prose: Any) : this() {
		setStringId(stringId)
		setProse(proseKey, prose)
	}

	/**
	 * Creates a new ProsePackage with multiple defined parameters. The first Object must be the prose key, followed by the keys value, and so on. If you're only setting 1 parameter, you should use the ProsePackage(key, prose) constructor instead. <br></br>
	 * <br></br>
	 * Example: <br></br>
	 * &nbsp&nbsp&nbsp&nbsp ProsePackage("StringId", new StringId("base_player", "prose_deposit_success"), "DI", 500)
	 *
	 * @param objects Key followed by the value. Can either be STF, TU, TT, TO, or DI.
	 */
	constructor(stringId: StringId, vararg objects: Any) : this(*objects) {
		setStringId(stringId)
	}

	/**
	 * Creates a new ProsePackage with multiple defined parameters. The first Object must be the prose key, followed by the keys value, and so on. If you're only setting 1 parameter, you should use the ProsePackage(key, prose) constructor instead. <br></br>
	 * <br></br>
	 * Example: <br></br>
	 * &nbsp&nbsp&nbsp&nbsp ProsePackage("StringId", new StringId("base_player", "prose_deposit_success"), "DI", 500)
	 *
	 * @param objects Key followed by the value. Can either be STF, TU, TT, TO, or DI.
	 */
	constructor(vararg objects: Any) : this() {
		val length = objects.size
		var i = 0
		while (i < length - 1) {
			if (objects[i] !is String) {
				i += 2
				// Make sure that it's a key, chance of it being a customString though
				continue
			}

			setProse(objects[i] as String, objects[i + 1])
			i += 2
		}
	}

	fun setStringId(prose: Any) {
		if (prose is StringId) {
			base = prose
		} else if (prose is String) {
			if (prose.startsWith("@")) {
				base = StringId(prose)
			} else {
				Log.w("The base STF cannot be a custom string!")
			}
		} else {
			Log.w("The base STF must be either a Stf or a String! Received class: " + prose.javaClass.name)
		}
	}

	fun setTU(prose: Any) {
		setProse(actor, prose)
	}

	fun setTT(prose: Any) {
		setProse(target, prose)
	}

	fun setTO(prose: Any) {
		setProse(other, prose)
	}

	fun setDI(prose: Int) {
		di = prose
	}

	fun setDF(prose: Float) {
		df = prose
	}

	fun setGrammarFlag(useGrammar: Boolean) {
		grammarFlag = useGrammar
	}

	private fun setProse(key: String, prose: Any) {
		when (key) {
			"StringId" -> setStringId(prose)
			"TU"       -> setTU(prose)
			"TT"       -> setTT(prose)
			"TO"       -> setTO(prose)
			"DI"       -> if (prose is Int) setDI(prose)
			else {
				Log.w("DI can only be a Integer!")
			}

			"DF"       -> if (prose is Float) setDF(prose)
			else {
				Log.w("DF can only be a Float!")
			}

			else       -> {}
		}
	}

	private fun setProse(prose: Prose, obj: Any) {
		if (obj is StringId) {
			prose.setStringId(obj)
		} else if (obj is String) {
			if (obj.startsWith("@")) {
				prose.setStringId(StringId(obj))
			} else {
				prose.setText(obj)
			}
		} else if (obj is Long) {
			prose.setObjectId(obj)
		} else if (obj is BigInteger) {
			prose.setObjectId(obj.toLong())
		} else {
			Log.w("Proses can only be Strings or Longs! Received class: " + prose.javaClass.name)
		}
	}

	override fun encode(): ByteArray {
		Objects.requireNonNull(base, "There must be a StringId base!")
		val data = NetBuffer.allocate(length)
		data.addEncodable(base)
		data.addEncodable(actor)
		data.addEncodable(target)
		data.addEncodable(other)
		data.addInt(di)
		data.addFloat(df)
		data.addBoolean(grammarFlag)
		return data.array()
	}

	override fun decode(data: NetBuffer) {
		base = data.getEncodable(StringId::class.java)
		actor = data.getEncodable(Prose::class.java)
		target = data.getEncodable(Prose::class.java)
		other = data.getEncodable(Prose::class.java)
		di = data.int
		df = data.int.toFloat()
		grammarFlag = data.boolean
	}

	override val length: Int
		get() = 9 + base.length + actor.length + target.length + other.length

	override fun readMongo(data: MongoData) {
		data.getDocument("base", base)
		data.getDocument("actor", actor)
		data.getDocument("target", target)
		data.getDocument("other", other)
		di = data.getInteger("di", di)
		df = data.getFloat("df", df)
		grammarFlag = data.getBoolean("grammarFlag", grammarFlag)
	}

	override fun saveMongo(data: MongoData) {
		data.putDocument("base", base)
		data.putDocument("actor", actor)
		data.putDocument("target", target)
		data.putDocument("other", other)
		data.putInteger("di", di)
		data.putFloat("df", df)
		data.putBoolean("grammarFlag", grammarFlag)
	}

	override val oobType: OutOfBandPackage.Type
		get() = OutOfBandPackage.Type.PROSE_PACKAGE

	override val oobPosition: Int
		get() = -1

	override fun toString(): String {
		return "ProsePackage[base=$base, grammarFlag=$grammarFlag, actor=$actor, target=$target, other=$other, di=$di, df=$df]"
	}

	override fun equals(other: Any?): Boolean {
		if (other !is ProsePackage) return false
		val pp = other
		return base == pp.base && actor == pp.actor && target == pp.target && this.other == pp.other && grammarFlag == pp.grammarFlag && di == pp.di && df == pp.df
	}

	override fun hashCode(): Int {
		return base.hashCode() * 3 + actor.hashCode() * 7 + target.hashCode() * 13 + other.hashCode() * 17 + (if (grammarFlag) 1 else 0) + di * 19 + ((df * 23).toInt())
	}

	class Prose : Encodable, MongoPersistable {
		private var objectId: Long = 0
		private var stringId: StringId
		private var text: String

		init {
			this.stringId = StringId("", "")
			this.text = ""
		}

		fun setObjectId(objectId: Long) {
			this.objectId = objectId
		}

		fun setStringId(stringId: StringId) {
			Objects.requireNonNull(stringId, "StringId cannot be null!")
			this.stringId = stringId
		}

		fun setText(text: String) {
			Objects.requireNonNull(text, "Text cannot be null!")
			this.text = text
		}

		override fun encode(): ByteArray {
			val data = NetBuffer.allocate(length)
			data.addLong(objectId)
			data.addEncodable(stringId)
			data.addUnicode(text)
			return data.array()
		}

		override fun decode(data: NetBuffer) {
			objectId = data.long
			stringId = data.getEncodable(StringId::class.java)
			text = data.unicode
		}

		override val length: Int
			get() { return 12 + stringId.length + text.length * 2 }

		override fun readMongo(data: MongoData) {
			objectId = data.getLong("objectId", objectId)
			data.getDocument("stringId", stringId)
			text = data.getString("text", text)
		}

		override fun saveMongo(data: MongoData) {
			data.putLong("objectId", objectId)
			data.putDocument("stringId", stringId)
			data.putString("text", text)
		}

		override fun equals(other: Any?): Boolean {
			if (other !is Prose) return false
			return stringId == other.stringId && objectId == other.objectId && text == other.text
		}

		override fun hashCode(): Int {
			return stringId.hashCode() * 3 + java.lang.Long.hashCode(objectId) * 7 + text.hashCode() * 13
		}

		override fun toString(): String {
			return "Prose[objectId=$objectId, stringId=$stringId, text='$text']"
		}
	}
}
