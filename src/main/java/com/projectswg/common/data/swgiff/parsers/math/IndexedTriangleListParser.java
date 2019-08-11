package com.projectswg.common.data.swgiff.parsers.math;

import com.projectswg.common.data.math.IndexedTriangleList;
import com.projectswg.common.data.location.Point3D;
import com.projectswg.common.data.swgiff.IffChunk;
import com.projectswg.common.data.swgiff.IffForm;
import com.projectswg.common.data.swgiff.parsers.SWGParser;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class IndexedTriangleListParser implements SWGParser {
	
	private IndexedTriangleList list;
	
	public IndexedTriangleListParser() {
		this(null);
	}
	
	public IndexedTriangleListParser(IndexedTriangleList list) {
		this.list = list;
	}
	
	@Override
	public void read(IffForm form) {
		assert form.getTag().equals("IDTL");
		assert form.getVersion() == 0;
		
		List<Point3D> vertices = new ArrayList<>();
		List<Integer> indices = new ArrayList<>();
		
		try (IffChunk chunk = form.readChunk("VERT")) {
			int vertexCount = chunk.remaining(12);
			for (int i = 0; i < vertexCount; i++) {
				vertices.add(chunk.readVector());
			}
		}
		try (IffChunk chunk = form.readChunk("INDX")) {
			int indexCount = chunk.remaining(4);
			for (int i = 0; i < indexCount; i++) {
				indices.add(chunk.readInt());
			}
		}
		
		this.list = IndexedTriangleList.from(vertices, indices);
	}
	
	@Override
	public IffForm write() {
		IffChunk vert = new IffChunk("VERT");
		IffChunk indx = new IffChunk("INDX");
		
		vert.ensureCapacity(list.getVertices().size() * 12);
		for (Point3D vertex : list.getVertices())
			vert.writeVector(vertex);
		
		indx.ensureCapacity(list.getIndices().size() * 4);
		for (int index : list.getIndices())
			indx.writeInt(index);
		
		return IffForm.of("IDTL", 0, vert, indx);
	}
	
	@NotNull
	public IndexedTriangleList getList() {
		return Objects.requireNonNull(list, "parser hasn't been read yet");
	}
	
}
