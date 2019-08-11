package com.projectswg.common.data.math.extents;

import com.projectswg.common.data.location.Point3D;

public class OrientedCylinderExtent extends CylinderExtent {
	
	private final Point3D axis;
	
	public OrientedCylinderExtent(Point3D base, Point3D axis, double radius, double height) {
		super(base, radius, height);
		
		this.axis = axis;
	}
	
	public Point3D getAxis() {
		return axis;
	}
	
	@Override
	public String toString() {
		return String.format("CylinderExtent[base=%s axis=%s r=%f h=%f]", getBase(), axis, getRadius(), getHeight());
	}
	
}
