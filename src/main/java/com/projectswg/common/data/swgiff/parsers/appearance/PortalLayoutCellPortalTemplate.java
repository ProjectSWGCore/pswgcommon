package com.projectswg.common.data.swgiff.parsers.appearance;

import com.projectswg.common.data.location.Location;
import com.projectswg.common.data.swgiff.IffChunk;
import com.projectswg.common.data.swgiff.IffForm;
import com.projectswg.common.data.swgiff.parsers.SWGParser;
import me.joshlarson.jlcommon.log.Log;

public class PortalLayoutCellPortalTemplate implements SWGParser {
	
	private boolean disabled;
	private boolean passable;
	private int geometryIndex;
	private boolean geometryWindingClockwise;
	private String doorStyle;
	private boolean hasDoorHardpoint;
	private Location doorHardpoint;
	
	public PortalLayoutCellPortalTemplate() {
		this.disabled = false;
		this.passable = true;
		this.geometryIndex = 0;
		this.geometryWindingClockwise = true;
		this.doorStyle = "";
		this.hasDoorHardpoint = false;
		this.doorHardpoint = null;
	}
	
	public boolean isDisabled() {
		return disabled;
	}
	
	public boolean isPassable() {
		return passable;
	}
	
	public int getGeometryIndex() {
		return geometryIndex;
	}
	
	public boolean isGeometryWindingClockwise() {
		return geometryWindingClockwise;
	}
	
	public String getDoorStyle() {
		return doorStyle;
	}
	
	public boolean isHasDoorHardpoint() {
		return hasDoorHardpoint;
	}
	
	public Location getDoorHardpoint() {
		return doorHardpoint;
	}
	
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
	
	public void setPassable(boolean passable) {
		this.passable = passable;
	}
	
	public void setGeometryIndex(int geometryIndex) {
		this.geometryIndex = geometryIndex;
	}
	
	public void setGeometryWindingClockwise(boolean geometryWindingClockwise) {
		this.geometryWindingClockwise = geometryWindingClockwise;
	}
	
	public void setDoorStyle(String doorStyle) {
		this.doorStyle = doorStyle;
	}
	
	public void setHasDoorHardpoint(boolean hasDoorHardpoint) {
		this.hasDoorHardpoint = hasDoorHardpoint;
	}
	
	public void setDoorHardpoint(Location doorHardpoint) {
		this.doorHardpoint = doorHardpoint;
	}
	
	@Override
	public void read(IffForm form) {
		try (IffChunk chunk = form.readChunk()) {
			assert chunk.isVersionForm();
			switch (chunk.calculateVersionFromTag()) {
				case 1:
					load0001(chunk);
					break;
				case 2:
					load0002(chunk);
					break;
				case 3:
					load0003(chunk);
					break;
				case 4:
					load0004(chunk);
					break;
			default:
				Log.w("Unknown PRTL version: %d", chunk.calculateVersionFromTag());
				case 5:
					load0005(chunk);
					break;
			}
		}
	}
	
	@Override
	public IffForm write() {
		return null;
	}
	
	private void load0001(IffChunk chunk) {
		geometryIndex = chunk.readInt();
		geometryWindingClockwise = chunk.readBoolean();
		chunk.readInt(); // targetCellIndex (unused)
	}
	
	private void load0002(IffChunk chunk) {
		passable = chunk.readBoolean();
		geometryIndex = chunk.readInt();
		geometryWindingClockwise = chunk.readBoolean();
		chunk.readInt(); // targetCellIndex (unused)
	}
	
	private void load0003(IffChunk chunk) {
		passable = chunk.readBoolean();
		geometryIndex = chunk.readInt();
		geometryWindingClockwise = chunk.readBoolean();
		chunk.readInt(); // targetCellIndex (unused)
		doorStyle = chunk.readString();
	}
	
	private void load0004(IffChunk chunk) {
		passable = chunk.readBoolean();
		geometryIndex = chunk.readInt();
		geometryWindingClockwise = chunk.readBoolean();
		chunk.readInt(); // targetCellIndex (unused)
		doorStyle = chunk.readString();
		hasDoorHardpoint = chunk.readBoolean();
		doorHardpoint = chunk.readTransform();
	}
	
	private void load0005(IffChunk chunk) {
		disabled = chunk.readBoolean();
		passable = chunk.readBoolean();
		geometryIndex = chunk.readInt();
		geometryWindingClockwise = chunk.readBoolean();
		chunk.readInt(); // targetCellIndex (unused)
		doorStyle = chunk.readString();
		hasDoorHardpoint = chunk.readBoolean();
		doorHardpoint = chunk.readTransform();
	}
	
}
