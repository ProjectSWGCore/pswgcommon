/***********************************************************************************
 * Copyright (c) 2025 /// Project SWG /// www.projectswg.com                       *
 *                                                                                 *
 * ProjectSWG is an emulation project for Star Wars Galaxies founded on            *
 * July 7th, 2011 after SOE announced the official shutdown of Star Wars Galaxies. *
 * Our goal is to create one or more emulators which will provide servers for      *
 * players to continue playing a game similar to the one they used to play.        *
 *                                                                                 *
 * This file is part of PSWGCommon.                                                *
 *                                                                                 *
 * --------------------------------------------------------------------------------*
 *                                                                                 *
 * PSWGCommon is free software: you can redistribute it and/or modify              *
 * it under the terms of the GNU Affero General Public License as                  *
 * published by the Free Software Foundation, either version 3 of the              *
 * License, or (at your option) any later version.                                 *
 *                                                                                 *
 * PSWGCommon is distributed in the hope that it will be useful,                   *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                  *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                   *
 * GNU Affero General Public License for more details.                             *
 *                                                                                 *
 * You should have received a copy of the GNU Affero General Public License        *
 * along with PSWGCommon.  If not, see <http://www.gnu.org/licenses/>.             *
 ***********************************************************************************/
package com.projectswg.common.data.swgiff.parsers.terrain

import com.projectswg.common.data.location.Point2f
import com.projectswg.common.data.swgiff.IffChunk
import com.projectswg.common.data.swgiff.IffForm
import com.projectswg.common.data.swgiff.parsers.SWGParser
import com.projectswg.common.data.swgiff.parsers.terrain.bitmap.BitmapGroup
import com.projectswg.common.data.swgiff.parsers.terrain.boundaries.BoundaryPolygon
import com.projectswg.common.data.swgiff.parsers.terrain.boundaries.BoundaryRectangle
import com.projectswg.common.data.swgiff.parsers.terrain.fractals.FractalGroup
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

class TerrainTemplate : SWGParser {

	var name = ""
	var mapWidth = 0f
	var chunkWidth = 0f
	var numberOfTilesPerChunk = 0
	var useGlobalWaterTable = false
	var globalWaterTableHeight = 0f
	var globalWaterTableShaderSize = 0f
	var globalWaterTableShaderTemplateName = ""
	var environmentCycleTime = 0f
	var collidableMinimumDistance = 0f
	var collidableMaximumDistance = 0f
	var collidableTileSize = 0f
	var collidableTileBorder = 0f
	var collidableSeed = 0
	var nonCollidableMinimumDistance = 0f
	var nonCollidableMaximumDistance = 0f
	var nonCollidableTileSize = 0f
	var nonCollidableTileBorder = 0f
	var nonCollidableSeed = 0
	var radialMinimumDistance = 0f
	var radialMaximumDistance = 0f
	var radialTileSize = 0f
	var radialTileBorder = 0f
	var radialSeed = 0
	var farRadialMinimumDistance = 0f
	var farRadialMaximumDistance = 0f
	var farRadialTileSize = 0f
	var farRadialTileBorder = 0f
	var farRadialSeed = 0
	var legacyMap = true

	val fractalGroup = FractalGroup()
	val bitmapGroup = BitmapGroup()
	private val lookupInformation = TerrainInfoLookup(fractalGroup.fractals, bitmapGroup.bitmaps)
	private val topTerrainLayer = TerrainListLayer()

	override fun read(form: IffForm) {
		assert(form.tag == "PTAT")
		// all versions are supported

		form.readChunk("DATA").use { chunk ->
			name = chunk.readString()
			mapWidth = chunk.readFloat()
			chunkWidth = chunk.readFloat()
			numberOfTilesPerChunk = chunk.readInt()
			useGlobalWaterTable = chunk.readInt() != 0
			globalWaterTableHeight = chunk.readFloat()
			globalWaterTableShaderSize = chunk.readFloat()
			globalWaterTableShaderTemplateName = chunk.readString()
			environmentCycleTime = chunk.readFloat()

			if (form.version == 13) {
				chunk.readString()
				chunk.readString()
				chunk.readFloat()
				chunk.readString()
				chunk.readFloat()
				chunk.readString()
				chunk.readFloat()
				chunk.readString()
				chunk.readFloat()
				chunk.readInt()
				chunk.readString()
			}

			collidableMinimumDistance = chunk.readFloat()
			collidableMaximumDistance = chunk.readFloat()
			collidableTileSize = chunk.readFloat()
			collidableTileBorder = chunk.readFloat()
			collidableSeed = chunk.readInt()
			nonCollidableMinimumDistance = chunk.readFloat()
			nonCollidableMaximumDistance = chunk.readFloat()
			nonCollidableTileSize = chunk.readFloat()
			nonCollidableTileBorder = chunk.readFloat()
			nonCollidableSeed = chunk.readInt()
			radialMinimumDistance = chunk.readFloat()
			radialMaximumDistance = chunk.readFloat()
			radialTileSize = chunk.readFloat()
			radialTileBorder = chunk.readFloat()
			radialSeed = chunk.readInt()
			farRadialMinimumDistance = chunk.readFloat()
			farRadialMaximumDistance = chunk.readFloat()
			farRadialTileSize = chunk.readFloat()
			farRadialTileBorder = chunk.readFloat()
			farRadialSeed = chunk.readInt()

			legacyMap = if (form.version >= 15) chunk.readBoolean() else true
		}

		form.readForm("TGEN").use { tgen ->
			tgen.readForm("MGRP")?.use { fractalGroup.read(it) }
			tgen.readForm("MGRP")?.use { bitmapGroup.read(it) }

			// Load layers
			tgen.readForm("LYRS").use { lyrs ->
				lyrs.readAllForms("LAYR") { layer ->
					layer.use {
						val terrainListLayer = TerrainListLayer()
						terrainListLayer.read(layer)
						topTerrainLayer.addLayer(terrainListLayer)
					}
				}
			}
		}
	}

