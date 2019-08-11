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

public class MessageQueueCraftCustomization extends ObjectController {

	public static final int CRC = 0x015A;
	
	private String itemName;
	private byte appearenceTemplate;
	private int itemAmount;
	private byte count;
	private int[] property;
	private int[] value;
	
	public MessageQueueCraftCustomization(String itemName, byte appearenceTemplate, int itemAmount, byte count, int[] property, int[] value) {
		super(CRC);
		this.itemName = itemName;
		this.appearenceTemplate = appearenceTemplate;
		this.itemAmount = itemAmount;
		this.count = count;
		this.property = property;
		this.value = value;
	}
	
	public MessageQueueCraftCustomization(NetBuffer data) {
		super(CRC);
		decode(data);
	} 

	@Override
	public void decode(NetBuffer data) {
		decodeHeader(data);
		itemName = data.getUnicode();	
		appearenceTemplate = data.getByte();
		itemAmount = data.getInt();
		count = data.getByte();
		
		for(int i = 0; i < count; i++){
			property[i] = data.getInt();
			value[i] = data.getInt();
		}		
	}

	@Override
	public NetBuffer encode() {
		NetBuffer data = NetBuffer.allocate(HEADER_LENGTH + 10 + itemName.length()*2 + count * 8);
		encodeHeader(data);
		data.addUnicode(itemName);
		data.addByte(appearenceTemplate);
		data.addInt(itemAmount);
		data.addByte(count);
		
		for(int i = 0; i < count; i++){
			data.addInt(property[i] );
			data.addInt(value[i]);
		}
		return data;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public byte getAppearenceTemplate() {
		return appearenceTemplate;
	}

	public void setAppearenceTemplate(byte appearenceTemplate) {
		this.appearenceTemplate = appearenceTemplate;
	}

	public int getItemAmount() {
		return itemAmount;
	}

	public void setItemAmount(int itemAmount) {
		this.itemAmount = itemAmount;
	}

	public byte getCount() {
		return count;
	}

	public void setCount(byte count) {
		this.count = count;
	}

	public int[] getProperty() {
		return property;
	}

	public void setProperty(int[] property) {
		this.property = property;
	}

	public int[] getValue() {
		return value;
	}

	public void setValue(int[] value) {
		this.value = value;
	}	
}
