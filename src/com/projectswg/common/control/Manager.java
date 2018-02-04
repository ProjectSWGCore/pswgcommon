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

import com.projectswg.common.concurrency.Delay;
import com.projectswg.common.debug.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A Manager is a class that will manage services, and generally controls the program as a whole
 */
public abstract class Manager extends Service {
	
	private final List<Service> initialized;
	private final List<Service> started;
	private final List<Service> children;
	
	public Manager() {
		initialized = new CopyOnWriteArrayList<>();
		started = new CopyOnWriteArrayList<>();
		children = new CopyOnWriteArrayList<>();
	}
	
	/**
	 * Initializes this manager. If the manager returns false on this method then the initialization failed and may not work as intended. This will
	 * initialize all children automatically.
	 *
	 * @return TRUE if initialization was successful, FALSE otherwise
	 */
	@Override
	public boolean initialize() {
		boolean success = super.initialize();
		for (Service child : children) {
			try {
				if (!child.initialize()) {
					Log.e("%s failed to initialize!", child.getClass().getSimpleName());
					success = false;
					break;
				}
				initialized.add(child);
			} catch (Throwable t) {
				Log.e("%s failed to initialize!", child.getClass().getSimpleName());
				Log.e(t);
				success = false;
				break;
			}
		}
		return success;
	}
	
	/**
	 * Starts this manager. If the manager returns false on this method then the manger failed to start and may not work as intended. This will start
	 * all children automatically.
	 *
	 * @return TRUE if starting was successful, FALSE otherwise
	 */
	@Override
	public boolean start() {
		boolean success = super.start();
		for (Service child : children) {
			try {
				if (!child.start()) {
					Log.e("%s failed to start!", child.getClass().getSimpleName());
					success = false;
					break;
				}
				started.add(child);
			} catch (Throwable t) {
				Log.e("%s failed to start!", child.getClass().getSimpleName());
				Log.e(t);
				success = false;
				break;
			}
		}
		return success;
	}
	
	/**
	 * Stops this manager. If the manager returns false on this method then the manger failed to stop and may not have fully locked down. This will
	 * start all children automatically.
	 *
	 * @return TRUE if stopping was successful, FALSE otherwise
	 */
	@Override
	public boolean stop() {
		boolean success = super.stop();
		for (Service child : started) {
			try {
				if (!child.stop()) {
					Log.e("%s failed to stop!", child.getClass().getSimpleName());
					success = false;
				}
			} catch (Throwable t) {
				Log.e("%s failed to stop!", child.getClass().getSimpleName());
				Log.e(t);
				success = false;
			}
		}
		return success;
	}
	
	/**
	 * Terminates this manager. If the manager returns false on this method then the manager failed to shut down and resources may not have been
	 * cleaned up. This will terminate all children automatically.
	 *
	 * @return TRUE if termination was successful, FALSE otherwise
	 */
	@Override
	public boolean terminate() {
		boolean success = super.terminate();
		for (Service child : initialized) {
			try {
				if (!child.terminate()) {
					Log.e("%s failed to terminate!", child.getClass().getSimpleName());
					success = false;
				}
			} catch (Throwable t) {
				Log.e("%s failed to terminate!", child.getClass().getSimpleName());
				Log.e(t);
				success = false;
			}
		}
		return success;
	}
	
	/**
	 * Determines whether or not this manager is operational
	 *
	 * @return TRUE if this manager is operational, FALSE otherwise
	 */
	@Override
	public boolean isOperational() {
		for (Service child : children) {
			if (!child.isOperational())
				return false;
		}
		return true;
	}
	
	/**
	 * Adds a child to the manager's list of children. This creates a tree of managers that allows information to propogate freely through the network
	 * in an easy way.
	 *
	 * @param service the service to add as a child.
	 */
	public void addChildService(Service service) {
		Objects.requireNonNull(service, "service");
		if (children.contains(service))
			return;
		children.add(service);
		IntentManager manager = getIntentManager();
		if (manager != null)
			service.setIntentManager(manager);
	}
	
	/**
	 * Removes the sub-manager from the list of children
	 *
	 * @param service the service to remove
	 */
	public void removeChildService(Service service) {
		Objects.requireNonNull(service, "service");
		children.remove(service);
	}
	
	/**
	 * Returns a copied ArrayList of the children of this manager
	 *
	 * @return a copied ArrayList of the children of this manager
	 */
	public List<Service> getManagerChildren() {
		return new ArrayList<>(children);
	}
	
	@Override
	public void setIntentManager(IntentManager intentManager) {
		super.setIntentManager(intentManager);
		for (Service s : children) {
			s.setIntentManager(intentManager);
		}
	}
	
	public static void startManager(Manager manager) {
		Log.i("Initializing...");
		if (!manager.initialize()) {
			Log.e("Failed to initialize!");
			terminateManager(manager);
			return;
		}
		Log.i("Initialized.");
		if (!manager.start()) {
			Log.e("Failed to start!");
			stopManager(manager);
			return;
		}
		Log.i("Started.");
		while (manager.isOperational()) {
			if (Delay.sleepMilli(50))
				break;
		}
		stopManager(manager);
	}
	
	private static void stopManager(Manager manager) {
		Log.i("Stopping...");
		manager.stop();
		Log.i("Stopped.");
		terminateManager(manager);
	}
	
	private static void terminateManager(Manager manager) {
		Log.i("Terminating...");
		manager.terminate();
		Log.i("Terminated.");
	}
	
}
