/***********************************************************************************
 * Copyright (c) 2018 /// Project SWG /// www.projectswg.com                       *
 *                                                                                 *
 * ProjectSWG is the first NGE emulator for Star Wars Galaxies founded on          *
 * July 7th, 2011 after SOE announced the official shutdown of Star Wars Galaxies. *
 * Our goal is to create an emulator which will provide a server for players to    *
 * continue playing a game similar to the one they used to play. We are basing     *
 * it on the final publish of the game prior to end-game events.                   *
 *                                                                                 *
 * This file is part of Holocore.                                                  *
 *                                                                                 *
 * --------------------------------------------------------------------------------*
 *                                                                                 *
 * Holocore is free software: you can redistribute it and/or modify                *
 * it under the terms of the GNU Affero General Public License as                  *
 * published by the Free Software Foundation, either version 3 of the              *
 * License, or (at your option) any later version.                                 *
 *                                                                                 *
 * Holocore is distributed in the hope that it will be useful,                     *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                  *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                   *
 * GNU Affero General Public License for more details.                             *
 *                                                                                 *
 * You should have received a copy of the GNU Affero General Public License        *
 * along with Holocore.  If not, see <http://www.gnu.org/licenses/>.               *
 *                                                                                 *
 ***********************************************************************************/
package com.projectswg.common.debug;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ThreadPrinter {
	
	public static void printActiveThreads() {
		ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
		int threadCount = threadGroup.activeCount();
		Thread[] threadsRaw = new Thread[threadCount];
		threadCount = threadGroup.enumerate(threadsRaw);
		List<Thread> threads = Arrays.stream(threadsRaw, 0, threadCount).filter(t -> t.getState() != Thread.State.TERMINATED).collect(Collectors.toList());
		int maxLength = threads.stream().mapToInt(t -> t.getName().length()).max().orElse(4);
		if (maxLength < 4)
			maxLength = 4;
		
		Log.w("Active Threads: %d", threads.size());
		Log.w("+-%s---%s-+", createRepeatingDash(maxLength), createRepeatingDash(13));
		Log.w("| %-" + maxLength + "s | %-13s |", "Name", "State");
		Log.w("+-%s-+-%s-+", createRepeatingDash(maxLength), createRepeatingDash(13));
		for (Thread t : threads) {
			Log.w("| %-" + maxLength + "s | %-13s |", t.getName(), t.getState());
		}
		Log.w("+-%s---%s-+", createRepeatingDash(maxLength), createRepeatingDash(13));
	}
	
	private static String createRepeatingDash(int count) {
		return String.join("", Collections.nCopies(count, "-"));
	}
	
}
