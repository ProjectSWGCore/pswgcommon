/***********************************************************************************
 * Copyright (c) 2018 /// Project SWG /// www.projectswg.com                       *
 *                                                                                 *
 * ProjectSWG is the first NGE emulator for Star Wars Galaxies founded on          *
 * July 7th, 2011 after SOE announced the official shutdown of Star Wars Galaxies. *
 * Our goal is to create an emulator which will provide a server for players to    *
 * continue playing a game similar to the one they used to play. We are basing     *
 * it on the final publish of the game prior to end-game events.                   *
 *                                                                                 *
 * This file is part of PSWGCommon.                                                *
 *                                                                                 *
 * --------------------------------------------------------------------------------*
 *                                                                                 *
 * PSWGCommon is free software: you can redistribute it and/or modify              *
 * it under the terms of the GNU Affero General Public License as                  *
 * published by the Free Software Foundation, either version 3 of the              *
 * License, or (at your option) any later version.                                 *
 *                                                                                 *
 * PSWGCommon is distributed in the hope that it will be useful,                   *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                  *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                   *
 * GNU Affero General Public License for more details.                             *
 *                                                                                 *
 * You should have received a copy of the GNU Affero General Public License        *
 * along with PSWGCommon.  If not, see <http://www.gnu.org/licenses/>.             *
 ***********************************************************************************/

package com.projectswg.common.utilities;

import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

public class LocalUtilities {
	
	private static final AtomicReference<String> APP_NAME = new AtomicReference<>(".projectswg");
	
	public static void setApplicationName(String name) {
		APP_NAME.set(name);
	}
	
	public static File getSubApplicationDirectory(String ... names) {
		File dir = getApplicationDirectory();
		for (String name : names) {
			dir = new File(dir, name);
			if (!dir.exists())
				dir.mkdir();
		}
		return dir;
	}
	
	public static File getApplicationDirectory() {
		String home = getHomeDirectory();
		if (home == null)
			throw new IllegalStateException("Unknown home directory for OS '"+System.getProperty("os.name")+"'");
		
		File dir = new File(home, APP_NAME.get());
		if (!dir.exists())
			dir.mkdirs();
		return dir;
	}
	
	private static String getHomeDirectory() {
		String os = System.getProperty("os.name").toUpperCase();
		
		if (os.contains("WIN"))
			return System.getenv("APPDATA");
		
		if (os.contains("MAC"))
			return System.getProperty("user.home") + "/Library/Application Support";
		
		if (os.contains("NUX"))
			return System.getProperty("user.home");
		
		return System.getProperty("user.dir");
	}
	
}
