package com.projectswg.common.data.swgiff.parsers.appearance.extents;

import com.projectswg.common.data.math.extents.CompositeExtent;
import com.projectswg.common.data.swgiff.IffForm;

public class ComponentExtentParser extends CompositeExtentParser {
	
	public ComponentExtentParser() {
		super();
	}
	
	public ComponentExtentParser(CompositeExtent composite) {
		super(composite);
	}
	
	@Override
	public void read(IffForm form) {
		assert form.getTag().equals("CMPT");
		assert form.getVersion() == 0;
		
		super.read(form.readForm("CPST"));
	}
	
	@Override
	public IffForm write() {
		return IffForm.of("CMPT", 0, super.write());
	}
	
}
