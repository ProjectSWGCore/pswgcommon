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

import com.projectswg.common.data.EnumLookup

enum class Posture(id: Int) {
	UPRIGHT(0x00),
	CROUCHED(0x01),
	PRONE(0x02),
	SNEAKING(0x03),
	BLOCKING(0x04),
	CLIMBING(0x05),
	FLYING(0x06),
	LYING_DOWN(0x07),
	SITTING(0x08),
	SKILL_ANIMATING(0x09),
	DRIVING_VEHICLE(0x0A),
	RIDING_CREATURE(0x0B),
	KNOCKED_DOWN(0x0C),
	INCAPACITATED(0x0D),
	DEAD(0x0E),
	INVALID(0xFF);

	val id: Byte = id.toByte()

	companion object {
		private val LOOKUP = EnumLookup(Posture::class.java) { obj: Posture -> obj.id }
		fun getFromId(id: Byte): Posture {
			return LOOKUP.getEnum(id, INVALID)
		}
	}
}