	override fun write(): IffForm {
		val data = IffChunk("DATA")
		data.writeString(name)
		data.writeFloat(mapWidth)
		data.writeFloat(chunkWidth)
		data.writeInt(numberOfTilesPerChunk)
		data.writeInt(if (useGlobalWaterTable) 1 else 0)
		data.writeFloat(globalWaterTableHeight)
		data.writeFloat(globalWaterTableShaderSize)
		data.writeString(globalWaterTableShaderTemplateName)
		data.writeFloat(environmentCycleTime)

		data.writeFloat(collidableMinimumDistance)
		data.writeFloat(collidableMaximumDistance)
		data.writeFloat(collidableTileSize)
		data.writeFloat(collidableTileBorder)
		data.writeInt(collidableSeed)
		data.writeFloat(nonCollidableMinimumDistance)
		data.writeFloat(nonCollidableMaximumDistance)
		data.writeFloat(nonCollidableTileSize)
		data.writeFloat(nonCollidableTileBorder)
		data.writeInt(nonCollidableSeed)
		data.writeFloat(radialMinimumDistance)
		data.writeFloat(radialMaximumDistance)
		data.writeFloat(radialTileSize)
		data.writeFloat(radialTileBorder)
		data.writeInt(radialSeed)
		data.writeFloat(farRadialMinimumDistance)
		data.writeFloat(farRadialMaximumDistance)
		data.writeFloat(farRadialTileSize)
		data.writeFloat(farRadialTileBorder)
		data.writeInt(farRadialSeed)

		if (!legacyMap) data.writeBoolean(false) // Only use the new v15 format if it's not legacy

		val version = if (legacyMap) 14 else 15

		val children = ArrayList<IffForm>()
		children.add(fractalGroup.write())
		children.add(bitmapGroup.write())
		val layerChildren = ArrayList<IffForm>()
		for (child in topTerrainLayer.children) {
			layerChildren.add(child.write())
		}
		children.add(IffForm.of("LYRS", layerChildren))

		val terrainGeneratorForm = IffForm.of("TGEN", 0, children)

		return IffForm.of("PTAT", version, data, terrainGeneratorForm)
	}

	fun isBitmapReferenced(bitmapId: Int): Boolean {
		return topTerrainLayer.isBitmapReferenced(bitmapId)
	}

	fun getHeight(x: Float, z: Float): TerrainInformation {
		val waterHeight = getWaterHeight(x, z)
		val terrainHeight = getTerrainHeight(x, z)

		if (waterHeight.isNaN() || terrainHeight.height >= waterHeight) return terrainHeight

		return TerrainInformation(waterHeight, 0f, 1f, 0f)
	}

	fun getWaterHeight(x: Float, z: Float): Float {
		var height = -Float.MAX_VALUE
		if (useGlobalWaterTable) height = globalWaterTableHeight
		height = max(height, getWaterHeightRecursive(Point2f(x, z), topTerrainLayer))

		return if (height == -Float.MAX_VALUE) Float.NaN else height
	}

	fun isWater(x: Float, z: Float): Boolean {
		val waterHeight = getWaterHeight(x, z)

		if (waterHeight.isNaN()) return false

		return getTerrainHeight(x, z).height <= waterHeight
	}

