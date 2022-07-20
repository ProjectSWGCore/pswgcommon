package com.projectswg.common.data.swgiff.parsers.terrain.affectors

import com.projectswg.common.data.location.Point2f
import com.projectswg.common.data.swgiff.IffChunk
import com.projectswg.common.data.swgiff.IffForm
import com.projectswg.common.data.swgiff.parsers.SWGParser
import com.projectswg.common.data.swgiff.parsers.terrain.TerrainInfoLookup

class AffectorHeightTerrace : AffectorHeightLayer(), SWGParser {
	
	private var fraction = 0f
	private var height = 0f
	
	override fun process(p: Point2f, transformAmount: Float, baseValue: Float, terrainInfo: TerrainInfoLookup): Float {
		if (transformAmount <= 0f || height <= 0f)
			return baseValue
		
		val lowHeight = baseValue - (if (baseValue < 0) (height + (baseValue % height)) else (baseValue % height))
		val midHeight = lowHeight + height * fraction
		val highHeight = lowHeight + height
		
		val maxHeight = if (baseValue <= midHeight)
			lowHeight
		else
			lowHeight + height * ((baseValue - midHeight) / (highHeight - midHeight))
		
		return baseValue + (maxHeight - baseValue) * transformAmount
	}
	
	override fun read(form: IffForm) {
		super.read(form)
		assert(form.tag == "AHTR")
		// all versions are supported
		
		form.readChunk("DATA").use { chunk ->
			fraction = chunk.readFloat()
			height = chunk.readFloat()
		}
	}
	
	override fun write(): IffForm {
		val data = IffChunk("DATA")
		data.writeFloat(fraction)
		data.writeFloat(height)
		
		return IffForm.of("AHTR", 4, writeHeaderChunk(), data)
	}
	
	override fun toString(): String {
		return "AffectorHeightTerrace[fraction=$fraction height=$height]"
	}
	
}
