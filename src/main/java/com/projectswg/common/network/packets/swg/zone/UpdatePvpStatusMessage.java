/***********************************************************************************
 * Copyright (c) 2024 /// Project SWG /// www.projectswg.com                       *
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

import com.projectswg.common.data.encodables.tangible.PvpFaction;
import com.projectswg.common.data.encodables.tangible.PvpFlag;
import com.projectswg.common.network.NetBuffer;
import com.projectswg.common.network.packets.SWGPacket;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public class UpdatePvpStatusMessage extends SWGPacket {
	public static final int CRC = getCrc("UpdatePvpStatusMessage");

	private EnumSet<PvpFlag> pvpFlags;
	private PvpFaction pvpFaction;
	private long objId;
	
	public UpdatePvpStatusMessage() {
		pvpFlags = EnumSet.noneOf(PvpFlag.class);
		pvpFaction = PvpFaction.NEUTRAL;
		objId = 0;
	}
	
	public UpdatePvpStatusMessage(PvpFaction pvpFaction, long objId, Set<PvpFlag> pvpFlags) {
		this.pvpFlags = EnumSet.copyOf(pvpFlags);
		this.pvpFaction = pvpFaction;
		this.objId = objId;
	}
	
	public void decode(@NotNull NetBuffer data) {
		if (!super.checkDecode(data, CRC))
			return;
		
		pvpFlags = PvpFlag.Companion.getFlags(data.getInt());
		pvpFaction = PvpFaction.Companion.getFactionForCrc(data.getInt());
		objId = data.getLong();
	}
	
	public NetBuffer encode() {
		NetBuffer data = NetBuffer.allocate(22);
		
		data.addShort(4);
		data.addInt(CRC);
		data.addInt(pvpFlags.stream().mapToInt(PvpFlag::getBitmask).reduce(0, (a, b) -> a | b));
		data.addInt(pvpFaction.getCrc());
		data.addLong(objId);
		return data;
	}
	
	public long getObjectId() { return objId; }
	public PvpFaction getPlayerFaction() { return pvpFaction; }
	public Set<PvpFlag> getPvpFlags() { return Collections.unmodifiableSet(pvpFlags); }
	
	@Override
	protected String getPacketData() {
		return createPacketInformation(
				"objId", objId,
				"faction", pvpFaction,
				"flags", pvpFlags
		);
	}
	
}
