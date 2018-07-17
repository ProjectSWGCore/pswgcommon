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
