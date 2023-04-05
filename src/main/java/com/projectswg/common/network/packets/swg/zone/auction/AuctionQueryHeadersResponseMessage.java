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

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.projectswg.common.network.NetBuffer;
import com.projectswg.common.network.packets.SWGPacket;

public class AuctionQueryHeadersResponseMessage extends SWGPacket {
	
	public static final int CRC = com.projectswg.common.data.CRC.getCrc("AuctionQueryHeadersResponseMessage");
	
	private int updateCounter;
	private int windowType;
	private List <AuctionItem> items;
	
	public AuctionQueryHeadersResponseMessage() {
		items = new ArrayList<>();
	}
	
	public AuctionQueryHeadersResponseMessage(NetBuffer data) {
		decode(data);
	}
	
	@Override
	public void decode(NetBuffer data) {
		if (!super.checkDecode(data, CRC))
			return;
		updateCounter = data.getInt();
		data.getInt();
		String [] locations = new String[data.getInt()];
		for (int i = 0; i < locations.length; i++)
			locations[i] = data.getAscii();
		int itemCount = data.getInt();
		AuctionItem [] items = new AuctionItem[itemCount];
		for (int itemI = 0; itemI < itemCount; itemI++) {
			AuctionItem item = new AuctionItem();
			item.setItemName(data.getUnicode());
			items[itemI] = item;
		}
		itemCount = data.getInt();
		if (itemCount != items.length)
			throw new IllegalStateException("I WAS LIED TO!");
		for (int itemI = 0; itemI < itemCount; itemI++) {
			AuctionItem item = items[itemI];
			item.setObjectId(data.getLong());
			data.getByte();
			item.setPrice(data.getInt());
			item.setExpireTime(data.getInt());
			if (data.getInt() != item.getPrice())
				throw new IllegalStateException("I WAS LIED TO AT INDEX " + itemI);
			item.setVuid(locations[data.getShort()]);
			item.setOwnerId(data.getLong());
			item.setOwnerName(locations[data.getShort()]);
			data.getLong();
			data.getInt();
			data.getInt();
			data.getShort();
			item.setItemType(data.getInt());
			data.getInt();
			item.setAuctionOptions(data.getInt());
			data.getInt();
		}
		data.getShort();
		data.getByte();
	}
	
	@Override
	public NetBuffer encode() {
		Set<String> stringList = new LinkedHashSet<>();
		for (AuctionItem item : items) {
			stringList.add(item.getVuid());
			stringList.add(item.getOwnerName());
			stringList.add(item.getBidderName());
		}
		int locationsSize = (stringList.size() * Short.BYTES) +  String.join("", stringList).length();
		int itemNamesSize = 4 + (items.size() * 4) + items.stream().map(AuctionItem::getItemName).collect(Collectors.joining()).length() * 2;
		int auctionItemMetadataSize = 4 + (items.size() * 60);
		NetBuffer data = NetBuffer.allocate(21 + locationsSize + itemNamesSize + auctionItemMetadataSize);
		data.addShort(8);
		data.addInt(CRC);
		data.addInt(updateCounter);
		data.addInt(windowType);
		
		data.addInt(stringList.size());
		for (String string : stringList) {
			data.addAscii(string);
		}
		
		data.addInt(items.size());
		for (AuctionItem item : items)
			data.addUnicode(item.getItemName());
		
		int i = 0;
		data.addInt(items.size());
		for(AuctionItem item : items) {
			data.addLong(item.getObjectId());
			data.addByte(i);
			data.addInt(item.getPrice());
			data.addInt(item.getExpireTime());
			data.addBoolean(item.isInstant());
			data.addShort(getString(stringList, item.getVuid()));
			data.addLong(item.getOwnerId());
			data.addShort(getString(stringList, item.getOwnerName()));
			data.addLong(item.getOfferToId());	// the highest bidder
			data.addShort(getString(stringList, item.getBidderName()));
			data.addInt(item.getProxyBid());
			data.addInt(0);	// My high bid
			data.addInt(item.getItemType()); // gameObjectType/category bitmask
			data.addInt(getAuctionItemFlags(item));
			data.addInt(0);	// Possibly access fee to the building that a vendor is located inside
			i++;
		}
		
		data.addShort(0);	// number of the first auction being displayed, possibly used for pagination
		data.addBoolean(false);	// true if there are more pages, false if there are not
		return data;
	}

