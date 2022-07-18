package com.projectswg.common.data.swgiff.parsers.terrain.boundaries

import com.projectswg.common.data.location.Rectangle2f
import com.projectswg.common.data.swgiff.parsers.terrain.TerrainLayer

abstract class BoundaryLayer : TerrainLayer() {
	
	var featherType = 0
	var featherAmount = 0f
	
	protected var minX: Float = 0f
	protected var minZ: Float = 0f
	protected var maxX: Float = 0f
	protected var maxZ: Float = 0f
	
	abstract fun isContained(x: Float, z: Float): Boolean
	abstract fun process(x: Float, z: Float): Float
	
	open val extent: Rectangle2f
		get() = Rectangle2f(minX, minZ, maxX, maxZ)
	
}
