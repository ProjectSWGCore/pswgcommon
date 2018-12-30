/***********************************************************************************
 * Copyright (c) 2018 /// Project SWG /// www.projectswg.com                       *
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
package com.projectswg.common.network.packets.swg.zone.object_controller;

import com.projectswg.common.network.NetBuffer;

public class BuffRemoveUpdate extends ObjectController {
	
	public static final int CRC = 0x022A;
	
	private int buffCrc;
	
	public BuffRemoveUpdate(long objectId, int buffCrc) {
		super(objectId, CRC);
		this.buffCrc = buffCrc;
	}
	
	public BuffRemoveUpdate(NetBuffer data) {
		super(CRC);
		decode(data);
	}
	
	public void decode(NetBuffer data) {
		decodeHeader(data);
		buffCrc = data.getInt();
	}
	
	public NetBuffer encode() {
		NetBuffer data = NetBuffer.allocate(HEADER_LENGTH + 4);
		encodeHeader(data);
		data.addInt(buffCrc);
		return data;
	}
	
	public int getBuffCrc() {
		return buffCrc;
	}
	
	public void setBuffCrc(int buffCrc) {
		this.buffCrc = buffCrc;
	}
	
	@Override
	protected String getPacketData() {
		return createPacketInformation(
				"objId", getObjectId(),
				"buffCrc", String.format("%08X", buffCrc),
				"buff", com.projectswg.common.data.CRC.getString(buffCrc)
		);
	}
	
}
