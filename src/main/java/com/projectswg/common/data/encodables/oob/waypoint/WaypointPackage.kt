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
package com.projectswg.common.data.encodables.oob.waypoint

import com.projectswg.common.data.encodables.mongo.MongoData
import com.projectswg.common.data.encodables.mongo.MongoPersistable
import com.projectswg.common.data.encodables.oob.OutOfBandData
import com.projectswg.common.data.encodables.oob.OutOfBandPackage
import com.projectswg.common.data.location.Point3D
import com.projectswg.common.data.location.Terrain
import com.projectswg.common.network.NetBuffer

class WaypointPackage : OutOfBandData, MongoPersistable {
	var position: Point3D

	var objectId: Long = 0
	var terrain: Terrain? = null
	var cellId: Long = 0
	var name: String? = null
	var color: WaypointColor? = null
	var isActive: Boolean = false

	constructor() {
		this.position = Point3D()
		this.objectId = 0
		this.terrain = Terrain.GONE
		this.cellId = 0
		this.name = "New Waypoint"
		this.color = WaypointColor.BLUE
		this.isActive = true
	}

	constructor(data: NetBuffer) {
		this.position = Point3D()
		decode(data)
	}

	override fun encode(): ByteArray {
		val data = NetBuffer.allocate(length)
		data.addInt(0)
		data.addEncodable(position)
		data.addLong(cellId)
		data.addInt(terrain!!.crc)
		data.addUnicode(name)
		data.addLong(objectId)
		data.addByte(color?.value ?: 0)
		data.addBoolean(isActive)
		return data.array()
	}

	override fun decode(data: NetBuffer) {
		data.int
		position.decode(data)
		cellId = data.long
		terrain = Terrain.getTerrainFromCrc(data.int)
		name = data.unicode
		objectId = data.long
		color = WaypointColor.Companion.valueOf(data.byte.toInt())
		isActive = data.boolean
	}

	override val length: Int
		get() = 42 + name!!.length * 2

	override fun saveMongo(data: MongoData) {
		data.putLong("objectId", objectId)
		data.putLong("cellId", cellId)
		data.putDocument("position", position)
		data.putString("terrain", terrain!!.name)
		data.putString("name", name)
		data.putInteger("color", color?.value ?: 0)
		data.putBoolean("active", isActive)
	}

	override fun readMongo(data: MongoData) {
		objectId = data.getLong("objectId", 0)
		cellId = data.getLong("cellId", 0)
		data.getDocument("position", position)
		terrain = Terrain.valueOf(data.getString("terrain", "GONE")!!)
		name = data.getString("name", "New Waypoint")
		color = WaypointColor.Companion.valueOf(data.getInteger("color", WaypointColor.BLUE.value))
		isActive = data.getBoolean("active", true)
	}

	override val oobType: OutOfBandPackage.Type
		get() = OutOfBandPackage.Type.WAYPOINT

	override val oobPosition: Int
		get() = -3

	override fun hashCode(): Int {
		return java.lang.Long.hashCode(objectId)
	}

	override fun equals(other: Any?): Boolean {
		if (other !is WaypointPackage) return false
		return other.objectId == objectId
	}
}
