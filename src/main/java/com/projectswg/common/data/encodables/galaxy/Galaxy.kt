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
package com.projectswg.common.data.encodables.galaxy

import java.time.ZoneOffset
import java.util.*
import java.util.concurrent.TimeUnit

class Galaxy {
	@get:Synchronized
	@set:Synchronized
	var id: Int = 0

	@get:Synchronized
	@set:Synchronized
	var name: String = ""

	@get:Synchronized
	@set:Synchronized
	var address: String = ""

	@get:Synchronized
	@set:Synchronized
	var zonePort: Int = 44463

	@get:Synchronized
	@set:Synchronized
	var pingPort: Int = 44462

	@get:Synchronized
	@set:Synchronized
	var population: Int = 0

	@get:Synchronized
	@set:Synchronized
	var status: GalaxyStatus = GalaxyStatus.DOWN
	private var zoneOffset: ZoneOffset? = null

	@get:Synchronized
	@set:Synchronized
	var maxCharacters: Int = 0

	@get:Synchronized
	@set:Synchronized
	var onlinePlayerLimit: Int = 0

	@get:Synchronized
	@set:Synchronized
	var onlineFreeTrialLimit: Int = 0

	@get:Synchronized
	@set:Synchronized
	var adminServerPort: Int = 0

	@get:Synchronized
	@set:Synchronized
	var isRecommended: Boolean = true

	@get:Synchronized
	val distance: Int
		get() = zoneOffset!!.totalSeconds - TimeUnit.SECONDS.convert(TimeZone.getDefault().dstSavings.toLong(), TimeUnit.MILLISECONDS).toInt()

	@get:Synchronized
	val populationStatus: Int
		get() {
			if (population < VERY_LIGHT) return 0
			else if (population < LIGHT) return 1
			else if (population < MEDIUM) return 2
			else if (population < HEAVY) return 3
			else if (population < VERY_HEAVY) return 4
			else if (population < EXTREMELY_HEAVY) return 5
			return 6
		}

	@Synchronized
	fun setZoneOffset(zoneOffset: ZoneOffset?) {
		this.zoneOffset = zoneOffset
	}

	@Synchronized
	fun incrementPopulationCount() {
		population++
	}

	@Synchronized
	fun decrementPopulationCount() {
		population--
	}

	override fun toString(): String {
		return String.format(
			"Galaxy[ID=%d Name=%s Address=%s Zone=%d Ping=%d Pop=%d PopStat=%d Status=%s Time=%s Max=%d Rec=%b]", id, name, address, zonePort, pingPort, population, populationStatus, status, zoneOffset!!.id, maxCharacters, isRecommended
		)
	}

	fun setStatus(status: Int) {
		for (gs in GalaxyStatus.entries) {
			if (gs.status.toInt() == status) {
				this.status = gs
				return
			}
		}
	}

	enum class GalaxyStatus(status: Int) {
		DOWN(0x00),
		LOADING(0x01),
		UP(0x02),
		LOCKED(0x03),
		RESTRICTED(0x04),
		FULL(0x05);

		val status: Byte = status.toByte()
	}

	companion object {
		// Population status values. Values are in percent.
		private const val VERY_LIGHT = 10.0
		private const val LIGHT = 20.0
		private const val MEDIUM = 30.0
		private const val HEAVY = 40.0
		private const val VERY_HEAVY = 50.0
		private const val EXTREMELY_HEAVY = 100.0
	}
}
