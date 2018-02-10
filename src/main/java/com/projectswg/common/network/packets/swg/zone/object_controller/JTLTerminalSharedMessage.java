package com.projectswg.common.network.packets.swg.zone.object_controller;

import com.projectswg.common.network.NetBuffer;

public class JTLTerminalSharedMessage extends ObjectController {

	public static final int CRC = 0x041C;
	
	private int tickCount;
	private long terminalId;	
	
	public JTLTerminalSharedMessage(NetBuffer data) {
		super(CRC);
	}

	public int getTickCount() {
		return tickCount;
	}

	public void setTickCount(int tickCount) {
		this.tickCount = tickCount;
	}

	public long getTerminalId() {
		return terminalId;
	}

	public void setTerminalId(long terminalId) {
		this.terminalId = terminalId;
	}

	@Override
	public void decode(NetBuffer data) {
		decodeHeader(data);
		setTickCount(data.getInt());
		setTerminalId(data.getLong());	
		
	}

	@Override
	public NetBuffer encode() {
		NetBuffer data = NetBuffer.allocate(HEADER_LENGTH + 12);
		encodeHeader(data);
		data.addInt(getTickCount());
		data.addLong(getTerminalId());			
		return data;
	}
}