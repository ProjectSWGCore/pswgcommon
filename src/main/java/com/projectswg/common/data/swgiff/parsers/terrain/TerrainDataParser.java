package com.projectswg.common.data.swgiff.parsers.terrain;

import com.projectswg.common.data.swgiff.IffChunk;
import com.projectswg.common.data.swgiff.IffForm;
import com.projectswg.common.data.swgiff.parsers.SWGParser;
import com.projectswg.common.data.swgiff.parsers.misc.PackedFloatMap;
import com.projectswg.common.data.swgiff.parsers.misc.PackedIntegerMap;
import me.joshlarson.jlcommon.log.Log;

public class TerrainDataParser implements SWGParser {
	
	public TerrainDataParser() {
		
	}
	
	@Override
	public void read(IffForm form) {
		form.readChunk("DATA").close();
		form.readForm("TGEN").close();
		Log.d("Children: %s", form.getChildren());
		assert form.getVersion() == 1;
		try (IffChunk chunk = form.readChunk("DATA")) {
			Log.d("mapWidth   = %f", chunk.readFloat());
			Log.d("chunkWidth = %f", chunk.readFloat());
			Log.d("width      = %d", chunk.readInt());
			Log.d("height     = %d", chunk.readInt());
			Log.d("rem        = %d", chunk.remaining());
		}
		try (IffChunk chunk = form.readChunk("WMAP")) {
			byte [] waterMap = chunk.readRemainingBytes();
			checkValue(waterMap, 3525, -4807); // 3525	4	-4807
			checkValue(waterMap, 3697, -4447); // 3525	4	-4807
			checkValue(waterMap, -1354, 2056); // 3525	4	-4807
		}
		try (IffChunk chunk = form.readChunk("SMAP")) {
			byte [] slopeMap = chunk.readRemainingBytes();
			checkValue(slopeMap, 3525, -4807); // 3525	4	-4807
			checkValue(slopeMap, 3697, -4447); // 3525	4	-4807
			checkValue(slopeMap, -1354, 2056); // 3525	4	-4807
		}
		
		PackedIntegerMap pimp = new PackedIntegerMap();
		PackedFloatMap pfpm = new PackedFloatMap();
		pimp.read(form.readForm("PIMP"));
		pfpm.read(form.readForm("PFPM"));
		
		Log.d("PIMP %dx%d", pimp.getWidth(), pimp.getHeight());
		Log.d("PFPM %dx%d", pfpm.getWidth(), pfpm.getHeight());
		
		checkIntValue(pimp, 3525, -4807);
		checkIntValue(pimp, 3697, -4447);
		checkIntValue(pimp, -1354, 2056);
		
		checkFloatValue(pfpm, 3525, -4807);
		checkFloatValue(pfpm, 3697, -4447);
		checkFloatValue(pfpm, -1354, 2056);
	}
	
	private void checkIntValue(PackedIntegerMap pim, int x, int z) {
		Log.d("PIMP (%d, %d) = %d", x, z, pim.getValue((int) ((x+8192) / 16384.0 * pim.getWidth()), (int) ((z+8192) / 16384.0 * pim.getHeight())));
	}
	
	private void checkFloatValue(PackedFloatMap pfm, int x, int z) {
		Log.d("PFPM (%d, %d) = %.3f", x, z, pfm.getValue((int) ((x+8192) / 16384.0 * pfm.getWidth()), (int) ((z+8192) / 16384.0 * pfm.getHeight())));
	}
	
	private void checkValue(byte [] map, int x, int z) {
		int mX = (int) ((x >= 0.f) ? Math.floor(x / 8.0) : Math.ceil(x / 8.0));
		if (mX < 0)
			mX -= 1;
		mX += (int) ((16384.0 / 8.0) / 2);
		
		int mZ = (int) ((z >= 0.f) ? Math.floor(z / 8.0) : Math.ceil(z / 8.0));
		if (mZ < 0)
			mZ -= 1;
		mZ += (int) ((16384.0 / 8.0) / 2);
		
		int index  = mX >> 3;
		int offset = mX % 8;
		
		if (index < 0 || index >= 256 || mZ < 0 || mZ >= 2048)
			return;
		
		Log.d("Value: (%d, %d) = %b", mX, mZ, (map[mZ * 256 + index] & (1 << offset)) != 0);
	}
	
	@Override
	public IffForm write() {
		return null;
	}
	
}
