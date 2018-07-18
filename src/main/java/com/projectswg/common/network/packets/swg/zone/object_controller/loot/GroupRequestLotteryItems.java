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
package com.projectswg.common.network.packets.swg.zone.object_controller.loot;

import com.projectswg.common.network.NetBuffer;
import com.projectswg.common.network.packets.swg.zone.object_controller.ObjectController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GroupRequestLotteryItems extends ObjectController {
	
	public static final int CRC = 0x0440;
	
	private long inventoryId;
	private final List<Long> requestedItems;
	
	public GroupRequestLotteryItems(long objectId, long inventoryId, List<Long> requestedItems) {
		super(objectId, CRC);
		this.inventoryId = inventoryId;
		this.requestedItems = new ArrayList<>(requestedItems);
	}
	
	public GroupRequestLotteryItems(NetBuffer data) {
		super(CRC);
		this.requestedItems = new ArrayList<>();
		decode(data);
	}
	
	public final void decode(NetBuffer data) {
		decodeHeader(data);
		inventoryId = data.getLong();
		
		int itemCount = data.getInt();
		requestedItems.clear();
		for (int i = 0; i < itemCount; i++)
			requestedItems.add(data.getLong());
	}
	
	public final NetBuffer encode() {
		NetBuffer data = NetBuffer.allocate(HEADER_LENGTH + 12 + requestedItems.size()*8);
		encodeHeader(data);
		data.addLong(inventoryId);
		
		data.addInt(requestedItems.size());
		for (long item : requestedItems)
			data.addLong(item);
		return data;
	}
	
	public long getInventoryId() { return inventoryId; }
	
	public List<Long> getRequestedItems() {
		return Collections.unmodifiableList(requestedItems);
	}
	
	public void setInventoryId(long inventoryId) { this.inventoryId = inventoryId; }
	
	public void setRequestedItems(List<Long> requestedItems) {
		this.requestedItems.clear();
		this.requestedItems.addAll(requestedItems);
	}
	
}
