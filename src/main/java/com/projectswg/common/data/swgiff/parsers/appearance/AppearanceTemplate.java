package com.projectswg.common.data.swgiff.parsers.appearance;

import com.projectswg.common.data.location.Location;
import com.projectswg.common.data.math.extents.Extent;
import com.projectswg.common.data.swgiff.IffChunk;
import com.projectswg.common.data.swgiff.IffForm;
import com.projectswg.common.data.swgiff.parsers.SWGParser;
import com.projectswg.common.data.swgiff.parsers.appearance.extents.ExtentParser;
import me.joshlarson.jlcommon.log.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppearanceTemplate implements SWGParser {
	
	private final List<Hardpoint> hardpoints;
	
	private Extent extent;
	private Extent collisionExtent;
	private String floor;
	
	public AppearanceTemplate() {
		this(null, null, null);
	}
	
	public AppearanceTemplate(Extent extent, Extent collisionExtent, String floor) {
		this.hardpoints = new ArrayList<>();
		this.extent = extent;
		this.collisionExtent = collisionExtent;
		this.floor = floor;
	}
	
	public Extent getExtent() {
		return extent;
	}
	
	public Extent getCollisionExtent() {
		return collisionExtent;
	}
	
	public String getFloor() {
		return floor;
	}
	
	public List<Hardpoint> getHardpoints() {
		return Collections.unmodifiableList(hardpoints);
	}
	
	@Override
	public void read(IffForm form) {
		assert form.getTag().equals("APPR");
		
		switch (form.getVersion()) {
			case 1:
				load0001(form);
				break;
			case 2:
				load0002(form);
				break;
			default:
				Log.w("Unknown APPR version: %d", form.getVersion());
			case 3:
				load0003(form);
				break;
		}
	}
	
	@Override
	public IffForm write() {
		return null;
	}
	
	@Override
	public String toString() {
		return String.format("AppearanceTemplate[extent=%s   collisionExtent=%s   floor=%s   hardpoints=%s]", extent, collisionExtent, floor, hardpoints);
	}
	
	private void load0001(IffForm form) {
		loadExtent(form);
		loadHardpoints(form);
	}
	
	private void load0002(IffForm form) {
		loadExtent(form);
		loadHardpoints(form);
		loadFloor(form);
	}
	
	private void load0003(IffForm form) {
		loadExtent(form);
		loadCollisionExtent(form);
		loadHardpoints(form);
		loadFloor(form);
	}
	
	protected void loadExtent(IffForm form) {
		try (IffForm next = form.readForm()) {
			extent = ((ExtentParser) SWGParser.parseNotNull(next)).getExtent();
		}
	}
	
	protected void loadCollisionExtent(IffForm form) {
		try (IffForm next = form.readForm()) {
			collisionExtent = ((ExtentParser) SWGParser.parseNotNull(next)).getExtent();
		}
	}
	
	protected void loadHardpoints(IffForm form) {
		try (IffForm hardpointForm = form.readForm("HPTS")) {
			if (hardpointForm == null)
				return;
			form = hardpointForm;
			while (true) {
				try (IffChunk hpnt = form.readChunk("HPNT")) {
					if (hpnt == null)
						break;
					Location location = hpnt.readTransform();
					String name = hpnt.readString();
					hardpoints.add(new Hardpoint(name, location));
				}
			}
		}
	}
	
	protected void loadFloor(IffForm form) {
		try (IffForm flor = form.readForm("FLOR")) {
			try (IffChunk data = flor.readChunk("DATA")) {
				if (data.readBoolean())
					floor = data.readString();
			}
		}
	}
	
	public static class Hardpoint {
		
		private final String name;
		private final Location location;
		
		public Hardpoint(String name, Location location) {
			this.name = name;
			this.location = location;
		}
		
		public String getName() {
			return name;
		}
		
		public Location getLocation() {
			return location;
		}
		
		@Override
		public String toString() {
			return "Hardpoint[name="+name+" loc="+location+"]";
		}
		
	}
	
}
