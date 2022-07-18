package com.projectswg.common.data.swgiff.parsers.terrain.boundaries

import com.projectswg.common.data.location.Rectangle2f
import com.projectswg.common.data.swgiff.parsers.terrain.TerrainLayer

abstract class BoundaryLayer : TerrainLayer() {
	
	var featherType = 0
	var featherAmount = 0f
	open val hasWater = false
	
	abstract fun isContained(x: Float, z: Float): Boolean
	abstract fun process(x: Float, z: Float): Float
	
	open var extent = Rectangle2f(0f, 0f, 0f, 0f)
		protected set
	
}
