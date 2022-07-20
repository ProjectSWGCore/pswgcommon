package com.projectswg.common.data.swgiff.parsers.terrain.affectors

import com.projectswg.common.data.location.Point2f
import com.projectswg.common.data.location.Point3D
import com.projectswg.common.data.location.Rectangle2f
import com.projectswg.common.data.swgiff.IffChunk
import com.projectswg.common.data.swgiff.IffForm
import com.projectswg.common.data.swgiff.parsers.SWGParser
import com.projectswg.common.data.swgiff.parsers.terrain.TerrainInfoLookup
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

class AffectorHeightRoad : AffectorHeightLayer(), SWGParser {
	
	private val vertices = ArrayList<Point2f>()
	private val heights = ArrayList<Float>()
	private val segments = ArrayList<List<Point3D>>()
	private var width = 0f
	private var fractalId = 0
	private var featheringType = 0
	private var featheringAmount = 0f
	private var hasFixedHeights = false
	private var extent = Rectangle2f(Float.MAX_VALUE, Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE)
	
	override fun process(p: Point2f, transformAmount: Float, baseValue: Float, terrainInfo: TerrainInfoLookup): Float {
		if (transformAmount <= 0)
			return baseValue
		
		if (!extent.isWithin(p, width))
			return baseValue
		
		val halfWidth = width / 2f
		val halfWidthSquared = halfWidth*halfWidth
		val closestLine = p.getClosestLineOnPolygon(vertices, closed=false)
		
		if (closestLine.squaredDistance >= halfWidthSquared)
			return baseValue
		
		if (hasFixedHeights)
			return getRampedHeight(p, closestLine, baseValue)
		
		val startIndex = vertices.indexOf(closestLine.p2)
		val matchPoint = p.getClosestPointOnPolygon(if (startIndex < segments.size) segments[startIndex] else segments[0], closed=false)
		val t = sqrt(closestLine.squaredDistance) / halfWidth
		
		if (t <= (1 - featheringAmount))
			return matchPoint.y.toFloat()
		
		return matchPoint.y.toFloat() + (baseValue - matchPoint.y.toFloat()) * t
	}
	
	private fun getRampedHeight(p: Point2f, closestLine: Point2f.ClosestLine2f, baseValue: Float): Float {
		val widthSquared = width*width / 4f
		
		if (closestLine.squaredDistance >= widthSquared)
			return baseValue
		
		val startIndex = vertices.indexOf(closestLine.p1)
		val endIndex = vertices.indexOf(closestLine.p2)
		
		if (startIndex >= heights.size)
			return baseValue
		
		val startHeight = heights[startIndex]
		if (startIndex == heights.size - 1)
			return startHeight
		
		val distanceBetweenHeights = closestLine.p1.squaredDistanceTo(closestLine.p2)
		val squaredDistanceToPoint1 = p.squaredDistanceTo(closestLine.p1) - closestLine.squaredDistance
		val squaredDistanceToPoint2 = p.squaredDistanceTo(closestLine.p2) - closestLine.squaredDistance
		
		if (distanceBetweenHeights == 0f || squaredDistanceToPoint1 > distanceBetweenHeights || squaredDistanceToPoint2 > distanceBetweenHeights)
			return startHeight
		
		val height = heights[endIndex] - startHeight
		return startHeight + sqrt(squaredDistanceToPoint1 / distanceBetweenHeights) * height
	}
	
	override fun read(form: IffForm) {
		super.read(form)
		assert(form.tag == "AROA")
		assert(form.version >= 5)
		
		form.readForm("DATA").use { data ->
			readRoadSegments(data)
			
			data.readChunk("DATA").use { chunk ->
				extent = Rectangle2f(Float.MAX_VALUE, Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE)
				
				val positionCount = chunk.readInt()
				for (i in 0 until positionCount) {
					val x = chunk.readFloat()
					val z = chunk.readFloat()
					val p = Point2f(x, z)
					
					vertices.add(p)
					extent.expand(p)
				}
				
				if (form.version == 6) {
					val heightCount = chunk.readInt()
					for (i in 0 until heightCount) {
						heights.add(chunk.readFloat())
					}
				}
				
				width = chunk.readFloat()
				fractalId = chunk.readInt()
				featheringType = chunk.readInt()
				featheringAmount = min(1.0f, max(0.0f, chunk.readFloat()))
				chunk.readInt()
				chunk.readFloat()
				if (form.version == 6)
					hasFixedHeights = chunk.readInt() != 0
			}
		}
	}
	
	override fun write(): IffForm {
		val data = IffChunk("DATA")
		data.writeInt(vertices.size)
		for (vertex in vertices) {
			data.writeFloat(vertex.x)
			data.writeFloat(vertex.z)
		}
		
		data.writeInt(heights.size)
		for (height in heights) {
			data.writeFloat(height)
		}
		
		data.writeFloat(width)
		data.writeInt(fractalId)
		data.writeInt(featheringType)
		data.writeFloat(featheringAmount)
		data.writeInt(0)
		data.writeFloat(0f)
		data.writeInt(if (hasFixedHeights) 1 else 0)
		
		return IffForm.of("AROA", 6, writeHeaderChunk(), data)
	}
	
	private fun readRoadSegments(form: IffForm) {
		(form.readForm("ROAD") ?: form.readForm("HDTA")).use { road ->
			road.readAllChunks("SGMT") { chunk ->
				chunk.use {
					val pointCount = chunk.remaining() / 12
					val segment = ArrayList<Point3D>()
					for (i in 0 until pointCount) {
						segment.add(Point3D(chunk.readFloat().toDouble(), chunk.readFloat().toDouble(), chunk.readFloat().toDouble()))
					}
					segments.add(segment)
				}
			}
		}
	}
	
	override fun toString(): String {
		return "AffectorRoad[points=${vertices.size} start=${vertices[0]} end=${vertices[vertices.size-1]} width=$width fractal=$fractalId  feathering=$featheringType/$featheringAmount fixedHeights=$hasFixedHeights extent=$extent]"
	}
	
}