package com.projectswg.common.network.packets.swg.zone.object_controller

import com.projectswg.common.encoding.Encodable
import com.projectswg.common.network.NetBuffer

class MorphParameter : Encodable {

	var name = ""
	var value = 0f

	override fun decode(data: NetBuffer) {
		name = data.ascii
		value = data.float
	}

	override fun encode(): ByteArray {
		val data = NetBuffer.allocate(length)

		data.addAscii(name)
		data.addFloat(value)

		return data.array()
	}

	override val length: Int
		get() = 6 + name.length

	override fun toString(): String {
		return "$name -> $value"
	}
}