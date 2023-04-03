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

package com.projectswg.common.network.packets.swg.zone

import com.projectswg.common.encoding.StringType
import com.projectswg.common.network.NetBuffer
import com.projectswg.common.network.packets.SWGPacket

data class FactionResponseMessage(
	var factionRank: String = "",
	var rebelPoints: Int = 0,
	var imperialPoints: Int = 0,
	var huttPoints: Int = 0,
	var factionNames: List<String> = mutableListOf(),
	var factionPoints: List<Float> = mutableListOf(),
) : SWGPacket() {

	override fun decode(data: NetBuffer) {
		if (!super.checkDecode(data, CRC)) return
		factionRank = data.ascii
	}

	override fun encode(): NetBuffer {
		val factionRankSize = Short.SIZE_BYTES + factionRank.length
		val factionNamesSize = 4 + (factionNames.size * Short.SIZE_BYTES) + factionNames.joinToString(separator = "").length
		val factionPointsSize = 4 + (factionPoints.size * Float.SIZE_BYTES)
		val data = NetBuffer.allocate(18 + factionRankSize + factionNamesSize + factionPointsSize)

		data.addShort(7)
		data.addInt(CRC)
		data.addAscii(factionRank)
		data.addInt(rebelPoints)
		data.addInt(imperialPoints)
		data.addInt(huttPoints)
		data.addList(factionNames, StringType.ASCII)
		data.addInt(factionPoints.size)
		factionPoints.forEach { data.addFloat(it) }
		return data
	}

	companion object {

		val CRC = getCrc("FactionResponseMessage")

	}

}