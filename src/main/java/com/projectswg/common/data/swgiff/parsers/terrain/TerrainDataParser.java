package com.projectswg.common.data.swgiff.parsers.terrain;

import com.projectswg.common.data.swgiff.IffChunk;
import com.projectswg.common.data.swgiff.IffForm;
import com.projectswg.common.data.swgiff.parsers.SWGParser;
import me.joshlarson.jlcommon.log.Log;

public class TerrainDataParser implements SWGParser {
	
	public TerrainDataParser() {
		
	}
	
	@Override
	public void read(IffForm form) {
		form.readChunk("DATA").close();
		form.readForm("TGEN").close();
		try (IffForm bakedForm = form.readForm()) {
			assert bakedForm.getVersion() == 1;
			try (IffChunk chunk = bakedForm.readChunk("DATA")) {
				Log.d("mapWidth   = %f", chunk.readFloat());
				Log.d("chunkWidth = %f", chunk.readFloat());
				Log.d("width      = %d", chunk.readInt());
				Log.d("height     = %d", chunk.readInt());
			}
			try (IffChunk chunk = bakedForm.readChunk("WMAP")) {
				Log.d("waterMap   = %d", chunk.readByte() & 0xFF);
			}
			try (IffChunk chunk = bakedForm.readChunk("SMAP")) {
				Log.d("slopeMap   = %d", chunk.readByte() & 0xFF);
			}
		}
	}
	
	@Override
	public IffForm write() {
		return null;
	}
	
}
