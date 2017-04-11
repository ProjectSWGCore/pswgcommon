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
package com.projectswg.common.control;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import com.projectswg.common.concurrency.PswgTaskThreadPool;
import com.projectswg.common.debug.Log;

public class IntentManager {
	
	private static final IntentManager INSTANCE = new IntentManager();
	
	private final Map <Class<Intent>, List<Consumer<Intent>>> intentRegistrations;
	private final PswgTaskThreadPool<Intent> broadcastThreads;
	private final AtomicBoolean initialized;
	
	public IntentManager() {
		this.intentRegistrations = new HashMap<>();
		int threadCount = Runtime.getRuntime().availableProcessors() * 10;
		this.broadcastThreads = new PswgTaskThreadPool<>(threadCount, "intent-processor-%d", i -> broadcast(i));
		this.broadcastThreads.setInterruptOnStop(true);
		this.initialized = new AtomicBoolean(false);
		initialize();
	}
	
	public void initialize() {
		if (initialized.getAndSet(true))
			return;
		broadcastThreads.start();
	}
	
	public void terminate() {
		if (!initialized.getAndSet(false))
			return;
		broadcastThreads.stop();
	}
	
	public void broadcastIntent(Intent i) {
		if (i == null)
			throw new NullPointerException("Intent cannot be null!");
		broadcastThreads.addTask(i);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Intent> void registerForIntent(Class<T> c, Consumer<T> r) {
		if (r == null)
			throw new NullPointerException("Cannot register a null consumer for an intent");
		synchronized (intentRegistrations) {
			List <Consumer<Intent>> intents = intentRegistrations.get(c);
			if (intents == null) {
				intents = new CopyOnWriteArrayList<>();
				intentRegistrations.put((Class<Intent>) c, intents);
			}
			synchronized (intents) {
				intents.add((Consumer<Intent>) r);
			}
		}
	}
	
	public <T extends Intent> void unregisterForIntent(Class<T> c, Consumer<T> r) {
		if (r == null)
			throw new NullPointerException("Cannot register a null consumer for an intent");
		synchronized (intentRegistrations) {
			List <Consumer<Intent>> intents = intentRegistrations.get(c);
			if (intents == null)
				return;
			synchronized (intents) {
				intents.remove(r);
			}
		}
	}
	
	private void broadcast(Intent i) {
		List <Consumer<Intent>> receivers;
		synchronized (intentRegistrations) {
			receivers = intentRegistrations.get(i.getClass());
		}
		try {
			if (receivers == null)
				return;
			
			for (Consumer<Intent> r : receivers) {
				broadcast(r, i);
			}
		} finally {
			i.markAsComplete(this);
		}
	}
	
	private void broadcast(Consumer<Intent> r, Intent i) {
		try {
			r.accept(i);
		} catch (Throwable t) {
			Log.e("Fatal Exception while processing intent: " + i);
			Log.e(t);
		}
	}
	
	public static IntentManager getInstance() {
		return INSTANCE;
	}
	
}
