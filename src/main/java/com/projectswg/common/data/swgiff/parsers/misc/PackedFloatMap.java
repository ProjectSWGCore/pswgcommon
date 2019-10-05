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

public class PackedFloatMap implements SWGParser {
	
	private PackedIntegerMap map;
	private double resolution;
	
	public PackedFloatMap() {
		this.map = null;
		this.resolution = 0;
	}
	
	@Override
	public void read(IffForm form) {
		assert form.getTag().equals("PFPM");
		assert form.getVersion() == 0;
		
		map = new PackedIntegerMap();
		map.read(form.readForm("PIMP"));
		
		try (IffChunk chunk = form.readChunk("CNTL")) {
			resolution = chunk.readFloat();
		}
	}
	
	@Override
	public IffForm write() {
		if (map == null)
			throw new IllegalStateException("packed integer map has not been initialized");
		
		IffForm pimp = map.write();
		
		IffChunk cntl = new IffChunk("CNTL");
		cntl.writeFloat((float) resolution);
		
		return IffForm.of("PFPM", 0, pimp, cntl);
	}
	
	public int getWidth() {
		return map.getWidth();
	}
	
	public int getHeight() {
		return map.getHeight();
	}
	
	public double getValue(int x, int y) {
		PackedIntegerMap map = this.map;
		return map == null ? 0.0 : map.getValue(x, y) * resolution;
	}
	
}
