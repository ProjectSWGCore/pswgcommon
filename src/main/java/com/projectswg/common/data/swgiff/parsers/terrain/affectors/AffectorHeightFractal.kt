package com.projectswg.common.data.swgiff.parsers.terrain.affectors

import com.projectswg.common.data.location.Point2f
import com.projectswg.common.data.swgiff.IffChunk
import com.projectswg.common.data.swgiff.IffForm
import com.projectswg.common.data.swgiff.parsers.SWGParser
import com.projectswg.common.data.swgiff.parsers.terrain.TerrainInfoLookup

class AffectorHeightFractal : AffectorHeightLayer(), SWGParser {
	
	private var fractalId = 0
	private var transformType = 0
	private var height = 0f
	
	override fun process(p: Point2f, transformAmount: Float, baseValue: Float, terrainInfo: TerrainInfoLookup): Float {
		if (transformAmount == 0f)
			return baseValue
		val fractal = terrainInfo.fractals[fractalId] ?: return 0f
		val noiseResult = fractal.getNoise(p.x, p.z) * height
		
		return when (transformType) {
			1 -> baseValue + transformAmount * noiseResult
			2 -> baseValue - transformAmount * noiseResult
			3 -> baseValue + (baseValue * noiseResult - baseValue) * transformAmount
			else -> baseValue + (noiseResult - baseValue) * transformAmount
		}
	}
	
	override fun read(form: IffForm) {
		super.read(form)
		assert(form.tag == "AHFR")
		assert(form.version == 3)
		
		form.readForm("DATA").use {
			it.readChunk("PARM").use { chunk ->
				fractalId = chunk.readInt()
				transformType = chunk.readInt()
				height = chunk.readFloat()
			}
		}
	}
	
	override fun write(): IffForm {
		val data = IffChunk("PARM")
		data.writeInt(fractalId)
		data.writeInt(transformType)
		data.writeFloat(height)
		
		return IffForm.of("AHFR", 3, writeHeaderChunk(), IffForm.of("DATA", data))
	}
	
	override fun toString(): String {
		return "AffectorHeightFractal[id=$fractalId type=$transformType height=$height]"
	}
	
}
