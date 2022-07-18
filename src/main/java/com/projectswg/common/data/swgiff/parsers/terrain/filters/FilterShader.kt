package com.projectswg.common.data.swgiff.parsers.terrain.filters

import com.projectswg.common.data.location.Rectangle2f
import com.projectswg.common.data.swgiff.IffChunk
import com.projectswg.common.data.swgiff.IffForm
import com.projectswg.common.data.swgiff.parsers.terrain.TerrainInfoLookup

class FilterShader : FilterLayer() {
	
	private var shaderId: Int = 0
	
	override fun process(x: Float, z: Float, transformValue: Float, baseValue: Float, rectangle: Rectangle2f, terrainInfo: TerrainInfoLookup): Float {
		return 0f
	}
	
	override fun read(form: IffForm) {
		super.read(form)
		assert(form.tag == "FSHD")
		assert(form.version == 0)
		
		form.readChunk("DATA").use { chunk ->
			shaderId = chunk.readInt()
		}
	}
	
	override fun write(): IffForm {
		val data = IffChunk("DATA")
		data.writeInt(shaderId)
		
		return IffForm.of("FSLP", 0, writeHeaderChunk(), data)
	}
	
	override fun toString(): String {
		return "FilterShader[shaderId=$shaderId]"
	}
	
}
