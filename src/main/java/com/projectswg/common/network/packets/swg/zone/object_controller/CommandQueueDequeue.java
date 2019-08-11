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


public class CommandQueueDequeue extends ObjectController {
	
	public static final int CRC = 0x0117;
	
	private int counter;
	private float timer;
	private ErrorCode error;
	private int action;
	
	public CommandQueueDequeue(long objectId) {
		super(objectId, CRC);
	}
	
	public CommandQueueDequeue(long objectId, int counter, float timer, ErrorCode error, int action) {
		super(objectId, CRC);
		this.counter = counter;
		this.timer = timer;
		this.error = error;
		this.action = action;
	}
	
	public CommandQueueDequeue(NetBuffer data) {
		super(CRC);
		decode(data);
	}
	
	public void decode(NetBuffer data) {
		decodeHeader(data);
		counter = data.getInt();
		timer = data.getFloat();
		error = ErrorCode.getFromCode(data.getInt());
		action = data.getInt();
	}
	
	public NetBuffer encode() {
		NetBuffer data = NetBuffer.allocate(HEADER_LENGTH + 16);
		encodeHeader(data);
		data.addInt(counter);
		data.addFloat(timer);
		data.addInt(error.getCode());
		data.addInt(action);
		return data;
	}
	
	public int getCounter() { return counter; }
	public float getTimer() { return timer; }
	public ErrorCode getError() { return error; }
	public int getAction() { return action; }
	
	public void setCounter(int counter) { this.counter = counter; }
	public void setTimer(float timer) { this.timer = timer; }
	public void setError(ErrorCode error) { this.error = error; }
	public void setAction(int action) { this.action = action; }
	
	@Override
	protected String getPacketData() {
		return createPacketInformation(
				"objId", getObjectId(),
				"counter", counter,
				"timer", timer,
				"error", error,
				"action", action
		);
	}
	
	public enum ErrorCode {
		SUCCESS				(0),
		LOCOMOTION			(1),
		ABILITY				(2),
		TARGET_TYPE			(3),
		TARGET_RANGE		(4),
		/** Can't be in this state */
		STATE_PROHIBITED	(5),
		/** Need to be in a particular state */
		STATE_REQUIRED		(6),
		GOD_MODE			(7),
		CANCELLED			(8),
		MAX					(9);
		
		private static final EnumLookup<Integer, ErrorCode> CODE_LOOKUP = new EnumLookup<>(ErrorCode.class, ErrorCode::getCode);
		
		int code;
		
		ErrorCode(int code) {
			this.code = code;
		}
		
		public int getCode() {
			return code;
		}
		
		public static ErrorCode getFromCode(int code) {
			return CODE_LOOKUP.getEnum(code, ErrorCode.SUCCESS);
		}
		
	}
	
}
