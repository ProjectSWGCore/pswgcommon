package com.projectswg.common.data.swgiff.parsers.terrain

import com.projectswg.common.data.swgiff.IffChunk
import com.projectswg.common.data.swgiff.IffForm
import com.projectswg.common.data.swgiff.parsers.SWGParser
import kotlin.math.max
import kotlin.math.min

abstract class TerrainLayer : SWGParser {
	
	var isEnabled = true
		private set
	var customName = ""
		private set
	
	override fun read(form: IffForm) {
		form.readForm("IHDR").use { ihdr ->
			ihdr.readChunk("DATA").use { chunk ->
				isEnabled = chunk.readInt() != 0
				customName = chunk.readString()
			}
		}
	}
	
	fun writeHeaderChunk(): IffForm {
		val data = IffChunk("DATA")
		data.writeInt(if (isEnabled) 1 else 0)
		data.writeString(customName)
		
		return IffForm.of("IHDR", 1, data)
	}
	
	companion object {
		fun Float.clamp(minValue: Float, maxValue: Float): Float {
			return min(maxValue, max(minValue, this))
		}
	}
	
}
