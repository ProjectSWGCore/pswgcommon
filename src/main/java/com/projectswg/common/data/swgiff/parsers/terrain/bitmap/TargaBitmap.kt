package com.projectswg.common.data.swgiff.parsers.terrain.bitmap

import java.io.FileInputStream
import java.io.InputStream

class TargaBitmap {
	
	private var idLength: Byte = 0
	private var colorMapType: Byte = 0
	private var dataTypeCode: Byte = 0
	private var colorMapDepth: Byte = 0
	private var bitsPerPixel: Int = 0
	private var imageDescriptor: Byte = 0
	private var colorMapOrigin: Short = 0
	private var colorMapLength: Short = 0
	private var originX: Short = 0
	private var originY: Short = 0
	var width: Int = 0
		private set
	var height: Int = 0
		private set
	private lateinit var pixelData: ByteArray
	var fileName: String? = null
	
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
			width = inputStream.readShort().toInt()
			height = inputStream.readShort().toInt()
			bitsPerPixel = inputStream.readByte().toInt()
			imageDescriptor = inputStream.readByte()
			
			var length = 0
			if (width == height)
				length = width * height
			else if (width > height)
				length = width * width
			else if (height > width)
				length = height * height
			
			inputStream.skip((idLength + colorMapType * colorMapLength).toLong())
			if (bitsPerPixel == 8) {
				pixelData = inputStream.readNBytes(length)
			} else {
				assert(dataTypeCode == 2.toByte() || dataTypeCode == 10.toByte())
				pixelData = ByteArray(length * 4)
				val pixelLoaderFunction = when (bitsPerPixel) {
					16 -> ::read2ByteColorPixel
					24 -> ::read3ByteColorPixel
					32 -> ::read4ByteColorPixel
					else -> throw NotImplementedError("unsupported bits per pixel: $bitsPerPixel")
				}
				val bytesPerPixel = bitsPerPixel / 8
				var index = 0
				for (i in 0 until length) {
					pixelLoaderFunction(inputStream, pixelData, index)
					index += bytesPerPixel
				}
			}
		}
	}
	
	fun getData(offset: Int): Byte {
		if (offset < 0 || offset >= width * height)
			throw ArrayIndexOutOfBoundsException(offset)
		return pixelData[offset]
	}
	
	private fun InputStream.readByte(): Byte {
		return read().toByte()
	}
	
	private fun InputStream.readShort(): Short {
		return ((read() or (read() shl 8)) and 0xFFFF).toShort()
	}
	
	companion object {
		private fun read4ByteColorPixel(src: InputStream, dst: ByteArray, indexIn: Int) {
			var index: Int = indexIn
			dst[index++] = src.read().toByte()
			dst[index++] = src.read().toByte()
			dst[index++] = src.read().toByte()
			dst[index] = src.read().toByte()
		}
		
		private fun read3ByteColorPixel(src: InputStream, dst: ByteArray, indexIn: Int) {
			var index: Int = indexIn
			dst[index++] = src.read().toByte()
			dst[index++] = src.read().toByte()
			dst[index++] = src.read().toByte()
			dst[index] = 0
		}
		
		private fun read2ByteColorPixel(src: InputStream, dst: ByteArray, indexIn: Int) {
			var index: Int = indexIn
			val first = src.read()
			val second = src.read()
			dst[index++] = (first and 0x7c shl 1).toByte()
			dst[index++] = (first and 0x03 shl 6 or (second and 0xe0 shl 2)).toByte()
			dst[index++] = (second and 0x1f shl 3).toByte()
			dst[index] = (first and 0x80).toByte()
		}
	}
	
}
