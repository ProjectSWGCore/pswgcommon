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

package com.projectswg.common.data;

import java.util.HashMap;
import java.util.Map;

public class EnumLookup<K, T extends Enum<?>> {
	
	private final Map<K, T> lookup;
	
	public EnumLookup(Class<T> c, CustomLookupAdder<K, T> adder) {
		lookup = new HashMap<>();
		for (T t : c.getEnumConstants()) {
			lookup.put(adder.getKey(t), t);
		}
	}
	
	public T getEnum(K k, T def) {
		T t = lookup.get(k);
		if (t == null)
			return def;
		return t;
	}
	
	public boolean containsEnum(K k) {
		return lookup.containsKey(k);
	}
	
	public int size() {
		return lookup.size();
	}
	
	public interface CustomLookupAdder<K, T> {
		K getKey(T t);
	}
	
}
