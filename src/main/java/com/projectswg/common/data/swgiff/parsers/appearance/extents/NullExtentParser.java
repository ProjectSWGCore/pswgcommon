package com.projectswg.common.data.swgiff.parsers.appearance.extents;

import com.projectswg.common.data.math.extents.Extent;
import com.projectswg.common.data.swgiff.IffForm;

public class NullExtentParser implements ExtentParser {
	
	public NullExtentParser() {
		
	}
	
	@Override
	public Extent getExtent() {
		return null;
	}
	
	@Override
	public void read(IffForm form) {
		assert form.getTag().equals("NULL");
		
	}
	
	@Override
	public IffForm write() {
		return IffForm.of("NULL", 0);
	}
	
}
