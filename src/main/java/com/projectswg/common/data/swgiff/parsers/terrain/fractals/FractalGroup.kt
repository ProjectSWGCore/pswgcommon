package com.projectswg.common.data.swgiff.parsers.terrain.fractals

import com.projectswg.common.data.swgiff.IffChunk
import com.projectswg.common.data.swgiff.IffForm
import com.projectswg.common.data.swgiff.parsers.SWGParser
import java.util.ArrayList
import java.util.HashMap

class FractalGroup : SWGParser {
	
	val fractals = HashMap<Int, FractalFamily>()
	
	override fun read(form: IffForm) {
		assert(form.tag == "MGRP")
		assert(form.version == 0)
		
		form.readAllForms("MFAM") { mfam ->
			mfam.use {
				val familyId: Int
				val familyName: String
				mfam.readChunk("DATA").use { chunk ->
					familyId = chunk.readInt()
					familyName = chunk.readString()
				}
				mfam.readForm("MFRC").use { mfrc ->
					val fractal = FractalFamily()
					fractal.fractalId = familyId
					fractal.fractalLabel = familyName
					fractal.read(mfrc)
					fractals[fractal.fractalId] = fractal
				}
			}
		}
	}
	
	override fun write(): IffForm {
		val families = ArrayList<IffForm>()
		for (family in fractals.values) {
			val familyData = IffChunk("DATA")
			familyData.writeInt(family.fractalId)
			familyData.writeString(family.fractalLabel)
			
			families.add(IffForm.of("MFAM", family.write()))
		}
		
		return IffForm.of("MGRP", 0, families)
	}
	
}
