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

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TestCustomizationString {
	@Test
	fun testPut() {
		val string = CustomizationString()
		val key = "test"
		Assertions.assertNull(string.put(key, 0)) // Nothing should be replaced because string's empty
		Assertions.assertEquals(0, string.put(key, 1)) // Same key, so the variable we put earlier should be replaced
	}

	@Test
	fun testRemove() {
		val string = CustomizationString()
		val key = "test"
		string.put(key, 0)
		Assertions.assertEquals(0, string.remove(key)) // Same key, so the variable we put earlier should be returned
	}

	@Test
	fun testIsEmpty() {
		val string = CustomizationString()
		val key = "test"
		Assertions.assertTrue(string.isEmpty)
		string.put(key, 0)
		Assertions.assertFalse(string.isEmpty)
		string.remove(key)
		Assertions.assertTrue(string.isEmpty)
	}

	@Test
	fun testGetLength() {
		val string = CustomizationString()
		Assertions.assertEquals(Short.SIZE_BYTES, string.length) // Should be an empty array at this point
		string.put("first", 7)
		var expected = Short.SIZE_BYTES + 7
		Assertions.assertEquals(expected, string.length)
		string.put("second", 0xFF)
		expected += 4 // Two escape characters, an ID and a value
		Assertions.assertEquals(expected, string.length)
	}
}
