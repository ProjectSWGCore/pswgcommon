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
package com.projectswg.common.callback;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.projectswg.common.concurrency.PswgThreadPool;
import com.projectswg.common.debug.Log;


public class CallbackManager<T> {
	
	private final PswgThreadPool executor;
	private final List<T> callbacks;
	private final AtomicInteger runningTasks;
	
	public CallbackManager(String name) {
		this(name, 1);
	}
	
	public CallbackManager(String name, int threadCount) {
		this.executor = new PswgThreadPool(threadCount, name);
		this.callbacks = new CopyOnWriteArrayList<>();
		this.runningTasks = new AtomicInteger(0);
	}
	
	public void addCallback(T callback) {
		callbacks.add(callback);
	}
	
	public void removeCallback(T callback) {
		callbacks.remove(callback);
	}
	
	public void setCallback(T callback) {
		callbacks.clear();
		callbacks.add(callback);
	}
	
	public void clearCallbacks() {
		callbacks.clear();
	}
	
	public void start() {
		executor.start();
	}
	
	public void stop() {
		executor.stop(false);
	}
	
	public boolean awaitTermination(long timeout, TimeUnit unit) {
		return executor.awaitTermination(unit.toMillis(timeout));
	}
	
	public boolean isRunning() {
		return executor.isRunning();
	}
	
	public boolean isQueueEmpty() {
		return runningTasks.get() == 0;
	}
	
	public boolean callOnEach(CallCallback<T> call) {
		runningTasks.incrementAndGet();
		return executor.execute(() -> {
			for (T callback : callbacks) {
				try {
					call.run(callback);
				} catch (Throwable t) {
					Log.e(t);
				}
			}
			runningTasks.decrementAndGet();
		});
	}
	
	
	public interface CallCallback<T> {
		void run(T callback);
	}
}
