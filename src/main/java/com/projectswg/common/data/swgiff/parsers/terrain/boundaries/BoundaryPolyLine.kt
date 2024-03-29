package com.projectswg.common.data.swgiff.parsers.terrain.boundaries

import com.projectswg.common.data.location.Point2f
import com.projectswg.common.data.location.Rectangle2f
import com.projectswg.common.data.swgiff.IffChunk
import com.projectswg.common.data.swgiff.IffForm
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

class BoundaryPolyLine : BoundaryLayer() {
	
	private var vertices = ArrayList<Point2f>()
	private var lineWidth = 0f
	
	override fun isContained(p: Point2f): Boolean {
		return extent.isWithin(p)
	}
	
	override fun process(p: Point2f): Float {
		if (!isContained(p))
			return 0.0f
		
		val closestPoint = p.getClosestPointOnPolygon(vertices, closed=false)
		val closestDistance = p.squaredDistanceTo(closestPoint)
		
		if (closestDistance >= lineWidth*lineWidth)
			return 0f
		
		val featherDistance = lineWidth * (1f - featherAmount)
		if (closestDistance < featherDistance*featherDistance)
			return 1f
		
		return 1f - (sqrt(closestDistance) - featherDistance) / (lineWidth - featherDistance)
	}
	
	override fun read(form: IffForm) {
		super.read(form)
		assert(form.tag == "BPLN")
		
		form.readChunk("DATA").use { chunk ->
			val sizeTemp: Int
			if (form.version == 0) {
				featherType = chunk.readInt()
				featherAmount = chunk.readFloat()
				lineWidth = chunk.readFloat()
				
				sizeTemp = chunk.remaining() / 8
			} else {
				sizeTemp = chunk.readInt()
			}
			
			var minX = Float.MAX_VALUE
			var maxX = -Float.MAX_VALUE
			var minZ = Float.MAX_VALUE
			var maxZ = -Float.MAX_VALUE
			for (j in 0 until sizeTemp) {
				val tempX = chunk.readFloat()
				val tempZ = chunk.readFloat()
				
				vertices.add(Point2f(tempX, tempZ))
				minX = min(minX, tempX)
				minZ = min(minZ, tempZ)
				maxX = max(maxX, tempX)
				maxZ = max(maxZ, tempZ)
			}
			extent = Rectangle2f(minX-lineWidth, minZ-lineWidth, maxX+lineWidth, maxZ+lineWidth)
			
			if (form.version != 0) {
				featherType = chunk.readInt()
				featherAmount = chunk.readFloat()
				lineWidth = chunk.readFloat()
			}
		}
	}
	
	override fun write(): IffForm {
		val data = IffChunk("DATA")
		data.writeInt(vertices.size)
		for (vertex in vertices) {
			data.writeFloat(vertex.x)
			data.writeFloat(vertex.z)
		}
		
		data.writeInt(featherType)
		data.writeFloat(featherAmount)
		data.writeFloat(lineWidth)
		
		return IffForm.of("BPLN", 3, writeHeaderChunk(), data)
	}
	
	override fun toString(): String {
		return "BoundaryPolyLine[points=${vertices.size} feathering=$featherType/$featherAmount lineWidth=$lineWidth]"
	}
	
}