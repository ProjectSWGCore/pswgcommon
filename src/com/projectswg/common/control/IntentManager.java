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

import com.projectswg.common.concurrency.PswgThreadPool;
import com.projectswg.common.debug.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class IntentManager {
	
	private static final AtomicReference<IntentManager> INSTANCE = new AtomicReference<>(null);
	
	private final Map <Class<Intent>, List<Consumer<Intent>>> intentRegistrations;
	private final IntentSpeedRecorder speedRecorder;
	private final PswgThreadPool processThreads;
	private final AtomicBoolean initialized;
	
	public IntentManager(int threadCount) {
		this.intentRegistrations = new ConcurrentHashMap<>();
		this.speedRecorder = new IntentSpeedRecorder();
		this.processThreads = new PswgThreadPool(true, threadCount, "intent-processor-%d");
		this.initialized = new AtomicBoolean(false);
		
		this.processThreads.setPriority(8);
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
		processThreads.awaitTermination(1000);
	}
	
	public int getIntentCount() {
		return processThreads.getQueuedTasks();
	}
	
	public IntentSpeedRecorder getSpeedRecorder() {
		return speedRecorder;
	}
	
	public void broadcastIntent(Intent i) {
		Objects.requireNonNull(i, "Intent cannot be null!");
		List <Consumer<Intent>> receivers = intentRegistrations.get(i.getClass());
		if (receivers == null)
			return;
		
		AtomicInteger remaining = new AtomicInteger(receivers.size());
		for (Consumer<Intent> r : receivers) {
//			processThreads.execute(() -> executeConsumer(r, i, remaining));
			processThreads.execute(new IntentRunner(r, i, remaining));
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Intent> void registerForIntent(Class<T> c, Consumer<T> r) {
		if (r == null)
			throw new NullPointerException("Cannot register a null consumer for an intent");
		List <Consumer<Intent>> intents = intentRegistrations.get(c);
		if (intents == null) {
			intents = new CopyOnWriteArrayList<>();
			List<Consumer<Intent>> replaced = intentRegistrations.putIfAbsent((Class<Intent>) c, intents);
			if (replaced != null) {
				intents = replaced;
			}
		}
		intents.add((Consumer<Intent>) r);
	}
	
	public <T extends Intent> void unregisterForIntent(Class<T> c, Consumer<T> r) {
		if (r == null)
			throw new NullPointerException("Cannot register a null consumer for an intent");
		List <Consumer<Intent>> intents = intentRegistrations.get(c);
		if (intents == null)
			return;
		intents.remove(r);
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
			this.times = new ConcurrentHashMap<>();
		}
		
		private void addRecord(Class<? extends Intent> intent, Consumer<Intent> consumer, long timeNanos) {
			IntentSpeedRecord record = times.get(consumer);
			if (record == null) {
				record = new IntentSpeedRecord(intent, consumer);
				IntentSpeedRecord replaced = times.putIfAbsent(consumer, record);
				if (replaced != null)
					record = replaced;
			}
			record.addTime(timeNanos);
		}
		
		public IntentSpeedRecord getTime(Consumer<Intent> consumer) {
			return times.get(consumer);
		}
		
		public List<IntentSpeedRecord> getAllTimes() {
			return new ArrayList<>(times.values());
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
		
		public int getPriority() {
			long time = getTime();
			long count = getCount();
			if (count == 0)
				return 0;
			return (int) (time / count / 1000);
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
	
	private class IntentRunner implements Comparable<IntentRunner>, Runnable {
		
		private final Consumer<Intent> r;
		private final Intent i;
		private final AtomicInteger remaining;
		private final int priority;
		
		public IntentRunner(Consumer<Intent> r, Intent i, AtomicInteger remaining) {
			this.r = r;
			this.i = i;
			this.remaining = remaining;
			IntentSpeedRecord record = speedRecorder.getTime(r);
			if (record == null)
				this.priority = 0;
			else
				this.priority = record.getPriority();
		}
		
		@Override
		public void run() {
			try {
				long start = System.nanoTime();
				r.accept(i);
				long time = System.nanoTime() - start;
				speedRecorder.addRecord(i.getClass(), r, time);
			} catch (Throwable t) {
				Log.e("Fatal Exception while processing intent: " + i);
				Log.e(t);
			} finally {
				if (remaining.decrementAndGet() <= 0) {
					i.markAsComplete(IntentManager.this);
					Consumer<Intent> completedCallback = i.getCompletedCallback();
					if (completedCallback != null)
						completedCallback.accept(i);
				}
			}
		}
		
		@Override
		public int compareTo(IntentRunner r) {
			return Integer.compare(priority, r.priority);
		}
		
	}
	
}
