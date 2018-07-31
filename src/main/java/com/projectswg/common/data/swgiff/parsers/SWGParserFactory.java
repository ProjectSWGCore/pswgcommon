package com.projectswg.common.data.swgiff.parsers;

import com.projectswg.common.data.swgiff.parsers.appearance.*;
import com.projectswg.common.data.swgiff.parsers.appearance.extents.*;
import com.projectswg.common.data.swgiff.parsers.footprint.FootprintDataParser;
import com.projectswg.common.data.swgiff.parsers.math.IndexedTriangleListParser;
import com.projectswg.common.data.swgiff.parsers.misc.CrcStringDataParser;
import com.projectswg.common.data.swgiff.parsers.terrain.TerrainDataParser;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

enum SWGParserFactory {
	INSTANCE;
	
	private static final Map<String, Supplier<SWGParser>> PARSERS = new HashMap<>();
	
	static {
		PARSERS.put("APT ", AppearanceTemplateList::new);
		PARSERS.put("APPR", AppearanceTemplate::new);
		PARSERS.put("CELL", PortalLayoutCellTemplate::new);
		PARSERS.put("CMPT", ComponentExtentParser::new);
		PARSERS.put("CMSH", MeshExtentParser::new);
		PARSERS.put("CPST", CompositeExtentParser::new);
		PARSERS.put("CSTB", CrcStringDataParser::new);
		PARSERS.put("DTAL", DetailExtentParser::new);
		PARSERS.put("DTLA", DetailAppearanceTemplate::new);
		PARSERS.put("EXBX", BoxExtentParser::new);
		PARSERS.put("EXSP", SphereExtentParser::new);
		PARSERS.put("FOOT", FootprintDataParser::new);
		PARSERS.put("IDTL", IndexedTriangleListParser::new);
		PARSERS.put("MESH", MeshAppearanceTemplate::new);
		PARSERS.put("NULL", NullExtentParser::new);
		PARSERS.put("PRTL", PortalLayoutCellPortalTemplate::new);
		PARSERS.put("PRTO", PortalLayoutTemplate::new);
		PARSERS.put("PTAT", TerrainDataParser::new);
		PARSERS.put("XCYL", CylinderExtentParser::new);
		PARSERS.put("XOCL", OrientedCylinderExtentParser::new);
	}
	
	@Nullable
	public static SWGParser createParser(String tag) {
		Supplier<SWGParser> parser = PARSERS.get(tag);
		return parser == null ? null : parser.get();
	}
}
