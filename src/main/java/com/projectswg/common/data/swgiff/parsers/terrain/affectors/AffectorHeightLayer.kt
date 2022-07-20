package com.projectswg.common.data.swgiff.parsers.terrain.affectors

import com.projectswg.common.data.location.Point2f
import com.projectswg.common.data.swgiff.parsers.terrain.TerrainInfoLookup
import com.projectswg.common.data.swgiff.parsers.terrain.TerrainLayer


abstract class AffectorHeightLayer : TerrainLayer() {
	
	abstract fun process(p: Point2f, transformAmount: Float, baseValue: Float, terrainInfo: TerrainInfoLookup): Float
	
}
