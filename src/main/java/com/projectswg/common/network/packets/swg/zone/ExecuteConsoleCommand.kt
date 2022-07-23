package com.projectswg.common.network.packets.swg.zone

import com.projectswg.common.network.NetBuffer
import com.projectswg.common.network.packets.SWGPacket

class ExecuteConsoleCommand : SWGPacket() {

	companion object {
		val crc = getCrc("ExecuteConsoleCommand")
	}

	private val commandSeparator = ";"

	val commands = ArrayList<String>()

	fun addCommand(command: String) {
		commands += "/$command"
	}

	override fun decode(data: NetBuffer) {
		if (!super.checkDecode(data, crc)) {
			return
		}
		commands.addAll(data.ascii.split(commandSeparator))
	}

	override fun encode(): NetBuffer {
		val commandsString = commands.joinToString(commandSeparator)
		val commandsStringLength = 2 + commandsString.length;
		val data = NetBuffer.allocate(2 + 4 + commandsStringLength)

		val operandCount = 1
		data.addShort(operandCount)
		data.addInt(crc)
		data.addAscii(commandsString)

		return data
	}

}