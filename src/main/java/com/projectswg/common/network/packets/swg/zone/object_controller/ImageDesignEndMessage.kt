/***********************************************************************************
 * Copyright (c) 2025 /// Project SWG /// www.projectswg.com                       *
 *                                                                                 *
 * ProjectSWG is an emulation project for Star Wars Galaxies founded on            *
 * July 7th, 2011 after SOE announced the official shutdown of Star Wars Galaxies. *
 * Our goal is to create one or more emulators which will provide servers for      *
 * players to continue playing a game similar to the one they used to play.        *
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
package com.projectswg.common.network.packets.swg.zone.object_controller

import com.projectswg.common.data.customization.CustomizationString
import com.projectswg.common.network.NetBuffer

class ImageDesignEndMessage : ObjectController {

	constructor(receiverId: Long) : super(receiverId, CRC)

	constructor(data: NetBuffer) : super(CRC) {
		decode(data)
	}

	companion object {
		const val CRC = 0x0239
	}

	var designerId = 0L
	var clientId = 0L
	var salonId = 0L
	var hairChanged = false
	var hair = ""
	var hairCustomization = CustomizationString()
	var designType = 0
	var startingTime = 0
	var moneyDemanded = 0
	var moneyOffered = 0
	var designerCommitted = false
	var clientAccepted = false
	var statMigration = false
	var bodySkillMod = 0
	var faceSkillMod = 0
	var markingsSkillMod = 0
	var hairSkillMod = 0
	var morphParameters: MutableList<MorphParameter> = ArrayList()
	var indexParameters: MutableList<IndexParameter> = ArrayList()
	var holoemote = ""

	override fun decode(data: NetBuffer) {
		decodeHeader(data)
		designerId = data.long
		clientId = data.long
		salonId = data.long
		hairChanged = data.boolean
		hair = data.ascii
		hairCustomization.decode(data)
		designType = data.int
		startingTime = data.int
		moneyDemanded = data.int
		moneyOffered = data.int
		designerCommitted = data.boolean
		clientAccepted = data.int != 0
		statMigration = data.boolean
		bodySkillMod = data.int
		faceSkillMod = data.int
		markingsSkillMod = data.int
		hairSkillMod = data.int
		morphParameters = data.getList(MorphParameter::class.java) ?: return
		indexParameters = data.getList(IndexParameter::class.java) ?: return
		holoemote = data.ascii
	}

	override fun encode(): NetBuffer {
		val booleanBytes = 3
		var morphParametersSize = Integer.BYTES
		for (morphParameter in morphParameters) {
			morphParametersSize += morphParameter.length
		}
		var indexParametersSize = Integer.BYTES
		for (indexParameter in indexParameters) {
			indexParametersSize += indexParameter.length
		}
		val holoemoteLength = 2 + holoemote.length
		val hairLength = 2 + hair.length
		val data = NetBuffer.allocate(HEADER_LENGTH + Long.SIZE_BYTES * 3 + booleanBytes + Integer.BYTES * 9 + hairLength + hairCustomization.length + morphParametersSize + indexParametersSize + holoemoteLength)
		encodeHeader(data)
		data.addLong(designerId)
		data.addLong(clientId)
		data.addLong(salonId)
		data.addBoolean(hairChanged)
		data.addAscii(hair)
		data.addEncodable(hairCustomization)
		data.addInt(designType)
		data.addInt(startingTime)
		data.addInt(moneyDemanded)
		data.addInt(moneyOffered)
		data.addBoolean(designerCommitted)
		data.addInt(if (clientAccepted) 1 else 0)
		data.addBoolean(statMigration)
		data.addInt(bodySkillMod)
		data.addInt(faceSkillMod)
		data.addInt(markingsSkillMod)
		data.addInt(hairSkillMod)
		data.addList(morphParameters)
		data.addList(indexParameters)
		data.addAscii(holoemote)

		return data
	}
}