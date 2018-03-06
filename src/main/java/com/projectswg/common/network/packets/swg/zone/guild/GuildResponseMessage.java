/***********************************************************************************
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
package com.projectswg.common.network.packets.swg.zone.guild;

import com.projectswg.common.network.NetBuffer;
import com.projectswg.common.network.packets.SWGPacket;

public class GuildResponseMessage extends SWGPacket {
	
	public static final int CRC = getCrc("GuildResponseMessage");
	
	private long objectId;
	private String guildName;
	private String memberTitle;
	
	public GuildResponseMessage() {
	
	}
	
	public GuildResponseMessage(long objectId, String guildName, String memberTitle) {
		this.objectId = objectId;
		this.guildName = guildName;
		this.memberTitle = memberTitle;
	}
	
	@Override
	public void decode(NetBuffer data) {
		if (!super.checkDecode(data, CRC))
			return;
		
		objectId = data.getLong();
		guildName = data.getAscii();
		memberTitle = data.getAscii();
		data.getShort();	// TODO figure out
	}
	
	@Override
	public NetBuffer encode() {
		int length = 18 + guildName.length() + memberTitle.length();
		NetBuffer data = NetBuffer.allocate(length);
		data.addShort(3);
		data.addInt(CRC);
		data.addLong(objectId);
		data.addAscii(guildName);
		data.addAscii(memberTitle);
		return data;
	}
	
	public long getObjectId() {
		return objectId;
	}
	
	public String getGuildName() {
		return guildName;
	}
}