	private static int getAuctionItemFlags(AuctionItem item) {
		int options = 0;

		if (item.getStatus() == AuctionState.OFFERED || item.getStatus() == AuctionState.FORSALE) 
			options |= 0x800;

		int i1 = item.getAuctionOptions() | options;
		return i1;
	}

	public void addItem(AuctionItem item) {
		items.add(item);
	}

	public int getUpdateCounter() {
		return updateCounter;
	}

	public void setUpdateCounter(int updateCounter) {
		this.updateCounter = updateCounter;
	}

	public int getWindowType() {
		return windowType;
	}

	public void setWindowType(int windowType) {
		this.windowType = windowType;
	}

	private int getString(Set <String> strings, String str) {
		int index = 0;
		for (String s : strings) {
			if (s.equals(str))
				return index;
			index++;
		}
		return index;
	}
	
	public enum AuctionState {
		PREMIUM		(0x400),
		WITHDRAW	(0x800),
		FORSALE		(1),
		SOLD		(2),
		EXPIRED		(4),
		OFFERED		(5),
		RETRIEVED	(6);
		
		private int id;
		
		AuctionState(int id) {
			this.id = id;
		}
		
		public int getId() { return id; }
	}
	
	public static class AuctionItem {
		private long objectId;
		private long ownerId;
		private long vendorId;
		private long buyerId;
		private long offerToId;
		private int itemType;
		private String ownerName;
		private String bidderName;
		private String itemName;
		private String itemDescription;
		private int price;
		private int proxyBid;
		private boolean instant;
		private String vuid;
		private int expireTime;
		private int auctionOptions;
		private AuctionState state;
		
		public long getObjectId() { return objectId; }
		public long getOwnerId() { return ownerId; }
		public long getVendorId() { return vendorId; }
		public long getBuyerId() { return buyerId; }
		public long getOfferToId() { return offerToId; }
		public int getItemType() { return itemType; }
		public String getOwnerName() { return ownerName; }
		public String getBidderName() { return bidderName; }
		public String getItemName() { return itemName; }
		public int getPrice() { return price; }
		public int getProxyBid() { return proxyBid; }
		public boolean isInstant() { return instant; }
		public String getVuid() { return vuid; }
		public AuctionState getStatus() { return state; }
		public int getAuctionOptions() { return auctionOptions; }
		
		public String getItemDescription() { return itemDescription; }
		public void setObjectId(long objectId) { this.objectId = objectId; }
		public void setOwnerId(long ownerId) { this.ownerId = ownerId; }
		public void setVendorId(long vendorId) { this.vendorId = vendorId; }
		public void setBuyerId(long buyerId) { this.buyerId = buyerId; }
		public void setOfferToId(long offerToId) { this.offerToId = offerToId; }
		public void setItemType(int itemType) { this.itemType = itemType; }
		public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
		public void setBidderName(String bidderName) { this.bidderName = bidderName; }
		public void setItemName(String itemName) { this.itemName = itemName; }
		public void setItemDescription(String itemDescription) { this.itemDescription = itemDescription; }
		public void setPrice(int price) { this.price = price; }
		public void setProxyBid(int proxyBid) { this.proxyBid = proxyBid; }
		public void setInstant(boolean instant) { this.instant = instant; }
		public void setVuid(String vuid) { this.vuid = vuid; }
		public void setStatus(AuctionState state) { this.state = state; }
		public int getExpireTime() { return expireTime; }
		public void setExpireTime(int expireTime) { this.expireTime = expireTime; }
		public void setAuctionOptions(int auctionOptions) { this.auctionOptions = auctionOptions; }
	}

}
