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

import com.projectswg.common.data.EnumLookup;
import com.projectswg.common.network.NetBuffer;

public class SecureTrade extends ObjectController {
	
	public static final int CRC = 0x0115;
	
	private TradeMessageType type;
	private long starterId;
	private long accepterId;
	
	public SecureTrade(TradeMessageType type, long starterId, long accepterId) {
		super(CRC);
		this.type = type;
		this.starterId = starterId;
		this.accepterId = accepterId;
	}
	
	public SecureTrade(NetBuffer data) {
		super(CRC);
		decode(data);
	}
	
	@Override
	public void decode(NetBuffer data) {
		decodeHeader(data);
		type = TradeMessageType.getTypeForInt(data.getInt());
		starterId = data.getLong();
		accepterId = data.getLong();		
	}
	
	@Override
	public NetBuffer encode() {
		NetBuffer data = NetBuffer.allocate(HEADER_LENGTH + 20);
		encodeHeader(data);
		data.addInt(type.getId());
		data.addLong(starterId);
		data.addLong(accepterId);
		return data;
	}
	
	public TradeMessageType getType() {
		return type;
	}
	
	public void setType(TradeMessageType type) {
		this.type = type;
	}
		
	public long getStarterId() {
		return starterId;
	}
	
	public long getAccepterId() {
		return accepterId;
	}
	
	public static enum TradeMessageType {
		UNDEFINED					(Integer.MIN_VALUE),
		REQUEST_TRADE				(0),
		TRADE_REQUESTED				(1),
		ACCEPT_TRADE				(2),
		DENIED_TRADE				(3),
		DENIED_PLAYER_BUSY			(4),
		DENIED_PLAYER_UNREACHABLE	(5),
		REQUEST_TRADE_REVERSED		(6),
		LAST_TRADE_MESSAGE			(7);
		
		private static final EnumLookup<Integer, TradeMessageType> LOOKUP = new EnumLookup<>(TradeMessageType.class, t -> t.getId());
		
		private int id;
		
		TradeMessageType(int id) {
			this.id = id;
		}	
		
		public int getId() {
			return id;
		}
		
		public static TradeMessageType getTypeForInt(int id) {
			return LOOKUP.getEnum(id, UNDEFINED);
		}
	}
}
