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
package com.projectswg.common.network.packets.swg.zone;

import com.projectswg.common.data.WeatherType;
import com.projectswg.common.network.NetBuffer;
import com.projectswg.common.network.packets.SWGPacket;

public class ServerWeatherMessage extends SWGPacket {
	public static final int CRC = getCrc("ServerWeatherMessage");
	
	private WeatherType type;
	private float cloudVectorX;
	private float cloudVectorZ;
	private float cloudVectorY;
	
	@Override
	public void decode(NetBuffer data) {
		if (!super.checkDecode(data, CRC))
			return;
		
		switch (data.getInt()) {
			case 0:
			default:
				type = WeatherType.CLEAR;
				break;
			case 1:
				type = WeatherType.LIGHT;
				break;
			case 2:
				type = WeatherType.MEDIUM;
				break;
			case 3:
				type = WeatherType.HEAVY;
				break;
		}
		
		cloudVectorX = data.getFloat();
		cloudVectorZ = data.getFloat();
		cloudVectorY = data.getFloat();
	}
	
	@Override
	public NetBuffer encode() {
		NetBuffer data = NetBuffer.allocate(22);
		
		data.addShort(3);
		data.addInt(CRC);
		data.addInt(type.getValue());
		
		data.addFloat(cloudVectorX);
		data.addFloat(cloudVectorZ);
		data.addFloat(cloudVectorY);
		
		return data;
	}

	public WeatherType getType() {
		return type;
	}

	public void setType(WeatherType type) {
		this.type = type;
	}

	public float getCloudVectorX() {
		return cloudVectorX;
	}

	public void setCloudVectorX(float cloudVectorX) {
		this.cloudVectorX = cloudVectorX;
	}

	public float getCloudVectorZ() {
		return cloudVectorZ;
	}

	public void setCloudVectorZ(float cloudVectorZ) {
		this.cloudVectorZ = cloudVectorZ;
	}

	public float getCloudVectorY() {
		return cloudVectorY;
	}

	public void setCloudVectorY(float cloudVectorY) {
		this.cloudVectorY = cloudVectorY;
	}
	
}
