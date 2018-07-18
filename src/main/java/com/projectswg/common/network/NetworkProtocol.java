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
package com.projectswg.common.network;

import me.joshlarson.jlcommon.log.Log;
import com.projectswg.common.network.packets.PacketType;
import com.projectswg.common.network.packets.SWGPacket;
import com.projectswg.common.network.packets.swg.zone.object_controller.ObjectController;

import java.io.IOException;

public class NetworkProtocol {
	
	public static final String VERSION = "2018-02-04";
	
	public static NetBuffer encode(SWGPacket p) {
		NetBuffer encoded = p.encode();
		encoded.flip();
		if (encoded.remaining() != encoded.capacity())
			Log.w("SWGPacket %s has invalid array length. Expected: %d  Actual: %d", p, encoded.remaining(), encoded.capacity());
		
		int remaining = encoded.remaining();
		NetBuffer data = NetBuffer.allocate(remaining + 4);
		data.addInt(remaining);
		data.add(encoded);
		data.flip();
		return data;
	}
	
	public static boolean canDecode(NetBufferStream buffer) throws IOException {
		if (buffer.remaining() < 4)
			return false;
		
		int length = buffer.getInt();
		buffer.seek(-4);
		
		if (length < 0)
			throw new IOException("Stream corrupted");
		return length <= buffer.remaining();
	}
	
	public static SWGPacket decode(NetBufferStream buffer) throws IOException {
		if (!canDecode(buffer))
			return null;
		
		NetBuffer swg = NetBuffer.wrap(buffer.getArray(buffer.getInt()));
		if (swg.remaining() < 6)
			return null;
		
		swg.position(2);
		int crc = swg.getInt();
		swg.position(0);
		
		if (crc == ObjectController.CRC)
			return ObjectController.decodeController(swg);
		
		SWGPacket packet = PacketType.getForCrc(crc);
		if (packet != null)
			packet.decode(swg);
		return packet;
	}
	
}
