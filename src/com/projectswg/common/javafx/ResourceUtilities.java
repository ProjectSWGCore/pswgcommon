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
package com.projectswg.common.javafx;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicReference;

public class ResourceUtilities {

	private static final AtomicReference<String> THEME = new AtomicReference<>(".");
	private static final AtomicReference<Class<?>> SRC = new AtomicReference<>(ResourceUtilities.class);
	private static final String RESOURCES_PATH = "/res/";
	
	public static void setTheme(String theme) {
		THEME.set(theme);
	}
	
	public static void setPrimarySource(Class<?> c) {
		SRC.set(c);
	}
	
	public static File getSourceDirectory() {
		try {
			return new File(SRC.get().getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
		} catch (URISyntaxException e) {
			return null;
		}
	}
	
	public static File getResourcesDirectory() {
		File source = getSourceDirectory();
		if (source == null)
			return null;
		return new File(source, RESOURCES_PATH);
	}
	
	public static URI getResourceURI(String path) {
		File file = getResource(path);
		if (file == null)
			return null;
		return file.toURI();
	}
	
	public static File getResource(String path) {
		try {
			String parent = new File(SRC.get().getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
			return new File(parent, cleanupPath(RESOURCES_PATH + THEME.get() + '/' + path));
		} catch (URISyntaxException e) {
			return null;
		}
	}
	
	public static URL getClassResource(String path) {
		return SRC.get().getClassLoader().getResource(path);
	}
	
	public static File getGeneralResource(String path) {
		try {
			String parent = new File(SRC.get().getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
			return new File(parent, cleanupPath(RESOURCES_PATH + "common/" + path));
		} catch (URISyntaxException e) {
			return null;
		}
	}
	
	private static String cleanupPath(String path) {
		return path.replace('/', File.separatorChar);
	}
	
}
