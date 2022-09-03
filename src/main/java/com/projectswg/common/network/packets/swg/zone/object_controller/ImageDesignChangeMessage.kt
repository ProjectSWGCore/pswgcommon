package com.projectswg.common.network.packets.swg.zone.object_controller

import com.projectswg.common.data.customization.CustomizationString
import com.projectswg.common.network.NetBuffer

class ImageDesignChangeMessage : ObjectController {

	constructor(receiverId: Long) : super(receiverId, CRC)

	constructor(data: NetBuffer) : super(CRC) {
		decode(data)
	}

	companion object {
		const val CRC = 0x0238
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
		morphParameters = data.getList(MorphParameter::class.java)
		indexParameters = data.getList(IndexParameter::class.java)
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