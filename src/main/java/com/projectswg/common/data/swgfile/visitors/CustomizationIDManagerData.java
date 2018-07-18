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
package com.projectswg.common.data.swgfile.visitors;

import com.projectswg.common.data.swgfile.ClientData;
import com.projectswg.common.data.swgfile.IffNode;
import com.projectswg.common.data.swgfile.SWGFile;
import me.joshlarson.jlcommon.log.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * This class loads customization variable names and their ID. The server
 * will be doing more name-to-ID-lookups, and the {@code Map} therefore has
 * the name as key and ID as value.
 */
public class CustomizationIDManagerData extends ClientData {
	
	private final Map<String, Short> customizationVariables;    // Name to index.
	
	public CustomizationIDManagerData() {
		customizationVariables = new HashMap<>();
	}
	
	@Override
	public void readIff(SWGFile iff) {
		IffNode versionForm = iff.enterNextForm();
		
		if (versionForm == null) {
			Log.e("Expected version FORM in CIDM format!");
			return;
		}
		
		int version = versionForm.getVersionFromTag();
		
		switch (version) {
			case 1:
				readVersion1(iff);
				break;
			default:
				Log.e("Unhandled CIDM version %d in file %s", version, iff.getFileName());
				break;
		}
	}
	
	/**
	 * IDs are not zero-indexed - the first ID is 1, not 0.
	 *
	 * @param id an above zero number.
	 * @return the first matching variable name, if any are found. Otherwise {@code null}.
	 */
	public String getVariableName(short id) {
		for(Map.Entry<String, Short> entrySet : customizationVariables.entrySet()) {
			String variableName = entrySet.getKey();
			short variableId = entrySet.getValue();
			
			if (id == variableId) {
				return variableName;	// Return immediately after having found the first match
			}
		}
		
		return null;
	}
	
	/**
	 *
	 * @param name the name of the customization variable to get an ID for
	 * @return the ID for this {@code name}
	 * @throws NullPointerException if {@code name} isn't mapped to an ID
	 */
	public short getVariableId(String name) {
		return customizationVariables.get(name);
	}
	
	private void readVersion1(SWGFile iff) {
		IffNode data = iff.enterChunk("DATA");
		
		data.readChunk((chunk) -> {
			short variableId = data.readShort();
			String variableName = chunk.readString();
			
			customizationVariables.put(variableName, variableId);
		});
	}
	
}
