/***********************************************************************************
* Copyright (c) 2015 /// Project SWG /// www.projectswg.com                        *
*                                                                                  *
* ProjectSWG is the first NGE emulator for Star Wars Galaxies founded on           *
* July 7th, 2011 after SOE announced the official shutdown of Star Wars Galaxies.  *
* Our goal is to create an emulator which will provide a server for players to     *
* continue playing a game similar to the one they used to play. We are basing      *
* it on the final publish of the game prior to end-game events.                    *
*                                                                                  *
* This file is part of Holocore.                                                   *
*                                                                                  *
* -------------------------------------------------------------------------------- *
*                                                                                  *
* Holocore is free software: you can redistribute it and/or modify                 *
* it under the terms of the GNU Affero General Public License as                   *
* published by the Free Software Foundation, either version 3 of the               *
* License, or (at your option) any later version.                                  *
*                                                                                  *
* Holocore is distributed in the hope that it will be useful,                      *
* but WITHOUT ANY WARRANTY; without even the implied warranty of                   *
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                    *
* GNU Affero General Public License for more details.                              *
*                                                                                  *
* You should have received a copy of the GNU Affero General Public License         *
* along with Holocore.  If not, see <http://www.gnu.org/licenses/>.                *
*                                                                                  *
***********************************************************************************/
package com.projectswg.common.debug;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Log {
	
	private static Log INSTANCE = null;
	
	private final List<LogWrapper> wrappers;
	private final Lock logLock;
	private final DateFormat timeFormat;
	
	private Log() {
		this.wrappers = new ArrayList<>();
		this.logLock = new ReentrantLock(true);
		this.timeFormat = new SimpleDateFormat("dd-MM-yy HH:mm:ss.SSS");
	}
	
	private void logAddWrapper(LogWrapper wrapper) {
		wrappers.add(wrapper);
	}
	
	private void logImplementation(LogLevel level, String str, Object ... args) {
		String date = timeFormat.format(System.currentTimeMillis());
		String logStr;
		if (args.length == 0)
			logStr = date + ' ' + level.getChar() + ": " + str;
		else
			logStr = date + ' ' + level.getChar() + ": " + String.format(str, args);
		for (LogWrapper wrapper : wrappers) {
			wrapper.onLog(level, logStr);
		}
	}
	
	private void lock() {
		logLock.lock();
	}
	
	private void unlock() {
		logLock.unlock();
	}
	
	private static synchronized final Log getInstance() {
		if (INSTANCE == null)
			INSTANCE = new Log();
		return INSTANCE;
	}
	
	public static final void addWrapper(LogWrapper wrapper) {
		getInstance().logAddWrapper(wrapper);
	}
	
	/**
	 * Logs the string to the server log file, formatted to display the log
	 * severity, time and message.
	 * @param level the log level of this message between VERBOSE and ASSERT
	 * @param tag the tag to use for the log
	 * @param str the format string for the log
	 * @param args the string format arguments, if specified
	 */
	public static final void log(LogLevel level, String str, Object ... args) {
		try {
			getInstance().lock();
			getInstance().logImplementation(level, str, args);
		} finally {
			getInstance().unlock();
		}
	}
	
	/**
	 * Logs the string to the server log file, formatted to display the log
	 * severity as VERBOSE, as well as the time and message.
	 * @param message the format string for the log
	 * @param args the string format arguments, if specified
	 */
	public static final void v(String message, Object ... args) {
		log(LogLevel.VERBOSE, message, args);
	}
	
	/**
	 * Logs the string to the server log file, formatted to display the log
	 * severity as DEBUG, as well as the time and message.
	 * @param message the format string for the log
	 * @param args the string format arguments, if specified
	 */
	public static final void d(String message, Object ... args) {
		log(LogLevel.DEBUG, message, args);
	}
	
	/**
	 * Logs the string to the server log file, formatted to display the log
	 * severity as INFO, as well as the time and message.
	 * @param message the format string for the log
	 * @param args the string format arguments, if specified
	 */
	public static final void i(String message, Object ... args) {
		log(LogLevel.INFO, message, args);
	}
	
	/**
	 * Logs the string to the server log file, formatted to display the log
	 * severity as WARN, as well as the time and message.
	 * @param message the format string for the log
	 * @param args the string format arguments, if specified
	 */
	public static final void w(String message, Object ... args) {
		log(LogLevel.WARN, message, args);
	}
	
	
	/**
	 * Logs the exception to the server log file, formatted to display the log
	 * severity as WARN, as well as the time, and tag.
	 * @param exception the exception to print
	 */
	public static final void w(Throwable exception) {
		printException(LogLevel.WARN, exception);
	}
	
	/**
	 * Logs the string to the server log file, formatted to display the log
	 * severity as ERROR, as well as the time and message.
	 * @param tag the tag to use for the log
	 * @param message the format string for the log
	 * @param args the string format arguments, if specified
	 */
	public static final void e(String message, Object ... args) {
		log(LogLevel.ERROR, message, args);
	}
	
	/**
	 * Logs the exception to the server log file, formatted to display the log
	 * severity as ERROR, as well as the time, and tag.
	 * @param exception the exception to print
	 */
	public static final void e(Throwable exception) {
		printException(LogLevel.ERROR, exception);
	}
	
	/**
	 * Logs the string to the server log file, formatted to display the log
	 * severity as ASSERT, as well as the time and message.
	 * @param message the format string for the log
	 * @param args the string format arguments, if specified
	 */
	public static final void a(String message, Object ... args) {
		log(LogLevel.ASSERT, message, args);
	}
	
	/**
	 * Logs the exception to the server log file, formatted to display the log
	 * severity as ASSERT, as well as the time, and tag.
	 * @param exception the exception to print
	 */
	public static final void a(Throwable exception) {
		printException(LogLevel.ASSERT, exception);
	}
	
	private static final void printException(LogLevel level, Throwable exception) {
		Log instance = getInstance();
		try {
			String header1 = String.format("Exception in thread \"%s\" %s: %s", Thread.currentThread().getName(), exception.getClass().getName(), exception.getMessage());
			String header2 = String.format("Caused by: %s: %s", exception.getClass().getCanonicalName(), exception.getMessage());
			StackTraceElement [] elements = exception.getStackTrace();
			instance.lock();
			instance.logImplementation(level, header1);
			instance.logImplementation(level, header2);
			for (StackTraceElement e : elements) {
				instance.logImplementation(level, "    " + e.toString());
			}
		} finally {
			instance.unlock();
		}
	}
	
	public static enum LogLevel {
		VERBOSE	('V'),
		DEBUG	('D'),
		INFO	('I'),
		WARN	('W'),
		ERROR	('E'),
		ASSERT	('A');
		
		private char c;
		
		LogLevel(char c) {
			this.c = c;
		}
		
		public char getChar() { return c; }
	}
	
}
