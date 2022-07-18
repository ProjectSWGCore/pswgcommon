package com.projectswg.common.data.swgiff.parsers.terrain.boundaries

import com.projectswg.common.data.location.Point2f
import com.projectswg.common.data.swgiff.IffChunk
import com.projectswg.common.data.swgiff.IffForm
import kotlin.math.*

class BoundaryPolygon : BoundaryLayer() {
	
	var waterHeight = 0f
		private set
	var useWaterHeight = false
		private set
	
	private var vertices = ArrayList<Point2f>()
	private var waterShaderSize = 0f
	private var waterShader: String? = null
	var waterType = 0
		private set
	
	override fun isContained(x: Float, z: Float): Boolean {
		if (x !in minX..maxX || z !in minZ..maxZ)
			return false
		
		var j = vertices.size - 1
		var crossings = 0
		for (i in vertices.indices) {
			val v1 = vertices[i]
			val v2 = vertices[j]
			j = i
			
			if ((((v1.z <= z) && (z < v2.z)) || ((v2.z <= z) && (z < v1.z))) && x < (v2.x - v1.x) * (z - v1.z) / (v2.z - v1.z) + v1.x)
				crossings++
		}
		return crossings % 2 == 1
	}
	
	override fun process(x: Float, z: Float): Float {
		if (!isContained(x, z))
			return 0.0f
		
		if (featherAmount == 0f)
			return 1.0f
		
		val p = Point2f(x, z)
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
			minX = Float.MAX_VALUE
			minZ = Float.MAX_VALUE
			maxX = -Float.MAX_VALUE
			maxZ = -Float.MAX_VALUE
			
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
