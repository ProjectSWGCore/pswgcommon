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
package com.projectswg.common.network.packets.swg.zone.auction;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.projectswg.common.data.customization.CustomizationString;
import com.projectswg.common.network.NetBuffer;
import com.projectswg.common.network.packets.SWGPacket;

public class GetAuctionDetailsResponse extends SWGPacket {
	
	public static final int CRC = com.projectswg.common.data.CRC.getCrc("GetAuctionDetailsResponse");
	
	private long itemId;
	private Map <String, String> properties;
	private String itemDescription;
	private String itemTemplate;
	private CustomizationString customizationString;

	public GetAuctionDetailsResponse() {
		this(0, new HashMap<String, String>(), "", "", new CustomizationString());
	}
	
	public GetAuctionDetailsResponse(long itemId, Map <String, String> properties, String itemDescription, String itemTemplate, CustomizationString customizationString) {
		this.itemId = itemId;
		this.properties = properties;
		this.itemDescription = itemDescription;
		this.itemTemplate = itemTemplate;
		this.customizationString = customizationString;
	}
	
	public GetAuctionDetailsResponse(NetBuffer data) {
		decode(data);
	}
	
	public void decode(NetBuffer data) {
		if (!super.checkDecode(data, CRC))
			return;
		itemId = data.getLong();
		itemDescription = data.getUnicode();
		int count = data.getInt();
		for (int i = 0; i < count; i++) {
			String key = data.getAscii();
			String val = data.getUnicode();
			properties.put(key, val);
		}
		itemTemplate = data.getAscii();
		customizationString.decode(data);
	}
	
	public NetBuffer encode() {
		int strSize = 0;
		for (Entry <String, String> e : properties.entrySet())
			strSize += 6 + e.getKey().length() + e.getValue().length()*2;
		strSize += itemDescription.length() * 2;
		strSize += itemTemplate.length();
		NetBuffer data = NetBuffer.allocate(24 + customizationString.getLength() + strSize);
		data.addShort(9);
		data.addInt(CRC);
		data.addLong(itemId);
		data.addUnicode(itemDescription);
		data.addInt(properties.size());
		for (Entry <String, String> e : properties.entrySet()) {
			data.addAscii(e.getKey());
			data.addUnicode(e.getValue());
		}
		data.addAscii(itemTemplate);
		data.addEncodable(customizationString);
		return data;
	}

}
