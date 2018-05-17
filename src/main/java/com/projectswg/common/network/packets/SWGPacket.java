package com.projectswg.common.network.packets;

import java.net.SocketAddress;

import com.projectswg.common.data.CRC;
import me.joshlarson.jlcommon.log.Log;
import com.projectswg.common.network.NetBuffer;

public abstract class SWGPacket {
	
	private SocketAddress socketAddress;
	private PacketType type;
	private int crc;
	
	public SWGPacket() {
		this.socketAddress = null;
		this.type = PacketType.UNKNOWN;
		this.crc = 0;
	}
	
	/**
	 * Sets the socket address that this packet was sent to or received from. Setting this value after it's received, or before it's sent has no effect
	 * 
	 * @param socketAddress the socket address
	 */
	public void setSocketAddress(SocketAddress socketAddress) {
		this.socketAddress = socketAddress;
	}
	
	public SocketAddress getSocketAddress() {
		return socketAddress;
	}
	
	public int getSWGOpcode() {
		return crc;
	}
	
	public PacketType getPacketType() {
		return type;
	}
	
	public boolean checkDecode(NetBuffer data, int crc) {
		data.getShort();
		this.crc = data.getInt();
		this.type = PacketType.fromCrc(crc);
		if (this.crc == crc)
			return true;
		Log.w("SWG Opcode does not match actual! Expected: 0x%08X  Actual: 0x%08X", crc, getSWGOpcode());
		return false;
	}
	
	public abstract void decode(NetBuffer data);
	public abstract NetBuffer encode();
	
	protected void packetAssert(boolean condition, String constraint) {
		if (!condition)
			throw new PacketSerializationException(this, constraint);
	}
	
	public static int getCrc(String string) {
		return CRC.getCrc(string);
	}
	
}
