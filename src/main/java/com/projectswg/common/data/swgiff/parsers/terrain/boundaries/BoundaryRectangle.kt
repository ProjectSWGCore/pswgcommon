package com.projectswg.common.data.swgiff.parsers.terrain.boundaries

import com.projectswg.common.data.location.Point2f
import com.projectswg.common.data.location.Rectangle2f
import com.projectswg.common.data.swgiff.IffChunk
import com.projectswg.common.data.swgiff.IffForm
import kotlin.math.min

class BoundaryRectangle : BoundaryLayer() {
	
	var waterHeight = 0f
		private set
	var useWaterHeight = false
		private set
	override val hasWater
		get() = useWaterHeight
	private var useGlobalWaterTable = false
	private var shaderSize = 0f
	private var shaderName = ""
	var waterType = 0
		private set
	
	override fun isContained(p: Point2f): Boolean {
		return extent.isWithin(p)
	}
	
	override fun process(p: Point2f): Float {
		if (!isContained(p))
			return 0f
		val minX = extent.minX
		val minZ = extent.minZ
		val maxX = extent.maxX
		val maxZ = extent.maxZ
		
		val feather = min(maxX - minX, maxZ - minZ) * featherAmount / 2f
		
		if (p.x in minX+feather..maxX-feather && p.z in minZ+feather..maxZ-feather)
			return 1f
		
		return min(feather, min(p.x - minX, min(maxX - p.x, min(p.z - minZ, maxZ - p.z)))) / feather
	}
	
	override fun read(form: IffForm) {
		super.read(form)
		assert(form.tag == "BREC")
		
		form.readChunk("DATA").use { chunk ->
			if (form.version == 1)
				chunk.readFloat()
			
			var minX = chunk.readFloat()
			var minZ = chunk.readFloat()
			var maxX = chunk.readFloat()
			var maxZ = chunk.readFloat()
			
			var temp: Float
			if (minX > maxX) {
				temp = minX
				minX = maxX
				maxX = temp
			}
			if (minZ > maxZ) {
				temp = minZ
				minZ = maxZ
				maxZ = temp
			}
			extent = Rectangle2f(minX, minZ, maxX, maxZ)
			
			if (form.version >= 2) {
				featherType = chunk.readInt()
				featherAmount = chunk.readFloat().clamp(0f, 1f)
			}
			if (form.version >= 3) {
				useWaterHeight = chunk.readInt() != 0
				useGlobalWaterTable = chunk.readInt() != 0
				waterHeight = chunk.readFloat()
				shaderSize = chunk.readFloat()
				shaderName = chunk.readString()
			}
			if (form.version >= 4) {
				waterType = chunk.readInt()
			}
		}
	}
	
	override fun write(): IffForm {
		val data = IffChunk("DATA")
		data.writeFloat(extent.minX)
		data.writeFloat(extent.minZ)
		data.writeFloat(extent.maxX)
		data.writeFloat(extent.maxZ)
		
		data.writeInt(featherType)
		data.writeFloat(featherAmount)
		
		data.writeInt(if (useWaterHeight) 1 else 0)
		data.writeInt(if (useGlobalWaterTable) 1 else 0)
		data.writeFloat(waterHeight)
		data.writeFloat(shaderSize)
		data.writeString(shaderName)
		data.writeInt(waterType)
		
		return IffForm.of("BREC", 4, writeHeaderChunk(), data)
	}
	
	override fun toString(): String {
		return "BoundaryRectangle[$extent water=l:$useWaterHeight/g:$useGlobalWaterTable/$waterHeight]"
	}
	
}
