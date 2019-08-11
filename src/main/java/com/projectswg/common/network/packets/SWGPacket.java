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

package com.projectswg.common.network.packets;

import java.net.SocketAddress;

import com.projectswg.common.data.CRC;
import me.joshlarson.jlcommon.log.Log;
import com.projectswg.common.network.NetBuffer;

public abstract class SWGPacket {
	
	private SocketAddress socketAddress;
	private PacketType type;
	private int crc;
	
	public SWGPacket() {
		this.socketAddress = null;
		this.type = PacketType.UNKNOWN;
		this.crc = 0;
	}
	
	/**
	 * Sets the socket address that this packet was sent to or received from. Setting this value after it's received, or before it's sent has no effect
	 * 
	 * @param socketAddress the socket address
	 */
	public void setSocketAddress(SocketAddress socketAddress) {
		this.socketAddress = socketAddress;
	}
	
	public SocketAddress getSocketAddress() {
		return socketAddress;
	}
	
	public int getSWGOpcode() {
		return crc;
	}
	
	public PacketType getPacketType() {
		return type;
	}
	
	public boolean checkDecode(NetBuffer data, int crc) {
		data.getShort();
		this.crc = data.getInt();
		this.type = PacketType.fromCrc(crc);
		if (this.crc == crc)
			return true;
		Log.w("SWG Opcode does not match actual! Expected: 0x%08X  Actual: 0x%08X", crc, getSWGOpcode());
		return false;
	}
	
	public abstract void decode(NetBuffer data);
	public abstract NetBuffer encode();
	
	protected void packetAssert(boolean condition, String constraint) {
		if (!condition)
			throw new PacketSerializationException(this, constraint);
	}
	
	public static int getCrc(String string) {
		return CRC.getCrc(string);
	}
	
	@Override
	public final String toString() {
		return getClass().getSimpleName() + "[" + getPacketData() + "]";
	}
	
	protected String getPacketData() {
		return "?";
	}
	
	protected final String createPacketInformation(Object ... data) {
		assert data.length % 2 == 0;
		StringBuilder str = new StringBuilder();
		for (int i = 0; i+1 < data.length; i += 2) {
			assert data[i] instanceof String;
			String key = data[i] == null ? "null" : data[i].toString();
			if (i > 0)
				str.append(' ');
			str.append(key);
			str.append('=');
			str.append(data[i+1]);
		}
		return str.toString();
	}
	
}
