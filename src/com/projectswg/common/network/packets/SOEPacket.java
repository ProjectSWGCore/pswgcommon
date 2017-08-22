package com.projectswg.common.network.packets;

import java.net.SocketAddress;
import java.time.Instant;

import com.projectswg.common.network.NetBuffer;

public abstract class SOEPacket {
	
	private SocketAddress address;
	private NetBuffer data;
	private Instant time;
	private int opcode;
	
	public SOEPacket() {
		this.address = null;
		this.data = null;
		this.time = null;
		this.opcode = 0;
	}
	
	public void decode(NetBuffer data) {
		data.position(0);
		this.data = NetBuffer.allocate(data.limit());
		this.data.addRawArray(data.getArray(this.data.limit()));
		
		data.position(0);
		opcode = data.getNetShort();
	}
	
	public void encode(NetBuffer data, int opcode) {
		data.addNetShort(opcode);
	}
	
	public abstract NetBuffer encode();
	
	public SocketAddress getAddress() {
		return address;
	}
	
	public NetBuffer getData() {
		return data;
	}
	
	public Instant getTime() {
		return time;
	}
	
	public int getOpcode() {
		return opcode;
	}
	
	public void setAddress(SocketAddress address) {
		this.address = address;
	}
	
	public void setData(NetBuffer data) {
		this.data = data;
	}
	
	public void setTime(Instant time) {
		this.time = time;
	}
	
	public void setOpcode(int opcode) {
		this.opcode = opcode;
	}
	
}
