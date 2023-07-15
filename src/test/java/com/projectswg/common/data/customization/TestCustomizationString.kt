/***********************************************************************************
 * Copyright (c) 2023 /// Project SWG /// www.projectswg.com                       *
 *                                                                                 *
 * ProjectSWG is the first NGE emulator for Star Wars Galaxies founded on          *
 * July 7th, 2011 after SOE announced the official shutdown of Star Wars Galaxies. *
 * Our goal is to create an emulator which will provide a server for players to    *
 * continue playing a game similar to the one they used to play. We are basing     *
 * it on the final publish of the game prior to end-game events.                   *
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

import com.projectswg.common.network.NetBuffer
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class TestCustomizationString {
	@Test
	fun testPut() {
		val string = CustomizationString()
		val key = "test"
		assertNull(string.put(key, 0)) // Nothing should be replaced because string's empty
		assertEquals(0, string.put(key, 1)) // Same key, so the variable we put earlier should be replaced
	}

	@Test
	fun testRemove() {
		val string = CustomizationString()
		val key = "test"
		string.put(key, 0)
		assertEquals(0, string.remove(key)) // Same key, so the variable we put earlier should be returned
	}

	@Test
	fun testIsEmpty() {
		val string = CustomizationString()
		val key = "test"
		assertTrue(string.isEmpty)
		string.put(key, 0)
		assertFalse(string.isEmpty)
		string.remove(key)
		assertTrue(string.isEmpty)
	}

	@Test
	fun encoding() {
		val string = CustomizationString()
		string.put("/private/index_color_1", 237)
		string.put("/private/index_color_2", 4)
		val expected = byteArrayOf(10, 0, 2, 2, (2 or 0x80).toByte(), 237.toByte(), 255.toByte(), 1, 1, 4, 255.toByte(), 3)
		
		val actual = string.encode()

		assertArrayEquals(expected, actual)
	}

	@Test
	fun decoding() {
		val string = CustomizationString()
		string.put("/private/index_color_1", 237)
		string.put("/private/index_color_2", 4)
		val input = byteArrayOf(10, 0, 2, 2, 2, -61, -83, 1, 4, -61, -65, 3)
		
		string.decode(NetBuffer.wrap(input))

		assertEquals(2, string.variables.size)
		assertEquals(string.get("/private/index_color_1"), 237)
		assertEquals(string.get("/private/index_color_2"), 4)
	}
}
