package com.projectswg.common.data.swgiff.parsers.terrain

import com.projectswg.common.data.swgiff.parsers.terrain.bitmap.BitmapFamily
import com.projectswg.common.data.swgiff.parsers.terrain.fractals.FractalFamily

class TerrainInfoLookup(val fractals: Map<Int, FractalFamily>, val bitmaps: Map<Int, BitmapFamily>)
