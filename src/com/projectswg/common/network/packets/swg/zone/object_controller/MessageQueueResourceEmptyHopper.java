/************************************************************************************
 * Copyright (c) 2015 /// Project SWG /// www.projectswg.com                        *
 *                                                                                  *
 * ProjectSWG is the first NGE emulator for Star Wars Galaxies founded on           *
 * July 7th, 2011 after SOE announced the official shutdown of Star Wars Galaxies.  *
 * Our goal is to create an emulator which will provide a server for players to     *
 * continue playing a game similar to the one they used to play. We are basing      *
 * it on the final publish of the game prior to end-game events.                    *
 *                                                                                  *
 * This file is part of Holocore.                                                   *
 *                                                                                  *
 * -------------------------------------------------------------------------------- *
 *                                                                                  *
 * Holocore is free software: you can redistribute it and/or modify                 *
 * it under the terms of the GNU Affero General Public License as                   *
 * published by the Free Software Foundation, either version 3 of the               *
 * License, or (at your option) any later version.                                  *
 *                                                                                  *
 * Holocore is distributed in the hope that it will be useful,                      *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                   *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                    *
 * GNU Affero General Public License for more details.                              *
 *                                                                                  *
 * You should have received a copy of the GNU Affero General Public License         *
 * along with Holocore.  If not, see <http://www.gnu.org/licenses/>.                *
 *                                                                                  *
 ***********************************************************************************/
package com.projectswg.common.network.packets.swg.zone.object_controller;

import com.projectswg.common.network.NetBuffer;

public class MessageQueueResourceEmptyHopper extends ObjectController {

	public static final int CRC = 0x00ED;
	
	private long playerId;
	private long harvesterId;
	private int amount;
	private boolean discard;
	private int sequenceId;
		
	public MessageQueueResourceEmptyHopper(long playerId, long harvesterId, int amount, boolean discard, int sequenceId) {
		super(CRC);
		this.playerId = playerId;
		this.harvesterId = harvesterId;
		this.amount = amount;
		this.discard = discard;
		this.sequenceId = sequenceId;
	}
	
	public MessageQueueResourceEmptyHopper(NetBuffer data){
		super(CRC);
		decode(data);
	}	

	@Override
	public void decode(NetBuffer data) {
		decodeHeader(data);
		playerId = data.getLong();
		harvesterId = data.getLong();
		amount = data.getInt();
		discard = data.getBoolean();
		sequenceId = data.getInt();
	}

	@Override
	public NetBuffer encode() {
		NetBuffer data = NetBuffer.allocate(HEADER_LENGTH + 25 );
		encodeHeader(data);
		data.addLong(playerId);
		data.addLong(harvesterId);
		data.addInt(amount);
		data.addBoolean(discard);
		data.addInt(sequenceId);
		return data;
	}

	public long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}

	public long getHarvesterId() {
		return harvesterId;
	}

	public void setHarvesterId(long harvesterId) {
		this.harvesterId = harvesterId;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public boolean isDiscard() {
		return discard;
	}

	public void setDiscard(boolean discard) {
		this.discard = discard;
	}

	public int getSequenceId() {
		return sequenceId;
	}

	public void setSequenceId(int sequenceId) {
		this.sequenceId = sequenceId;
	}	
}