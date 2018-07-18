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

package com.projectswg.common.process;

import me.joshlarson.jlcommon.log.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public class ArgumentParser {
	
	public static Map<String, String> parseArguments(String [] args, Function<String, String> renameArgument, Predicate<String>expectsArgument) {
		Map<String, String> arguments = new HashMap<>();
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			int equalIndex = arg.indexOf('=');
			
			String key = arg;
			String value = null;
			if (equalIndex != -1)
				key = arg.substring(0, equalIndex);
			key = renameArgument.apply(key);
			
			if (expectsArgument.test(key)) {
				if (equalIndex == -1) { // seek ahead for argument
					if (i + 1 < args.length)
						value = args[i+1];
				} else { // parse out argument
					value = arg.substring(equalIndex+1);
				}
			}
			arguments.put(key, value);
		}
		return arguments;
	}
	
	public static void requireValue(Map<String, String> arguments, String key) {
		if (!arguments.containsKey(key) || arguments.get(key) == null) {
			throw new IllegalArgumentException(key + " - no argument");
		}
	}
	
	public static void requireShort(Map<String, String> arguments, String key) {
		requireValue(arguments, key);
		try {
			Short.parseShort(arguments.get(key));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(key + " - invalid argument");
		}
	}
	
	public static void requireInt(Map<String, String> arguments, String key) {
		requireValue(arguments, key);
		try {
			Integer.parseInt(arguments.get(key));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(key + " - invalid argument");
		}
	}
	
	public static void requireLong(Map<String, String> arguments, String key) {
		requireValue(arguments, key);
		try {
			Long.parseLong(arguments.get(key));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(key + " - invalid argument");
		}
	}
	
	public static int parseShort(String str, int defaultValue) {
		try {
			return Short.parseShort(str);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
	
	public static int parseInt(String str, int defaultValue) {
		try {
			return Integer.parseInt(str);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
	
	public static long parseLong(String str, long defaultValue) {
		try {
			return Long.parseLong(str);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
	
}
