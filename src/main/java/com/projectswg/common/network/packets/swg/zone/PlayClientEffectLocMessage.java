package com.projectswg.common.network.packets.swg.zone;

import com.projectswg.common.data.location.Point3D;
import com.projectswg.common.data.location.Terrain;
import com.projectswg.common.network.NetBuffer;
import com.projectswg.common.network.packets.SWGPacket;

/**
 * Plays an effect at a specified location.
 */
public class PlayClientEffectLocMessage extends SWGPacket {
	public static final int CRC = getCrc("PlayClientEffectLocMessage");
	
	private String effectFile;
	private Terrain terrain;
	private Point3D point;
	private long cellId;
	private float terrainDelta;
	private String commandString;
	
	public PlayClientEffectLocMessage() {
	}
	
	public PlayClientEffectLocMessage(String effectFile, Terrain terrain, Point3D point, long cellId, float terrainDelta, String commandString) {
		this.effectFile = effectFile;
		this.terrain = terrain;
		this.point = point;
		this.cellId = cellId;
		this.terrainDelta = terrainDelta;
		this.commandString = commandString;
	}
	
	@Override
	public void decode(NetBuffer data) {
		if (!super.checkDecode(data, CRC))
			return;
		
		effectFile = data.getAscii();
		terrain = Terrain.getTerrainFromName(data.getAscii());
		point = data.getEncodable(Point3D.class);
		cellId = data.getLong();
		terrainDelta = data.getFloat();
		commandString = data.getAscii();
	}
	
	@Override
	public NetBuffer encode() {
		NetBuffer data = NetBuffer.allocate(2 + 4 + 2 + effectFile.length() + 2 + terrain.getName().length() + point.getLength() + 8 + 4 + 2 + commandString.length());
		
		data.addShort(6);
		data.addInt(CRC);
		data.addAscii(effectFile);
		data.addAscii(terrain.getName());
		data.addEncodable(point);
		data.addLong(cellId);
		data.addFloat(terrainDelta);
		data.addAscii(commandString);
		
		return data;
	}
	
	public String getEffectFile() {
		return effectFile;
	}
	
	public Terrain getTerrain() {
		return terrain;
	}
	
	public Point3D getPoint() {
		return point;
	}
	
	public long getCellId() {
		return cellId;
	}
	
	public float getTerrainDelta() {
		return terrainDelta;
	}
	
	public String getCommandString() {
		return commandString;
	}
}
