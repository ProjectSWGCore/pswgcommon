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

import com.projectswg.common.data.schematic.DraftSchematic;
import com.projectswg.common.data.schematic.IngridientSlot;
import com.projectswg.common.network.NetBuffer;

public class DraftSlotsQueryResponse extends ObjectController {

	private final static int CRC = 0x01BF;
	
	private DraftSchematic schematic;
	
	public DraftSlotsQueryResponse(DraftSchematic schematic) {
		this.schematic = schematic;
	}

	public DraftSlotsQueryResponse(NetBuffer data){
		super(CRC);
		decode(data);
	}
	
	@Override
	public void decode(NetBuffer data) {
		decodeHeader(data);
		schematic.setCombinedCrc(data.getLong());
		schematic.setComplexity(data.getInt());
		schematic.setVolume(data.getInt());
		schematic.setCanManufacture(data.getBoolean());
		schematic.getIngridientSlot().clear();
		schematic.getIngridientSlot().addAll(data.getList(IngridientSlot.class));
	}

	@Override
	public NetBuffer encode() {
		int length = 0;
		for (IngridientSlot ingridientSlot : schematic.getIngridientSlot()) {
			length += ingridientSlot.getLength();
		}
		NetBuffer data = NetBuffer.allocate(HEADER_LENGTH + 21 + length);
		encodeHeader(data);
		data.addLong(schematic.getCombinedCrc());
		data.addInt(schematic.getComplexity());
		data.addInt(schematic.getVolume());
		data.addBoolean(schematic.isCanManufacture());
		data.addList(schematic.getIngridientSlot());		
		return data;
	}
	
	public DraftSchematic getSchematic() {
		return schematic;
	}
}
