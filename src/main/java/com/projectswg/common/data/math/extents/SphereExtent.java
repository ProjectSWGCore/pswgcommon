package com.projectswg.common.data.math.extents;

import com.projectswg.common.data.location.Point3D;

public class SphereExtent implements Extent {
	
	private final Point3D center;
	private final double radius;
	
	public SphereExtent(Point3D center, double radius) {
		this.center = center;
		this.radius = radius;
	}
	
	public Point3D getCenter() {
		return center;
	}
	
	public double getRadius() {
		return radius;
	}
	
	@Override
	public SphereExtent getSphere() {
		return this;
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
		return String.format("SphereExtent[center=%s r=%f]", center, radius);
	}
	
}
