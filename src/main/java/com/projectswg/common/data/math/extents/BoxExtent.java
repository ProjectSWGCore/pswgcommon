package com.projectswg.common.data.math.extents;

import com.projectswg.common.data.location.Point3D;

public class BoxExtent implements Extent {
	
	private final Point3D min;
	private final Point3D max;
	private final SphereExtent sphere;
	
	public BoxExtent(Point3D min, Point3D max, SphereExtent sphere) {
		this.min = min;
		this.max = max;
		this.sphere = sphere;
	}
	
	public Point3D getMin() {
		return min;
	}
	
	public Point3D getMax() {
		return max;
	}
	
	@Override
	public SphereExtent getSphere() {
		return sphere;
	}
	
	@Override
	public boolean contains(Point3D start, Point3D end) {
		return false;
	}
	
	@Override
	public boolean intersects(Point3D p) {
		return false;
	}
	
	@Override
	public String toString() {
		return String.format("BoxExtent[%s -> %s  sphere=%s]", min, max, sphere);
	}
	
}
