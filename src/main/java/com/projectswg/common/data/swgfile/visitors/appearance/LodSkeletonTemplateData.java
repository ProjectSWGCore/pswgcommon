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
package com.projectswg.common.data.swgfile.visitors.appearance;

import com.projectswg.common.data.swgfile.ClientData;
import com.projectswg.common.data.swgfile.IffNode;
import com.projectswg.common.data.swgfile.SWGFile;


public class LodSkeletonTemplateData extends ClientData {
	
	private int lodCount;
	
	public LodSkeletonTemplateData() {
		lodCount = 0;
	}
	
	@Override
	public void readIff(SWGFile iff) {
		IffNode node = iff.enterNextForm();
		switch (node.getTag()) {
			case "0000":
				readForm0(iff);
				break;
			default:
				System.err.println("Unknown LodMeshGeneratorTemplateData version: " + node.getTag());
				break;
		}
		iff.exitForm();
	}
	
	private void readForm0(SWGFile iff) {
		readInfo(iff.enterChunk("INFO"));
		for (int i = 0; i < lodCount; i++) {
			iff.enterNextForm();
			BasicSkeletonTemplate list = new BasicSkeletonTemplate();
			list.readIff(iff);
			iff.exitForm();
		}
	}
	
	private void readInfo(IffNode node) {
		lodCount = node.readShort();
	}
	
}
