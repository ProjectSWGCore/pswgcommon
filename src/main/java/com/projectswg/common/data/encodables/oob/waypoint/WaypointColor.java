package com.projectswg.common.data.encodables.oob.waypoint;

import com.projectswg.common.data.EnumLookup;

public enum WaypointColor {
	BLUE		(1, "blue"),
	GREEN		(2, "green"),
	ORANGE		(3, "orange"),
	YELLOW		(4, "yellow"),
	PURPLE		(5, "purple"),
	WHITE		(6, "white"),
	MULTICOLOR	(7, "multicolor");
	
	private static final EnumLookup<String, WaypointColor> NAME_LOOKUP = new EnumLookup<>(WaypointColor.class, WaypointColor::getName);
	private static final EnumLookup<Integer, WaypointColor> VALUE_LOOKUP = new EnumLookup<>(WaypointColor.class, WaypointColor::getValue);
	
	private final String name;
	private final int i;
	
	WaypointColor(int i, String name) {
		this.name = name;
		this.i = i;
	}
	
	public String getName() {
		return name;
	}
	
	public int getValue() {
		return i;
	}
	
	public static WaypointColor valueOf(int colorId) {
		return VALUE_LOOKUP.getEnum(colorId, WaypointColor.BLUE);
	}
	
	public static WaypointColor fromString(String str) {
		return NAME_LOOKUP.getEnum(str, WaypointColor.BLUE);
	}
	
}
