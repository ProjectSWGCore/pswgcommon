package com.projectswg.common.data.swgiff.parsers.terrain.filters

import com.projectswg.common.data.location.Point2f
import com.projectswg.common.data.location.Rectangle2f
import com.projectswg.common.data.swgiff.IffChunk
import com.projectswg.common.data.swgiff.IffForm
import com.projectswg.common.data.swgiff.parsers.terrain.TerrainInfoLookup
import kotlin.math.sin

class FilterSlope : FilterLayer() {
	
	private var minAngle = 0f
	private var maxAngle = 0f
	private var minAngleSin = 0f
	private var maxAngleSin = 0f
	
	override fun process(p: Point2f, transformValue: Float, baseValue: Float, rectangle: Rectangle2f, terrainInfo: TerrainInfoLookup): Float {
		return if (baseValue > minAngleSin && baseValue < maxAngleSin) {
			val featherResult = (maxAngleSin - minAngleSin * featherAmount * 0.5).toFloat()
			if (minAngleSin + featherResult <= baseValue) {
				if (maxAngleSin - featherResult >= baseValue) {
					1.0f
				} else {
					(maxAngleSin - baseValue) / featherResult
				}
			} else (baseValue - minAngleSin) / featherResult
		} else 0f
	}
	
	override fun read(form: IffForm) {
		super.read(form)
		assert(form.tag == "FSLP")
		assert(form.version <= 2)
		
		form.readChunk("DATA").use { chunk ->
			if (form.version == 1)
				chunk.readFloat()
			
			minAngle = Math.toRadians(chunk.readFloat().toDouble()).toFloat().clamp(0f, MAX_ANGLE)
			maxAngle = Math.toRadians(chunk.readFloat().toDouble()).toFloat().clamp(0f, MAX_ANGLE)
			minAngleSin = sin(minAngle)
			maxAngleSin = sin(maxAngle)
			if (form.version >= 2) {
				featherType = chunk.readInt()
				featherAmount = chunk.readFloat().clamp(0f, 1f)
			}
		}
	}
	
	override fun write(): IffForm {
		val data = IffChunk("DATA")
		data.writeFloat(Math.toDegrees(minAngle.toDouble()).toFloat())
		data.writeFloat(Math.toDegrees(maxAngle.toDouble()).toFloat())
		data.writeInt(featherType)
		data.writeFloat(featherAmount)
		
		return IffForm.of("FSLP", 2, writeHeaderChunk(), data)
	}
	
	override fun toString(): String {
		return "FilterSlope[feathering=$featherType/$featherAmount minAngle=$minAngle maxAngle=$maxAngle]"
	}
	
	companion object {
		const val MAX_ANGLE = (Math.PI / 2).toFloat()
	}
	
}