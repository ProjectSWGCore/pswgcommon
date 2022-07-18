package com.projectswg.common.data.location

import kotlin.math.max
import kotlin.math.min

data class Rectangle2f(var minX: Float, var minZ: Float, var maxX: Float, var maxZ: Float) {
	
	fun isWithin(p: Point2f, extendedRegion: Float = 0f): Boolean {
		return p.x in minX-extendedRegion..maxX+extendedRegion && p.z in minZ-extendedRegion..maxZ+extendedRegion
	}
	
	fun expand(other: Rectangle2f) {
		minX = min(minX, other.minX)
		minZ = min(minZ, other.minZ)
		maxX = max(maxX, other.maxX)
		maxZ = max(maxZ, other.maxZ)
	}
	
	fun expand(p: Point2f) {
		minX = min(minX, p.x)
		minZ = min(minZ, p.z)
		maxX = max(maxX, p.x)
		maxZ = max(maxZ, p.z)
	}

}
