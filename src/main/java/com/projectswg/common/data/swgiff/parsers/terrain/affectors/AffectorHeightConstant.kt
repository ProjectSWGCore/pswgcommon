package com.projectswg.common.data.swgiff.parsers.terrain.affectors

import com.projectswg.common.data.location.Point2f
import com.projectswg.common.data.swgiff.IffChunk
import com.projectswg.common.data.swgiff.IffForm
import com.projectswg.common.data.swgiff.parsers.SWGParser
import com.projectswg.common.data.swgiff.parsers.terrain.TerrainInfoLookup

class AffectorHeightConstant : AffectorHeightLayer(), SWGParser {
	
	private var transformType = 0
	private var height = 0f
	
	override fun process(p: Point2f, transformAmount: Float, baseValue: Float, terrainInfo: TerrainInfoLookup): Float {
		if (transformAmount <= 0f)
			return baseValue
		
		return when (transformType) {
			1 -> baseValue + transformAmount * height
			2 -> baseValue - transformAmount * height
			3 -> baseValue + (baseValue * height - baseValue) * transformAmount
			else -> transformAmount * height + (1f - transformAmount) * baseValue
		}
	}
	
	override fun read(form: IffForm) {
		super.read(form)
		assert(form.tag == "AHCN")
		assert(form.version == 0)
		
		form.readChunk("DATA").use { chunk ->
			transformType = chunk.readInt()
			height = chunk.readFloat()
		}
	}
	
	override fun write(): IffForm {
		val data = IffChunk("DATA")
		data.writeInt(transformType)
		data.writeFloat(height)
		
		return IffForm.of("AHCN", 0, writeHeaderChunk(), data)
	}
	
	override fun toString(): String {
		return "AffectorHeightConstant[type=$transformType height=$height]"
	}
	
}
