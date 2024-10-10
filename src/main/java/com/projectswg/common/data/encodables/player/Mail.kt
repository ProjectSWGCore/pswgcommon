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
package com.projectswg.common.data.encodables.player

import com.projectswg.common.data.encodables.mongo.MongoData
import com.projectswg.common.data.encodables.mongo.MongoPersistable
import com.projectswg.common.data.encodables.oob.OutOfBandPackage
import com.projectswg.common.encoding.Encodable
import com.projectswg.common.network.NetBuffer
import java.time.Instant
import java.util.function.Supplier

class Mail(var sender: String, subject: String, message: String, receiverId: Long) : Encodable, MongoPersistable {
	var id: Int = 0
	var status: Byte = NEW
	var subject: String = subject
		private set
	var message: String = message
		private set
	var receiverId: Long = receiverId
		private set
	var outOfBandPackage: OutOfBandPackage = OutOfBandPackage()
	var timestamp: Instant = Instant.now()

	constructor() : this("", "", "", 0)

	override fun encode(): ByteArray {
		val data = NetBuffer.allocate(length)
		data.addUnicode(message)
		data.addUnicode(subject)
		data.addEncodable(outOfBandPackage)
		return data.array()
	}

	override fun decode(data: NetBuffer) {
		message = data.unicode
		subject = data.unicode
		outOfBandPackage = data.getEncodable(OutOfBandPackage::class.java)
	}

	override val length: Int
		get() = 8 + message.length * 2 + subject.length * 2 + outOfBandPackage.length

	override fun readMongo(data: MongoData) {
		id = data.getInteger("id", id)
		timestamp = data.getDate("timestamp", timestamp)
		receiverId = data.getLong("receiverId", receiverId)
		status = data.getInteger("status", status.toInt()).toByte()
		sender = data.getString("sender", sender)
		subject = data.getString("subject", subject)
		message = data.getString("message", message)
		outOfBandPackage = data.getDocument("oobPackage", Supplier { OutOfBandPackage() }) ?: OutOfBandPackage()
	}

	override fun saveMongo(data: MongoData) {
		data.putInteger("id", id)
		data.putDate("timestamp", timestamp)
		data.putLong("receiverId", receiverId)
		data.putInteger("status", status.toInt())
		data.putString("sender", sender)
		data.putString("subject", subject)
		data.putString("message", message)

		data.putDocument("oobPackage", this.outOfBandPackage)
	}

	override fun hashCode(): Int {
		return id
	}

	override fun equals(other: Any?): Boolean {
		if (other !is Mail) return false
		return other.id == id
	}

	fun encodeHeader(): ByteArray {
		val data = NetBuffer.allocate(12 + subject.length * 2)
		data.addInt(0)
		data.addUnicode(subject)
		data.addInt(0)
		return data.array()
	}

	fun decodeHeader(data: NetBuffer) {
		data.int
		subject = data.unicode
		data.int
	}

	companion object {
		const val NEW: Byte = 0x4E
		const val READ: Byte = 0x52
		const val UNREAD: Byte = 0x55
	}

	init {
		this.id = 0
		this.status = NEW
		this.outOfBandPackage = OutOfBandPackage()
		this.timestamp = Instant.now()
	}
}
