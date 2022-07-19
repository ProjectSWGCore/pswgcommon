package com.projectswg.common.data.swgiff.parsers.terrain.filters

import com.projectswg.common.data.location.Point2f
import com.projectswg.common.data.location.Rectangle2f
import com.projectswg.common.data.swgiff.IffChunk
import com.projectswg.common.data.swgiff.IffForm
import com.projectswg.common.data.swgiff.parsers.terrain.TerrainInfoLookup

class FilterHeight : FilterLayer() {
	
	private var minHeight = 0f
	private var maxHeight = 0f
	
	override fun process(p: Point2f, transformValue: Float, baseValue: Float, rectangle: Rectangle2f, terrainInfo: TerrainInfoLookup): Float {
		if (baseValue <= minHeight || baseValue >= maxHeight)
			return 0f
		
		val feather = featherAmount * (maxHeight - minHeight) / 2f
		
		if (baseValue < minHeight + feather)
			return (baseValue - minHeight) / feather
		
		if (baseValue > maxHeight - feather)
			return (maxHeight - baseValue) / feather
		
		return 1f
	}
	
	override fun read(form: IffForm) {
		super.read(form)
		assert(form.tag == "FHGT")
		assert(form.version <= 2)
		
		form.readChunk("DATA").use { chunk ->
			if (form.version == 1)
				chunk.readFloat()
			
			minHeight = chunk.readFloat()
			maxHeight = chunk.readFloat()
			
			if (form.version >= 2) {
				featherType = chunk.readInt()
				featherAmount = chunk.readFloat().clamp(0f, 1f)
			}
		}
	}
	
	override fun write(): IffForm {
		val data = IffChunk("DATA")
		data.writeFloat(minHeight)
		data.writeFloat(maxHeight)
		data.writeInt(featherType)
		data.writeFloat(featherAmount)
		
		return IffForm.of("FHGT", 2, writeHeaderChunk(), data)
	}
	
	override fun toString(): String {
		return "FilterHeight[min=$minHeight max=$maxHeight type=$featherType feather=$featherAmount]"
	}
	
}
