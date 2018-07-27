package com.projectswg.common.data.swgiff.parsers.appearance.extents;

import com.projectswg.common.data.location.Point3D;
import com.projectswg.common.data.math.extents.BoxExtent;
import com.projectswg.common.data.math.extents.SphereExtent;
import com.projectswg.common.data.swgiff.IffChunk;
import com.projectswg.common.data.swgiff.IffForm;
import com.projectswg.common.data.swgiff.parsers.SWGParser;
import me.joshlarson.jlcommon.log.Log;

public class BoxExtentParser implements ExtentParser {
	
	private BoxExtent box;
	
	public BoxExtentParser() {
		this.box = null;
	}
	
	public BoxExtentParser(BoxExtent box) {
		this.box = box;
	}
	
	@Override
	public BoxExtent getExtent() {
		return box;
	}
	
	@Override
	public void read(IffForm form) {
		assert form.getTag().equals("EXBX");
		
		switch (form.getVersion()) {
			case 0:
				load0000(form);
				break;
			default:
				Log.w("Unknown EXBX version: %d", form.getVersion());
			case 1:
				load0001(form);
				break;
		}
	}
	
	@Override
	public IffForm write() {
		IffChunk box = new IffChunk("BOX ");
		box.writeVector(this.box.getMax());
		box.writeVector(this.box.getMin());
		
		return IffForm.of("EXBX", 1, box, new SphereExtentParser(this.box.getSphere()).write());
	}
	
	private void load0000(IffForm form) {
		SphereExtent sphere = SphereExtentParser.loadOld(form);
		try (IffChunk box = form.readChunk("BOX ")) {
			loadBOX(box, sphere);
		}
	}
	
	private void load0001(IffForm form) {
		try (IffChunk box = form.readChunk("BOX ")) {
			loadBOX(box, ((SphereExtentParser) SWGParser.parseNotNull(form.readForm("EXSP"))).getExtent());
		}
	}
	
	private void loadBOX(IffChunk chunk, SphereExtent sphere) {
		Point3D max = chunk.readVector();
		Point3D min = chunk.readVector();
		box = new BoxExtent(min, max, sphere);
	}
	
}
