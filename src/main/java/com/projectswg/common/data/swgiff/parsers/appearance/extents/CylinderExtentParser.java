package com.projectswg.common.data.swgiff.parsers.appearance.extents;

import com.projectswg.common.data.math.extents.CylinderExtent;
import com.projectswg.common.data.swgiff.IffChunk;
import com.projectswg.common.data.swgiff.IffForm;

public class CylinderExtentParser implements ExtentParser {
	
	private CylinderExtent cylinder;
	
	public CylinderExtentParser() {
		this.cylinder = null;
	}
	
	public CylinderExtentParser(CylinderExtent cylinder) {
		this.cylinder = cylinder;
	}
	
	@Override
	public CylinderExtent getExtent() {
		return cylinder;
	}
	
	@Override
	public void read(IffForm form) {
		assert form.getTag().equals("XCYL");
		assert form.getVersion() == 0;
		
		try (IffChunk cyln = form.readChunk("CYLN")) {
			cylinder = new CylinderExtent(cyln.readVector(), cyln.readFloat(), cyln.readFloat());
		}
		
	}
	
	@Override
	public IffForm write() {
		IffChunk cyln = new IffChunk("CYLN");
		cyln.writeVector(cylinder.getBase());
		cyln.writeFloat((float) cylinder.getRadius());
		cyln.writeFloat((float) cylinder.getHeight());
		
		return IffForm.of("XCYL", 0, cyln);
	}
	
}
