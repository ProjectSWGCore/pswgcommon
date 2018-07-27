package com.projectswg.common.data.math.extents;

import com.projectswg.common.data.location.Point3D;

public interface Extent {
	
	SphereExtent getSphere();
	boolean contains(Point3D start, Point3D end);
	boolean intersects(Point3D p);
	
}
