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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import com.projectswg.common.concurrency.PswgThreadPool;
import com.projectswg.common.debug.Log;

public class IntentManager {
	
	private static final AtomicReference<IntentManager> INSTANCE = new AtomicReference<>(null);
	
	private final Map <Class<Intent>, List<Consumer<Intent>>> intentRegistrations;
	private final IntentSpeedRecorder speedRecorder;
	private final PswgThreadPool processThreads;
	private final AtomicBoolean initialized;
	
	public IntentManager(int threadCount) {
		this.intentRegistrations = new HashMap<>();
		this.speedRecorder = new IntentSpeedRecorder();
		this.processThreads = new PswgThreadPool(threadCount, "intent-processor-%d");
		this.initialized = new AtomicBoolean(false);
	}
	
	public void initialize() {
		if (initialized.getAndSet(true))
			return;
		processThreads.start();
	}
	
	public void terminate() {
		if (!initialized.getAndSet(false))
			return;
		processThreads.stop(true);
	}
	
	public int getIntentCount() {
		return processThreads.getQueuedTasks();
	}
	
	public IntentSpeedRecorder getSpeedRecorder() {
		return speedRecorder;
	}
	
	public void broadcastIntent(Intent i) {
		if (i == null)
			throw new NullPointerException("Intent cannot be null!");
		broadcast(i);
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
		if (receivers == null)
			return;
		
		AtomicInteger remaining = new AtomicInteger(receivers.size());
		for (Consumer<Intent> r : receivers) {
			broadcast(r, i, remaining);
		}
	}
	
	private void broadcast(Consumer<Intent> r, Intent i, AtomicInteger remaining) {
		processThreads.execute(() -> {
			try {
				long start = System.nanoTime();
				r.accept(i);
				long time = System.nanoTime() - start;
				speedRecorder.addRecord(i.getClass(), r, time);
			} catch (Throwable t) {
				Log.e("Fatal Exception while processing intent: " + i);
				Log.e(t);
			} finally {
				if (remaining.decrementAndGet() <= 0)
					i.markAsComplete(this);
			}
		});
	}
	
	public static IntentManager getInstance() {
		return INSTANCE.get();
	}
	
	public static void setInstance(IntentManager intentManager) {
		IntentManager prev = INSTANCE.getAndSet(intentManager);
		if (prev != null)
			prev.terminate();
	}
	
	public static class IntentSpeedRecorder {
		
		private final Map<Consumer<Intent>, IntentSpeedRecord> times;
		
		public IntentSpeedRecorder() {
			this.times = new HashMap<>();
		}
		
		private void addRecord(Class<? extends Intent> intent, Consumer<Intent> consumer, long timeNanos) {
			IntentSpeedRecord record;
			synchronized (times) {
				record = times.get(consumer);
				if (record == null)
					times.put(consumer, record = new IntentSpeedRecord(intent, consumer));
			}
			record.addTime(timeNanos);
		}
		
		public List<IntentSpeedRecord> getAllTimes() {
			synchronized (times) {
				return new ArrayList<>(times.values());
			}
		}
		
	}
	
	public static class IntentSpeedRecord implements Comparable<IntentSpeedRecord> {
		
		private final Class<? extends Intent> intent;
		private final Consumer<Intent> consumer;
		private final AtomicLong time;
		private final AtomicLong count;
		
		public IntentSpeedRecord(Class<? extends Intent> intent, Consumer<Intent> consumer) {
			this.intent = intent;
			this.consumer = consumer;
			this.time = new AtomicLong(0);
			this.count = new AtomicLong(0);
		}
		
		public Class<? extends Intent> getIntent() {
			return intent;
		}
		
		public Consumer<Intent> getConsumer() {
			return consumer;
		}
		
		public long getTime() {
			return time.get();
		}
		
		public long getCount() {
			return count.get();
		}
		
		public void addTime(long timeNanos) {
			time.addAndGet(timeNanos);
			count.incrementAndGet();
		}
		
		@Override
		public int compareTo(IntentSpeedRecord record) {
			return Long.compare(record.getTime(), getTime());
		}
		
	}
	
}
