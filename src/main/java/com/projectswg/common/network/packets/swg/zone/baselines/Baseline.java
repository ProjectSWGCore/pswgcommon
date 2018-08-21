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
package com.projectswg.common.network.packets.swg.zone.baselines;

import com.projectswg.common.network.NetBuffer;
import com.projectswg.common.network.packets.SWGPacket;

import java.nio.charset.StandardCharsets;

public class Baseline extends SWGPacket {
	public static final int CRC = getCrc("BaselinesMessage");

	private BaselineType type;
	private int num;
	private short opCount;
	private long objId;
	private byte [] baseData;
	
	public Baseline() {
		
	}
	
	public Baseline(long objId, Baseline subData) {
		this.objId = objId;
		type = subData.getType();
		num = subData.getNum();
		baseData = subData.encodeBaseline().array();
	}
	
	@Override
	public void decode(NetBuffer data) {
		if (!super.checkDecode(data, CRC))
			return;
		objId = data.getLong();
		type = BaselineType.valueOf(new StringBuffer(new String(data.getArray(4), StandardCharsets.UTF_8)).reverse().toString());
		num = data.getByte();
		baseData = data.getArrayLarge();
		if (baseData.length >= 2)
			opCount = NetBuffer.wrap(baseData).getShort();
	}
	
	@Override
	public NetBuffer encode() {
		NetBuffer data = NetBuffer.allocate(25 + baseData.length);
		data.addShort(5);
		data.addInt(CRC);
		data.addLong(objId);
		data.addRawArray(new StringBuffer(type.toString()).reverse().toString().getBytes(StandardCharsets.UTF_8));
		data.addByte(num);
		data.addInt(baseData.length + 2);
		data.addShort((opCount == 0 ? 5 : opCount));
		data.addRawArray(baseData);
		return data;
	}
	
	public NetBuffer encodeBaseline() { return NetBuffer.allocate(0); }
	
	public long getObjectId() { return objId; }
	
	public void setType(BaselineType type) { this.type = type; }
	public void setNum(int num) { this.num = num; }
	public void setId(long id) { this.objId = id; }
	public void setBaselineData(byte [] data) { this.baseData = data; }
	
	public enum BaselineType {
		/** Battlefield Marker > Tangible */
		BMRK,
		/** Building > Tangible */
		BUIO,
		/** City > Universe */
		CITY,
		/** Construction Contract > Intangible */
		CONC,
		/** Creature > Tangible */
		CREO,
		/** Draft Schematic > Intangible */
		DSCO,
		/** Factory > Tangible */
		FCYT,
		/** Guild > Universe */
		GILD,
		/** Group > Universe */
		GRUP,
		/** Harvester Installation > Installation */
		HINO,
		/** Installation > Tangible */
		INSO,
		/** Intangible > Object */
		ITNO,
		/** Jedi > Universe */
		JEDI,
		/** Manufacture Installation > Installation */
		MINO,
		/** Mission Board > Universe */
		MISB,
		/** Mission Data > Intangible */
		MISD,
		/** Mission > Intangible */
		MISO,
		/** Mission List > Intangible */
		MLEO,
		/** Manufacture Schematic > Intangible */
		MSCO,
		/** Planet Object > Universe */
		PLAN,
		/** Player > Intangible */
		PLAY,
		/** Player Quest > Tangible */
		PQOS,
		/** Resource Container > Tangible */
		RCNO,
		/** Cell Object > Object */
		SCLT,
		/** Ship > Tangible */
		SHIP,
		/** Static > Object */
		STAO,
		/** Object */
		SWOO,
		/** Tangible > Object */
		TANO,
		/** Token > Intangible */
		TOKN,
		/** Universe > Object */
		UNIO,
		/** Vehicle > Tangible */
		VEHO,
		/** Waypoint > Intangible */
		WAYP,
		/** Weapon > Tangible */
		WEAO,
		/** XP Manager > Universe */
		XPMG
	}
	
	public void setOperandCount(int count) { this.opCount = (short) count;}
	public BaselineType getType() { return type; }
	public int getNum() { return num; }
	public long getId() { return objId; }
	
	public byte [] getBaselineData() { return baseData; }
	
	@Override
	public String getPacketData() {
		return createPacketInformation(
				"objId", objId,
				"type", type,
				"num", num
		);
	}
	
}
