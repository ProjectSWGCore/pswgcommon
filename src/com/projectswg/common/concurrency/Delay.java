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
package com.projectswg.common.concurrency;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class Delay {
	
	/**
	 * Sleeps for the specified number of nanoseconds
	 * @param nanos the number of nanoseconds to sleep
	 * @return TRUE if this operation has been interrupted
	 */
	public static boolean sleepNano(long nanos) {
		LockSupport.parkNanos(nanos);
		return isInterrupted();
	}
	
	/**
	 * Sleeps for the specified number of microseconds
	 * @param micro the number of microseconds to sleep
	 * @return TRUE if this operation has been interrupted
	 */
	public static boolean sleepMicro(long micro) {
		return sleepNano(micro * 1000);
	}
	
	/**
	 * Sleeps for the specified number of milliseconds
	 * @param milli the number of milliseconds to sleep
	 * @return TRUE if this operation has been interrupted
	 */
	public static boolean sleepMilli(long milli) {
		return sleepNano(milli * 1000000);
	}
	
	/**
	 * Sleeps for the specified number of seconds
	 * @param sec the number of seconds to sleep
	 * @return TRUE if this operation has been interrupted
	 */
	public static boolean sleepSeconds(long sec) {
		return sleepNano(sec * 1000000000);
	}
	
	/**
	 * Sleeps for the specified amount of time
	 * @param time the amount of time to sleep
	 * @param unit the unit of time
	 * @return TRUE if this operation has been interrupted
	 */
	public static boolean sleep(long time, TimeUnit unit) {
		return sleepNano(unit.toNanos(time));
	}
	
	/**
	 * Returns whether or not this thread has been interrupted
	 * @return TRUE if interrupted, FALSE otherwise
	 */
	public static boolean isInterrupted() {
		return Thread.currentThread().isInterrupted();
	}
	
	/**
	 * Clears the interrupted flag so future calls to isInterrupted will return
	 * FALSE
	 */
	public static void clearInterrupted() {
		Thread.interrupted();
	}
	
}
