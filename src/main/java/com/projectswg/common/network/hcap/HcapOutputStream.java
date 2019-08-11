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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class HcapOutputStream implements AutoCloseable {
	
	private final DataOutputStream os;
	
	public HcapOutputStream(OutputStream os) throws IOException {
		this.os = new DataOutputStream(os);
		
		this.os.writeByte(3);
		writeSystemHeader();
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
		os.close();
	}
	
	public void record(PacketRecord record) throws IOException {
		this.os.writeBoolean(record.isServer());
		this.os.writeLong(record.getTime().toEpochMilli());
		this.os.writeShort(record.getData().length);
		this.os.write(record.getData());
	}
	
	private void writeSystemHeader() throws IOException {
		OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
		Map<String, String> systemStrings = new TreeMap<>();
		systemStrings.put("os.arch",			os.getArch());
		systemStrings.put("os.details",			os.getName()+":"+os.getVersion());
		systemStrings.put("os.processor_count", Integer.toString(os.getAvailableProcessors()));
		systemStrings.put("java.version",		System.getProperty("java.version"));
		systemStrings.put("java.vendor",		System.getProperty("java.vendor"));
		systemStrings.put("time.time_zone",		ZoneId.systemDefault().getId());
		systemStrings.put("time.current_time",	Instant.now().toString());
		this.os.writeByte(systemStrings.size()); // Count of strings
		for (Entry<String, String> e : systemStrings.entrySet())
			this.os.writeUTF(e.getKey() + "=" + e.getValue());
	}
	
}
