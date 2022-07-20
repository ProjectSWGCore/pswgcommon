package com.projectswg.common.data.location

import kotlin.math.max
import kotlin.math.min

data class Rectangle2f(var minX: Float, var minZ: Float, var maxX: Float, var maxZ: Float) {
	
	fun isWithin(x: Float, z: Float, extendedRegion: Float = 0f): Boolean {
		if (x < minX - extendedRegion)
			return false
		if (x > maxX + extendedRegion)
			return false
		if (z < minZ - extendedRegion)
			return false
		if (z > maxZ + extendedRegion)
			return false
		return true
	}
	
	fun isWithin(p: Point2f, extendedRegion: Float = 0f): Boolean {
		return isWithin(p.x, p.z, extendedRegion)
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
