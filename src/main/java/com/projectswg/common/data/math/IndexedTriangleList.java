package com.projectswg.common.data.math;

import com.projectswg.common.data.location.Point3D;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IndexedTriangleList {
	
	private final List<Point3D> vertices;
	private final List<Integer> indices;
	
	private IndexedTriangleList(List<Point3D> vertices, List<Integer> indices) {
		this.vertices = new ArrayList<>(vertices);
		this.indices = new ArrayList<>(indices);
	}
	
	public List<Point3D> getVertices() {
		return Collections.unmodifiableList(vertices);
	}
	
	public List<Integer> getIndices() {
		return Collections.unmodifiableList(indices);
	}
	
	@Override
	public String toString() {
		return String.format("IndexedTriangleList[vertices=%s indices=%s]", vertices, indices);
	}
	
	public static IndexedTriangleList fromTriangleList(List<Point3D> vertices) {
		List<Integer> indices = new ArrayList<>();
		for (int i = 0; i < vertices.size(); i++)
			indices.add(i);
		return from(vertices, indices);
	}
	
	public static IndexedTriangleList fromTriangleStrip(List<Point3D> vertices) {
		int triangleCount = vertices.size()-2;
		assert triangleCount >= 0;
		List<Integer> indices = new ArrayList<>(triangleCount*3);
		for (int i = 0; i < triangleCount; i++) {
			if (i % 2 == 1) {
				indices.add(i);
				indices.add(i+2);
				indices.add(i+1);
			} else {
				indices.add(i);
				indices.add(i+1);
				indices.add(i+2);
			}
		}
		return from(vertices, indices);
	}
	
	public static IndexedTriangleList fromTriangleFan(List<Point3D> vertices) {
		int triangleCount = vertices.size()-2;
		assert triangleCount >= 0;
		List<Integer> indices = new ArrayList<>(triangleCount*3);
		for (int i = 0; i < triangleCount; i++) {
			indices.add(i);
			indices.add(i+1);
			indices.add(i+2);
		}
		return from(vertices, indices);
	}
	
	public static IndexedTriangleList from(List<Point3D> vertices, List<Integer> indices) {
		return new IndexedTriangleList(vertices, indices);
	}
	
}
