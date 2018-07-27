package com.projectswg.common.data.swgiff.parsers.appearance.extents;

import com.projectswg.common.data.math.extents.Extent;
import com.projectswg.common.data.swgiff.parsers.SWGParser;

public interface ExtentParser extends SWGParser {
	
	Extent getExtent();
	
}
