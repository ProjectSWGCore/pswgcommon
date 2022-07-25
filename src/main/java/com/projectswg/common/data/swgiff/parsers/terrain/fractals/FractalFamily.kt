package com.projectswg.common.data.swgiff.parsers.terrain.fractals

import com.projectswg.common.data.swgiff.IffChunk
import com.projectswg.common.data.swgiff.IffForm
import com.projectswg.common.data.swgiff.parsers.SWGParser
import kotlin.math.*

class FractalFamily : SWGParser {
	
	var fractalId = 0
	var fractalLabel: String? = null
	var seed: Int = 0
		set(seed) {
			field = seed
			noise = PerlinNoise(SWGRandom(seed))
		}
	var useBias = false
	var bias = 0f
	var useGain = false
	var gain = 0f
		set(value) {
			field = value
			logGain = ln(1.0f - gain) / ln(0.5f)
		}
	private var logGain: Float = 0f
	var octaves = 0
	var octavesArg = 0f
	var amplitude = 0f
		set(value) {
			field = value
			offset = 0.0f
			var currAmplitude = 0.0f
			var nextAmplitude = 1.0f
			for (i in 0 until octaves) {
				currAmplitude += nextAmplitude
				nextAmplitude *= value
			}
			offset = currAmplitude
			if (offset != 0f) {
				offset = 1.0f / offset
			}
		}
	var freqX = 0f
	var freqZ = 0f
	var offsetX = 0f
	var offsetZ = 0f
	var combinationType = 0
	var offset = 0f
	
	private var noise: PerlinNoise? = null
	
	fun getNoise(x: Float, z: Float): Float {
		var result: Float = when (combinationType) {
			0, 1 -> (calculateCombination(x, z) { noise -> noise } + 1) / 2f
			2    -> calculateCombination(x, z)  { noise -> (1.0f - abs(noise)) }
			3    -> calculateCombination(x, z)  { noise -> abs(noise) }
			4    -> calculateCombination(x, z)  { noise -> 1 - max(0.0f, min(1.0f, noise)) }
			5    -> calculateCombination(x, z)  { noise -> max(0.0f, min(1.0f, noise)) }
			else -> throw IllegalStateException("unknown combination type: $combinationType")
		}
		
		if (useBias)
			result = result.pow(ln(bias) / ln(0.5f))
		
		if (useGain) {
			result = when {
				result < 0.001 -> 0.0f
				result > 0.999 -> 1.0f
				result < 0.50f -> (2 * result).pow(logGain) / 2f
				else           -> 1f - (2 * (1 - result)).pow(logGain) / 2f
			}
		}
		return result
	}
	
	private fun calculateCombination(x: Float, z: Float, noiseCalculator: (noise: Float) -> Float): Float {
		var noiseGen = 0.0f
		var xNoise = offsetX + x * freqX
		var zNoise = offsetZ + z * freqZ
		var currAmpl = 1.0f
		
		for (i in 0 until octaves) {
			noiseGen += noiseCalculator(noise!!.noise2(xNoise, zNoise)) * currAmpl
			
			xNoise *= octavesArg
			zNoise *= octavesArg
			currAmpl *= amplitude
		}
		
		return noiseGen * offset
	}
	
	override fun read(form: IffForm) {
		assert(form.tag == "MFRC")
		
		form.readChunk("DATA").use { chunk ->
			seed = chunk.readInt()
			useBias = chunk.readInt() != 0
			bias = chunk.readFloat()
			useGain = chunk.readInt() != 0
			gain = chunk.readFloat()
			octaves = chunk.readInt()
			octavesArg = chunk.readFloat()
			amplitude = chunk.readFloat()
			freqX = chunk.readFloat()
			freqZ = chunk.readFloat()
			if (form.version > 0) {
				offsetX = chunk.readFloat()
				offsetZ = chunk.readFloat()
			}
			combinationType = chunk.readInt()
		}
	}
	
	override fun write(): IffForm {
		val data = IffChunk("DATA")
		data.writeInt(seed)
		data.writeInt(if (useBias) 1 else 0)
		data.writeFloat(bias)
		data.writeInt(if (useGain) 1 else 0)
		data.writeFloat(gain)
		data.writeInt(octaves)
		data.writeFloat(octavesArg)
		data.writeFloat(amplitude)
		data.writeFloat(freqX)
		data.writeFloat(freqZ)
		data.writeFloat(offsetX)
		data.writeFloat(offsetZ)
		data.writeInt(combinationType)
		
		return IffForm.of("MFRC", 1, data)
	}
	
	override fun toString(): String {
		return "FractalFamily[combinationType=$combinationType   offset=($offsetX, $offsetZ)   " +
				"freq=($freqX, $freqZ)  octaves=$octaves($octavesArg)   amplitude=$amplitude   " +
				"useBias=$useBias   useGain=$useGain   " +
				"result=${getNoise(-5939f, -6033f)}]"
	}
}
