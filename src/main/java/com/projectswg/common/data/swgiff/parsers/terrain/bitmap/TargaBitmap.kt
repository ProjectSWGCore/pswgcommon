package com.projectswg.common.data.swgiff.parsers.terrain.bitmap

import java.io.FileInputStream
import java.io.InputStream

class TargaBitmap {
	
	private var idLength: Byte = 0
	private var colorMapType: Byte = 0
	private var dataTypeCode: Byte = 0
	private var colorMapDepth: Byte = 0
	private var bitsPerPixel: Byte = 0
	private var imageDescriptor: Byte = 0
	private var colorMapOrigin: Short = 0
	private var colorMapLength: Short = 0
	private var originX: Short = 0
	private var originY: Short = 0
	var width: Short = 0
		private set
	var height: Short = 0
		private set
	private lateinit var pixelData: Array<TargaPixel?>
	var fileName: String? = null
	
	inner class TargaColorPixel : TargaPixel() {
		var r = 0.toChar()
		var g = 0.toChar()
		var b = 0.toChar()
		var a = 0.toChar()
		
		override fun read(buffer: InputStream, bytes: Int) {
			when (bytes) {
				4 -> {
					r = Char(buffer.read().toUShort())
					g = Char(buffer.read().toUShort())
					b = Char(buffer.read().toUShort())
					a = Char(buffer.read().toUShort())
				}
				3 -> {
					r = Char(buffer.read().toUShort())
					g = Char(buffer.read().toUShort())
					b = Char(buffer.read().toUShort())
					a = 0.toChar()
				}
				2 -> {
					val first = Char(buffer.read().toUShort())
					val second = Char(buffer.read().toUShort())
					r = (first.code and 0x7c shl 1).toChar()
					g = (first.code and 0x03 shl 6 or (second.code and 0xe0 shl 2)).toChar()
					b = (second.code and 0x1f shl 3).toChar()
					a = (first.code and 0x80).toChar()
				}
			}
		}
	}
	
	inner class TargaBlackPixel : TargaPixel() {
		var `val` = 0.toChar()
		override fun read(buffer: InputStream, bytes: Int) {
			`val` = Char(buffer.readByte().toUShort())
		}
	}
	
	abstract inner class TargaPixel {
		abstract fun read(buffer: InputStream, bytes: Int)
	}
	
	fun readFile(filePath: String) {
		fileName = filePath
		FileInputStream("clientdata/$filePath").use { inputStream ->
			idLength = inputStream.readByte()
			colorMapType = inputStream.readByte()
			dataTypeCode = inputStream.readByte()
			colorMapOrigin = inputStream.readShort()
			colorMapLength = inputStream.readShort()
			colorMapDepth = inputStream.readByte()
			originX = inputStream.readShort()
			originY = inputStream.readShort()
			width = inputStream.readShort()
			height = inputStream.readShort()
			bitsPerPixel = inputStream.readByte()
			imageDescriptor = inputStream.readByte()
			
			var length = 0
			if (width == height)
				length = width * height
			else if (width > height)
				length = width * width
			else if (height > width)
				length = height * height
			
			pixelData = arrayOfNulls(length)
			for (i in 0 until length) {
				var pixel: TargaPixel? = null
				when (dataTypeCode.toInt()) {
					3 -> pixel = TargaBlackPixel()
					2, 10 -> pixel = TargaColorPixel()
				}
				pixelData[i] = pixel
			}
			inputStream.skip((idLength + colorMapType * colorMapLength).toLong())
			val bytes = bitsPerPixel / 8
			if (width <= height) {
				for (i in width - 1 downTo 0) {
					for (j in 0 until height) {
						pixelData[i * width + j]!!.read(inputStream, bytes)
					}
				}
			} else {
				for (i in height - 1 downTo 0) {
					for (j in 0 until width) {
						pixelData[i * height + j]!!.read(inputStream, bytes)
					}
				}
			}
		}
	}
	
	fun getData(offset: Int): Char {
		if (offset < 0 || offset >= width * height)
			throw ArrayIndexOutOfBoundsException(offset)
		return (pixelData[offset] as TargaBlackPixel?)!!.`val`
	}
	
	private fun InputStream.readByte(): Byte {
		return read().toByte()
	}
	
	private fun InputStream.readShort(): Short {
		return ((read() or (read() shl 8)) and 0xFFFF).toShort()
	}
	
}
