package com.projectswg.common.data.swgiff.parsers.terrain.boundaries

import com.projectswg.common.data.location.Point2f
import com.projectswg.common.data.location.Rectangle2f
import com.projectswg.common.data.swgiff.IffChunk
import com.projectswg.common.data.swgiff.IffForm
import kotlin.math.*

class BoundaryPolygon : BoundaryLayer() {
	
	var waterHeight = 0f
		private set
	var useWaterHeight = false
		private set
	override val hasWater
		get() = useWaterHeight
	
	private var vertices = ArrayList<Point2f>()
	private var waterShaderSize = 0f
	private var waterShader: String? = null
	var waterType = 0
		private set
	
	override fun isContained(p: Point2f): Boolean {
		if (!extent.isWithin(p))
			return false
		
		var j = vertices.size - 1
		var crossings = 0
		for (i in vertices.indices) {
			val v1 = vertices[i]
			val v2 = vertices[j]
			j = i
			
			if ((((v1.z <= p.z) && (p.z < v2.z)) || ((v2.z <= p.z) && (p.z < v1.z))) && p.x < (v2.x - v1.x) * (p.z - v1.z) / (v2.z - v1.z) + v1.x)
				crossings++
		}
		return crossings % 2 == 1
	}
	
	override fun process(p: Point2f): Float {
		if (!isContained(p))
			return 0.0f
		
		if (featherAmount == 0f)
			return 1.0f
		
		val closestPoint = p.getClosestPointOnPolygon(vertices)
		val closestDistance = p.squaredDistanceTo(closestPoint)
		
		if (closestDistance >= featherAmount * featherAmount)
			return 1.0f
		return sqrt(closestDistance) / featherAmount
	}
	
	override fun read(form: IffForm) {
		super.read(form)
		assert(form.tag == "BPOL")
		
		form.readChunk("DATA").use { chunk ->
			if (form.version == 0)
				chunk.readFloat()
			
			if (form.version >= 5)
				readVertices(chunk)
			
			featherType = chunk.readInt()
			featherAmount = chunk.readFloat()
			if (form.version >= 3) {
				useWaterHeight = chunk.readInt() != 0
				waterHeight = chunk.readFloat()
				
				if (form.version >= 4)
					waterShaderSize = chunk.readFloat()
				
				if (form.version == 6)
					chunk.readInt()
				else if (form.version == 7)
					waterType = chunk.readInt()
				
				waterShader = chunk.readString()
			}
			
			if (form.version < 5)
				readVertices(chunk)
		}
	}
	
	private fun readVertices(chunk: IffChunk) {
		var minX = Float.MAX_VALUE
		var minZ = Float.MAX_VALUE
		var maxX = -Float.MAX_VALUE
		var maxZ = -Float.MAX_VALUE
		
		val sizeTemp = chunk.readInt()
		for (j in 0 until sizeTemp) {
			val tempX = chunk.readFloat()
			val tempZ = chunk.readFloat()
			
			vertices.add(Point2f(tempX, tempZ))
			minX = min(minX, tempX)
			minZ = min(minZ, tempZ)
			maxX = max(maxX, tempX)
			maxZ = max(maxZ, tempZ)
		}
		
		extent = Rectangle2f(minX, minZ, maxX, maxZ)
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
		data.writeInt(if (useWaterHeight) 1 else 0)
		data.writeFloat(waterHeight)
		data.writeFloat(waterShaderSize)
		data.writeInt(waterType)
		data.writeString(waterShader)
		
		return IffForm.of("BPOL", 7, writeHeaderChunk(), data)
	}
	
	override fun toString(): String {
		return "BoundaryPolygon[points=${vertices.size} feathering=$featherType/$featherAmount waterHeight=$useWaterHeight/$waterHeight extent=$extent]"
	}
	
}
