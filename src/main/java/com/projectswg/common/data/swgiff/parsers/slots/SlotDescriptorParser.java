/***********************************************************************************
 * Copyright (c) 2019 /// Project SWG /// www.projectswg.com                       *
 *                                                                                 *
 * ProjectSWG is the first NGE emulator for Star Wars Galaxies founded on          *
 * July 7th, 2011 after SOE announced the official shutdown of Star Wars Galaxies. *
 * Our goal is to create an emulator which will provide a server for players to    *
 * continue playing a game similar to the one they used to play. We are basing     *
 * it on the final publish of the game prior to end-game events.                   *
 *                                                                                 *
 * This file is part of Holocore.                                                  *
 *                                                                                 *
 * --------------------------------------------------------------------------------*
 *                                                                                 *
 * Holocore is free software: you can redistribute it and/or modify                *
 * it under the terms of the GNU Affero General Public License as                  *
 * published by the Free Software Foundation, either version 3 of the              *
 * License, or (at your option) any later version.                                 *
 *                                                                                 *
 * Holocore is distributed in the hope that it will be useful,                     *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                  *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                   *
 * GNU Affero General Public License for more details.                             *
 *                                                                                 *
 * You should have received a copy of the GNU Affero General Public License        *
 * along with Holocore.  If not, see <http://www.gnu.org/licenses/>.               *
 ***********************************************************************************/
package com.projectswg.common.data.swgiff.parsers.slots;

import com.projectswg.common.data.swgiff.IffChunk;
import com.projectswg.common.data.swgiff.IffForm;
import com.projectswg.common.data.swgiff.parsers.SWGParser;

import java.util.ArrayList;
import java.util.List;

public class SlotDescriptorParser implements SWGParser {
	
	private final List<String> descriptors;
	
	public SlotDescriptorParser() {
		this.descriptors = new ArrayList<>();
	}
	
	@Override
	public void read(IffForm form) {
		assert form.getTag().equals("SLTD");
		assert form.getVersion() == 0;
		try (IffChunk chunk = form.readChunk("DATA")) {
			String str;
			while (chunk.remaining() > 0 && (str = chunk.readString()) != null) {
				descriptors.add(str);
			}
		}
	}
	
	@Override
	public IffForm write() {
		IffChunk data = new IffChunk("DATA");
		for (String descriptor : descriptors)
			data.writeString(descriptor);
		
		return IffForm.of("SLTD", 0, data);
	}
	
	public List<String> getSlots() {
		return new ArrayList<>(descriptors);
	}
}
