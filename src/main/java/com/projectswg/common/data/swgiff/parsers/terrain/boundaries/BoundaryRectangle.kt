package com.projectswg.common.data.swgiff.parsers.terrain.boundaries

import com.projectswg.common.data.swgiff.IffChunk
import com.projectswg.common.data.swgiff.IffForm
import kotlin.math.min

class BoundaryRectangle : BoundaryLayer() {
	
	var waterHeight = 0f
		private set
	var useWaterHeight = false
		private set
	private var useGlobalWaterTable = false
	private var shaderSize = 0f
	private var shaderName = ""
	var waterType = 0
		private set
	
	override fun isContained(x: Float, z: Float): Boolean {
		return x in minX..maxX && z in minZ..maxZ
	}
	
	override fun process(x: Float, z: Float): Float {
		if (!isContained(x, z))
			return 0f
		
		val feather = min(maxX - minX, maxZ - minZ) * featherAmount / 2f
		
		if (x in minX+feather..maxX-feather && z in minZ+feather..maxZ-feather)
			return 1f
		
		
		return min(feather, min(x - minX, min(maxX - x, min(z - minZ, maxZ - z)))) / feather
	}
	
	override fun read(form: IffForm) {
		super.read(form)
		assert(form.tag == "BREC")
		
		form.readChunk("DATA").use { chunk ->
			if (form.version == 1)
				chunk.readFloat()
			
			minX = chunk.readFloat()
			minZ = chunk.readFloat()
			maxX = chunk.readFloat()
			maxZ = chunk.readFloat()
			
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
		data.writeFloat(minX)
		data.writeFloat(minZ)
		data.writeFloat(maxX)
		data.writeFloat(maxZ)
		
		data.writeInt(featherType)
		data.writeFloat(featherAmount)
		
		data.writeInt(if (useWaterHeight) 1 else 0)
		data.writeInt(if (useGlobalWaterTable) 1 else 0)
		data.writeFloat(waterHeight)
		data.writeFloat(shaderSize)
		data.writeString(shaderName)
		data.writeInt(waterType)
		
		return IffForm.of("BREC", 0, writeHeaderChunk(), data)
	}
	
	override fun toString(): String {
		return "BoundaryRectangle[min=($minX, $minZ) max=($maxX, $maxZ) water=l:$useWaterHeight/g:$useGlobalWaterTable/$waterHeight]"
	}
	
}
