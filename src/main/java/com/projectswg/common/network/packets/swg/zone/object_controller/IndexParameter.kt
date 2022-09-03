package com.projectswg.common.network.packets.swg.zone.object_controller

import com.projectswg.common.encoding.Encodable
import com.projectswg.common.network.NetBuffer

class IndexParameter : Encodable {

	var name = ""
	var value = 0

	override fun decode(data: NetBuffer) {
		name = data.ascii
		value = data.int
	}

	override fun encode(): ByteArray {
		val data = NetBuffer.allocate(length)

		data.addAscii(name)
		data.addInt(value)

		return data.array()
	}

	override val length: Int
		get() = 6 + name.length

	override fun toString(): String {
		return "$name -> $value"
	}
}