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
package com.projectswg.common.utilities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import me.joshlarson.jlcommon.log.Log;


public class TimeUtilities {

	private static final String DATE_TIME_FORMAT = "EEE, dd MMM yyyy HH:mm:ss z";
	private static final TimeZone CURRENT_TIMEZONE = TimeZone.getDefault();
	private static final TimeZone UTC = TimeZone.getTimeZone("UTC");
	private static final DateFormat DATE_TIME_FORMATTER_CUR = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.US);
	private static final DateFormat DATE_TIME_FORMATTER_UTC = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.US);
	
	static {
		DATE_TIME_FORMATTER_UTC.setTimeZone(UTC);
	}
	
	public static long getTime() {
		return System.currentTimeMillis() - CURRENT_TIMEZONE.getOffset(System.currentTimeMillis());
	}
	
	public static long getTime(long offset, TimeUnit unit) {
		return getTime() + unit.toMillis(offset);
	}
	
	public static String getDateStringUtc(long time) {
		synchronized (DATE_TIME_FORMATTER_UTC) {
			return DATE_TIME_FORMATTER_UTC.format(time);
		}
	}
	
	public static String getDateString(long time) {
		synchronized (DATE_TIME_FORMATTER_CUR) {
			return DATE_TIME_FORMATTER_CUR.format(time);
		}
	}
	
	public static long getTimeFromStringUtc(String str) {
		synchronized (DATE_TIME_FORMATTER_UTC) {
			try {
				return DATE_TIME_FORMATTER_UTC.parse(str).getTime();
			} catch (ParseException e) {
				Log.e(e);
				return -1;
			}
		}
	}
	
	public static long getTimeFromString(String str) {
		synchronized (DATE_TIME_FORMATTER_CUR) {
			try {
				return DATE_TIME_FORMATTER_CUR.parse(str).getTime();
			} catch (ParseException e) {
				Log.e(e);
				return -1;
			}
		}
	}
	
	public static long convertUtcToLocal(long time) {
		return time + CURRENT_TIMEZONE.getOffset(time);
	}
	
}
