package com.projectswg.common.data.swgiff.parsers.terrain.filters

import com.projectswg.common.data.location.Point2f
import com.projectswg.common.data.location.Rectangle2f
import com.projectswg.common.data.swgiff.IffChunk
import com.projectswg.common.data.swgiff.IffForm
import com.projectswg.common.data.swgiff.parsers.terrain.TerrainInfoLookup

class FilterFractal : FilterLayer() {
	
	var fractalId = 0
	var min = 0f
	var max = 0f
	var step = 0f
	
	override fun process(p: Point2f, transformValue: Float, baseValue: Float, rectangle: Rectangle2f, terrainInfo: TerrainInfoLookup): Float {
		val fractal = terrainInfo.fractals[fractalId] ?: return 0f
		val noiseResult = fractal.getNoise(p.x, p.z) * step
		
		if (noiseResult !in min..max)
			return 0f
		
		val feather = (max - min) * featherAmount / 2
		if (noiseResult < min + feather)
			return (noiseResult - min) / feather
		if (noiseResult > max - feather)
			return (max - noiseResult) / feather
		return 1f
	}
	
	override fun read(form: IffForm) {
		super.read(form)
		assert(form.tag == "FFRA")
		assert(form.version == 5)
		
		form.readForm("DATA").use {
			it.readChunk("PARM").use { chunk ->
				fractalId = chunk.readInt()
				featherType = chunk.readInt()
				featherAmount = chunk.readFloat().clamp(0f, 1f)
				min = chunk.readFloat()
				max = chunk.readFloat()
				step = chunk.readFloat()
			}
		}
	}
	
	override fun write(): IffForm {
		val data = IffChunk("PARM")
		data.writeInt(fractalId)
		data.writeInt(featherType)
		data.writeFloat(featherAmount)
		data.writeFloat(min)
		data.writeFloat(max)
		data.writeFloat(step)
		
		return IffForm.of("FFRA", 5, writeHeaderChunk(), IffForm.of("DATA", data))
	}
	
	override fun toString(): String {
		return "FilterFractal[fractalId=$fractalId feathering=$featherType/$featherAmount min=$min max=$max step=$step]"
	}
	
}
