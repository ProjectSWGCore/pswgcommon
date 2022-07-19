package com.projectswg.common.data.swgiff.parsers.terrain.filters

import com.projectswg.common.data.location.Point2f
import com.projectswg.common.data.location.Rectangle2f
import com.projectswg.common.data.swgiff.parsers.terrain.TerrainInfoLookup
import com.projectswg.common.data.swgiff.parsers.terrain.TerrainLayer

abstract class FilterLayer : TerrainLayer() {
	
	var featherType = 0
	var featherAmount = 0f
	
	abstract fun process(p: Point2f, transformValue: Float, baseValue: Float, rectangle: Rectangle2f, terrainInfo: TerrainInfoLookup): Float
	
}
