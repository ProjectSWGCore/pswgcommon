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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.projectswg.common.data.location.Point3D;
import com.projectswg.common.data.swgfile.ClientData;
import com.projectswg.common.data.swgfile.ClientFactory;
import com.projectswg.common.data.swgfile.IffNode;
import com.projectswg.common.data.swgfile.SWGFile;
import com.projectswg.common.data.swgfile.visitors.appearance.render.RenderData;
import com.projectswg.common.data.swgfile.visitors.appearance.render.RenderSegment;
import com.projectswg.common.data.swgfile.visitors.appearance.render.RenderableData;
import com.projectswg.common.data.swgfile.visitors.appearance.render.RenderableDataChild;

/**
 * @author Waverunner
 */
public class PortalLayoutData extends ClientData implements RenderableData {

	private List<Cell> cells = new LinkedList<>();
	private List<Point3D> radarPoints = new ArrayList<>();

	@Override
	public void readIff(SWGFile iff) {
		IffNode versionForm = iff.enterNextForm();
		if (versionForm == null) {
			System.err.println("Expected version for a POB IFF");
			return;
		}

		int version = versionForm.getVersionFromTag();
		switch(version) {
			case 1:
			case 2:
			case 3:
				readVersion3(iff);
				break;
			case 4:
				readVersion4(iff);
				break;
			default: System.err.println("Do not know how to handle POB version type " + version + " in file " + iff.getFileName());
		}
	}
	
	@Override
	public List<RenderData> getAllRenderData() {
		List<RenderData> render = new ArrayList<>();
		render.add(getHighestRenderData());
		for (Cell cell : cells) {
			if (cell.isSingleRenderData())
				render.add(cell.getRenderData());
			else
				render.addAll(cell.getRenderDataList());
		}
		return render;
	}
	
	@Override
	public RenderData getHighestRenderData() {
		RenderData ret = new RenderData();
		for (Cell cell : cells) {
			if (cell.isSingleRenderData())
				mergeAll(cell.getRenderData(), ret);
			else
				mergeAll(cell.getHighestRenderData(), ret);
		}
		return ret;
	}
	
	@Override
	public RenderData getProportionalRenderData(double percent) {
		RenderData ret = new RenderData();
		for (Cell cell : cells) {
			if (cell.isSingleRenderData())
				mergeAll(cell.getRenderData(), ret);
			else
				mergeAll(cell.getProportionalRenderData(percent), ret);
		}
		return ret;
	}
	
	private void mergeAll(RenderData src, RenderData dst) {
		if (src == null)
			return;
		for (RenderSegment segment : src.getSegments()) {
			dst.addSegment(segment);
		}
	}
	
	private void readVersion3(SWGFile iff) {
		IffNode data = iff.enterChunk("DATA");
		int portalCount = data.readInt();
		loadPortals3(iff, portalCount);
		loadCells(iff);
	}
	
	private void readVersion4(SWGFile iff) {
		IffNode data = iff.enterChunk("DATA");
		int portalCount = data.readInt();
		loadPortals4(iff, portalCount);
		loadCells(iff);
	}

	public List<Cell> getCells() {
		return cells;
	}
	
	public List<Point3D> getRadarPoints() {
		return radarPoints;
	}
	
	private void loadCells(SWGFile iff) {
		iff.enterForm("CELS");
		while (iff.enterForm("CELL") != null) {
			cells.add(new Cell(iff));
			iff.exitForm();
		}
		iff.exitForm(); // Exit CELS form
	}
	
	private void loadPortals3(SWGFile iff, int portalCount) {
		IffNode node = iff.enterForm("PRTS");
		if (node == null)
			System.err.println("Failed to enter PRTS!");
		for (int i = 0; i < portalCount; i++) {
			IffNode chunk = iff.enterChunk("PRTL");
			if (chunk == null)
				continue;
			int vertices = chunk.readInt();
			List<Point3D> points = new ArrayList<>(vertices);
			for (int j = 0; j < vertices; j++) {
				points.add(chunk.readVector());
			}
			buildRadar(points);
		}
		iff.exitForm();
	}
	
	private void loadPortals4(SWGFile iff, int portalCount) {
		IffNode node = iff.enterForm("PRTS");
		if (node == null)
			System.err.println("Failed to enter PRTS!");
		for (int i = 0; i < portalCount; i++) {
			iff.enterForm("IDTL");
			iff.enterForm("0000");
			IffNode chunk = iff.enterChunk("VERT");
			List<Point3D> points = new ArrayList<>(chunk.remaining() / 12);
			while (chunk.remaining() >= 12) {
				points.add(chunk.readVector());
			}
			chunk = iff.enterChunk("INDX");
			while (chunk.remaining() >= 4) {
				radarPoints.add(new Point3D(points.get(chunk.readInt())));
			}
			iff.exitForm();
			iff.exitForm();
		}
		iff.exitForm();
	}
	
