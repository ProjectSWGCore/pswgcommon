package com.projectswg.common.data.swgiff.parsers.terrain.boundaries

import com.projectswg.common.data.location.Point2f
import com.projectswg.common.data.swgiff.IffChunk
import com.projectswg.common.data.swgiff.IffForm
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

class BoundaryPolyLine : BoundaryLayer() {
	
	private var vertices = ArrayList<Point2f>()
	private var lineWidth = 0f
	
	override fun isContained(x: Float, z: Float): Boolean {
		return x in minX-lineWidth..maxX+lineWidth && z in minZ-lineWidth..maxZ+lineWidth
	}
	
	override fun process(x: Float, z: Float): Float {
		if (!isContained(x, z))
			return 0.0f
		
		val p = Point2f(x, z)
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
			minX = Float.MAX_VALUE
			maxX = -Float.MAX_VALUE
			minZ = Float.MAX_VALUE
			maxZ = -Float.MAX_VALUE
			
			val sizeTemp: Int
			if (form.version == 0) {
				featherType = chunk.readInt()
				featherAmount = chunk.readFloat()
				lineWidth = chunk.readFloat()
				
				sizeTemp = chunk.remaining() / 8
			} else {
				sizeTemp = chunk.readInt()
			}
			
			for (j in 0 until sizeTemp) {
				val tempX = chunk.readFloat()
				val tempZ = chunk.readFloat()
				
				vertices.add(Point2f(tempX, tempZ))
				minX = min(minX, tempX)
				minZ = min(minZ, tempZ)
				maxX = max(maxX, tempX)
				maxZ = max(maxZ, tempZ)
			}
			
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