package com.projectswg.common.data.math.extents;

import com.projectswg.common.data.location.Point3D;

public class CylinderExtent implements Extent {
	
	private final SphereExtent sphere;
	private final Point3D base;
	private final double radius;
	private final double height;
	
	public CylinderExtent(Point3D base, double radius, double height) {
		Point3D center = new Point3D(base.getX(), base.getY()+height/2, base.getZ());
		this.sphere = new SphereExtent(center, Math.sqrt(height*height/4 + radius*radius));
		this.base = base;
		this.radius = radius;
		this.height = height;
	}
	
	public Point3D getBase() {
		return base;
	}
	
	public double getRadius() {
		return radius;
	}
	
	public double getHeight() {
		return height;
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
		return String.format("CylinderExtent[base=%s r=%f h=%f]", base, radius, height);
	}
	
}
