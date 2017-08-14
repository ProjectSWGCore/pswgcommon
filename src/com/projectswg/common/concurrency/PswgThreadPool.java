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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.projectswg.common.debug.Assert;
import com.projectswg.common.debug.Log;
import com.projectswg.common.utilities.ThreadUtilities;

public class PswgThreadPool {
	
	private static final Runnable END_OF_QUEUE = () -> {};
	
	private final AtomicBoolean running;
	private final int nThreads;
	private final String nameFormat;
	private PswgThreadExecutor executor;
	
	public PswgThreadPool(int nThreads, String nameFormat) {
		this.running = new AtomicBoolean(false);
		this.nThreads = nThreads;
		this.nameFormat = nameFormat;
		this.executor = null;
	}
	
	public void start() {
		Assert.test(!running.getAndSet(true), "PswgThreadPool has already been started!");
		executor = new PswgThreadExecutor(nThreads, ThreadUtilities.newThreadFactory(nameFormat));
		executor.start();
	}
	
	public void stop(boolean interrupt) {
		Assert.test(running.getAndSet(false), "PswgThreadPool has already been stopped!");
		executor.stop(interrupt);
	}
	
	public boolean awaitTermination(long timeout) {
		Assert.notNull(executor, "Executor hasn't been started yet!");
		return executor.awaitTermination(timeout);
	}
	
	public int getQueuedTasks() {
		return executor.getQueuedTasks();
	}
	
	public boolean execute(Runnable runnable) {
		Assert.notNull(executor, "Executor hasn't been started yet!");
		return executor.execute(runnable);
	}
	
	public boolean isRunning() {
		return running.get();
	}
	
	private static class PswgThreadExecutor {
		
		private final AtomicInteger runningThreads;
		private final BlockingQueue<Runnable> tasks;
		private final List<Thread> threads;
		private final int nThreads;
		
		public PswgThreadExecutor(int nThreads, ThreadFactory threadFactory) {
			this.runningThreads = new AtomicInteger(0);
			this.tasks = new LinkedBlockingQueue<>();
			this.threads = new ArrayList<>(nThreads);
			this.nThreads = nThreads;
			for (int i = 0; i < nThreads; i++) {
				threads.add(threadFactory.newThread(this::threadExecutor));
			}
		}
		
		public void start() {
			runningThreads.set(nThreads);
			for (Thread t : threads) {
				t.start();
			}
		}
		
		public void stop(boolean interrupt) {
			for (int i = 0; i < nThreads; i++) {
				tasks.add(END_OF_QUEUE);
			}
			if (interrupt) {
				for (Thread t : threads) {
					t.interrupt();
				}
			}
		}
		
		public int getQueuedTasks() {
			return tasks.size();
		}
		
		public boolean execute(Runnable runnable) {
			return tasks.offer(runnable);
		}
		
		public boolean awaitTermination(long time) {
			try {
				synchronized (runningThreads) {
					while (runningThreads.get() > 0 && time > 0) {
						long startWait = System.nanoTime();
						runningThreads.wait(time);
						time -= (long) ((System.nanoTime() - startWait) / 1E6 + 0.5);
					}
				}
			} catch (InterruptedException e) {
				return false;
			}
			return runningThreads.get() == 0;
		}
		
		private void threadExecutor() {
			try {
				Runnable task = null;
				while (task != END_OF_QUEUE) {
					task = tasks.take();
					threadRun(task);
				}
			} catch (InterruptedException e) {
				
			} finally {
				synchronized (runningThreads) {
					runningThreads.decrementAndGet();
					runningThreads.notifyAll();
				}
			}
		}
		
		private void threadRun(Runnable r) {
			try {
				r.run();
			} catch (Throwable t) {
				Log.e(t);
			}
		}
		
	}
	
}
