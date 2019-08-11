package com.projectswg.common.data.swgiff.parsers.appearance.extents;

import com.projectswg.common.data.math.extents.OrientedCylinderExtent;
import com.projectswg.common.data.swgiff.IffChunk;
import com.projectswg.common.data.swgiff.IffForm;

public class OrientedCylinderExtentParser implements ExtentParser {
	
	private OrientedCylinderExtent cylinder;
	
	public OrientedCylinderExtentParser() {
		this.cylinder = null;
	}
	
	public OrientedCylinderExtentParser(OrientedCylinderExtent cylinder) {
		this.cylinder = cylinder;
	}
	
	@Override
	public OrientedCylinderExtent getExtent() {
		return cylinder;
	}
	
	@Override
	public void read(IffForm form) {
		assert form.getTag().equals("XOCL");
		assert form.getVersion() == 0;
		
		try (IffChunk ocyl = form.readChunk("OCYL")) {
			cylinder = new OrientedCylinderExtent(ocyl.readVector(), ocyl.readVector(), ocyl.readFloat(), ocyl.readFloat());
		}
		
	}
	
	@Override
	public IffForm write() {
		IffChunk ocyl = new IffChunk("OCYL");
		ocyl.writeVector(cylinder.getBase());
		ocyl.writeVector(cylinder.getAxis());
		ocyl.writeFloat((float) cylinder.getRadius());
		ocyl.writeFloat((float) cylinder.getHeight());
		
		return IffForm.of("XOCL", 0, ocyl);
	}
	
}
