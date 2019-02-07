/***********************************************************************************
 * Copyright (c) 2015 /// Project SWG /// www.projectswg.com                        *
 *                                                                                  *
 * ProjectSWG is the first NGE emulator for Star Wars Galaxies founded on           *
 * July 7th, 2011 after SOE announced the official shutdown of Star Wars Galaxies.  *
 * Our goal is to create an emulator which will provide a server for players to     *
 * continue playing a game similar to the one they used to play. We are basing      *
 * it on the final publish of the game prior to end-game events.                    *
 *                                                                                  *
 * This file is part of Holocore.                                                   *
 *                                                                                  *
 * -------------------------------------------------------------------------------- *
 *                                                                                  *
 * Holocore is free software: you can redistribute it and/or modify                 *
 * it under the terms of the GNU Affero General Public License as                   *
 * published by the Free Software Foundation, either version 3 of the               *
 * License, or (at your option) any later version.                                  *
 *                                                                                  *
 * Holocore is distributed in the hope that it will be useful,                      *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                   *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                    *
 * GNU Affero General Public License for more details.                              *
 *                                                                                  *
 * You should have received a copy of the GNU Affero General Public License         *
 * along with Holocore.  If not, see <http://www.gnu.org/licenses/>.                *
 *                                                                                  *W
 ***********************************************************************************/
package com.projectswg.common.data.customization;

import org.junit.Assert;
import org.junit.Test;

public class TestCustomizationString {
	
	@Test
	public void testPut() {
		CustomizationString string = new CustomizationString();
		String key = "test";
		
		Assert.assertNull(string.put(key, 0));	// Nothing should be replaced because string's empty
		Assert.assertEquals((Integer) 0, string.put(key, 1));	// Same key, so the variable we put earlier should be replaced
	}
	
	@Test
	public void testRemove() {
		CustomizationString string = new CustomizationString();
		String key = "test";
		
		string.put(key, 0);
		
		Assert.assertEquals((Integer) 0, string.remove(key));	// Same key, so the variable we put earlier should be returned
	}
	
	@Test
	public void testIsEmpty() {
		CustomizationString string = new CustomizationString();
		String key = "test";
		
		Assert.assertTrue(string.isEmpty());
		string.put(key, 0);
		Assert.assertFalse(string.isEmpty());
		string.remove(key);
		Assert.assertTrue(string.isEmpty());
	}
	
	@Test
	public void testGetLength() {
		CustomizationString string = new CustomizationString();
		Assert.assertEquals(Short.BYTES, string.getLength());	// Should be an empty array at this point
		
		string.put("first", 7);
		int expected = Short.BYTES + 7;
		Assert.assertEquals(expected, string.getLength());
		
		string.put("second", 0xFF);
		expected += 4;	// Two escape characters, an ID and a value
		Assert.assertEquals(expected, string.getLength());
	}
	
}
