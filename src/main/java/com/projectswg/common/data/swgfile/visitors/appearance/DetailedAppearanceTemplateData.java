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

import java.util.ArrayList;
import java.util.List;

import com.projectswg.common.data.swgfile.ClientData;
import com.projectswg.common.data.swgfile.ClientFactory;
import com.projectswg.common.data.swgfile.IffNode;
import com.projectswg.common.data.swgfile.SWGFile;
import com.projectswg.common.data.swgfile.visitors.appearance.render.RenderData;
import com.projectswg.common.data.swgfile.visitors.appearance.render.RenderableData;
import com.projectswg.common.data.swgfile.visitors.appearance.render.RenderableDataChild;
import me.joshlarson.jlcommon.log.Log;

public class DetailedAppearanceTemplateData extends ClientData implements RenderableData {
	
	private final List<RenderableDataChild> children;
	private IndexedTriangleList radar;
	private IndexedTriangleList test;
	private IndexedTriangleList write;
	
	public DetailedAppearanceTemplateData() {
		children = new ArrayList<>();
		radar = null;
		test = null;
		write = null;
	}
	
	@Override
	public void readIff(SWGFile iff) {
		IffNode node = iff.enterNextForm();
		switch (node.getTag()) {
			case "0007":
				readForm7(iff);
				break;
			case "0008":
				readForm7(iff);
				break;
			default:
				System.err.println("Unknown DetailedAppearanceTemplateData version: " + node.getTag());
				break;
		}
		iff.exitForm();
	}
	
	@Override
	public List<RenderData> getAllRenderData() {
		List<RenderData> all = new ArrayList<>();
		for (RenderableDataChild child : children)
			all.add(child.getRenderData());
		return all;
	}
	
	@Override
	public RenderData getHighestRenderData() {
		return children.isEmpty() ? null : children.get(children.size()-1).getRenderData();
	}
	
	@Override
	public RenderData getProportionalRenderData(double percent) {
		return children.isEmpty() ? null : children.get((int) Math.min(children.size()-1, percent*children.size()+0.5)).getRenderData();
	}
	
	public IndexedTriangleList getRadar() {
		return radar;
	}
	
	public IndexedTriangleList getTest() {
		return test;
	}
	
	public IndexedTriangleList getWrite() {
		return write;
	}
	
	private void readForm7(SWGFile iff) {
		IffNode node = iff.enterForm("APPR");
		if (node != null) {
			readAppearance(iff);
			iff.exitForm();
		}
		node = iff.enterForm("RADR");
		radar = readVertices(iff);
		iff.exitForm();
		node = iff.enterForm("TEST");
		test = readVertices(iff);
		iff.exitForm();
		node = iff.enterForm("WRIT");
		write = readVertices(iff);
		iff.exitForm();
		node = iff.enterForm("DATA");
		readChildren(iff);
		iff.exitForm();
	}
	
	private void readAppearance(SWGFile iff) {
		AppearanceTemplateData appearance = new AppearanceTemplateData();
		appearance.readIff(iff);
	}
	
	private IndexedTriangleList readVertices(SWGFile iff) {
		IffNode node = iff.enterChunk("INFO");
		boolean hasVertices = node.readInt() != 0;
		if (!hasVertices)
			return new IndexedTriangleList();
		IndexedTriangleList list = new IndexedTriangleList();
		list.readIff(iff);
		return list;
	}
	
	private void readChildren(SWGFile iff) {
		IffNode node = null; // Only going to read the last one
		while ((node = iff.enterChunk("CHLD")) != null) {
			node.readInt(); // id
			String name = node.readString();
			ClientData child = ClientFactory.getInfoFromFile("appearance/"+name);
			if (child instanceof RenderableDataChild)
				children.add((RenderableDataChild) child);
			else
				Log.e("Child does not implement RenderableDataChild!");
		}
	}
	
}
