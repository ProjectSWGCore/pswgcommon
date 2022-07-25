package com.projectswg.common.data.swgiff.parsers.terrain

import com.projectswg.common.data.location.Point3D
import com.projectswg.common.data.swgiff.parsers.SWGParser
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.io.File

class TestTerrainEngine {
	
	companion object {
		private fun createEngine(fileName: String): TerrainTemplate? {
			return SWGParser.parse("terrain/$fileName")
		}
	}
	
	@Nested
	inner class HeightCoordinateReading {
		private var engine: TerrainTemplate? = null
		@BeforeEach
		fun setup() {
			engine = createEngine("endor.trn")
		}
		
		@Test
		fun testCanReadHeightCoordinateAtEndorSmugglerOutpostBetweenHilltops() {
			val actual = engine?.getTerrainHeight(-935.0f, 1555.0f)?.height ?: return
			val expected = 76.00f
			assertEquals(expected, actual, 0.01f, "Unexpected height coordinate")
		}
		
		@Test
		fun testCanReadHeightAtEndorSmugglerOutpostSmallHilltop() {
			val actual = engine?.getTerrainHeight(-1010.0f, 1525.0f)?.height ?: return
			val expected = 74.69f
			assertEquals(expected, actual, 0.01f, "Unexpected height coordinate")
		}
	}
	
	@Nested
	inner class WaterDetection {
		
		private var engine: TerrainTemplate? = null
		
		@BeforeEach
		fun setup() {
			engine = createEngine("corellia.trn")
		}
		
		@Test
		fun testCanDetectWaterPositive() {
			val water = engine?.isWater(6700.0f, 2700.0f) ?: return
			Assertions.assertTrue(water, "The ocean on Corellia should be considered water")
		}
		
		@Test
		fun testCanDetectWaterNegative() {
			val water = engine?.isWater(-6600.0f, 2100.0f) ?: return
			Assertions.assertFalse(water, "The land on Corellia should not be considered water")
		}
	}
	
	@Nested
	inner class HandlesInterpolation {
		
		@Test
		fun `handles interpolation`() {
			/*
			 * 0.0 11.13 0.0
			 * 0.0 12.27 8.0
			 * 8.0 14.61 0.0
			 * 8.0 14.95 8.0
			 *
			 *      [  0      1      2      3     4     5     6     7     8  ]
			 * exp: [11.13, 10.67, 10.39, 10.00, 9.61, 9.20, 8.79, 8.36, 7.93]
			 * act: [11.13, 10.77, 10.41, 10.03, 9.64, 9.24, 8.84, 8.42, 7.98]
			 * dif: [ 0.00,  0.10,  0.02,  0.03, 0.03, 0.04, 0.05, 0.06, 0.05]
			 */
			
			val engine = createEngine("tatooine.trn") ?: return
			
			val testCases = listOf(
				listOf(0f, 195.13f, 16f),
				listOf(4f, 194.85f, 16f),
				listOf(0f, 196.56f, 20f),
				listOf(4f, 195.68f, 20f),
				listOf(4f, 194.85f, 16f),
				listOf(8f, 194.02f, 16f),
				listOf(4f, 195.68f, 20f),
				listOf(8f, 194.17f, 20f),
				listOf(1f, 195.97f, 19f),
				listOf(1f, 196.99f, 21f),
				listOf(3f, 197.74f, 23f),
				listOf(5f, 196.95f, 23f),
				listOf(7f, 195.01f, 21f),
				listOf(7f, 194.40f, 19f),
				listOf(5f, 194.74f, 17f),
				listOf(3f, 195.11f, 17f),
				
				// Type 1
				listOf(2f, 195.20f, 17f),
				listOf(2f, 195.77f, 19f),
				listOf(1f, 195.49f, 18f),
				listOf(3f, 195.22f, 18f),
				// Type 0
				listOf(6f, 194.59f, 17f),
				listOf(6f, 194.84f, 19f),
				listOf(5f, 194.85f, 18f),
				listOf(7f, 194.28f, 18f),
				// Type 0
				listOf(2f, 196.79f, 21f),
				listOf(2f, 198.22f, 23f),
				listOf(1f, 197.74f, 22f),
				listOf(3f, 197.07f, 22f),
				// Type 1
				listOf(6f, 195.46f, 21f),
				listOf(6f, 196.40f, 23f),
				listOf(5f, 196.29f, 22f),
				listOf(7f, 195.31f, 22f)
			)
			
			for (testCase in testCases) {
				 assertEquals(testCase[1], engine.getTerrainHeight(testCase[0], testCase[2]).height, 2e-2f, "$testCase")
			}
		}
		
	}
	
