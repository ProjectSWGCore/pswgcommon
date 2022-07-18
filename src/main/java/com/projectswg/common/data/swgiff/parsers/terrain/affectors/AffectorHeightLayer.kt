package com.projectswg.common.data.swgiff.parsers.terrain.affectors

import com.projectswg.common.data.swgiff.parsers.terrain.TerrainInfoLookup
import com.projectswg.common.data.swgiff.parsers.terrain.TerrainLayer


abstract class AffectorHeightLayer : TerrainLayer() {
	
	abstract fun process(x: Float, z: Float, transformAmount: Float, baseValue: Float, terrainInfo: TerrainInfoLookup): Float
	
}
