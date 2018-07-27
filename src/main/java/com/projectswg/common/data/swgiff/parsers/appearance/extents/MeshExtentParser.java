package com.projectswg.common.data.swgiff.parsers.appearance.extents;

import com.projectswg.common.data.math.extents.MeshExtent;
import com.projectswg.common.data.swgiff.IffForm;
import com.projectswg.common.data.swgiff.parsers.SWGParser;
import com.projectswg.common.data.swgiff.parsers.math.IndexedTriangleListParser;

public class MeshExtentParser implements ExtentParser {
	
	private MeshExtent mesh;
	
	public MeshExtentParser() {
		this.mesh = null;
	}
	
	public MeshExtentParser(MeshExtent mesh) {
		this.mesh = mesh;
	}
	
	@Override
	public MeshExtent getExtent() {
		return mesh;
	}
	
	@Override
	public void read(IffForm form) {
		assert form.getTag().equals("CMSH");
		assert form.getVersion() == 0;
		
		this.mesh = new MeshExtent(((IndexedTriangleListParser) SWGParser.parseNotNull(form.readForm())).getList());
	}
	
	@Override
	public IffForm write() {
		return IffForm.of("CMSH", 0, new IndexedTriangleListParser(mesh.getMesh()).write());
	}
	
}
