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
import java.util.Collections;
import java.util.List;

public class SlotArrangementParser implements SWGParser {
	
	private final List<List<String>> arrangements;
	
	public SlotArrangementParser() {
		this.arrangements = new ArrayList<>();
	}
	
	@Override
	public void read(IffForm form) {
		assert form.getTag().equals("ARGD");
		assert form.getVersion() == 0;
		while (form.hasChunk("ARG ")) {
			List<String> slots = new ArrayList<>();
			try (IffChunk chunk = form.readChunk("ARG ")) {
				String str;
				while (chunk.remaining() > 0 && (str = chunk.readString()) != null) {
					slots.add(str);
				}
			}
			arrangements.add(Collections.unmodifiableList(slots));
		}
	}
	
	@Override
	public IffForm write() {
		List<IffChunk> chunks = new ArrayList<>();
		for (List<String> slots : arrangements) {
			IffChunk chunk = new IffChunk("ARG ");
			for (String slot : slots) {
				chunk.writeString(slot);
			}
			chunks.add(chunk);
		}
		
		return IffForm.of("ARGD", 0, chunks);
	}
	
	public List<List<String>> getArrangement() {
		return new ArrayList<>(arrangements);
	}
}
