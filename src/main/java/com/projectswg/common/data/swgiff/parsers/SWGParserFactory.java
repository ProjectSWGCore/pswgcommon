package com.projectswg.common.data.swgiff.parsers;

import com.projectswg.common.data.swgiff.parsers.appearance.*;
import com.projectswg.common.data.swgiff.parsers.appearance.extents.*;
import com.projectswg.common.data.swgiff.parsers.creation.CombinedProfessionTemplateParser;
import com.projectswg.common.data.swgiff.parsers.creation.ProfessionTemplateParser;
import com.projectswg.common.data.swgiff.parsers.footprint.FootprintDataParser;
import com.projectswg.common.data.swgiff.parsers.math.IndexedTriangleListParser;
import com.projectswg.common.data.swgiff.parsers.misc.CrcStringDataParser;
import com.projectswg.common.data.swgiff.parsers.slots.SlotArrangementParser;
import com.projectswg.common.data.swgiff.parsers.slots.SlotDescriptorParser;
import com.projectswg.common.data.swgiff.parsers.terrain.TerrainListLayer;
import com.projectswg.common.data.swgiff.parsers.terrain.TerrainTemplate;
import com.projectswg.common.data.swgiff.parsers.terrain.affectors.AffectorHeightConstant;
import com.projectswg.common.data.swgiff.parsers.terrain.affectors.AffectorHeightFractal;
import com.projectswg.common.data.swgiff.parsers.terrain.affectors.AffectorHeightTerrace;
import com.projectswg.common.data.swgiff.parsers.terrain.affectors.AffectorHeightRoad;
import com.projectswg.common.data.swgiff.parsers.terrain.boundaries.BoundaryCircle;
import com.projectswg.common.data.swgiff.parsers.terrain.boundaries.BoundaryPolyLine;
import com.projectswg.common.data.swgiff.parsers.terrain.boundaries.BoundaryPolygon;
import com.projectswg.common.data.swgiff.parsers.terrain.boundaries.BoundaryRectangle;
import com.projectswg.common.data.swgiff.parsers.terrain.filters.*;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

enum SWGParserFactory {
	INSTANCE;
	
	private static final Map<String, Supplier<SWGParser>> PARSERS = new HashMap<>();
	
	static {
		// Terrain
		PARSERS.put("PTAT", TerrainTemplate::new);
		PARSERS.put("LAYR", TerrainListLayer::new);
		PARSERS.put("AHCN", AffectorHeightConstant::new);
		PARSERS.put("AHFR", AffectorHeightFractal::new);
		PARSERS.put("AHTR", AffectorHeightTerrace::new);
		PARSERS.put("AROA", AffectorHeightRoad::new);
		PARSERS.put("BREC", BoundaryRectangle::new);
		PARSERS.put("BCIR", BoundaryCircle::new);
		PARSERS.put("BPLN", BoundaryPolyLine::new);
		PARSERS.put("BPOL", BoundaryPolygon::new);
		PARSERS.put("FBIT", FilterBitmap::new);
		PARSERS.put("FDIR", FilterDirection::new);
		PARSERS.put("FFRA", FilterFractal::new);
		PARSERS.put("FHGT", FilterHeight::new);
		PARSERS.put("FSLP", FilterSlope::new);
		PARSERS.put("FSHD", FilterShader::new);
		
		PARSERS.put("APT ", AppearanceTemplateList::new);
		PARSERS.put("APPR", AppearanceTemplate::new);
		PARSERS.put("ARGD", SlotArrangementParser::new);
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
		PARSERS.put("PFDT", CombinedProfessionTemplateParser::new);
		PARSERS.put("PRFI", ProfessionTemplateParser::new);
		PARSERS.put("PRTL", PortalLayoutCellPortalTemplate::new);
		PARSERS.put("PRTO", PortalLayoutTemplate::new);
		PARSERS.put("SLTD", SlotDescriptorParser::new);
		PARSERS.put("XCYL", CylinderExtentParser::new);
		PARSERS.put("XOCL", OrientedCylinderExtentParser::new);
	}
	
	@Nullable
	public static SWGParser createParser(String tag) {
		Supplier<SWGParser> parser = PARSERS.get(tag);
		return parser == null ? null : parser.get();
	}
}
