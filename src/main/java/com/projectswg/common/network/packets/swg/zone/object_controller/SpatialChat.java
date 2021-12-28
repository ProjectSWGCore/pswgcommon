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


public class SpatialChat extends ObjectController {
	
	public static final int CRC = 0x00F4;
	
	private long sourceId = 0;
	private long targetId = 0;
	private String text = "";
	private short balloonSize = 5;
	private short balloonType = 0;
	private short moodId = 0;
	private byte chatFlags;
	private byte languageId = 0;
	private String outOfBand = "";
	private String sourceName = "";
	
	public SpatialChat(long objectId) {
		super(objectId, CRC);
	}
	
	public SpatialChat(long objectId, long sourceId, long targetId, String text, short balloonType, short moodId, byte languageId) {
		super(objectId, CRC);
		this.sourceId = sourceId;
		this.targetId = targetId;
		this.text = text;
		this.balloonType = balloonType;
		this.moodId = moodId;
		this.languageId = languageId;
	}
	
	public SpatialChat(long objectId, SpatialChat chat) {
		super(objectId, CRC);
		this.sourceId = chat.sourceId;
		this.targetId = chat.targetId;
		this.text = chat.text;
		this.balloonSize = chat.balloonSize;
		this.balloonType = chat.balloonType;
		this.moodId = chat.moodId;
		this.languageId = chat.languageId;
		this.outOfBand = chat.outOfBand;
		this.sourceName = chat.sourceName;
	}
	
	public SpatialChat(NetBuffer data) {
		super(CRC);
		decode(data);
	}
	
	public void decode(NetBuffer data) {
		decodeHeader(data);
		sourceId = data.getLong();
		targetId = data.getLong();
		text = data.getUnicode();
		balloonSize = data.getShort();
		balloonType = data.getShort();
		moodId = data.getShort();
		chatFlags = data.getByte();
		languageId = data.getByte();
		outOfBand = data.getUnicode();
		sourceName = data.getUnicode();
	}
	
	public NetBuffer encode() {
		int length = 36 + text.length()*2 + outOfBand.length()*2 + sourceName.length()*2;
		NetBuffer data = NetBuffer.allocate(HEADER_LENGTH + length);
		encodeHeader(data);
		data.addLong(sourceId);
		data.addLong(targetId);
		data.addUnicode(text);
		data.addShort(balloonSize);
		data.addShort(balloonType);
		data.addShort(moodId);
		data.addByte(chatFlags);
		data.addByte(languageId);
		data.addUnicode(outOfBand);
		data.addUnicode(sourceName);
		return data;
	}
	
	public void setSourceId(long sourceId) { this.sourceId = sourceId; }
	public void setTargetId(long targetId) { this.targetId = targetId; }
	public void setText(String text) { this.text = text; }
	public void setBalloonSize(short size) { this.balloonSize = size; }
	public void setBalloonType(short type) { this.balloonType = type; }
	public void setMoodId(short moodId) { this.moodId = moodId; }
	public void setLanguageId(byte id) { this.languageId = id; }
	public void setOutOfBand(String oob) { this.outOfBand = oob; }
	public void setSourceName(String name) { this.sourceName = name; }
	
	public long getSourceId() { return sourceId; }
	public long getTargetId() { return targetId; }
	public String getText() { return text; }
	public short getBalloonSize() { return balloonSize; }
	public short getBalloonType() { return balloonType; }
	public short getMoodId() { return moodId; }
	public byte getLanguageId() { return languageId; }
	public String getOutOfBand() { return outOfBand; }
	public String getSourceName() { return sourceName; }
	
}
