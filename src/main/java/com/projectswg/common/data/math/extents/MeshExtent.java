package com.projectswg.common.data.math.extents;

import com.projectswg.common.data.location.Point3D;
import com.projectswg.common.data.math.IndexedTriangleList;

public class MeshExtent implements Extent {
	
	private final SphereExtent sphere;
	private final IndexedTriangleList mesh;
	
	public MeshExtent(IndexedTriangleList mesh) {
		this.sphere = buildSphere(mesh);
		this.mesh = mesh;
	}
	
	public IndexedTriangleList getMesh() {
		return mesh;
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
		return String.format("MeshExtent[mesh=%s]", mesh);
	}
	
	private static SphereExtent buildSphere(IndexedTriangleList mesh) {
		double x = 0;
		double y = 0;
		double z = 0;
		int vertexCount = 0;
		for (Point3D vertex : mesh.getVertices()) {
			x += vertex.getX();
			y += vertex.getY();
			z += vertex.getZ();
			vertexCount++;
		}
		x /= vertexCount;
		y /= vertexCount;
		z /= vertexCount;
		Point3D center = new Point3D(x, y, z);
		double rX = 0;
		double rY = 0;
		double rZ = 0;
		for (Point3D vertex : mesh.getVertices()) {
			rX = Math.max(rX, Math.abs(vertex.getX() - x));
			rY = Math.max(rY, Math.abs(vertex.getY() - y));
			rZ = Math.max(rZ, Math.abs(vertex.getZ() - z));
		}
		return new SphereExtent(center, Math.sqrt(rX*rX + rY*rY + rZ*rZ));
	}
	
}
