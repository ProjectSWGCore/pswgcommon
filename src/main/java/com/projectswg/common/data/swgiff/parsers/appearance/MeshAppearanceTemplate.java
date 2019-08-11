package com.projectswg.common.data.swgiff.parsers.appearance;

import com.projectswg.common.data.math.extents.SphereExtent;
import com.projectswg.common.data.swgiff.IffForm;
import com.projectswg.common.data.swgiff.parsers.appearance.extents.SphereExtentParser;
import me.joshlarson.jlcommon.log.Log;

public class MeshAppearanceTemplate extends AppearanceTemplate {
	
	private SphereExtent sphere;
	
	public MeshAppearanceTemplate() {
		this.sphere = null;
	}
	
	public SphereExtent getSphere() {
		return sphere;
	}
	
	@Override
	public void read(IffForm form) {
		assert form.getTag().equals("MESH");
		
		switch (form.getVersion()) {
			case 2:
			case 3:
				loadOld(form);
				break;
			default:
				Log.w("Unknown MESH version: %d", form.getVersion());
			case 4:
			case 5:
				load(form);
				break;
		}
	}
	
	@Override
	public IffForm write() {
		return null;
	}
	
	@Override
	public String toString() {
		return String.format("MeshAppearanceTemplate[sphere=%s super=%s]", sphere, super.toString());
	}
	
	private void loadOld(IffForm form) {
		form.readForm("SPS ").close();
		sphere = SphereExtentParser.loadOld(form);
		loadExtent(form);
		loadHardpoints(form);
	}
	
	private void load(IffForm form) {
		super.read(form.readForm("APPR"));
		sphere = getExtent().getSphere();
		form.readForm("SPS ").close();
	}
	
}
