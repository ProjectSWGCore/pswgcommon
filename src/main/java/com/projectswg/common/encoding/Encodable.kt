package com.projectswg.common.encoding

import com.projectswg.common.network.NetBuffer

interface Encodable {
	fun decode(data: NetBuffer)
	fun encode(): ByteArray
	val length: Int
}