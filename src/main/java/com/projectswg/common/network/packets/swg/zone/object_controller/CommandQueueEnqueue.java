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
package com.projectswg.common.network.packets.swg.zone.object_controller;

import com.projectswg.common.network.NetBuffer;


public class CommandQueueEnqueue extends ObjectController {
	
	public static final int CRC = 0x0116;
	
	private int counter = 0;
	private int crc = 0;
	private long targetId = 0;
	private String arguments = "";
	
	public CommandQueueEnqueue(long objectId, int counter, int crc, long targetId, String arguments) {
		super(objectId, CRC);
		this.counter = counter;
		this.crc = crc;
		this.targetId = targetId;
		this.arguments = arguments;
	}
	
	public CommandQueueEnqueue(NetBuffer data) {
		super(CRC);
		decode(data);
	}
	
	public void decode(NetBuffer data) {
		decodeHeader(data);
		counter = data.getInt();
		crc = data.getInt();
		targetId = data.getLong();
		arguments = data.getUnicode();
	}
	
	public NetBuffer encode() {
		NetBuffer data = NetBuffer.allocate(HEADER_LENGTH + 20 + arguments.length()*2);
		encodeHeader(data);
		data.addInt(counter);
		data.addInt(crc);
		data.addLong(targetId);
		data.addUnicode(arguments);
		return data;
	}
	
	public int getCounter() { return counter; }
	public int getCommandCrc() { return crc; }
	public String getArguments() { return arguments; }
	public long getTargetId() { return targetId; }
	
	public void setCounter(int counter) { this.counter = counter; }
	public void setCommandCrc(int crc) { this.crc = crc; }
	public void setArguments(String args) { this.arguments = args; }
	public void setTargetId(long targetId) { this.targetId = targetId; }
	
}
