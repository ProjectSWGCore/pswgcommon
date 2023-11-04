/***********************************************************************************
 * Copyright (c) 2023 /// Project SWG /// www.projectswg.com                       *
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
package com.projectswg.common.network.packets.swg.zone;

import com.projectswg.common.data.CRC;
import com.projectswg.common.data.encodables.oob.OutOfBandPackage;
import com.projectswg.common.network.NetBuffer;
import com.projectswg.common.network.packets.SWGPacket;

/**
 * Makes the client display a comm message with a given text and a given sender.
 */
public class CommPlayerMessage extends SWGPacket {
	
	public static final int CRC = getCrc("CommPlayerMessage");
	
	private long objectId;
	private OutOfBandPackage message;
	private CRC modelCrc;
	private String soundFile;
	private float displayTime;
	
	/**
	 *
	 * @param objectId ID for object to show. Can be overridden by {@code modelCrc} param.
	 * @param message text that is displayed.
	 * @param modelCrc override displayed object from {@code objectId} param with CRC for a shared template to display instead.
	 * @param soundFile plays specified sound to the receiver.
	 * @param displayTime amount of time the comm message is displayed before automatically closing. Time unit is seconds.
	 *                    If 0, the client tries to automatically determine an appropriate amount of time based on the amount of text shown.
	 *                    If be
	 */
	public CommPlayerMessage(long objectId, OutOfBandPackage message, CRC modelCrc, String soundFile, float displayTime) {
		this.objectId = objectId;
		this.message = message;
		this.modelCrc = modelCrc;
		this.soundFile = soundFile;
		this.displayTime = displayTime;
	}
	
	public CommPlayerMessage(NetBuffer data) {
		decode(data);
	}
	
	@Override
	public void decode(NetBuffer data) {
		if (!super.checkDecode(data, CRC))
			return;
		
		objectId = data.getLong();
		message = data.getEncodable(OutOfBandPackage.class);
		modelCrc = data.getEncodable(CRC.class);
		soundFile = data.getAscii();
		displayTime = data.getFloat();
	}
	
	@Override
	public NetBuffer encode() {
		int length = 0;
		
		length += Short.BYTES;
		length += Integer.BYTES;
		length += Long.BYTES;
		length += message.getLength();
		length += Integer.BYTES;
		length += Short.BYTES + soundFile.length();
		length += Float.BYTES;
		
		NetBuffer data = NetBuffer.allocate(length);
		data.addShort(2);
		data.addInt(CRC);
		data.addLong(objectId);
		data.addEncodable(message);
		data.addEncodable(modelCrc);
		data.addAscii(soundFile);
		data.addFloat(displayTime);
		
		return data;
	}
	
	@Override
	protected String getPacketData() {
		return createPacketInformation(
				"objectId", objectId,
				"message", message,
				"modelCrc", modelCrc,
				"soundFile", soundFile,
				"displayTime", displayTime
		);
	}
}
