package com.projectswg.common.data.swgiff.parsers.appearance;

import com.projectswg.common.data.math.IndexedTriangleList;
import com.projectswg.common.data.swgiff.IffChunk;
import com.projectswg.common.data.swgiff.IffForm;
import com.projectswg.common.data.swgiff.parsers.SWGParser;
import com.projectswg.common.data.swgiff.parsers.math.IndexedTriangleListParser;
import me.joshlarson.jlcommon.log.Log;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DetailAppearanceTemplate extends AppearanceTemplate {
	
	private final Map<Integer, MeshData> meshData;
	
	private IndexedTriangleList testShape;
	private IndexedTriangleList writeShape;
	private IndexedTriangleList radarShape;
	private byte lodFlags;
	
	public DetailAppearanceTemplate() {
		this.meshData = new HashMap<>();
		
		this.testShape = null;
		this.writeShape = null;
		this.radarShape = null;
		this.lodFlags = 0;
	}
	
	public Map<Integer, MeshData> getMeshData() {
		return Collections.unmodifiableMap(meshData);
	}
	
	public IndexedTriangleList getTestShape() {
		return testShape;
	}
	
	public IndexedTriangleList getWriteShape() {
		return writeShape;
	}
	
	public IndexedTriangleList getRadarShape() {
		return radarShape;
	}
	
	public boolean isUsePivotPoint() {
		return (lodFlags & 1) != 0;
	}
	
	public boolean isDisableLodCrossFade() {
		return (lodFlags & 2) != 0;
	}
	
	@Override
	public void read(IffForm form) {
		assert form.getTag().equals("DTLA");
		
		switch (form.getVersion()) {
			case 1:
				load0001(form);
				break;
			case 2:
				load0002(form);
				break;
			case 3:
				load0003(form);
				break;
			case 4:
				load0004(form);
				break;
			case 5:
				load0005(form);
				break;
			case 6:
				load0006(form);
				break;
			case 7:
				load0007(form);
				break;
			default:
				Log.w("Unknown DTLA version: %d", form.getVersion());
			case 8:
				load0008(form);
				break;
		}
	}
	
	@Override
	public IffForm write() {
		return null;
	}
	
	@Override
	public String toString() {
		return String.format("DetailAppearanceTemplate[meshData=%s testShape=%s writeShape=%s radarShape=%s super=%s]", meshData.values(), testShape, writeShape, radarShape, super.toString());
	}
	
	private void load0001(IffForm form) {
		try (IffChunk info = form.readChunk("INFO")) {
			int count = info.remaining(12);
			for (int i = 0; i < count; i++) {
				MeshData data = new MeshData(info.readInt());
				data.setNearDistance(info.readFloat());
				data.setFarDistance(info.readFloat());
				meshData.put(data.getId(), data);
			}
		}
		try (IffForm data = form.readForm("DATA")) {
			data.readAllChunks("CHLD", this::loadData);
		}
	}
	
	private void load0002(IffForm form) {
		load0001(form);
		try (IffForm test = form.readForm("TEST")) {
			boolean hasTestShape;
			try (IffChunk info = test.readChunk("INFO")) {
				hasTestShape = info.readInt() != 0;
			}
			if (hasTestShape)
				this.testShape = ((IndexedTriangleListParser) SWGParser.parseNotNull(test.readForm("IDTL"))).getList();
		}
		try (IffForm writ = form.readForm("WRIT")) {
			boolean hasWriteShape;
			try (IffChunk info = writ.readChunk("INFO")) {
				hasWriteShape = info.readInt() != 0;
			}
			if (hasWriteShape)
				this.writeShape = ((IndexedTriangleListParser) SWGParser.parseNotNull(writ.readForm("IDTL"))).getList();
		}
	}
	
	private void load0003(IffForm form) {
		load0002(form);
		if (form.hasForm("FLOR"))
			loadFloor(form);
	}
	
	private void load0004(IffForm form) {
		load0003(form);
		super.read(form.readForm("APPR"));
	}
	
	private void load0005(IffForm form) {
		load0004(form);
	}
	
	private void load0006(IffForm form) {
		load0005(form);
		try (IffChunk pivt = form.readChunk("PIVT")) {
			lodFlags = pivt.readByte();
		}
	}
	
	private void load0007(IffForm form) {
		load0006(form);
		try (IffForm radr = form.readForm("RADR")) {
			boolean hasRadarShape;
			try (IffChunk info = radr.readChunk("INFO")) {
				hasRadarShape = info.readInt() != 0;
			}
			if (hasRadarShape)
				this.radarShape = ((IndexedTriangleListParser) SWGParser.parseNotNull(radr.readForm("IDTL"))).getList();
		}
	}
	
	private void load0008(IffForm form) {
		load0007(form);
	}
	
	private void loadData(IffChunk child) {
		int id = child.readInt();
		assert meshData.containsKey(id);
		meshData.get(id).setName(child.readString());
	}
	
	public static class MeshData {
		
		private final int id;
		
		private float nearDistance;
		private float farDistance;
		private String name;
		
		public MeshData(int id) {
			this.id = id;
			this.nearDistance = 0;
			this.farDistance = 0;
			this.name = null;
		}
		
		public int getId() {
			return id;
		}
		
		public float getNearDistance() {
			return nearDistance;
		}
		
		public float getFarDistance() {
			return farDistance;
		}
		
		public String getName() {
			return name;
		}
		
		public void setNearDistance(float nearDistance) {
			this.nearDistance = nearDistance;
		}
		
		public void setFarDistance(float farDistance) {
			this.farDistance = farDistance;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		@Override
		public String toString() {
			return "MeshData[id="+id+" name='"+name+"' near="+nearDistance+"/far="+farDistance+"]";
		}
		
	}
	
}
