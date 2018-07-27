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
package com.projectswg.common.data.swgiff.parsers.misc;

import com.projectswg.common.data.swgiff.IffChunk;
import com.projectswg.common.data.swgiff.IffForm;
import com.projectswg.common.data.swgiff.parsers.SWGParser;

import java.util.*;
import java.util.Map.Entry;

public class CrcStringDataParser implements SWGParser {
	
	private final Map<Integer, String> crcMap;
	private final Map<String, Integer> reverseCrcMap;
	
	public CrcStringDataParser() {
		this.crcMap = new HashMap<>();
		this.reverseCrcMap = new HashMap<>();
	}
	
	@Override
	public void read(IffForm form) {
		assert form.getTag().equals("CSTB");
		assert form.getVersion() == 0;
		int [] crcList;
		int count;
		try (IffChunk chunk = form.readChunk("DATA")) {
			count = chunk.readInt(); 
			crcList = new int[count];
		}
		try (IffChunk chunk = form.readChunk("CRCT")) {
			for (int i = 0; i < count; ++i) {
				crcList[i] = chunk.readInt();
			}
		}
		try (IffChunk chunk = form.readChunk("STNG")) {
			for (int i = 0; i < count; ++i) {
				String str = chunk.readString();
				crcMap.put(crcList[i], str);
				reverseCrcMap.put(str, crcList[i]);
			}
		}
	}
	
	@Override
	public IffForm write() {
		IffChunk data = new IffChunk("DATA");
		data.writeInt(crcMap.size());
		
		IffChunk crct = new IffChunk("CRCT");
		IffChunk stng = new IffChunk("STNG");
		for (Entry<Integer, String> e : crcMap.entrySet()) {
			crct.writeInt(e.getKey());
			stng.writeString(e.getValue());
		}
		
		return IffForm.of("CSTB", 0, data, crct, stng);
	}
	
	public boolean isValidCrc(int crc) {
		return reverseCrcMap.containsValue(crc);
	}
	
	public String getTemplateString(int crc) {
		return crcMap.get(crc);
	}
	
	public int getCrcForString(String str) {
		Integer crc = reverseCrcMap.get(str);
		return crc == null ? 0 : crc;
	}
	
	public Map<Integer, String> getCrcMap() {
		return Collections.unmodifiableMap(crcMap);
	}
	
	public List<String> getStrings() {
		return new ArrayList<>(reverseCrcMap.keySet());
	}
}
