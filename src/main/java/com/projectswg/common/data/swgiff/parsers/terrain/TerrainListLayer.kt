package com.projectswg.common.data.swgiff.parsers.terrain

import com.projectswg.common.data.location.Rectangle2f
import com.projectswg.common.data.swgiff.IffChunk
import com.projectswg.common.data.swgiff.IffForm
import com.projectswg.common.data.swgiff.IffNode
import com.projectswg.common.data.swgiff.parsers.SWGParser
import com.projectswg.common.data.swgiff.parsers.terrain.affectors.AffectorHeightLayer
import com.projectswg.common.data.swgiff.parsers.terrain.boundaries.BoundaryLayer
import com.projectswg.common.data.swgiff.parsers.terrain.filters.FilterBitmap
import com.projectswg.common.data.swgiff.parsers.terrain.filters.FilterLayer

class TerrainListLayer : TerrainLayer(), SWGParser {
	
	val boundaries = ArrayList<BoundaryLayer>()
	val filters = ArrayList<FilterLayer>()
	val heights = ArrayList<AffectorHeightLayer>()
	val children = ArrayList<TerrainListLayer>()
	
	var invertBoundaries = false
	var invertFilters = false
	var expanded = false
	var notes = ""
	var extent = Rectangle2f(0f, 0f, 0f, 0f)
	
	fun hasAffectors(): Boolean {
		if (heights.isNotEmpty())
			return true
		
		for (boundary in boundaries) {
			if (boundary.hasWater)
				return true
		}
		
		for (child in children) {
			if (child.hasAffectors())
				return true
		}
		
		return false
	}
	
	override fun read(form: IffForm) {
		super.read(form)
		assert(form.tag == "LAYR")
		
		form.readChunk("ADTA").use { chunk ->
			if (form.version <= 0)
				return@use
			
			invertBoundaries = chunk.readInt() != 0
			invertFilters = chunk.readInt() != 0
			
			if (form.version == 4)
				chunk.readInt()
			
			if (form.version >= 2)
				expanded = chunk.readInt() != 0
			
			if (form.version >= 3)
				notes = chunk.readString()
		}
		
		form.readAllForms { childForm ->
			childForm.use {
				addLayer(SWGParser.parse(childForm) ?: return@use)
			}
		}
	}
	
	override fun write(): IffForm {
		val data = IffChunk("ADTA")
		data.writeInt(if (invertBoundaries) 1 else 0)
		data.writeInt(if (invertFilters) 1 else 0)
		data.writeInt(if (expanded) 1 else 0)
		data.writeString(notes)
		val childForms = ArrayList<IffNode>()
		childForms.add(super.writeHeaderChunk())
		childForms.add(data)
		for (child in boundaries)
			childForms.add(child.write())
		for (child in filters)
			childForms.add(child.write())
		for (child in heights)
			childForms.add(child.write())
		for (child in children)
			childForms.add(child.write())
		
		return IffForm.of("LAYR", 3, childForms)
	}
	
	override fun toString(): String {
		return "TerrainListLayer[boundaries=${boundaries.size} filters=0 heights=${heights.size} children=${children.size}]"
	}
	
	fun isBitmapReferenced(bitmapId: Int): Boolean {
		for (child in filters) {
			if (child is FilterBitmap && child.bitmapId == bitmapId)
				return true
		}
		
		for (child in children) {
			if (child.isBitmapReferenced(bitmapId))
				return true
		}
		
		return false
	}
	
	fun addLayer(layer: TerrainLayer) {
		if (!layer.isEnabled)
			return
		when (layer) {
			is BoundaryLayer -> {
				boundaries.add(layer)
				recalculateExtent()
			}
			is FilterLayer -> filters.add(layer)
			is AffectorHeightLayer -> heights.add(layer)
			is TerrainListLayer -> {
				if (layer.hasAffectors())
					children.add(layer)
			}
		}
	}
	
	private fun recalculateExtent() {
		val rectangle = Rectangle2f(Float.MAX_VALUE, Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE)
		for (boundary in boundaries) {
			rectangle.expand(boundary.extent)
		}
		this.extent = rectangle
	}
	
}