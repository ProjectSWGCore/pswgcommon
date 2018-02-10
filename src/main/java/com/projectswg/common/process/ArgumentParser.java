package com.projectswg.common.process;

import com.projectswg.common.debug.Log;

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
