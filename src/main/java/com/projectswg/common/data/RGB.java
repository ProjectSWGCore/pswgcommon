/***********************************************************************************
 * Copyright (c) 2018 /// Project SWG /// www.projectswg.com                       *
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
package com.projectswg.common.data;

import java.awt.Color;

import com.projectswg.common.encoding.Encodable;
import com.projectswg.common.network.NetBuffer;

public class RGB implements Encodable {
	
	private byte r;
	private byte g;
	private byte b;
	
	public RGB() {
		this(0, 0, 0);
	}
	
	public RGB(int r, int g, int b) {
		setR(r);
		setG(g);
		setB(b);
	}
	
	public RGB(Color c) {
		this(c.getRed(), c.getGreen(), c.getBlue());
	}
	
	@Override
	public byte[] encode() {
		NetBuffer buffer = NetBuffer.allocate(3);
		buffer.addByte(r);
		buffer.addByte(g);
		buffer.addByte(b);
		return buffer.array();
	}
	
	@Override
	public void decode(NetBuffer data) {
		r = data.getByte();
		g = data.getByte();
		b = data.getByte();
	}
	
	@Override
	public int getLength() {
		return 3;
	}
	
	public byte getR() {
		return r;
	}
	
	public void setR(int r) {
		this.r = (byte) r;
	}
	
	public byte getG() {
		return g;
	}
	
	public void setG(int g) {
		this.g = (byte) g;
	}
	
	public byte getB() {
		return b;
	}
	
	public void setB(int b) {
		this.b = (byte) b;
	}
	
}
