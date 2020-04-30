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
package com.projectswg.common.network.packets

import com.projectswg.common.data.CRC
import com.projectswg.common.network.NetBuffer
import me.joshlarson.jlcommon.log.Log
import java.net.SocketAddress

abstract class SWGPacket {
	/**
	 * The socket address that this packet was sent to or received from. Setting this value after it's received, or before it's sent has no effect
	 */
	var socketAddress: SocketAddress? = null
	var packetType: PacketType = PacketType.UNKNOWN
		private set
	protected open val packetData: String
		get() = "?"
	private var crc: Int = 0
	
	abstract fun decode(data: NetBuffer)
	abstract fun encode(): NetBuffer
	
	protected fun packetAssert(condition: Boolean, constraint: String?) {
		if (!condition) throw PacketSerializationException(this, constraint)
	}
	
	protected fun createPacketInformation(vararg data: Any?): String {
		assert(data.size % 2 == 0)
		val str = StringBuilder()
		var i = 0
		while (i + 1 < data.size) {
			assert(data[i] is String)
			val key = if (data[i] == null) "null" else data[i].toString()
			if (i > 0) str.append(' ')
			str.append(key)
			str.append('=')
			str.append(data[i + 1])
			i += 2
		}
		return str.toString()
	}
	
	fun decode(op: NetBuffer.() -> Unit) {
		
	}
	
	fun checkDecode(data: NetBuffer, crc: Int): Boolean {
		data.short
		this.crc = data.int
		this.packetType = PacketType.fromCrc(crc)
		if (this.crc == crc)
			return true
		Log.w("SWG Opcode does not match actual! Expected: 0x%08X  Actual: 0x%08X", crc, this.crc)
		return false
	}
	
	override fun toString(): String = javaClass.simpleName + "[" + packetData + "]"
	
	companion object {
		@JvmStatic
		fun getCrc(string: String?): Int {
			return CRC.getCrc(string)
		}
	}
	
}
