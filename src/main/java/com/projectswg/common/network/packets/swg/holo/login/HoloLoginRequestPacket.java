package com.projectswg.common.network.packets.swg.holo.login;

import com.projectswg.common.network.NetBuffer;
import com.projectswg.common.network.packets.swg.holo.HoloPacket;

public class HoloLoginRequestPacket extends HoloPacket {
	
	public static final int CRC = getCrc("HoloLoginRequestPacket");
	
	private String username;
	private String password;
	
	public HoloLoginRequestPacket() {
		this.username = "";
		this.password = "";
	}
	
	public HoloLoginRequestPacket(NetBuffer data) {
		decode(data);
	}
	
	public HoloLoginRequestPacket(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	@Override
	public final void decode(NetBuffer data) {
		if (!super.checkDecode(data, CRC))
			return;
		this.username = data.getAscii();
		this.password = data.getAscii();
	}
	
	@Override
	public final NetBuffer encode() {
		NetBuffer data = NetBuffer.allocate(10 + username.length() + password.length());
		data.addShort(3);
		data.addInt(CRC);
		data.addAscii(username);
		data.addAscii(password);
		return data;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
}
