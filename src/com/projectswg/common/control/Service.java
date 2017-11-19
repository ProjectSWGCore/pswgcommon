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

import java.util.function.Consumer;

/**
 * A Service is a class that does a specific job for the application
 */
public abstract class Service {
	
	private IntentManager intentManager;
	
	public Service() {
		this.intentManager = IntentManager.getInstance();
	}
	
	/**
	 * Initializes this service. If the service returns false on this method
	 * then the initialization failed and may not work as intended.
	 * @return TRUE if initialization was successful, FALSE otherwise
	 */
	public boolean initialize() {
		return true;
	}
	
	/**
	 * Starts this service. If the service returns false on this method then
	 * the service failed to start and may not work as intended.
	 * @return TRUE if starting was successful, FALSE otherwise
	 */
	public boolean start() {
		return true;
	}
	
	/**
	 * Stops the service. If the service returns false on this method then the
	 * service failed to stop and may not have fully locked down.
	 * @return TRUE if stopping was successful, FALSe otherwise
	 */
	public boolean stop() {
		return true;
	}
	
	/**
	 * Terminates this service. If the service returns false on this method
	 * then the service failed to shut down and resources may not have been
	 * cleaned up.
	 * @return TRUE if termination was successful, FALSE otherwise
	 */
	public boolean terminate() {
		IntentManager im = IntentManager.getInstance();
		if (im != null)
			im.terminate();
		return true;
	}
	
	/**
	 * Determines whether or not this service is operational
	 * @return TRUE if this service is operational, FALSE otherwise
	 */
	public boolean isOperational() {
		return true;
	}
	
	/**
	 * Registers for the intent using the specified consumer
	 * @param c the class of intent to register for
	 * @param consumer the consumer to run when the intent is fired
	 */
	protected <T extends Intent> void registerForIntent(Class<T> c, Consumer<T> consumer) {
		intentManager.registerForIntent(c, consumer);
	}
	
	/**
	 * Unregisters for the intent using the specified consumer
	 * @param c the class of intent to unregister
	 * @param consumer the consumer that was previous registered
	 */
	protected <T extends Intent> void unregisterForIntent(Class<T> c, Consumer<T> consumer) {
		intentManager.unregisterForIntent(c, consumer);
	}
	
	public void setIntentManager(IntentManager intentManager) {
		if (intentManager == null)
			throw new NullPointerException("IntentManager cannot be null!");
		this.intentManager = intentManager;
	}
	
	public IntentManager getIntentManager() {
		return intentManager;
	}
	
}
