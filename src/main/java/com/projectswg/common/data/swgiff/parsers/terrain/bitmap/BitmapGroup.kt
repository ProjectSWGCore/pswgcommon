package com.projectswg.common.data.swgiff.parsers.terrain.bitmap

import com.projectswg.common.data.swgiff.IffForm
import com.projectswg.common.data.swgiff.parsers.SWGParser
import java.util.*

class BitmapGroup : SWGParser {
	
	val bitmaps = HashMap<Int, BitmapFamily>()
	
	override fun read(form: IffForm) {
		assert(form.tag == "MGRP")
		assert(form.version == 0)
		
		form.readAllForms("MFAM") { mfam ->
			mfam.use {
				val bitmap = BitmapFamily()
				bitmap.read(mfam)
				bitmaps[bitmap.familyId] = bitmap
			}
		}
	}
	
	override fun write(): IffForm {
		val families = ArrayList<IffForm>()
		for (family in bitmaps.values) {
			families.add(family.write())
		}
		
		return IffForm.of("MGRP", 0, families)
	}
	
}
