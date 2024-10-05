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
package com.projectswg.common.data.sui

import com.projectswg.common.encoding.Encodable
import com.projectswg.common.encoding.StringType
import com.projectswg.common.network.NetBuffer
import me.joshlarson.jlcommon.log.Log
import java.nio.charset.StandardCharsets
import kotlin.collections.ArrayList

class SuiComponent(type: Type, widget: String) : Encodable {
	var type: Type = type
		private set

	private var _wideParams: MutableList<String> = ArrayList()
	private var _narrowParams: MutableList<String> = ArrayList()

	val target: String
		/**
		 * Retrieve the base widget that this component targets
		 * @return Base widget this component targets
		 */
		get() = _narrowParams[0]

	@Deprecated("Properly initialize each field with the full constructor.", ReplaceWith("SuiComponent(type, widget)"))
	constructor() : this(Type.NONE, "")

	fun addNarrowParam(param: String) {
		_narrowParams.add(param)
	}

	fun setNarrowParam(index: Int, param: String) {
		_narrowParams[index] = param
	}

	fun addWideParam(param: String) {
		_wideParams.add(param)
	}

	val subscribedProperties: List<String>?
		get() {
			if (type != Type.SUBSCRIBE_TO_EVENT) return null

			val size = _narrowParams.size
			if (size < 3) {
				Log.w("Tried to get subscribed properties when there are none for target %s", target)
			} else {
				val subscribedProperties: MutableList<String> = ArrayList()

				var i = 3
				while (i < size) {
					val property = _narrowParams[i++] + "." + _narrowParams[i++]
					subscribedProperties.add(property)
				}

				return subscribedProperties
			}
			return null
		}

	val subscribeToEventCallback: String?
		get() {
			if (type != Type.SUBSCRIBE_TO_EVENT) return null

			val size = _narrowParams.size
			if (size < 3) {
				Log.w("Tried to get subscribed callback when there is none for target %s", target)
			} else {
				return _narrowParams[2]
			}
			return null
		}

	val subscribedToEventType: Int
		get() {
			if (type != Type.SUBSCRIBE_TO_EVENT) return -1

			val size = _narrowParams.size
			if (size < 3) {
				Log.w("Tried to get subscribed event type when there is none for target %s", target)
			} else {
				val bytes = _narrowParams[1].toByteArray(StandardCharsets.UTF_8)
				if (bytes.size > 1) {
					Log.w("Tried to get eventType but narrowparams string byte array length is more than 1")
					return -1
				}

				return bytes[0].toInt()
			}
			return -1
		}

	override fun encode(): ByteArray {
		val data = NetBuffer.allocate(length)
		data.addByte(type.value.toInt())
		data.addList(_wideParams, StringType.UNICODE)
		data.addList(_narrowParams, StringType.ASCII)
		return data.array()
	}

	override fun decode(data: NetBuffer) {
		type = Type.valueOf(data.byte)
		_wideParams = data.getList(StringType.UNICODE)
		_narrowParams = data.getList(StringType.ASCII)
	}

	override val length: Int
		get() {
			var size = 9

			for (param in _wideParams) {
				size += 4 + (param.length * 2)
			}
			for (param in _narrowParams) {
				size += 2 + param.length
			}

			return size
		}

	enum class Type(value: Int) {
		NONE(0),
		CLEAR_DATA_SOURCE(1),
		ADD_CHILD_WIDGET(2),
		SET_PROPERTY(3),
		ADD_DATA_ITEM(4),
		SUBSCRIBE_TO_EVENT(5),
		ADD_DATA_SOURCE_CONTAINER(6),
		CLEAR_DATA_SOURCE_CONTAINER(7),
		ADD_DATA_SOURCE(8);

		val value: Byte = value.toByte()

		companion object {
			fun valueOf(value: Byte): Type {
				for (type in entries) {
					if (type.value == value) return type
				}
				return NONE
			}
		}
	}

	init {
		this._wideParams = ArrayList(3)
		this._narrowParams = ArrayList(3)
		_narrowParams.add(widget)
	}
}
