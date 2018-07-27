package com.projectswg.common.data.swgiff.parsers.appearance;

import com.projectswg.common.data.location.Location;
import com.projectswg.common.data.math.extents.Extent;
import com.projectswg.common.data.math.RawColor;
import com.projectswg.common.data.swgiff.IffChunk;
import com.projectswg.common.data.swgiff.IffForm;
import com.projectswg.common.data.swgiff.parsers.SWGParser;
import com.projectswg.common.data.swgiff.parsers.appearance.extents.ExtentParser;
import me.joshlarson.jlcommon.log.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PortalLayoutCellTemplate implements SWGParser {
	
	private final List<LightData> lights;
	private final List<PortalLayoutCellPortalTemplate> portals;
	private final List<Extent> collisionExtents;
	
	private boolean canSeeParentCell;
	private String name;
	private String appearanceName;
	private String floorName;
	
	public PortalLayoutCellTemplate() {
		this.lights = new ArrayList<>();
		this.portals = new ArrayList<>();
		this.collisionExtents = new ArrayList<>();
		
		this.canSeeParentCell = false;
		this.name = "";
		this.appearanceName = "";
		this.floorName = null;
	}
	
	public List<LightData> getLights() {
		return Collections.unmodifiableList(lights);
	}
	
	public List<PortalLayoutCellPortalTemplate> getPortals() {
		return Collections.unmodifiableList(portals);
	}
	
	public List<Extent> getCollisionExtents() {
		return Collections.unmodifiableList(collisionExtents);
	}
	
	@Override
	public void read(IffForm form) {
		assert form.getTag().equals("CELL");
		
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
		}
	}
	
	@Override
	public IffForm write() {
		return null;
	}
	
	public boolean isCanSeeParentCell() {
		return canSeeParentCell;
	}
	
	@NotNull
	public String getName() {
		return name;
	}
	
	@NotNull
	public String getAppearanceName() {
		return appearanceName;
	}
	
	@Nullable
	public String getFloorName() {
		return floorName;
	}
	
	private void load0001(IffForm form) {
		try (IffChunk data = form.readChunk("DATA")) {
			int portalCount = data.readInt();
			canSeeParentCell = data.readBoolean();
			appearanceName = data.readString();
			
			loadPortals(form, portalCount);
		}
	}
	
	private void load0002(IffForm form) {
		try (IffChunk data = form.readChunk("DATA")) {
			int portalCount = data.readInt();
			canSeeParentCell = data.readBoolean();
			appearanceName = data.readString();
			if (data.readBoolean())
				floorName = data.readString();
			
			loadPortals(form, portalCount);
		}
	}
	
	private void load0003(IffForm form) {
		load0002(form);
		loadLight(form);
	}
	
	private void load0004(IffForm form) {
		try (IffChunk data = form.readChunk("DATA")) {
			int portalCount = data.readInt();
			canSeeParentCell = data.readBoolean();
			name = data.readString();
			appearanceName = data.readString();
			if (data.readBoolean())
				floorName = data.readString();
			
			loadPortals(form, portalCount);
		}
		loadLight(form);
	}
	
	private void load0005(IffForm form) {
		load0004(form);
		IffForm extent;
		while ((extent = form.readForm()) != null) {
			ExtentParser e = SWGParser.parse(extent);
			if (e == null) {
				Log.w("Unknown Collision Extent: %s", extent.getTag());
			} else if (e.getExtent() != null) {
				collisionExtents.add(e.getExtent());
			}
			extent.close();
		}
	}
	
	private void loadPortals(IffForm form, int count) {
		for (int i = 0; i < count; i++) {
			try (IffForm prtl = form.readForm("PRTL")) {
				portals.add(SWGParser.parse(prtl));
			}
		}
	}
	
	private void loadLight(IffForm form) {
		try (IffChunk lght = form.readChunk("LGHT")) {
			int count = lght.readInt();
			for (int i = 0; i < count; i++)
				lights.add(new LightData(lght));
		}
	}
	
	public static class LightData {
		
		private final byte type;
		private final RawColor diffuseColor;
		private final RawColor specularColor;
		private final Location location;
		private final float constantAttenuation;
		private final float linearAttenuation;
		private final float quadraticAttenuation;
		
		private LightData(IffChunk chunk) {
			this.type = chunk.readByte();
			this.diffuseColor = chunk.readVectorArgb();
			this.specularColor = chunk.readVectorArgb();
			this.location = chunk.readTransform();
			this.constantAttenuation = chunk.readFloat();
			this.linearAttenuation = chunk.readFloat();
			this.quadraticAttenuation = chunk.readFloat();
		}
		
		public byte getType() {
			return type;
		}
		
		public RawColor getDiffuseColor() {
			return diffuseColor;
		}
		
		public RawColor getSpecularColor() {
			return specularColor;
		}
		
		public Location getLocation() {
			return location;
		}
		
		public float getConstantAttenuation() {
			return constantAttenuation;
		}
		
		public float getLinearAttenuation() {
			return linearAttenuation;
		}
		
		public float getQuadraticAttenuation() {
			return quadraticAttenuation;
		}
		
	}
	
}
