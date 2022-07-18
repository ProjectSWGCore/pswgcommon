package com.projectswg.common.data.swgiff.parsers.terrain

import com.projectswg.common.data.swgiff.IffForm
import com.projectswg.common.data.swgiff.parsers.SWGParser
import com.projectswg.common.data.swgiff.parsers.terrain.affectors.AffectorHeightLayer
import com.projectswg.common.data.swgiff.parsers.terrain.boundaries.BoundaryLayer
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
	
	fun hasAffectors(): Boolean {
		for (affector in heights) {
			if (affector.isEnabled)
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
		
		form.readAllForms {
			it.use {
				val layer = SWGParser.parse<TerrainLayer>(it)
				when (layer) {
					is BoundaryLayer -> boundaries.add(layer)
					is FilterLayer -> filters.add(layer)
					is AffectorHeightLayer -> heights.add(layer)
					is TerrainListLayer -> children.add(layer)
				}
			}
		}
	}
	
	override fun write(): IffForm {
		TODO("Not yet implemented")
	}
	
	override fun toString(): String {
		return "TerrainListLayer[boundaries=${boundaries.size} filters=0 heights=${heights.size} children=${children.size}]"
	}
	
	fun printTree(indent: Int=0) {
		if (!isEnabled || !hasAffectors())
			return
		
		val indentStr = "\t".repeat(indent)
		for (layer in boundaries) {
			if (layer.isEnabled)
				println("$indentStr$layer")
		}
		for (layer in filters) {
			if (layer.isEnabled)
				println("$indentStr$layer")
		}
		for (layer in heights) {
			if (layer.isEnabled)
				println("$indentStr$layer")
		}
		for (child in children)
			child.printTree(indent+1)
	}
	
}