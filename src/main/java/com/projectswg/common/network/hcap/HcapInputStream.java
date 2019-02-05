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

package com.projectswg.common.network.hcap;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HcapInputStream implements AutoCloseable {
	
	private final DataInputStream is;
	private final Map<String, Object> systemInformation;
	private final byte version;
	
	public HcapInputStream(InputStream is) throws IOException {
		this.is = new DataInputStream(is);
		this.systemInformation = new ConcurrentHashMap<>();
		
		this.version = this.is.readByte();
		readSystemInformation();
	}
	
	/**
	 * Closes this input stream and releases any system resources
	 * associated with the stream.
	 * This method simply performs <code>is.close()</code>.
	 *
	 * @exception IOException  if an I/O error occurs.
	 * @see        DataInputStream#close
	 */
	@Override
	public void close() throws IOException {
		is.close();
	}
	
	@NotNull
	public Map<String, Object> getSystemInformation() {
		return Collections.unmodifiableMap(systemInformation);
	}
	
	@Nullable
	public PacketRecord readPacket() throws IOException {
		if (is.available() >= 11) {
			boolean server = is.readBoolean();
			Instant time = Instant.ofEpochMilli(is.readLong());
			int dataLength = version < 4 ? is.readUnsignedShort() : is.readInt();
			byte [] data = new byte[dataLength];
			int ret = is.read(data, 0, dataLength);
			int n = ret;
			while (n < dataLength && ret >= 0) {
				ret = is.read(data, n, dataLength - n);
				n += ret;
			}
			if (n != dataLength)
				return null;
			return new PacketRecord(server, time, data);
		}
		return null;
	}
	
	private void readSystemInformation() throws IOException {
		int count = is.readByte();
		for (int i = 0; i < count; i++) {
			Map.Entry<String, Object> entry = parseEntry(version, is.readUTF());
			systemInformation.put(entry.getKey(), entry.getValue());
		}
	}
	
	private static Map.Entry<String, Object> parseEntry(byte version, String str) {
		String [] keyValue = str.split("=", 2);
		assert keyValue.length == 2;
		String key = keyValue[0].toLowerCase(Locale.US);
		String value = keyValue[1];
		
		if (version == 2) {
			switch (key) {
				case "time.current_time":
					return Map.entry(key, Instant.ofEpochMilli(Long.parseLong(value)));
				case "time.time_zone":
					return Map.entry(key, ZoneId.of(value.split(":")[0]));
				default:
					return Map.entry(key, value);
			}
		} else if (version >= 3) {
			switch (key) {
				case "time.current_time":
					return Map.entry(key, Instant.parse(value));
				case "time.time_zone":
					return Map.entry(key, ZoneId.of(value));
				default:
					return Map.entry(key, value);
			}
		} else {
			return Map.entry(key, value);
		}
	}
	
}
