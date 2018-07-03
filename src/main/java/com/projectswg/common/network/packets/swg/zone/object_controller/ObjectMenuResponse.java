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

import java.util.List;

import com.projectswg.common.data.radial.RadialOption;
import com.projectswg.common.data.radial.RadialOptionList;
import com.projectswg.common.network.NetBuffer;

public class ObjectMenuResponse extends ObjectController {
	
	public static final int CRC = 0x0147;
	
	private RadialOptionList options;
	private long targetId;
	private long requestorId;
	private int counter;
	
	public ObjectMenuResponse(long objectId) {
		super(objectId, CRC);
		this.options = new RadialOptionList();
	}
	
	public ObjectMenuResponse(long objectId, long targetId, long requestorId, List<RadialOption> options, int counter) {
		super(objectId, CRC);
		this.targetId = targetId;
		this.requestorId = requestorId;
		this.options = new RadialOptionList(options);
		this.counter = counter;
	}
	
	public ObjectMenuResponse(NetBuffer data) {
		super(CRC);
		decode(data);
	}
	
	@Override
	public void decode(NetBuffer data) {
		decodeHeader(data);
		targetId = data.getLong();
		requestorId = data.getLong();
		options = data.getEncodable(RadialOptionList.class);
		counter = data.getByte();
	}
	
	@Override
	public NetBuffer encode() {
		NetBuffer data = NetBuffer.allocate(HEADER_LENGTH + options.getSize() + 17);
		encodeHeader(data);
		data.addLong(targetId);
		data.addLong(requestorId);
		data.addEncodable(options);
		data.addByte(counter);
		return data;
	}

	public void setTargetId(long targetId) {
		this.targetId = targetId;
	}
	
	public void setRequestorId(long requestorId) {
		this.requestorId = requestorId;
	}
	
	public void setRadialOptions(List<RadialOption> options) {
		this.options.setOptions(options);
	}
	
	public void setCounter(int counter) {
		this.counter = counter;
	}
	
	public long getTargetId() {
		return targetId;
	}
	
	public long getRequestorId() {
		return requestorId;
	}
	
	public List<RadialOption> getOptions() {
		return options.getOptions();
	}
	
	public int getCounter() {
		return counter;
	}
	
}
