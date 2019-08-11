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

public class CombinedProfessionTemplateParser implements SWGParser {
	
	private final List<String> templateFiles;
	private final Map<String, ProfessionTemplateParser> templates;
	
	public CombinedProfessionTemplateParser() {
		this.templateFiles = new ArrayList<>();
		this.templates = new HashMap<>();
	}
	
	@Override
	public void read(IffForm form) {
		assert form.getTag().equals("PFDT");
		assert form.getVersion() == 0;
		
		while (form.hasChunk("DATA")) {
			try (IffChunk chunk = form.readChunk("DATA")) {
				String file = chunk.readString();
				templateFiles.add(file);
				templates.put(file, SWGParser.parse("creation/profession_defaults_"+file+".iff"));
			}
		}
	}
	
	@Override
	public IffForm write() {
		List<IffChunk> chunks = new ArrayList<>();
		for (String file : templateFiles) {
			IffChunk chunk = new IffChunk("DATA");
			chunk.writeString(file);
			chunks.add(chunk);
		}
		
		return IffForm.of("PRFI", 0, chunks);
	}
	
	public Map<String, ProfessionTemplateParser> getTemplates() {
		return Collections.unmodifiableMap(templates);
	}
	
}
