package com.projectswg.common.data.location

data class Point2f(val x: Float, val z: Float) {
	
	fun squaredDistanceTo(v: Point2f): Float {
		val dX = x - v.x
		val dZ = z - v.z
		return dX*dX + dZ*dZ
	}
	
	fun getClosestPointOnPolygon(vertices: List<Point2f>, closed: Boolean = true): Point2f {
		val closestVertex = getClosestVertex(vertices)
		val vertexDistance = squaredDistanceTo(closestVertex)
		
		val closestLine = getClosestPerpendicularPointOnPolygon(vertices, closed)
		
		if (vertexDistance <= closestLine.squaredDistance)
			return closestVertex
		
		return closestLine.closestPoint
	}
	
	fun getClosestLineOnPolygon(vertices: List<Point2f>, closed: Boolean = true): ClosestLine2f {
		val closestVertex = getClosestVertex(vertices)
		val vertexDistance = squaredDistanceTo(closestVertex)
		
		val closestLine = getClosestPerpendicularPointOnPolygon(vertices, closed)
		
		if (vertexDistance <= closestLine.squaredDistance) {
			val vertexIndex = vertices.indexOf(closestVertex)
			return ClosestLine2f(closestVertex, vertices[(vertexIndex+1) % vertices.size], closestVertex, vertexDistance)
		}
		
		return closestLine
	}
	
	private fun getClosestVertex(vertices: List<Point2f>): Point2f {
		var closestDistance = squaredDistanceTo(vertices[0])
		var closestVertex = vertices[0]
		for (vert in vertices) {
			val distance = squaredDistanceTo(vert)
			if (distance < closestDistance) {
				closestDistance = distance
				closestVertex = vert
			}
		}
		return closestVertex
	}
	
	private fun getClosestPerpendicularPointOnPolygon(vertices: List<Point2f>, closed: Boolean): ClosestLine2f {
		var closestLine = ClosestLine2f(vertices[0], vertices[0], vertices[0], Float.MAX_VALUE)
		
		var j = vertices.size - 1
		for (i in vertices.indices) {
			val v1 = vertices[i]
			val v2 = vertices[j]
			val lineX = v2.x - v1.x
			val lineZ = v2.z - v1.z
			j = i
			if (!closed && i == 0)
				continue
			
			val w = ((this.x - v1.x) * lineX + (this.z - v1.z) * lineZ) / (lineX*lineX + lineZ*lineZ)
			if (w in 0f..1f) {
				val vertexCandidate = Point2f(v1.x + w * lineX, v1.z + w * lineZ)
				val squaredDistance = squaredDistanceTo(vertexCandidate)
				if (squaredDistance < closestLine.squaredDistance) {
					closestLine = ClosestLine2f(v1, v2, vertexCandidate, squaredDistance)
				}
			}
		}
		
		return closestLine
	}
	
	fun squaredDistanceTo(v: Point3D): Float {
		val dX = x - v.x
		val dZ = z - v.z
		return (dX*dX + dZ*dZ).toFloat()
	}
	
	fun getClosestPointOnPolygon(vertices: List<Point3D>, closed: Boolean = true): Point3D {
		val closestVertex = getClosestVertex(vertices)
		val vertexDistance = squaredDistanceTo(closestVertex)
		
		val closestLine = getClosestPerpendicularPointOnPolygon(vertices, closed)
		
		if (vertexDistance <= closestLine.squaredDistance)
			return closestVertex
		
		return closestLine.closestPoint
	}
	
	fun getClosestLineOnPolygon(vertices: List<Point3D>, closed: Boolean = true): ClosestLine3D {
		val closestVertex = getClosestVertex(vertices)
		val vertexDistance = squaredDistanceTo(closestVertex)
		
		val closestLine = getClosestPerpendicularPointOnPolygon(vertices, closed)
		
		if (vertexDistance <= closestLine.squaredDistance) {
			val vertexIndex = vertices.indexOf(closestVertex)
			return ClosestLine3D(closestVertex, vertices[(vertexIndex+1) % vertices.size], closestVertex, vertexDistance)
		}
		
		return closestLine
	}
	
	private fun getClosestVertex(vertices: List<Point3D>): Point3D {
		var closestDistance = squaredDistanceTo(vertices[0])
		var closestVertex = vertices[0]
		for (vert in vertices) {
			val distance = squaredDistanceTo(vert)
			if (distance < closestDistance) {
				closestDistance = distance
				closestVertex = vert
			}
		}
		return closestVertex
	}
	
	private fun getClosestPerpendicularPointOnPolygon(vertices: List<Point3D>, closed: Boolean): ClosestLine3D {
		var closestLine = ClosestLine3D(vertices[0], vertices[0], vertices[0], Float.MAX_VALUE)
		
		var j = vertices.size - 1
		for (i in vertices.indices) {
			val v1 = vertices[i]
			val v2 = vertices[j]
			val lineX = v2.x - v1.x
			val lineY = v2.y - v1.y
			val lineZ = v2.z - v1.z
			j = i
			if (!closed && i == 0)
				continue
			
			val w = ((this.x - v1.x) * lineX + (this.z - v1.z) * lineZ) / (lineX*lineX + lineZ*lineZ)
			if (w in 0f..1f) {
				val vertexCandidate = Point3D(v1.x + w * lineX, v1.y + w * lineY, v1.z + w * lineZ)
				val squaredDistance = squaredDistanceTo(vertexCandidate)
				if (squaredDistance < closestLine.squaredDistance) {
					closestLine = ClosestLine3D(v1, v2, vertexCandidate, squaredDistance)
				}
			}
		}
		
		return closestLine
	}
	
	data class ClosestLine3D(val p1: Point3D, val p2: Point3D, val closestPoint: Point3D, val squaredDistance: Float)
	
	data class ClosestLine2f(val p1: Point2f, val p2: Point2f, val closestPoint: Point2f, val squaredDistance: Float)
	
	
}
