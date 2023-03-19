package com.projectswg.common.network.packets.swg.zone.object_controller

import com.projectswg.common.network.NetBuffer

class MissionAcceptResponse : ObjectController {

    var missionObjectId = 0L
    var terminalType = 0
    var success = 0
    
    constructor(receiverId: Long) : super(receiverId, CRC)

    constructor(data: NetBuffer) : super(CRC) {
        decode(data)
    }

    companion object {
        const val CRC = 0x00FA
    }
    
    override fun decode(data: NetBuffer) {
        decodeHeader(data)
        missionObjectId = data.long
        terminalType = data.byte.toInt()
        success = data.byte.toInt()
    }

    override fun encode(): NetBuffer {
        val data = NetBuffer.allocate(HEADER_LENGTH + 10)
        encodeHeader(data)
        data.addLong(missionObjectId)
        data.addByte(terminalType)
        data.addByte(success)
        return data
    }
}