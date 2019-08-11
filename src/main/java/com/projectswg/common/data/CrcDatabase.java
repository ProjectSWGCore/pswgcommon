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
package com.projectswg.common.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import me.joshlarson.jlcommon.log.Log;

public class CrcDatabase {
	
	private static final CrcDatabase INSTANCE = new CrcDatabase();
	
	static {
		INSTANCE.loadStrings();
	}
	
	private final Map<Integer, String> crcTable;
	
	private CrcDatabase() {
		crcTable = new HashMap<>();
	}
	
	public void saveStrings(OutputStream os) throws IOException {
		for (Entry<Integer, String> e : crcTable.entrySet()) {
			os.write((Integer.toString(e.getKey(), 16) + ',' + e.getValue() + '\n').getBytes(StandardCharsets.US_ASCII));
		}
		os.flush();
	}
	
	public void addCrc(String string) {
		crcTable.put(CRC.getCrc(string), string);
	}
	
	public String getString(int crc) {
		return crcTable.get(crc);
	}
	
	private void loadStrings() {
		try (InputStream is = getClass().getResourceAsStream("/com/projectswg/common/data/crc_database.csv")) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
			String line;
			while ((line = reader.readLine()) != null) {
				int index = line.indexOf(',');
				assert index > 0 : "invalid line in CRC csv";
				int crc = Integer.parseInt(line.substring(0, index), 16);
				crcTable.put(crc, line.substring(index+1).intern());
			}
		} catch (IOException e) {
			Log.e(e);
		}
	}
	
	public static CrcDatabase getInstance() {
		return INSTANCE;
	}
	
}
