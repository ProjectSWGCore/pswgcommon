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
package com.projectswg.common.network.packets.swg.zone.object_controller.quest;

import com.projectswg.common.network.NetBuffer;
import com.projectswg.common.network.packets.swg.zone.object_controller.ObjectController;

public class QuestTaskCounterMessage extends ObjectController {

	public static final int CRC = 0x0441;

	private String questName;
	private int taskId;
	private String counterText;
	private int current;
	private int max;
	
	public QuestTaskCounterMessage(long objectId, String questName, int taskId, String counterText, int current, int max) {
		super(objectId, CRC);
		this.questName = questName;
		this.taskId = taskId;
		this.counterText = counterText;
		this.current = current;
		this.max = max;
	}
	
	public QuestTaskCounterMessage(NetBuffer data) {
		super(CRC);
		decode(data);
	}
	
	@Override
	public void decode(NetBuffer data) {
		decodeHeader(data);
		questName = data.getAscii();
		max = data.getInt();
		counterText = data.getUnicode();
		data.getInt();
		current = data.getInt();
	}

	@Override
	public NetBuffer encode() {
		NetBuffer data = NetBuffer.allocate(HEADER_LENGTH + 18 + questName.length() + counterText.length() * 2);
		encodeHeader(data);
		
		data.addAscii(questName);
		data.addInt(taskId);
		data.addUnicode(counterText);
		data.addInt(current);
		data.addInt(max);
		
		return data;
	}
}

