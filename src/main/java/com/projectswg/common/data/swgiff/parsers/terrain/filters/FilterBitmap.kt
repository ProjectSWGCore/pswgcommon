package com.projectswg.common.data.swgiff.parsers.terrain.filters

import com.projectswg.common.data.location.Rectangle2f
import com.projectswg.common.data.swgiff.IffChunk
import com.projectswg.common.data.swgiff.IffForm
import com.projectswg.common.data.swgiff.parsers.terrain.TerrainInfoLookup
import com.projectswg.common.data.swgiff.parsers.terrain.bitmap.TargaBitmap
import kotlin.math.min

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
		val width = bitmap.width
		val height = bitmap.height
		val transformedX = (x - rectangle.minX) * width / (rectangle.maxX - rectangle.minX)
		val transformedZ = (z - rectangle.minZ) * height / (rectangle.maxZ - rectangle.minZ)
		val bitmapZ0 = transformedZ.toInt()
		val bitmapX0 = transformedX.toInt()
		val bitmapX1 = bitmapX0 + 1
		val bitmapZ1 = bitmapZ0 + 1
		if (bitmapX0 < 0 || bitmapX1 >= width || bitmapZ0 < 0 || bitmapZ1 >= height)
			return 1f
		
		val topLeft = (bitmap.getData(bitmapZ0 * width + bitmapX0).toInt() and 0xFF).toFloat()
		val topRight = (bitmap.getData(bitmapZ0 * width + bitmapX1).toInt() and 0xFF).toFloat()
		val bottomRight = bitmap.getData(bitmapZ1 * width + bitmapX0).toInt() and 0xFF
		val bottomLeft = bitmap.getData(bitmapZ1 * width + bitmapX1).toInt() and 0xFF
		
		val pX = transformedX - bitmapX0
		val pZ = transformedZ - bitmapZ0
		val mapResult = (gain + (
				pZ * (1f - pX) * bottomRight +
				pZ * pX * bottomLeft +
				(1f - pZ) * (1f - pX) * topLeft +
				(1f - pZ) * pX * topRight) / 255).clamp(0f, 1f)
		
		if (mapResult !in min..max)
			return 0f
		val v35 = ((max - min) * featherAmount * 0.5).toFloat()
		return if (mapResult >= min + v35) {
			if (mapResult <= max - v35) mapResult else mapResult * (max - mapResult) / v35
		} else {
			mapResult * (mapResult - min) / v35
			//result = 0;
		}
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