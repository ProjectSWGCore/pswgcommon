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

public class CraftAcknowledge extends ObjectController {
	
	public static final int CRC = 0x010C;
	
	private int acknowledgeId;
	private int errorId;
	private byte updateCounter;
	
	public CraftAcknowledge(int acknowledgeId, int errorId, byte updateCounter) {
		super(CRC);
		this.acknowledgeId = acknowledgeId;
		this.errorId = errorId;
		this.updateCounter = updateCounter;
	}
	
	public CraftAcknowledge(NetBuffer data) {
		super(CRC);
		decode(data);
	}
	
	@Override
	public void decode(NetBuffer data) {
		decodeHeader(data);
		acknowledgeId = data.getInt();
		errorId = data.getInt();
		updateCounter = data.getByte();
	}

	@Override
	public NetBuffer encode() {
		NetBuffer data = NetBuffer.allocate(HEADER_LENGTH + 9);
		encodeHeader(data);
		data.addInt(acknowledgeId);
		data.addInt(errorId);
		data.addByte(updateCounter);
		return data;
	}

	public int getAcknowledgeId() {
		return acknowledgeId;
	}

	public void setAcknowledgeId(int acknowledgeId) {
		this.acknowledgeId = acknowledgeId;
	}

	public int getErrorId() {
		return errorId;
	}

	public void setErrorId(int errorId) {
		this.errorId = errorId;
	}

	public byte getUpdateCounter() {
		return updateCounter;
	}

	public void setUpdateCounter(byte updateCounter) {
		this.updateCounter = updateCounter;
	}
}
