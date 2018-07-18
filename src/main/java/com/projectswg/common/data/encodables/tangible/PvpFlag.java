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
package com.projectswg.common.data.encodables.tangible;

import java.util.EnumSet;

public enum PvpFlag {
	ATTACKABLE		(1 << 0),
	AGGRESSIVE		(1 << 1),
	OVERT			(1 << 2),
	TEF				(1 << 3),
	PLAYER			(1 << 4),
	ENEMY			(1 << 5),
	GOING_OVERT		(1 << 6),
	GOING_COVERT	(1 << 7),
	DUEL			(1 << 8);
	
	private int bitmask;
	
	PvpFlag(int bitmask) {
		this.bitmask = bitmask;
	}
	
	public int getBitmask() {
		return bitmask;
	}
	
	public static EnumSet<PvpFlag> getFlags(int bits) {
		EnumSet <PvpFlag> states = EnumSet.noneOf(PvpFlag.class);
		for (PvpFlag state : values()) {
			if ((state.getBitmask() & bits) != 0)
				states.add(state);
		}
		return states;
	}
	
}
