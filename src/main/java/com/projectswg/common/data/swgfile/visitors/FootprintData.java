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

package com.projectswg.common.data.swgfile.visitors;

import com.projectswg.common.data.swgfile.ClientData;
import com.projectswg.common.data.swgfile.IffNode;
import com.projectswg.common.data.swgfile.SWGFile;
import me.joshlarson.jlcommon.log.Log;

import java.util.Arrays;

public class FootprintData extends ClientData {
	
	private int width = 0;
	private int height = 0;
	private int pivotX = 0;
	private int pivotZ = 0;
	private double hardReservationTolerance = 0;
	private double structureReservationTolerance = 0;
	private FootprintCell [][] footprint = null;
	
	@Override
	public void readIff(SWGFile iff) {
		assert iff.getCurrentForm().getTag().equals("FOOT");
		
		IffNode form;
		while((form = iff.enterNextForm()) != null) {
			switch (form.getVersionFromTag()) {
				default:
					Log.e("Unknown version for footprint clientdata: %d", form.getVersionFromTag());
				case 0:
					parseInfo(iff.enterChunk("INFO"));
					parseData(iff.enterChunk("DATA"));
					parseFootprint(iff.enterChunk("PRNT"));
					break;
			}
			
			iff.exitForm();
			iff.exitForm();
		}
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getPivotX() {
		return pivotX;
	}
	
	public int getPivotZ() {
		return pivotZ;
	}
	
	public double getHardReservationTolerance() {
		return hardReservationTolerance;
	}
	
	public double getStructureReservationTolerance() {
		return structureReservationTolerance;
	}
	
	public FootprintCell[][] getFootprint() {
		FootprintCell [][] footprintCopy = new FootprintCell[height][width];
		for (int i = 0; i < height; i++)
			footprintCopy[i] = Arrays.copyOf(footprint[i], width);
		return footprintCopy;
	}
	
	private void parseInfo(IffNode chunk) {
		width = chunk.readInt();
		height = chunk.readInt();
		pivotX = chunk.readInt();
		pivotZ = chunk.readInt();
		hardReservationTolerance = chunk.readFloat();
		structureReservationTolerance = chunk.readFloat();
	}
	
	private void parseData(IffNode chunk) {
		if (chunk == null)
			return; // optional chunk
		chunk.readInt(); // boxTestRect.x0
		chunk.readInt(); // boxTestRect.y0
		chunk.readInt(); // boxTestRect.x1
		chunk.readInt(); // boxTestRect.y1
	}
	
	private void parseFootprint(IffNode chunk) {
		footprint = new FootprintCell[height][width];
		for (int y = 0; y < height; y++) {
			String strip = chunk.readString();
			assert strip.length() == width;
			for (int x = 0; x < width; x++) {
				switch (strip.charAt(x)) {
					case 'F':
					case 'f':
						footprint[y][x] = FootprintCell.STRUCTURE;
						break;
					case 'H':
					case 'h':
						footprint[y][x] = FootprintCell.HARDPOINT;
						break;
					case '.':
					default:
						footprint[y][x] = FootprintCell.NOTHING;
						break;
				}
			}
		}
	}
	
	public enum FootprintCell {
		STRUCTURE,
		HARDPOINT,
		NOTHING
	}
	
}
