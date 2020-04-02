/***********************************************************************************
 * Copyright (c) 2018 /// Project SWG /// www.projectswg.com                       *
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
package com.projectswg.common.data.combat;

import com.projectswg.common.data.EnumLookup;

/**
 * Determines color for entries in the combat log client-side
 */
public enum CombatSpamType {
	MISS(0),
	HIT(1),
	BLOCK(2),
	EVADE(3),
	REDIRECT(4),
	COUNTER(5),
	FUMBLE(6),
	LIGHTSABER_BLOCK(7),
	LIGHTSABER_COUNTER(8),
	LIGHTSABER_COUNTER_TARGET(9),
	GENERIC(10),
	OUT_OF_RANGE(11),
	POSTURE_CHANGE(12),
	TETHERED(13),	// AI was forced to return to original location
	MEDICAL(14),
	BUFF(15),
	DEBUFF(16);
	
	private static final EnumLookup<Integer, CombatSpamType> LOOKUP = new EnumLookup<>(CombatSpamType.class, CombatSpamType::getNum);
	
	private int num;
	
	CombatSpamType(int num) {
		this.num = num;
	}
	
	public int getNum() {
		return num;
	}
	
	public static CombatSpamType getCombatSpamType(int num) {
		return LOOKUP.getEnum(num, HIT);
	}
	
}
