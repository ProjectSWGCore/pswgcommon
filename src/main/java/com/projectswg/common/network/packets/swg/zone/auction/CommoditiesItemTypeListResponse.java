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
package com.projectswg.common.network.packets.swg.zone.auction;

import com.projectswg.common.network.NetBuffer;
import com.projectswg.common.network.packets.SWGPacket;

public class CommoditiesItemTypeListResponse extends SWGPacket {
	
	public static final int CRC = com.projectswg.common.data.CRC.getCrc("CommoditiesItemTypeListResponse");
	
	private String serverName;
	private int subCategoryCounter;
	private int subCatagory;
	private int itemsInSubCategory;
	private String categoryName;
	private int placeholder;
	private String type;

	public CommoditiesItemTypeListResponse(String serverName, int subCategoryCounter, int subCatagory, int itemsInSubCategory, String categoryName, int placeholder, String type) {
		this.serverName = serverName;
		this.subCategoryCounter = subCategoryCounter;
		this.subCatagory = subCatagory;
		this.itemsInSubCategory = itemsInSubCategory;
		this.categoryName = categoryName;
		this.placeholder = placeholder;
		this.type = type;
	}

	@Override
	public void decode(NetBuffer data) {
		if (!super.checkDecode(data, CRC))
			return;
		serverName = data.getAscii();
		subCategoryCounter = data.getInt();
		subCatagory = data.getInt();
		itemsInSubCategory = data.getInt();
		categoryName = data.getAscii();
		placeholder = data.getInt();
		type = data.getUnicode();
	}

	@Override
	public NetBuffer encode() {
		NetBuffer data = NetBuffer.allocate(30 + serverName.length() + categoryName.length() + type.length()*2);
		data.addShort(8);
		data.addInt(CRC);
		data.addAscii(serverName);
		data.addInt(subCategoryCounter);
		data.addInt(subCatagory);
		data.addInt(itemsInSubCategory);
		data.addAscii(categoryName);
		data.addInt(placeholder);
		data.addUnicode(type);
		return data;
	}

	public String getServerName() {
		return serverName;
	}

	public int getSubCategoryCounter() {
		return subCategoryCounter;
	}

	public int getSubCatagory() {
		return subCatagory;
	}

	public int getItemsInSubCategory() {
		return itemsInSubCategory;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public int getPlaceholder() {
		return placeholder;
	}

	public String getType() {
		return type;
	}
}
