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
package com.projectswg.common.data.encodables.oob;

import com.projectswg.common.data.encodables.mongo.MongoData;
import com.projectswg.common.data.encodables.mongo.MongoPersistable;
import com.projectswg.common.data.encodables.oob.OutOfBandPackage.Type;
import com.projectswg.common.data.encodables.oob.waypoint.WaypointPackage;
import com.projectswg.common.network.NetBufferStream;

public class OutOfBandFactory {
	
	public static void save(OutOfBandData oob, NetBufferStream stream) {
		if (oob instanceof StringId)
			stream.addByte(1);
		else if (oob instanceof ProsePackage)
			stream.addByte(2);
		else if (oob instanceof WaypointPackage) {
			stream.addByte(4);
		} else
			throw new IllegalArgumentException("Unknown OOB data!");
		oob.save(stream);
	}
	
	public static OutOfBandData create(NetBufferStream stream) {
		OutOfBandData oob;
		byte type = stream.getByte();
		switch (type) {
			case 1:
				oob = new StringId();
				break;
			case 2:
				oob = new ProsePackage();
				break;
			case 4:
				oob = new WaypointPackage();
				break;
			default:
				throw new IllegalStateException("Unknown type byte! Type: " + type);
		}
		oob.read(stream);
		return oob;
	}
	
	public static MongoData save(OutOfBandData oob) {
		return save(oob, new MongoData());
	}
	
	public static MongoData save(OutOfBandData oob, MongoData data) {
		switch (oob.getOobType()) {
			case STRING_ID:
			case PROSE_PACKAGE:
			case WAYPOINT:
				data.putString("oobType", oob.getOobType().name());
				break;
			default:
				throw new IllegalArgumentException("Unknown OOB data!");
		}
		oob.saveMongo(data);
		return data;
	}
	
	public static OutOfBandData create(MongoData data) {
		OutOfBandData oob;
		switch (OutOfBandPackage.Type.valueOf(data.getString("oobType", OutOfBandPackage.Type.UNDEFINED.name()))) {
			case STRING_ID:
				oob = new StringId();
				break;
			case PROSE_PACKAGE:
				oob = new ProsePackage();
				break;
			case WAYPOINT:
				oob = new WaypointPackage();
				break;
			default:
				throw new IllegalArgumentException("Unknown OOB data: " + data.getString("oobType"));
		}
		oob.readMongo(data);
		return oob;
	}
	
}
