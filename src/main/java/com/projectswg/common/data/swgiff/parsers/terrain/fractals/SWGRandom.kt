package com.projectswg.common.data.swgiff.parsers.terrain.fractals

class SWGRandom(private var seed: Int) {
	
	private var table = IntArray(322)
	private var rand = 0
	
	init {
		initializeTable()
	}
	
	operator fun next(): Int {
		if (rand == 0)
			initializeTable()
		rotateSeed()
		
		val tableIndex = (rand / (1 + 0x7FFFFFFE/322.0)).toInt()
		rand = table[tableIndex]
		table[tableIndex] = seed
		return rand
	}
	
	private fun initializeTable() {
		if (seed < 1)
			seed = 1
		
		for (i in 329 downTo 0) {
			rotateSeed()
			if (i < 322)
				table[i] = seed
		}
		rand = table[0]
	}
	
	private fun rotateSeed() {
		val r = seed / 127773
		seed = 16807 * (seed - r * 127773) - 2836 * r
		if (seed < 0)
			seed += 0x7FFFFFFF
	}
	
}