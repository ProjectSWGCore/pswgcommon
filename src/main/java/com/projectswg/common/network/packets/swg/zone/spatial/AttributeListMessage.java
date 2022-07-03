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
package com.projectswg.common.network.packets.swg.zone.spatial;

import com.projectswg.common.network.NetBuffer;
import com.projectswg.common.network.packets.SWGPacket;

public class AttributeListMessage extends SWGPacket {
	public static final int CRC = getCrc("AttributeListMessage");
	
	private long objectId;
	private AttributeList attributeList;
	private int serverRevision;

	public AttributeListMessage() {
		this(0, new AttributeList(), 0);
	}

	public AttributeListMessage(long objectId, AttributeList attributeList, int serverRevision) {
		this.objectId = objectId;
		this.attributeList = attributeList;
		this.serverRevision = serverRevision;
	}
	
	public AttributeListMessage(NetBuffer data) {
		decode(data);
	}
	
	@Override
	public void decode(NetBuffer data) {
		if (!super.checkDecode(data, CRC))
			return;
		objectId = data.getLong();
		attributeList = data.getEncodable(AttributeList.class);
		serverRevision = data.getInt();
	}
	
	@Override
	public NetBuffer encode() {
		int size = attributeList.getLength();
		NetBuffer data = NetBuffer.allocate(18 + size);
		data.addShort(3);
		data.addInt(CRC);
		data.addLong(objectId);
		data.addEncodable(attributeList);
		data.addInt(serverRevision);
		return data;
	}
	
	public long getObjectId() {
		return objectId;
	}

	public AttributeList getAttributeList() {
		return attributeList;
	}

	public int getServerRevision() {
		return serverRevision;
	}

	public void setObjectId(long objectId) {
		this.objectId = objectId;
	}

	public void setAttributes(AttributeList attributeList) {
		this.attributeList = attributeList;
	}

	public void setServerRevision(int serverRevision) {
		this.serverRevision = serverRevision;
	}
}
