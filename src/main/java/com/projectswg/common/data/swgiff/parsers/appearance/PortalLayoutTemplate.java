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

package com.projectswg.common.data.swgiff.parsers.appearance;

import com.projectswg.common.data.location.Point3D;
import com.projectswg.common.data.math.IndexedTriangleList;
import com.projectswg.common.data.swgiff.IffChunk;
import com.projectswg.common.data.swgiff.IffForm;
import com.projectswg.common.data.swgiff.parsers.SWGParser;
import com.projectswg.common.data.swgiff.parsers.math.IndexedTriangleListParser;
import me.joshlarson.jlcommon.log.Log;

import java.util.ArrayList;
import java.util.List;

public class PortalLayoutTemplate implements SWGParser {
	
	private final List<IndexedTriangleList> portals;
	private final List<PortalLayoutCellTemplate> cells;
	
	private int crc;
	
	public PortalLayoutTemplate() {
		this.portals = new ArrayList<>();
		this.cells = new ArrayList<>();
		
		this.crc = 0;
	}
	
	public List<IndexedTriangleList> getPortals() {
		return portals;
	}
	
	public List<PortalLayoutCellTemplate> getCells() {
		return cells;
	}
	
	public int getCrc() {
		return crc;
	}
	
	@Override
	public void read(IffForm form) {
		assert form.getTag().equals("PRTO");
		
		int portalCount, cellCount;
		try (IffChunk chunk = form.readChunk("DATA")) {
			portalCount = chunk.readInt();
			cellCount = chunk.readInt();
		}
		
		switch (form.getVersion()) {
			case 0:
				load0000(form, portalCount, cellCount);
				break;
			case 1:
				load0001(form, portalCount, cellCount);
				break;
			case 2:
				load0002(form, portalCount, cellCount);
				break;
			case 3:
				load0003(form, portalCount, cellCount);
				break;
			default:
				Log.w("Unknown PRTO version: %d", form.getVersion());
			case 4:
				load0004(form, portalCount, cellCount);
				break;
		}
	}
	
	@Override
	public IffForm write() {
		return null;
	}
	
	@Override
	public String toString() {
		return "PortalLayoutTemplate[portals="+portals+" cells="+cells+" crc="+crc+"]";
	}
	
	private void load0000(IffForm form, int portalCount, int cellCount) {
		try (IffForm prts = form.readForm("PRTS")) {
			for (int i = 0; i < portalCount; i++)
				loadPRTL0(prts);
		}
		try (IffForm cels = form.readForm("CELS")) {
			for (int i = 0; i < cellCount; i++)
				loadCELL0(cels);
		}
		
		this.crc = 0;
	}
	
	private void load0001(IffForm form, int portalCount, int cellCount) {
		load0000(form, portalCount, cellCount);
		
		try (IffChunk chunk = form.readChunk("CRC ")) {
			this.crc = chunk.readInt();
		}
	}
	
	private void load0002(IffForm form, int portalCount, int cellCount) {
		load0001(form, portalCount, cellCount);
		// TODO: Path graph
		for (PortalLayoutCellTemplate cell : cells) {
			for (PortalLayoutCellPortalTemplate portal : cell.getPortals()) {
				portal.setPassable(!portal.isPassable());
			}
		}
	}
	
	private void load0003(IffForm form, int portalCount, int cellCount) {
		load0001(form, portalCount, cellCount);
		// TODO: Path graph
	}
	
	private void load0004(IffForm form, int portalCount, int cellCount) {
		try (IffForm prts = form.readForm("PRTS")) {
			for (int i = 0; i < portalCount; i++)
				loadPRTL4(prts);
		}
		try (IffForm cels = form.readForm("CELS")) {
			for (int i = 0; i < cellCount; i++)
				loadCELL0(cels);
		}
		try (IffChunk chunk = form.readChunk("CRC ")) {
			this.crc = chunk.readInt();
		}
	}
	
	private void loadPRTL0(IffForm form) {
		try (IffChunk chunk = form.readChunk("PRTL")) {
			int vertexCount = chunk.readInt();
			List<Point3D> vertices = new ArrayList<>();
			for (int i = 0; i < vertexCount; i++)
				vertices.add(chunk.readVector());
			portals.add(IndexedTriangleList.fromTriangleFan(vertices));
		}
	}
	
	private void loadPRTL4(IffForm form) {
		try (IffForm idtl = form.readForm("IDTL")) {
			IndexedTriangleListParser parser = SWGParser.parse(idtl);
			assert parser != null;
			portals.add(parser.getList());
		}
	}
	
	private void loadCELL0(IffForm form) {
		try (IffForm cell = form.readForm("CELL")) {
			cells.add(SWGParser.parse(cell));
		}
	}
	
}
