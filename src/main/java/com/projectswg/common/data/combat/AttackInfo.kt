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

class AttackInfo {
	var isSuccess: Boolean = true
	var armor: Long = 0
	var rawDamage: Int = 0
	var damageType: DamageType = DamageType.KINETIC
	var elementalDamage: Int = 0
	var elementalDamageType: DamageType = DamageType.KINETIC
	var bleedDamage: Int = 0
	var criticalDamage: Int = 0
	var blockedDamage: Int = 0
	var finalDamage: Int = 0
	var hitLocation: HitLocation = HitLocation.HIT_LOCATION_BODY
	var isCrushing: Boolean = false
	var isStrikethrough: Boolean = false
	var strikethroughAmount: Double = 0.0
	var isEvadeResult: Boolean = false
	var evadeAmount: Double = 0.0
	var isBlockResult: Boolean = false
	var block: Int = 0
	var isDodge: Boolean = false
	var isParry: Boolean = false
	var isCritical: Boolean = false
	var isGlancing: Boolean = false
	var isProc: Boolean = false
}
