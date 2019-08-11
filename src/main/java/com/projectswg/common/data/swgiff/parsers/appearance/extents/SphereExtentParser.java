package com.projectswg.common.data.swgiff.parsers.appearance.extents;

import com.projectswg.common.data.location.Point3D;
import com.projectswg.common.data.math.extents.SphereExtent;
import com.projectswg.common.data.swgiff.IffChunk;
import com.projectswg.common.data.swgiff.IffForm;
import me.joshlarson.jlcommon.log.Log;

public class SphereExtentParser implements ExtentParser {
	
	private SphereExtent sphere;
	
	public SphereExtentParser() {
		this.sphere = null;
	}
	
	public SphereExtentParser(SphereExtent sphere) {
		this.sphere = sphere;
	}
	
	@Override
	public SphereExtent getExtent() {
		return sphere;
	}
	
	@Override
	public void read(IffForm form) {
		assert form.getTag().equals("EXSP");
		switch (form.getVersion()) {
			case 0:
				load0000(form);
				break;
			default:
				Log.w("Unknown EXSP version: %d", form.getVersion());
			case 1:
				load0001(form);
				break;
		}
	}
	
	@Override
	public IffForm write() {
		IffChunk sphr = new IffChunk("SPHR");
		sphr.writeVector(sphere.getCenter());
		sphr.writeFloat((float) sphere.getRadius());
		
		return IffForm.of("EXSP", 1, sphr);
	}
	
	private void load0000(IffForm form) {
		sphere = loadOld(form);
	}
	
	private void load0001(IffForm form) {
		try (IffChunk sphr = form.readChunk("SPHR")) {
			sphere = new SphereExtent(sphr.readVector(), sphr.readFloat());
		}
	}
	
	public static SphereExtent loadOld(IffForm form) {
		Point3D center;
		double radius;
		try (IffChunk cntr = form.readChunk("CNTR")) {
			center = cntr.readVector();
		}
		try (IffChunk radi = form.readChunk("RADI")) {
			radius = radi.readFloat();
		}
		return new SphereExtent(center, radius);
	}
}
