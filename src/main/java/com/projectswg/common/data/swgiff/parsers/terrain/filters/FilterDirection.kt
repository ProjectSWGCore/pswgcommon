package com.projectswg.common.data.swgiff.parsers.terrain.filters

import com.projectswg.common.data.location.Point2f
import com.projectswg.common.data.location.Rectangle2f
import com.projectswg.common.data.swgiff.IffChunk
import com.projectswg.common.data.swgiff.IffForm
import com.projectswg.common.data.swgiff.parsers.terrain.TerrainInfoLookup

class FilterDirection : FilterLayer() {
	
	private var minAngle = 0f
	private var maxAngle = 0f
	
	override fun process(p: Point2f, transformValue: Float, baseValue: Float, rectangle: Rectangle2f, terrainInfo: TerrainInfoLookup): Float {
		TODO("unimplemented")
	}
	
	override fun read(form: IffForm) {
		super.read(form)
		assert(form.tag == "FDIR")
		
		form.readChunk("DATA").use { chunk ->
			minAngle = Math.toRadians(chunk.readFloat().toDouble()).toFloat()
			maxAngle = Math.toRadians(chunk.readFloat().toDouble()).toFloat()
			featherType = chunk.readInt()
			featherAmount = chunk.readFloat()
		}
	}
	
	override fun write(): IffForm {
		val data = IffChunk("DATA")
		data.writeFloat(Math.toDegrees(minAngle.toDouble()).toFloat())
		data.writeFloat(Math.toDegrees(maxAngle.toDouble()).toFloat())
		data.writeInt(featherType)
		data.writeFloat(featherAmount)
		
		return IffForm.of("FDIR", 0, writeHeaderChunk(), data)
	}
	
	override fun toString(): String {
		return "FilterDirection[angle=[$minAngle, $maxAngle] feathering=$featherType/$featherAmount]"
	}
	
}