	@Nested
	inner class HandlesTerrainEdgeCases {
		
		private val testCases = mapOf(
			"corellia.trn" to listOf(
				Point3D(5000.0, 5.72, -6000.0),
				Point3D(194.39, 28.48, 110.22),
			),
			"dantooine.trn" to listOf(
				Point3D(4000.0, -1.09 + 0.99, 0.0),
				Point3D(4093.0, 31.81, 5181.32),
				Point3D(4230.0, 8.0, 5153.43),
				Point3D(3100.0, 6.84, 1350.0),
				Point3D(5941.03, 1.46, -5272.31),
				Point3D(589.89, 1.53, 722.74),
			),
			"endor.trn" to listOf(
				Point3D(0.0, 5.0, 0.0),
				Point3D(-943.0, 85.69, 1565.0),
				Point3D(-880.0, 93.96, 1532.0),
				Point3D(-1233.0, 126.23, 1723.0),
			),
			"tatooine.trn" to listOf(
				Point3D(-5939.0, 58.73, -6033.0),
				Point3D(5000.0, 52.20, 0.0),
				Point3D(-5829.30, 90.0, -6147.56),
				Point3D(-5819.30, 86.86, -6211.96),
			),
			"mustafar.trn" to listOf(
				Point3D(-2754.40, 104.00, 607.22), // AffectorRoad::getRampedHeight
				Point3D(-2580.63, 104.26, 524.60), // AffectorRoad::getRampedHeight
				Point3D(-2910.00, 104.00, 678.00), // AffectorRoad::getRampedHeight
				Point3D(-2876.56, 135.05, 663.97), // AffectorRoad::getRampedHeight
				Point3D(-2992.57, 170.47, 3004.39),
				Point3D(-3000.0, 160.35, 3000.0),
			),
			"taanab.trn" to listOf(
				Point3D(-2000.0, 56.22, -3000.0),
			),
			"talus.trn" to listOf(
				Point3D(890.76, 6.43, 1032.54),
				Point3D(890.57, 4.95, 1038.51),
				Point3D(-6000.0, 339.53, -6000.0),
				Point3D(-4672.0, 9.14, -4643.0),
				Point3D(-4673.91, 2.58, -4635.55),
				Point3D(-4768.73, -0.99 + 0.99, -4546.53),
			),
			"yavin4.trn" to listOf(
				Point3D(-973.59, 81.82, -1947.97),
				Point3D(-1000.0, 70.14, -2000.0),
				Point3D(3997.22, 15.17, -6541.48),
				Point3D(4033.69, 11.63, -6597.70)
			),
			"lok.trn" to listOf(
				Point3D(-16.51, 2.79, 15.81),
				Point3D(-47.54, 13.11, 23.01),
				Point3D(464.48, 6.66, 5466.0),
				Point3D(496.93, 19.33, 5466.90),
				Point3D(531.96, 9.00, 5476.03),
				Point3D(600.55, 28.99, 5254.67),
			),
			"naboo.trn" to listOf(
				Point3D(1000.0, 10.68, -5000.0),
				Point3D(1000.0, 2.01 + 0.99, -6000.0),
				Point3D(2080.67, 51.70, 2811.38),
				Point3D(2045.25, 36.01 + 0.99, 2801.44),
				Point3D(2036.03, 37.11, 2801.16),
				Point3D(2016.43, 26.93, 2783.19)
			),
			"kashyyyk.trn" to listOf(
				Point3D(-2000.0, 25.96, -3000.0),
				Point3D(20.54, 22.04, 2328.31),
			),
			"umbra.trn" to listOf(
				Point3D(0.0, 0.0, 0.0),
				Point3D(150.0, 25.0, 150.0),
			),
		)
		
		@ParameterizedTest(name = "{0}")
		@ValueSource(strings = [
			"corellia.trn",
			"dantooine.trn",
			"endor.trn",
			"tatooine.trn",
			"mustafar.trn",
			"taanab.trn",
			"talus.trn",
			"yavin4.trn",
			"lok.trn",
			"naboo.trn",
			"kashyyyk.trn",
			"umbra.trn"
		])
		fun testTerrain(fileName: String) {
			val engine = createEngine(fileName) ?: return
			for (testCase in testCases[fileName] ?: Assertions.fail("no test cases defined")) {
				val actualHeight = engine.getHeight(testCase.x.toFloat(), testCase.z.toFloat()).height
				assertEquals(testCase.y.toFloat(), actualHeight, 2e-1f, "$fileName  $testCase")
			}
		}
		
	}
	
}
