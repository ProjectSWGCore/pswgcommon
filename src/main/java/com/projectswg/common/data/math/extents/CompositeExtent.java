package com.projectswg.common.data.math.extents;

import com.projectswg.common.data.location.Point3D;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompositeExtent implements Extent {
	
	private final SphereExtent sphere;
	private final List<Extent> extents;
	
	public CompositeExtent(List<Extent> extents) {
		this.extents = new ArrayList<>(extents);
		this.sphere = buildSphere(this.extents);
	}
	
	public List<Extent> getExtents() {
		return Collections.unmodifiableList(extents);
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
		return String.format("CompositeExtent[%s]", extents);
	}
	
	private static SphereExtent buildSphere(List<Extent> extents) {
		if (extents.isEmpty())
			return new SphereExtent(new Point3D(0, 0, 0), 0);
		if (extents.size() == 1)
			return extents.get(0).getSphere();
		
		boolean first = true;
		double x = 0;
		double y = 0;
		double z = 0;
		double r = 0;
		for (Extent e : extents) {
			SphereExtent s = e.getSphere();
			Point3D c = s.getCenter();
			if (first) {
				x = c.getX();
				y = c.getY();
				z = c.getZ();
				r = s.getRadius();
				first = false;
			} else {
				r += s.getRadius() + Math.sqrt(square(c.getX() - x) + square(c.getY() - y) + square(c.getZ() - z));
				x = (x + c.getX()) / 2;
				y = (y + c.getY()) / 2;
				z = (z + c.getZ()) / 2;
			}
		}
		return new SphereExtent(new Point3D(x, y, z), r);
	}
	
	private static double square(double x) {
		return x * x;
	}
	
}
