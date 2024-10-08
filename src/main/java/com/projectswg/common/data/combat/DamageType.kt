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
package com.projectswg.common.data.combat

import java.util.*

enum class DamageType(val num: Int) {
	KINETIC(1),
	ENERGY(2),
	BLAST(4),
	STUN(8),
	RESTAINT(16),
	ELEMENTAL_HEAT(32),
	ELEMENTAL_COLD(64),
	ELEMENTAL_ACID(128),
	ELEMENTAL_ELECTRICAL(256);

	companion object {
		private val VALUES = entries.toTypedArray()

		fun getDamageType(num: Int): DamageType {
			for (type in VALUES) {
				if ((num and type.num) != 0) return type
			}
			return KINETIC
		}

		fun getDamageTypes(num: Int): Set<DamageType> {
			val types: MutableSet<DamageType> = EnumSet.noneOf(DamageType::class.java)
			for (type in VALUES) {
				if ((num and type.num) != 0) types.add(type)
			}
			return types
		}
	}
}
