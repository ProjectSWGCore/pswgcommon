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
package com.projectswg.common.network.packets.swg.zone.deltas;

import java.nio.charset.StandardCharsets;

import com.projectswg.common.network.NetBuffer;
import com.projectswg.common.network.packets.SWGPacket;
import com.projectswg.common.network.packets.swg.zone.baselines.Baseline.BaselineType;

public class DeltasMessage extends SWGPacket {
	
	public static final int CRC = getCrc("DeltasMessage");
	
	private long objId;
	private BaselineType type;
	private int num;
	private byte[] deltaData;
	private int update;
	
	public DeltasMessage() {
		
	}
	
	public DeltasMessage(long objId, BaselineType type, int typeNumber, int update, byte[] data) {
		this.objId = objId;
		this.type = type;
		this.update = update;
		this.num = typeNumber;
		this.deltaData = data;
	}
	
	public DeltasMessage(NetBuffer data) {
		decode(data);
	}
	
	@Override
	public void decode(NetBuffer data) {
		if (!super.checkDecode(data, CRC))
			return;
		objId = data.getLong();
		type = BaselineType.valueOf(reverse(new String(data.getArray(4), StandardCharsets.UTF_8)));
		num = data.getByte();
		NetBuffer deltaDataBuffer = NetBuffer.wrap(data.getArrayLarge());
		deltaDataBuffer.getShort();
		update = deltaDataBuffer.getShort();
		deltaData = deltaDataBuffer.getArray(deltaDataBuffer.remaining());
	}
	
	@Override
	public NetBuffer encode() {
		NetBuffer data = NetBuffer.allocate(27 + deltaData.length);
		data.addShort(5);
		data.addInt(CRC);
		data.addLong(objId);
		data.addRawArray(reverse(type.toString()).getBytes(StandardCharsets.UTF_8));
		data.addByte(num);
		data.addInt(deltaData.length + 4);
		data.addShort(1); // updates - only 1 cause we're boring
		data.addShort(update);
		data.addRawArray(deltaData);
		return data;
	}
	
	public long getObjectId() {
		return objId;
	}
	
	public BaselineType getType() {
		return type;
	}
	
	public int getNum() {
		return num;
	}
	
	public int getUpdate() {
		return update;
	}
	
	public byte[] getDeltaData() {
		return deltaData;
	}
	
	public void setType(BaselineType type) {
		this.type = type;
	}
	
	public void setNum(int num) {
		this.num = num;
	}
	
	public void setId(long id) {
		this.objId = id;
	}
	
	public void setData(byte[] data) {
		this.deltaData = data;
	}
	
	public void setUpdate(int update) {
		this.update = update;
	}
	
	private String reverse(String str) {
		return new StringBuffer(str).reverse().toString();
	}
}
