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

package com.projectswg.common.data.encodables.oob.waypoint;

import com.projectswg.common.data.EnumLookup;

public enum WaypointColor {
	BLUE		(1, "blue"),
	GREEN		(2, "green"),
	ORANGE		(3, "orange"),
	YELLOW		(4, "yellow"),
	PURPLE		(5, "purple"),
	WHITE		(6, "white"),
	MULTICOLOR	(7, "multicolor");
	
	private static final EnumLookup<String, WaypointColor> NAME_LOOKUP = new EnumLookup<>(WaypointColor.class, WaypointColor::getName);
	private static final EnumLookup<Integer, WaypointColor> VALUE_LOOKUP = new EnumLookup<>(WaypointColor.class, WaypointColor::getValue);
	
	private final String name;
	private final int i;
	
	WaypointColor(int i, String name) {
		this.name = name;
		this.i = i;
	}
	
	public String getName() {
		return name;
	}
	
	public int getValue() {
		return i;
	}
	
	public static WaypointColor valueOf(int colorId) {
		return VALUE_LOOKUP.getEnum(colorId, WaypointColor.BLUE);
	}
	
	public static WaypointColor fromString(String str) {
		return NAME_LOOKUP.getEnum(str, WaypointColor.BLUE);
	}
	
}
