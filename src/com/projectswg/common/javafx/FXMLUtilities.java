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
import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import com.projectswg.common.debug.Log;

import javafx.fxml.FXMLLoader;

public class FXMLUtilities {
	
	private static final Set<FXMLController> CONTROLLERS = new HashSet<>();
	
	public static void terminate() {
		synchronized (CONTROLLERS) {
			Log.i("Terminating FXML controllers");
			for (FXMLController controller : CONTROLLERS) {
				controller.terminate();
			}
			CONTROLLERS.clear();
		}
	}
	
	public static ResourceBundle getResourceBundle(Locale locale) {
		return ResourceBundle.getBundle("bundles.strings.strings", locale);
	}
	
	public static void onFxmlLoaded(FXMLController controller) {
		synchronized (CONTROLLERS) {
			CONTROLLERS.add(controller);
		}
	}
	
	public static FXMLController loadFxml(String fxml, Locale locale) {
		try {
			File file = ResourceUtilities.getResource("fxml/" + fxml);
			if (file == null || !file.isFile()) {
				Log.e("Unable to load fxml - doesn't exist: %s", file);
				return null;
			}
			Log.i("Loading fxml: %s", file);
			FXMLLoader fxmlLoader = new FXMLLoader(file.toURI().toURL());
			fxmlLoader.setResources(getResourceBundle(locale));
			fxmlLoader.load();
			onFxmlLoaded(fxmlLoader.getController());
			synchronized (CONTROLLERS) {
				CONTROLLERS.add(fxmlLoader.getController());
			}
			return fxmlLoader.getController();
		} catch (IOException e) {
			Log.e("Error loading fmxl: %s", fxml);
			Log.e(e);
			return null;
		}
	}
	
}
