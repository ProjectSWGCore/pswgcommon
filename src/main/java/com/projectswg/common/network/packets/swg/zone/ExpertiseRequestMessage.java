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

import com.projectswg.common.network.NetBuffer;
import com.projectswg.common.network.packets.SWGPacket;
import org.jetbrains.annotations.NotNull;

public final class ExpertiseRequestMessage extends SWGPacket {
	
	public static final int CRC = getCrc("ExpertiseRequestMessage");
	
	private String[] requestedSkills;
	private boolean clearAllExpertisesFirst;
	
	public ExpertiseRequestMessage() {
		this(new String[0], false);
	}
	
	public ExpertiseRequestMessage(@NotNull String[] requestedSkills, boolean clearAllExpertisesFirst) {
		this.requestedSkills = requestedSkills.clone();
		this.clearAllExpertisesFirst = clearAllExpertisesFirst;
	}
	
	public ExpertiseRequestMessage(NetBuffer data) {
		decode(data);
	}
	
	@Override
	public void decode(NetBuffer data) {
		if (!super.checkDecode(data, CRC))
			return;
		requestedSkills = new String[data.getInt()];
		
		for (int i = 0; i < requestedSkills.length; i++) {
			requestedSkills[i] = data.getAscii();
		}
		
		clearAllExpertisesFirst = data.getBoolean();
	}
	
	@Override
	public NetBuffer encode() {
		int skillNamesLength = 0;
		
		for (String skillName : requestedSkills)
			skillNamesLength += 2 + skillName.length();
		
		NetBuffer data = NetBuffer.allocate(11 + skillNamesLength);
		data.addShort(3);
		data.addInt(CRC);
		data.addInt(requestedSkills.length);
		
		for (String requestedSkill : requestedSkills) {
			data.addAscii(requestedSkill);
		}
		
		data.addBoolean(clearAllExpertisesFirst);
		
		return data;
	}

	public String[] getRequestedSkills() {
		return requestedSkills;
	}

	public boolean isClearAllExpertisesFirst() {
		return clearAllExpertisesFirst;
	}

}