	fun getTerrainHeight(x: Float, z: Float): TerrainInformation {
		// can be cached
		val tileSize = chunkWidth / (2 * numberOfTilesPerChunk)
		val halfMap = mapWidth / 2
		// smart stuff
		val tileLocalX = ((x + halfMap) % chunkWidth) / tileSize
		val tileLocalZ = ((z + halfMap) % chunkWidth) / tileSize
		val tileX = x - (tileLocalX - tileLocalX.toInt()) * tileSize
		val tileZ = z - (tileLocalZ - tileLocalZ.toInt()) * tileSize

		val angleRight = ((tileLocalX.toInt() xor tileLocalZ.toInt()) and 1) == 0
		val sideLeft = when (angleRight) {
			false -> ((tileLocalX - tileLocalX.toInt()) <= 1 - (tileLocalZ - tileLocalZ.toInt())) // left
			true  -> ((tileLocalX - tileLocalX.toInt()) <= (tileLocalZ - tileLocalZ.toInt()))      // top
		}

		val planeInfo = FloatArray(9)
		planeInfo[0] = tileX
		planeInfo[2] = tileZ
		planeInfo[3] = tileX
		planeInfo[5] = tileZ
		planeInfo[6] = tileX
		planeInfo[8] = tileZ
		if (angleRight) {
			planeInfo[3] += tileSize
			planeInfo[if (sideLeft) 5 else 6] += tileSize
			planeInfo[8] += tileSize
		} else {
			planeInfo[0] += tileSize
			planeInfo[5] += tileSize
			if (!sideLeft) {
				planeInfo[3] += tileSize
				planeInfo[8] += tileSize
			}
		}
		// Set the y value for each vertex
		planeInfo[1] = getHeightAt(planeInfo[0], planeInfo[2])
		planeInfo[4] = getHeightAt(planeInfo[3], planeInfo[5])
		planeInfo[7] = getHeightAt(planeInfo[6], planeInfo[8])

		// Set up the a and b vectors for the cross product
		for (i in 0..2) {
			planeInfo[3 + i] -= planeInfo[i]
			planeInfo[6 + i] -= planeInfo[i]
		}

		// Cross product! This is the plane normal
		val cx = planeInfo[4] * planeInfo[8] - planeInfo[5] * planeInfo[7]
		val cy = planeInfo[5] * planeInfo[6] - planeInfo[3] * planeInfo[8]
		val cz = planeInfo[3] * planeInfo[7] - planeInfo[4] * planeInfo[6]
		val planeOffset = planeInfo[0] * cx + planeInfo[1] * cy + planeInfo[2] * cz
		val height = (planeOffset - cx * x - cz * z) / cy

		return TerrainInformation(height, cx, cy, cz)
	}

	private fun getHeightAt(x: Float, z: Float): Float {
		val p = Point2f(x, z)
		return getLayerHeight(topTerrainLayer, p, 1f, 0f)
	}

	private fun getLayerHeight(layer: TerrainListLayer, p: Point2f, previousTransformValue: Float, previousHeight: Float): Float {
		val transformValue = calculateTransformValue(lookupInformation, layer, p, previousHeight)

		var newHeight = previousHeight
		if (transformValue > 0f) {
			for (affector in layer.heights) {
				newHeight = affector.process(p, transformValue * previousTransformValue, newHeight, lookupInformation)
			}
		}

		val newTransformValue = transformValue * previousTransformValue
		for (child in layer.children) {
			newHeight = getLayerHeight(child, p, newTransformValue, newHeight)
		}
		return newHeight
	}

	private fun getWaterHeightRecursive(p: Point2f, layer: TerrainListLayer): Float {
		var waterHeight = -Float.MAX_VALUE
		for (boundary in layer.boundaries) {
			if (boundary is BoundaryPolygon && boundary.useWaterHeight && boundary.isContained(p)) {
				waterHeight = max(waterHeight, boundary.waterHeight)
			} else if (boundary is BoundaryRectangle && boundary.useWaterHeight && boundary.isContained(p)) {
				waterHeight = max(waterHeight, boundary.waterHeight)
			}
		}
		for (child in layer.children) {
			waterHeight = max(waterHeight, getWaterHeightRecursive(p, child))
		}
		return waterHeight
	}

	data class TerrainInformation(val height: Float, val normalX: Float, val normalY: Float, val normalZ: Float)

	companion object {

		private fun calculateTransformValue(lookupInformation: TerrainInfoLookup, layer: TerrainListLayer, p: Point2f, height: Float): Float {
			var transformValue = if (layer.boundaries.isEmpty()) 1f else 0f

			val rectangle = layer.extent
			for (boundary in layer.boundaries) {
				if (!boundary.isContained(p)) continue

				transformValue = max(transformValue, calculateFeathering(boundary.process(p), boundary.featherType))
			}

			if (layer.invertBoundaries) transformValue = 1.0f - transformValue

			for (filter in layer.filters) {
				transformValue = min(transformValue, calculateFeathering(filter.process(p, transformValue, height, rectangle, lookupInformation), filter.featherType))
				if (transformValue == 0f) break
			}

			if (layer.invertFilters) transformValue = 1.0f - transformValue

			val onlySubLayers = layer.boundaries.isEmpty() && layer.filters.isEmpty() && layer.heights.isEmpty()
			if (onlySubLayers) transformValue = 1f

			assert(transformValue in 0f..1f)
			return transformValue
		}

		private fun calculateFeathering(value: Float, featheringType: Int): Float {
			return when (featheringType) {
				0    -> value
				1    -> value * value
				2    -> sqrt(value)
				3    -> value * value * (3 - 2 * value)
				else -> 0f
			}
		}

	}

}
