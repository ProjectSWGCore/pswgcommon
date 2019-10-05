/***********************************************************************************
 * Copyright (c) 2019 /// Project SWG /// www.projectswg.com                       *
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
package com.projectswg.common.data.swgiff.parsers.misc;

import com.projectswg.common.data.swgiff.IffChunk;
import com.projectswg.common.data.swgiff.IffForm;
import com.projectswg.common.data.swgiff.parsers.SWGParser;

import java.util.*;
import java.util.Map.Entry;

public class PackedIntegerMap implements SWGParser {
	
	private int width;
	private int height;
	private int bitsPerElement;
	private int minimumValue;
	private byte [] data;
	
	public PackedIntegerMap() {
		this.width = 0;
		this.height = 0;
		this.bitsPerElement = 0;
		this.data = new byte[0];
	}
	
	@Override
	public void read(IffForm form) {
		assert form.getTag().equals("PIMP");
		assert form.getVersion() == 0;
		
		try (IffChunk chunk = form.readChunk("CNTL")) {
			width = chunk.readInt();
			height = chunk.readInt();
			bitsPerElement = chunk.readInt();
			minimumValue = chunk.readInt();
		}
		try (IffChunk chunk = form.readChunk("DATA")) {
			data = chunk.readRemainingBytes();
		}
	}
	
	@Override
	public IffForm write() {
		IffChunk cntl = new IffChunk("CNTL");
		cntl.writeInt(width);
		cntl.writeInt(height);
		cntl.writeInt(bitsPerElement);
		cntl.writeInt(minimumValue);
		
		IffChunk data = new IffChunk("DATA");
		data.writeRemainingBytes(this.data);
		
		return IffForm.of("PIMP", 0, cntl, data);
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getValue(int x, int y) {
		int elementOffset = y * width + x;
		int bitOffset = elementOffset * bitsPerElement;
		int byteOffset = bitOffset / 8; // Divide by 8 to get the byte
		int bitIndex = bitOffset % 8; // Modulus 8 to get bit within byte
		
		int bits = 0;
		int destMask = 1;
		int srcMask = 1 << bitIndex;
		
		for (int i = 0; i < bitsPerElement; i++) {
			if ((data[byteOffset] & srcMask) != 0) {
				bits |= destMask;
			}
			destMask <<= 1;
			srcMask <<= 1;
			if (((byte) srcMask) == 0) { // If we overflowed our byte, move to the next one
				srcMask = 1;
				byteOffset++;
			}
		}
		
		return bits + minimumValue;
	}
	
}
