/***********************************************************************************
 * Copyright (c) 2022 /// Project SWG /// www.projectswg.com                       *
 *                                                                                 *
 * ProjectSWG is the first NGE emulator for Star Wars Galaxies founded on          *
 * July 7th, 2011 after SOE announced the official shutdown of Star Wars Galaxies. *
 * Our goal is to create an emulator which will provide a server for players to    *
 * continue playing a game similar to the one they used to play. We are basing     *
 * it on the final publish of the game prior to end-game events.                   *
 *                                                                                 *
 * This file is part of Holocore.                                                  *
 *                                                                                 *
 * --------------------------------------------------------------------------------*
 *                                                                                 *
 * Holocore is free software: you can redistribute it and/or modify                *
 * it under the terms of the GNU Affero General Public License as                  *
 * published by the Free Software Foundation, either version 3 of the              *
 * License, or (at your option) any later version.                                 *
 *                                                                                 *
 * Holocore is distributed in the hope that it will be useful,                     *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                  *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                   *
 * GNU Affero General Public License for more details.                             *
 *                                                                                 *
 * You should have received a copy of the GNU Affero General Public License        *
 * along with Holocore.  If not, see <http://www.gnu.org/licenses/>.               *
 ***********************************************************************************/

package com.projectswg.common.network.packets.swg.zone

import com.projectswg.common.data.location.Point3D
import com.projectswg.common.network.NetBuffer
import com.projectswg.common.network.packets.SWGPacket

data class CharacterSheetResponseMessage(
	var bornDate: Int = 0,
	var played: Int = 0,
	var bindLocation: Point3D = Point3D(),
	var bindPlanet: String = "",
	var bankLocation: Point3D = Point3D(),
	var bankPlanet: String = "",
	var residenceLocation: Point3D = Point3D(),
	var residencePlanet: String = "",
	var spouseName: String = "",
	var lotsUsed: Int = 0,
	var factionCrc: Int = 0,
	var factionStatus: Int = 0
) : SWGPacket() {
	
	override fun decode(data: NetBuffer) {
		if (!super.checkDecode(data, CRC))
			return
		bornDate = data.int
		played = data.int
		bindLocation.decode(data)
		bindPlanet = data.ascii
		bankLocation.decode(data)
		bankPlanet = data.ascii
		residenceLocation.decode(data)
		residencePlanet = data.ascii
		spouseName = data.unicode
		lotsUsed = data.int
		factionCrc = data.int
		factionStatus = data.int
	}
	
	override fun encode(): NetBuffer {
		val data = NetBuffer.allocate(72 +
				bindPlanet.length + bankPlanet.length +
				residencePlanet.length + spouseName.length * 2)
		
		data.addShort(13)
		data.addInt(CRC)
		data.addInt(bornDate)
		data.addInt(played)
		data.addFloat(bindLocation.x.toFloat())
		data.addFloat(bindLocation.y.toFloat())
		data.addFloat(bindLocation.z.toFloat())
		data.addAscii(bindPlanet)
		data.addFloat(bankLocation.x.toFloat())
		data.addFloat(bankLocation.y.toFloat())
		data.addFloat(bankLocation.z.toFloat())
		data.addAscii(bankPlanet)
		data.addFloat(residenceLocation.x.toFloat())
		data.addFloat(residenceLocation.y.toFloat())
		data.addFloat(residenceLocation.z.toFloat())
		data.addAscii(residencePlanet)
		data.addUnicode(spouseName)
		data.addInt(lotsUsed)
		data.addInt(factionCrc)
		data.addInt(factionStatus)
		return data
	}
	
	companion object {
		
		val CRC = getCrc("CharacterSheetResponseMessage")
		
	}
	
}