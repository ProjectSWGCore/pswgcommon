package com.projectswg.common.data.swgiff.parsers.terrain.boundaries

import com.projectswg.common.data.location.Rectangle2f
import com.projectswg.common.data.swgiff.IffChunk
import com.projectswg.common.data.swgiff.IffForm
import com.projectswg.common.data.swgiff.parsers.SWGParser

class BoundaryCircle : BoundaryLayer(), SWGParser {
	
	private var x = 0f
	private var z = 0f
	private var radius = 0f
	private var radiusSquared = 0f
	
	override fun isContained(x: Float, z: Float): Boolean {
		val rX = x - this.x
		val rZ = z - this.z
		
		return rX*rX + rZ*rZ <= radius*radius
	}
	
	override fun process(x: Float, z: Float): Float {
		if (!isContained(x, z))
			return 0f
		
		val distanceX = x - this.x
		val distanceZ = z - this.z
		val distanceSquared = distanceX*distanceX + distanceZ*distanceZ
		if (distanceSquared >= radiusSquared)
			return 0f
		
		val innerRadius = radius * (1f - featherAmount)
		val innerRadiusSquared = innerRadius * innerRadius
		
		if (distanceSquared <= innerRadiusSquared)
			return 1f
		
		return 1f - (distanceSquared - innerRadiusSquared) / (radiusSquared - innerRadiusSquared)
	}
	
	override fun read(form: IffForm) {
		super.read(form)
		assert(form.tag == "BCIR")
		// all versions are supported
		
		form.readChunk("DATA").use { chunk ->
			x = chunk.readFloat()
			z = chunk.readFloat()
			radius = chunk.readFloat()
			radiusSquared = radius * radius
			extent = Rectangle2f(x - radius, z - radius, x + radius, z + radius)
			
			if (form.version == 2) {
				featherType = chunk.readInt()
				featherAmount = chunk.readFloat().clamp(0f, 1f)
			}
		}
	}
	
	override fun write(): IffForm {
		val data = IffChunk("DATA")
		data.writeFloat(x)
		data.writeFloat(z)
		data.writeFloat(radius)
		
		data.writeInt(featherType)
		data.writeFloat(featherAmount)
		
		return IffForm.of("BCIR", 2, writeHeaderChunk(), data)
	}
	
	override fun toString(): String {
		return "BoundaryCircle[($x, $z)  r=$radius  type=$featherType  feather=$featherAmount]"
	}
	
}