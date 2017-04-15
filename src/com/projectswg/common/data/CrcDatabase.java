/************************************************************************************
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
package com.projectswg.common.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.projectswg.common.debug.Log;

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
		StringBuilder str = new StringBuilder(256);
		try (InputStream is = getClass().getResourceAsStream("crc_database.csv")) {
			BufferedByteReader reader = new BufferedByteReader(is);
			while (reader.canRead()) {
				str.setLength(0);
				processLine(str, reader);
			}
		} catch (IOException e) {
			Log.e(e);
		}
	}
	
	private void processLine(StringBuilder str, BufferedByteReader reader) throws IOException {
		int crc = 0;
		int b;
		while ((b = reader.getNextByte()) != -1) {
			if (b == ',' && crc == 0) {
				crc = Integer.parseInt(str.toString(), 16);
				str.setLength(0);
			} else if (b == '\n') {
				if (crc == 0)
					break;
				crcTable.put(crc, str.toString().intern());
				break;
			} else {
				str.append((char) b);
			}
		}
	}
	
	public static CrcDatabase getInstance() {
		return INSTANCE;
	}
	
	private static class BufferedByteReader {
		
		private final InputStream is;
		private final byte [] buffer;
		private int remaining;
		private int position;
		
		public BufferedByteReader(InputStream is) {
			this.is = is;
			this.buffer = new byte[4096];
			this.remaining = 0;
			this.position = 0;
		}
		
		public int getNextByte() throws IOException {
			if (remaining <= 0) {
				remaining = is.read(buffer);
				position = 0;
				if (remaining <= 0)
					return -1;
			}
			remaining--;
			return buffer[position++];
		}
		
		public boolean canRead() {
			return remaining >= 0;
		}
		
	}
	
}