	private void buildRadar(List<Point3D> points) {
//		Point3D first = points.get(0);
//		for (int i = 0; i < points.size()-2; i++) {
//			radarPoints.add(first);
//			radarPoints.add(points.get(i+1));
//			radarPoints.add(points.get(i+2));
//		}
		radarPoints.addAll(points);
	}

	public static class Cell extends ClientData {
		
		private String name;
		private String appearance;
		private List<Integer> portalIndices;
		private List<RenderData> renderDataList;
		private RenderData renderData;
		private boolean isSingleRenderData;
		
		public Cell(SWGFile iff) {
			this.name = null;
			this.appearance = null;
			this.portalIndices = new ArrayList<>();
			renderDataList = new ArrayList<>();
			renderData = new RenderData();
			isSingleRenderData = true;
			readIff(iff);
		}

		@Override
		public void readIff(SWGFile iff) {
			IffNode versionForm = iff.enterNextForm();
			if (versionForm == null) {
				System.err.println("Expected version for CELL in IFF " + iff.getFileName());
				return;
			}

			int version = versionForm.getVersionFromTag();
			switch(version) {
				case 3: readVersion3(iff); break;
				case 4:
				case 5: readVersion5(iff); break;
				default: System.err.println("Don't know how to handle version " + version + " CELL " + iff.getFileName());
			}

			iff.exitForm();
		}
		
		public List<Integer> getPortals() {
			return portalIndices;
		}
		
		public RenderData getRenderData() {
			return renderData;
		}
		
		public boolean isSingleRenderData() {
			return isSingleRenderData;
		}
		
		public List<RenderData> getRenderDataList() {
			return renderDataList;
		}
		
		public RenderData getHighestRenderData() {
			return renderDataList.isEmpty() ? null : renderDataList.get(renderDataList.size()-1);
		}
		
		public RenderData getProportionalRenderData(double percent) {
			return renderDataList.isEmpty() ? null : renderDataList.get((int) Math.min(renderDataList.size()-1, percent*renderDataList.size()+0.5));
		}
		
		private void readVersion3(SWGFile iff) {
			IffNode dataChunk = iff.enterChunk("DATA");
			int portalCount = dataChunk.readInt();
			dataChunk.readBoolean(); // canSeeParentCell
			appearance = dataChunk.readString();
			ClientData data = ClientFactory.getInfoFromFile(appearance);
			if (data instanceof RenderableDataChild) {
				renderData = ((RenderableDataChild) data).getRenderData();
				isSingleRenderData = true;
			} else if (data instanceof RenderableData) {
				renderDataList.clear();
				renderDataList.addAll(((RenderableData) data).getAllRenderData());
				isSingleRenderData = false;
			}
			
			readPortals(iff, portalCount);
		}
		
		private void readVersion5(SWGFile iff) {
			IffNode dataChunk = iff.enterChunk("DATA");
			int portalCount = dataChunk.readInt();
			dataChunk.readBoolean(); // canSeeParentCell
			name = dataChunk.readString();
			appearance = dataChunk.readString();
			ClientData data = ClientFactory.getInfoFromFile(appearance);
			if (data instanceof RenderableDataChild) {
				renderData = ((RenderableDataChild) data).getRenderData();
				isSingleRenderData = true;
			} else if (data instanceof RenderableData) {
				renderDataList.clear();
				renderDataList.addAll(((RenderableData) data).getAllRenderData());
				isSingleRenderData = false;
			}
			
			readPortals(iff, portalCount);
		}
		
		private void readPortals(SWGFile iff, int portalCount) {
			for (int i = 0; i < portalCount; i++) {
				iff.enterForm("PRTL");
				IffNode chunk = iff.enterNextChunk();
				int portalIndex;
				switch (chunk.getVersionFromTag()) {
					case 1:
						portalIndex = chunk.readInt();
						break;
					case 2:
					case 3:
					case 4:
						chunk.readByte(); // passable
						portalIndex = chunk.readInt();
						break;
					case 5:
					default:
						chunk.readByte(); // disabled
						chunk.readByte(); // passable
						portalIndex = chunk.readInt();
						break;
				}
				portalIndices.add(portalIndex);
				iff.exitForm();
			}
		}
		
		public String getName() {
			return name;
		}
		
		public String getAppearance() {
			return appearance;
		}
	}
}
