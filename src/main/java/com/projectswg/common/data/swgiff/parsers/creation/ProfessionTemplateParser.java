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
package com.projectswg.common.data.swgiff.parsers.creation;

import com.projectswg.common.data.swgiff.IffChunk;
import com.projectswg.common.data.swgiff.IffForm;
import com.projectswg.common.data.swgiff.parsers.SWGParser;

import java.util.*;
import java.util.Map.Entry;

public class ProfessionTemplateParser implements SWGParser {
	
	private final List<String> skills;
	private final Map<String, List<String>> raceItems;
	
	public ProfessionTemplateParser() {
		this.skills = new ArrayList<>();
		this.raceItems = new HashMap<>();
	}
	
	@Override
	public void read(IffForm form) {
		assert form.getTag().equals("PRFI");
		assert form.getVersion() == 0;
		
		try (IffForm skillForm = form.readForm("SKLS")) {
			while (skillForm.hasChunk("SKIL")) {
				try (IffChunk skillChunk = skillForm.readChunk("SKIL")) {
					skills.add(skillChunk.readString());
				}
			}
		}
		while (form.hasForm("PTMP")) {
			try (IffForm templateForm = form.readForm("PTMP")) {
				String race;
				List<String> items = new ArrayList<>();
				try (IffChunk nameChunk = templateForm.readChunk("NAME")) {
					race = nameChunk.readString();
				}
				while (templateForm.hasChunk("ITEM")) {
					try (IffChunk itemChunk = templateForm.readChunk("ITEM")) {
						itemChunk.readInt(); // arrangementId
						items.add(itemChunk.readString()); // shared template
						itemChunk.readString(); // server template
					}
				}
				raceItems.put(race, Collections.unmodifiableList(items));
			}
		}
	}
	
	@Override
	public IffForm write() {
		List<IffForm> forms = new ArrayList<>();
		
		{
			List<IffChunk> skillChunks = new ArrayList<>();
			for (String skill : skills) {
				IffChunk chunk = new IffChunk("SKIL");
				chunk.writeString(skill);
				skillChunks.add(chunk);
			}
			forms.add(IffForm.of("SKLS", skillChunks));
		}
		{
			for (Entry<String, List<String>> raceItem : raceItems.entrySet()) {
				List<IffChunk> raceItemChunks = new ArrayList<>();
				{
					IffChunk nameChunk = new IffChunk("NAME");
					nameChunk.writeString(raceItem.getKey());
					raceItemChunks.add(nameChunk);
				}
				for (String sharedTemplate : raceItem.getValue()) {
					IffChunk chunk = new IffChunk("ITEM");
					chunk.writeInt(0);
					chunk.writeString(sharedTemplate);
					chunk.writeString("");
					raceItemChunks.add(chunk);
				}
				forms.add(IffForm.of("PTMP", raceItemChunks));
			}
		}
		
		return IffForm.of("PRFI", 0, forms);
	}
	
	public List<String> getSkills() {
		return Collections.unmodifiableList(skills);
	}
	
	public Map<String, List<String>> getRaceItems() {
		return Collections.unmodifiableMap(raceItems);
	}
}
