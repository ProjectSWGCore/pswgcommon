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

import com.projectswg.common.network.NetBuffer;

import java.util.EnumSet;


public class CommandTimer extends ObjectController {
	
	public static final int CRC = 0x0448;
	
	private final EnumSet<CommandTimerFlag> flags;
	private float warmupTime = 0xFFFFFFFF;
	private int sequenceId;
	private int commandNameCrc;
	private int cooldownGroupCrc;
	private float globalCooldownReductionTime;
	private float globalCooldownTime;
	private float cooldownGroupTime;
	
	public CommandTimer(long objectId) {
		super(objectId, CRC);
		this.flags = EnumSet.noneOf(CommandTimerFlag.class);
	}
	
	public CommandTimer(NetBuffer data) {
		super(CRC);
		this.flags = EnumSet.noneOf(CommandTimerFlag.class);
		decode(data);
	}
	
	public void decode(NetBuffer data) {
		decodeHeader(data);
	}
	
	public NetBuffer encode() {
		NetBuffer data = NetBuffer.allocate(HEADER_LENGTH + 41);
		encodeHeader(data);
		data.addByte(flagsToByte());	// 0 for no cooldown, 0x26 to add defaultTime (usually 0.25)
		data.addInt(sequenceId);
		data.addInt(commandNameCrc);
		data.addInt(cooldownGroupCrc);
		data.addFloat(warmupTime);
		data.addFloat(globalCooldownReductionTime);
		data.addFloat(globalCooldownTime);
		data.addInt(0);
		data.addFloat(cooldownGroupTime);
		data.addInt(0);
		data.addInt(0);
		return data;
	}
	
	public EnumSet<CommandTimerFlag> getFlags() {
		return flags.clone();
	}
	
	public void addFlag(CommandTimerFlag flag) {
		this.flags.add(flag);
	}
	
	public void clearFlag(CommandTimerFlag flag) {
		this.flags.remove(flag);
	}

	public int getSequenceId() {
		return sequenceId;
	}

	public void setSequenceId(int sequenceId) {
		this.sequenceId = sequenceId;
	}

	public int getCommandNameCrc() {
		return commandNameCrc;
	}

	public void setCommandNameCrc(int commandNameCrc) {
		this.commandNameCrc = commandNameCrc;
	}

	public int getCooldownGroupCrc() {
		return cooldownGroupCrc;
	}

	public void setCooldownGroupCrc(int cooldownGroupCrc) {
		this.cooldownGroupCrc = cooldownGroupCrc;
	}

	public float getGlobalCooldownReductionTime() {
		return globalCooldownReductionTime;
	}

	public void setGlobalCooldownReductionTime(float globalCooldownReductionTime) {
		this.globalCooldownReductionTime = globalCooldownReductionTime;
	}

	public float getGlobalCooldownTime() {
		return globalCooldownTime;
	}

	public void setGlobalCooldownTime(float cooldownMax) {
		this.globalCooldownTime = cooldownMax;
	}
	
	public void setCooldownGroupTime(float cooldownGroupTime) {
		this.cooldownGroupTime = cooldownGroupTime;
	}
	
	public float getWarmupTime() {
		return warmupTime;
	}
	
	public void setWarmupTime(float warmupTime) {
		this.warmupTime = warmupTime;
	}
	
	@Override
	protected String getPacketData() {
		return createPacketInformation(
				"objId", getObjectId(),
				"sequence", sequenceId,
				"name", com.projectswg.common.data.CRC.getString(commandNameCrc),
				"cooldownGroup", com.projectswg.common.data.CRC.getString(cooldownGroupCrc),
				"globalCooldownReductionTime", globalCooldownReductionTime,
				"globalCooldownTime", globalCooldownTime,
				"cooldownGroupTime", cooldownGroupTime
		);
	}
	
	private byte flagsToByte() {
		int b = 0;
		for (CommandTimerFlag flag : flags) {
			b |= flag.getBitmask();
		}
		return (byte) b;
	}
	
	public enum CommandTimerFlag {
		WARMUP       (0x01), // 1
		EXECUTE      (0x02), // 2
		COOLDOWN     (0x04), // 4
		FAILED       (0x08), // 8
		FAILED_RETRY (0x10), // 16
		COOLDOWN2    (0x20); // 32
		
		private final int bitmask;
		
		CommandTimerFlag(int bitmask) {
			this.bitmask = bitmask;
		}
		
		public int getBitmask() {
			return bitmask;
		}
		
		public static EnumSet<CommandTimerFlag> getFlags(int bits) {
			EnumSet<CommandTimerFlag> states = EnumSet.noneOf(CommandTimerFlag.class);
			for (CommandTimerFlag state : values()) {
				if ((state.getBitmask() & bits) != 0)
					states.add(state);
			}
			return states;
		}
	}
}
