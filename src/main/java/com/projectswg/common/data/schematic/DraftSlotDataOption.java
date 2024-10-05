/***********************************************************************************
 * Copyright (c) 2024 /// Project SWG /// www.projectswg.com                       *
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
package com.projectswg.common.data.schematic;

import com.projectswg.common.data.encodables.oob.StringId;
import com.projectswg.common.encoding.Encodable;
import com.projectswg.common.network.NetBuffer;

public class DraftSlotDataOption implements Encodable {
	
	private StringId stfName;
	private String ingredientName;
	private SlotType slotType;
	private int amount;
	
	public DraftSlotDataOption(StringId stfName, String ingredientName, SlotType slotType, int amount) {
		this.stfName = stfName;
		this.ingredientName = ingredientName;
		this.slotType = slotType;
		this.amount = amount;
	}

	public DraftSlotDataOption(){
		this.stfName = StringId.Companion.getEMPTY();
		this.ingredientName = "";
		this.slotType = SlotType.RESOURCES;
		this.amount = 0;
	}
	
	public StringId getStfName() {
		return stfName;
	}
	
	public String getIngredientName() {
		return ingredientName;
	}
	
	public SlotType getSlotType() {
		return slotType;
	}
	
	public int getAmount() {
		return amount;
	}
	
	@Override
	public void decode(NetBuffer data) {
		stfName = data.getEncodable(StringId.class);
		ingredientName = data.getUnicode();
		slotType =  SlotType.IDENTICAL.getSlotType(data.getByte());
		amount = data.getInt();		
	}
	
	@Override
	public byte[] encode() {
		NetBuffer data = NetBuffer.allocate(getLength());
		data.addEncodable(stfName);
		data.addUnicode(ingredientName);
		data.addByte(slotType.getId());
		data.addInt(amount);
		if (slotType == SlotType.IDENTICAL) {
			data.addShort(0);	// Client crash if we don't do this
		}
		return data.array();
	}
	
	@Override
	public int getLength() {
		int length = 9 + stfName.getLength() + ingredientName.length() * 2;
		
		if (slotType == SlotType.IDENTICAL) {
			length += 2;
		}
		
		return length;
	}	
}
