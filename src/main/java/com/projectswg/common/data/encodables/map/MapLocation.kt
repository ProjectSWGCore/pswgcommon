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
package com.projectswg.common.data.encodables.map

import com.projectswg.common.encoding.CachedEncode
import com.projectswg.common.encoding.Encodable
import com.projectswg.common.network.NetBuffer

class MapLocation() : Encodable {
	private var id: Long = 0
	private var name: String? = null
	private var x = 0f
	private var y = 0f
	private var category: Byte = 0
	private var subcategory: Byte = 0
	private var isActive = false

	private val cache: CachedEncode

	init {
		this.cache = CachedEncode { encodeImpl() }
	}

	constructor(id: Long, name: String?, x: Float, y: Float, category: Byte, subcategory: Byte, isActive: Boolean) : this() {
		this.id = id
		this.name = name
		this.x = x
		this.y = y
		this.category = category
		this.subcategory = subcategory
		this.isActive = isActive
	}

	fun getId(): Long {
		return id
	}

	fun setId(id: Long) {
		cache.clearCached()
		this.id = id
	}

	fun getName(): String? {
		return name
	}

	fun setName(name: String?) {
		cache.clearCached()
		this.name = name
	}

	fun getX(): Float {
		return x
	}

	fun setX(x: Float) {
		cache.clearCached()
		this.x = x
	}

	fun getY(): Float {
		return y
	}

	fun setY(y: Float) {
		cache.clearCached()
		this.y = y
	}

	fun getCategory(): Byte {
		return category
	}

	fun setCategory(category: Byte) {
		cache.clearCached()
		this.category = category
	}

	fun getSubcategory(): Byte {
		return subcategory
	}

	fun setSubcategory(subcategory: Byte) {
		cache.clearCached()
		this.subcategory = subcategory
	}

	fun isActive(): Boolean {
		return isActive
	}

	fun setIsActive(isActive: Boolean) {
		cache.clearCached()
		this.isActive = isActive
	}

	override fun encode(): ByteArray {
		return cache.encode()
	}

	override fun decode(data: NetBuffer) {
		id = data.long
		name = data.unicode
		x = data.float
		y = data.float
		category = data.byte
		subcategory = data.byte
		isActive = data.boolean
	}

	private fun encodeImpl(): ByteArray {
		val data = NetBuffer.allocate(length)
		data.addLong(id)
		data.addUnicode(name)
		data.addFloat(x)
		data.addFloat(y)
		data.addByte(category.toInt())
		data.addByte(subcategory.toInt())
		data.addBoolean(isActive)
		return data.array()
	}

	override val length: Int
		get() = name!!.length * 2 + 23

	override fun toString(): String {
		return name + " x: " + x + "y: " + y
	}
}
