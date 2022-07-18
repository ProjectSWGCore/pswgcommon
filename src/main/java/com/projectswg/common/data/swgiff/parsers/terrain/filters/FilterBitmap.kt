package com.projectswg.common.data.swgiff.parsers.terrain.filters

import com.projectswg.common.data.location.Rectangle2f
import com.projectswg.common.data.swgiff.IffChunk
import com.projectswg.common.data.swgiff.IffForm
import com.projectswg.common.data.swgiff.parsers.terrain.TerrainInfoLookup
import com.projectswg.common.data.swgiff.parsers.terrain.bitmap.TargaBitmap

class FilterBitmap : FilterLayer() {
	
	private var bitmapId = 0
	private var min = 0f
	private var max = 0f
	private var gain = 0f
	private var bitmap: TargaBitmap? = null
	
	override fun process(x: Float, z: Float, transformValue: Float, baseValue: Float, rectangle: Rectangle2f, terrainInfo: TerrainInfoLookup): Float {
		if (bitmap == null)
			bitmap = terrainInfo.bitmaps[bitmapId]?.bitmap
		
		val bitmap = bitmap ?: return 1f  // Didnt find bitmap for filter
		val width = bitmap.width.toFloat()
		val height = bitmap.height.toFloat()
		val v44 = width.toLong()
		var arg6 = width
		var transformedX = (x - rectangle.minX) * width / (rectangle.maxX - rectangle.minX)
		var transformedZ = (z - rectangle.minZ) * height / (rectangle.maxZ - rectangle.minZ)
		if (transformedX > width - 1) {
			transformedX = width - 1
		}
		if (transformedZ > height - 1) {
			transformedZ = height - 1
		}
		val v41 = transformedZ.toInt()
		val v39 = transformedX + 1
		val v43 = transformedX.toInt()
		val v42 = transformedX
		val v40 = transformedZ
		var arg1 = (transformedZ.toInt() + 1).toFloat()
		var v25 = (v44 * (height - v41 - 1)).toInt()
		val v26 = (v39 * v44 / arg6).toInt()
		var v27 = (v43 * v44 / arg6).toInt()
		val v28 = (bitmap.getData(v27 + v25).code and 0xFF).toFloat()
		//byte mapValue = v28;
		var arg2 = (bitmap.getData(v26 + v25).code and 0xFF).toFloat()
		val v29 = (v44 * (height - arg1 - 1)).toInt()
		v25 = bitmap.getData(v29 + v27).code and 0xFF
		v27 = bitmap.getData(v26 + v29).code and 0xFF
		arg6 = v25.toFloat()
		arg1 = v27.toFloat()
		val v30 = v42 - v43.toDouble()
		val v31 = v40 - v41.toDouble()
		var mapResult = (v31 * (1.0 - v30) * arg6 * 0.003921568859368563 + v31 * v30 * arg1 * 0.003921568859368563 + (1.0 - v31) * (1.0 - v30) * v28 * 0.003921568859368563 + (1.0 - v31) * v30 * arg2 * 0.003921568859368563).toFloat()
		mapResult += gain
		if (mapResult >= 1.0) mapResult = 0.9999899864196777.toFloat()
		if (mapResult < 0) mapResult = 0f
		val v32 = featherAmount
		val v33 = max
		arg6 = min
		arg2 = v32
		arg1 = v33
		var result = 0f
		if (mapResult > arg6 && mapResult < arg1) {
			val v35 = ((arg1 - arg6) * arg2 * 0.5).toFloat()
			result = if (mapResult >= arg6 + v35) {
				if (mapResult <= arg1 - v35) mapResult else mapResult * (arg1 - mapResult) / v35
			} else {
				mapResult * (mapResult - arg6) / v35
				//result = 0;
			}
		}
		return result
	}
	
	override fun read(form: IffForm) {
		super.read(form)
		assert(form.tag == "FBIT")
		
		form.readForm("DATA").use {
			it.readChunk("PARM").use { chunk ->
				bitmapId = chunk.readInt()
				featherType = chunk.readInt()
				featherAmount = chunk.readFloat()
				min = chunk.readFloat()
				max = chunk.readFloat()
				if (form.version == 1)
					gain = chunk.readFloat()
			}
		}
	}
	
	override fun write(): IffForm {
		val data = IffChunk("PARM")
		data.writeInt(bitmapId)
		data.writeInt(featherType)
		data.writeFloat(featherAmount)
		data.writeFloat(min)
		data.writeFloat(max)
		data.writeFloat(gain)
		
		return IffForm.of("FBIT", 1, writeHeaderChunk(), IffForm.of("DATA", data))
	}
	
	override fun toString(): String {
		return "FilterBitmap[bitmapId=$bitmapId feathering=$featherType/$featherAmount min=$min max=$max]"
	}
	
}