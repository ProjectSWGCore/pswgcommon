/***********************************************************************************
 * Copyright (c) 2024 /// Project SWG /// www.projectswg.com                       *
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
package com.projectswg.common.data.customization

import com.projectswg.common.encoding.StringType
import com.projectswg.common.network.NetBuffer
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import java.util.stream.Stream

class TestCustomizationString {
	@Test
	fun testPut() {
		val string = CustomizationString()
		val key = "/private/index_color_pattern"
		assertNull(string.put(key, 0)) // Nothing should be replaced because string's empty
		assertEquals(0, string.put(key, 1)) // Same key, so the variable we put earlier should be replaced
	}

	@Test
	fun testRemove() {
		val string = CustomizationString()
		val key = "/private/index_color_pattern"
		string.put(key, 0)
		assertEquals(0, string.remove(key)) // Same key, so the variable we put earlier should be returned
	}

	@Test
	fun testIsEmpty() {
		val string = CustomizationString()
		val key = "/private/index_color_pattern"
		assertTrue(string.isEmpty)
		string.put(key, 0)
		assertFalse(string.isEmpty)
		string.remove(key)
		assertTrue(string.isEmpty)
	}

	@Test
	fun encodeDecode() {
		val string = CustomizationString()
		string.put("/private/index_color_1", 237)
		string.put("/private/index_color_2", 4)
		string.put("/private/index_color_tattoo", -100)

		val decoded = CustomizationString()
		decoded.decode(NetBuffer.wrap(string.encode()))
		assertEquals(3, string.variables.size)
		assertEquals(237, decoded.get("/private/index_color_1"))
		assertEquals(4, decoded.get("/private/index_color_2"))
		assertEquals(-100, decoded.get("/private/index_color_tattoo"))
	}
	
	@ParameterizedTest
	@MethodSource("provideRealWorldValues")
	fun testRealWorld(input: ByteArray) {
		val encodedFull = NetBuffer.allocate(input.size + 2)
		encodedFull.addArray(input)
		val decoded = CustomizationString()
		decoded.decode(NetBuffer.wrap(encodedFull.buffer.array()))
		
		assertArrayEquals(encodedFull.buffer.array(), decoded.encode())
	}
	
	companion object {
		@JvmStatic
		private fun provideRealWorldValues(): Stream<Arguments> {
			return Stream.of(
				Arguments.of(byteArrayOf(2, 35, 23, -62, -90, 24, -61, -65, 1, 28, 115, 27, -61, -65, 1, 5, -61, -65, 1, 26, -61, -65, 1, 25, -61, -67, 13, -61, -65, 1, 9, -62, -110, 18, 13, 19, -61, -65, 1, 32, -61, -65, 1, 16, 84, 33, -61, -116, 15, -61, -65, 1, 20, 10, 17, -61, -117, 14, -61, -65, 1, 3, -61, -76, 11, -61, -65, 1, 12, 11, 6, -61, -113, 8, -61, -65, 1, 21, -61, -65, 1, 22, -62, -128, 4, -61, -101, 7, -61, -65, 1, 10, -61, -83, 35, 7, 37, 3, 36, -61, -65, 1, 1, 21, 29, -61, -65, 1, 31, 7, 30, -61, -65, 1, -61, -65, 3,)),
				Arguments.of(byteArrayOf(2, 29, 23, -61, -66, 24, -61, -65, 1, 28, -61, -65, 1, 27, 127, 5, -62, -82, 26, -61, -65, 1, 25, -61, -68, 13, -61, -65, 1, 9, 34, 18, 13, 19, -61, -65, 1, 16, -61, -122, 15, -61, -65, 1, 20, 9, 17, -61, -65, 1, 14, 117, 3, -61, -65, 1, 11, -61, -65, 2, 12, -61, -65, 1, 6, -61, -119, 8, -61, -65, 1, 21, -61, -65, 1, 22, -62, -106, 4, -62, -90, 7, -61, -65, 1, 10, -62, -95, 59, 25, 1, 16, 38, 3, -61, -65, 3)),
			)
		}
	}
}
