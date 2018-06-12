package com.projectswg.common.network.packets.swg.zone.object_controller;

import com.projectswg.common.network.NetBuffer;

public class TeleportAck extends ObjectController {
	
	public static final int CRC = 0x013F;
	
	private int sequenceId;
	
	public TeleportAck(long objectId, int sequenceId) {
		super(objectId, CRC);
		this.sequenceId = sequenceId;
	}
	
	public TeleportAck(NetBuffer data) {
		super(CRC);
		decode(data);
	}
	
	public void decode(NetBuffer data) {
		decodeHeader(data);
		sequenceId = data.getInt();
	}
	
	public NetBuffer encode() {
		NetBuffer data = NetBuffer.allocate(HEADER_LENGTH + 4);
		encodeHeader(data);
		data.addInt(sequenceId);
		return data;
	}
	
	public int getSequenceId() {
		return sequenceId;
	}
	
	public void setSequenceId(int sequenceId) {
		this.sequenceId = sequenceId;
	}
	
}
