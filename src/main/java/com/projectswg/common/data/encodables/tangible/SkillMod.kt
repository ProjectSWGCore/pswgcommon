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
package com.projectswg.common.data.encodables.tangible

import com.projectswg.common.data.encodables.mongo.MongoData
import com.projectswg.common.data.encodables.mongo.MongoPersistable
import com.projectswg.common.encoding.Encodable
import com.projectswg.common.network.NetBuffer

class SkillMod @JvmOverloads constructor(private var base: Int = 0, private var modifier: Int = 0) : Encodable, MongoPersistable {
	override fun encode(): ByteArray {
		val data = NetBuffer.allocate(8)

		data.addInt(base)
		data.addInt(modifier)

		return data.array()
	}

	override fun decode(data: NetBuffer) {
		base = data.int
		modifier = data.int
	}

	override val length: Int
		get() = 8

	override fun readMongo(data: MongoData) {
		base = data.getInteger("base", 0)
		modifier = data.getInteger("modifier", 0)
	}

	override fun saveMongo(data: MongoData) {
		data.putInteger("base", base)
		data.putInteger("modifier", modifier)
	}

	fun adjustBase(adjustment: Int) {
		base += adjustment
	}

	fun adjustModifier(adjustment: Int) {
		modifier += adjustment
	}

	val value: Int
		get() = base + modifier

	override fun toString(): String {
		return "SkillMod[Base=$base, Modifier=$modifier]"
	}
}
