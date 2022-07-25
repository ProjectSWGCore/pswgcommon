package com.projectswg.common.data.swgiff.parsers;

import com.projectswg.common.data.swgiff.IffForm;
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

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

enum SWGParserFactory {
	INSTANCE;
	
	private final Map<String, Supplier<SWGParser>> parsers = new HashMap<>();
	private final AtomicReference<String> basePath = new AtomicReference<>("clientdata");
	
	SWGParserFactory() {
		// Terrain
		parsers.put("PTAT", TerrainTemplate::new);
		parsers.put("LAYR", TerrainListLayer::new);
		parsers.put("AHCN", AffectorHeightConstant::new);
		parsers.put("AHFR", AffectorHeightFractal::new);
		parsers.put("AHTR", AffectorHeightTerrace::new);
		parsers.put("AROA", AffectorHeightRoad::new);
		parsers.put("BREC", BoundaryRectangle::new);
		parsers.put("BCIR", BoundaryCircle::new);
		parsers.put("BPLN", BoundaryPolyLine::new);
		parsers.put("BPOL", BoundaryPolygon::new);
		parsers.put("FBIT", FilterBitmap::new);
		parsers.put("FDIR", FilterDirection::new);
		parsers.put("FFRA", FilterFractal::new);
		parsers.put("FHGT", FilterHeight::new);
		parsers.put("FSLP", FilterSlope::new);
		parsers.put("FSHD", FilterShader::new);
		
		parsers.put("APT ", AppearanceTemplateList::new);
		parsers.put("APPR", AppearanceTemplate::new);
		parsers.put("ARGD", SlotArrangementParser::new);
		parsers.put("CELL", PortalLayoutCellTemplate::new);
		parsers.put("CMPT", ComponentExtentParser::new);
		parsers.put("CMSH", MeshExtentParser::new);
		parsers.put("CPST", CompositeExtentParser::new);
		parsers.put("CSTB", CrcStringDataParser::new);
		parsers.put("DTAL", DetailExtentParser::new);
		parsers.put("DTLA", DetailAppearanceTemplate::new);
		parsers.put("EXBX", BoxExtentParser::new);
		parsers.put("EXSP", SphereExtentParser::new);
		parsers.put("FOOT", FootprintDataParser::new);
		parsers.put("IDTL", IndexedTriangleListParser::new);
		parsers.put("MESH", MeshAppearanceTemplate::new);
		parsers.put("NULL", NullExtentParser::new);
		parsers.put("PFDT", CombinedProfessionTemplateParser::new);
		parsers.put("PRFI", ProfessionTemplateParser::new);
		parsers.put("PRTL", PortalLayoutCellPortalTemplate::new);
		parsers.put("PRTO", PortalLayoutTemplate::new);
		parsers.put("SLTD", SlotDescriptorParser::new);
		parsers.put("XCYL", CylinderExtentParser::new);
		parsers.put("XOCL", OrientedCylinderExtentParser::new);
	}
	
	public void setBasePath(String basePath) {
		this.basePath.set(basePath);
	}
	
	public String getBasePath() {
		return this.basePath.get();
	}
	
	@Nullable
	public static SWGParser createParser(String tag) {
		Supplier<SWGParser> parser = INSTANCE.parsers.get(tag);
		return parser == null ? null : parser.get();
	}
	
	static <T extends SWGParser> T parse(String file) {
		return parse(new File(INSTANCE.basePath.get(), file));
	}
	
	static <T extends SWGParser> T parse(File file) {
		return SWGParserCache.parseIfAbsent(file);
	}
	
	@Nullable
	static <T extends SWGParser> T parse(IffForm form) {
		SWGParser parser = createParser(form.getTag());
		if (parser == null)
			return null;
		parser.read(form);
		{
			@SuppressWarnings("unchecked")
			T ret = (T) parser;
			return ret;
		}
	}
	
}
