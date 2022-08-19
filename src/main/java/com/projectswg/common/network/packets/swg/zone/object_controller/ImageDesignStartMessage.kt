package com.projectswg.common.network.packets.swg.zone.object_controller

import com.projectswg.common.network.NetBuffer

class ImageDesignStartMessage : ObjectController {

	constructor(receiverId: Long) : super(receiverId, CRC)

	constructor(data: NetBuffer) : super(CRC) {
		decode(data)
	}

	companion object {
		const val CRC = 0x023A
	}

	var designerId = 0L
	var clientId = 0L
	var salonId = 0L
	var holoEmote = ""

	override fun decode(data: NetBuffer) {
		decodeHeader(data)
		designerId = data.long
		clientId = data.long
		salonId = data.long
		holoEmote = data.ascii
	}

	override fun encode(): NetBuffer {
		val data = NetBuffer.allocate(HEADER_LENGTH + 26 + holoEmote.length)
		encodeHeader(data)
		data.addLong(designerId)
		data.addLong(clientId)
		data.addLong(salonId)
		data.addAscii(holoEmote)

		return data
	}
}