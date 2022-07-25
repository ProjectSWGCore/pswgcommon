package com.projectswg.common.data.swgiff.parsers.terrain.bitmap

import com.projectswg.common.data.swgiff.IffChunk
import com.projectswg.common.data.swgiff.IffForm
import com.projectswg.common.data.swgiff.parsers.SWGParser
import java.io.IOException

class BitmapFamily : SWGParser {
	
	var familyId = 0
	var name: String? = null
	var file: String? = null
		set(value) {
			field = value
			if (value == null) {
				this.bitmap = null
			} else {
				try {
					val bitmap = TargaBitmap()
					bitmap.readFile(value)
					this.bitmap = bitmap
				} catch (e: IOException) {
					this.bitmap = null
				}
			}
		}
	var bitmap: TargaBitmap? = null
		private set
	
	override fun read(form: IffForm) {
		assert(form.tag == "MFAM")
		
		form.readChunk("DATA").use { chunk ->
			familyId = chunk.readInt()
			name = chunk.readString()
			file = chunk.readString()
		}
	}
	
	override fun write(): IffForm {
		val data = IffChunk("DATA")
		data.writeInt(familyId)
		data.writeString(name)
		data.writeString(file)
		
		return IffForm.of("MFAM", 0, data)
	}
	
}
