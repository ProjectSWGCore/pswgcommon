/***********************************************************************************
 * Copyright (c) 2018 /// Project SWG /// www.projectswg.com                       *
 * *
 * ProjectSWG is the first NGE emulator for Star Wars Galaxies founded on          *
 * July 7th, 2011 after SOE announced the official shutdown of Star Wars Galaxies. *
 * Our goal is to create an emulator which will provide a server for players to    *
 * continue playing a game similar to the one they used to play. We are basing     *
 * it on the final publish of the game prior to end-game events.                   *
 * *
 * This file is part of Holocore.                                                  *
 * *
 * --------------------------------------------------------------------------------*
 * *
 * Holocore is free software: you can redistribute it and/or modify                *
 * it under the terms of the GNU Affero General Public License as                  *
 * published by the Free Software Foundation, either version 3 of the              *
 * License, or (at your option) any later version.                                 *
 * *
 * Holocore is distributed in the hope that it will be useful,                     *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                  *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                   *
 * GNU Affero General Public License for more details.                             *
 * *
 * You should have received a copy of the GNU Affero General Public License        *
 * along with Holocore.  If not, see <http:></http:>//www.gnu.org/licenses/>.               *
 */
package com.projectswg.common.data.location

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TestLocation {
	@Test
	fun testHeadingConsistent() {
		val origin = Location.builder().setPosition(0.0, 0.0, 0.0).build()
		val pointNorth = Location.builder().setPosition(0.0, 0.0, 10.0).build()
		val pointEast = Location.builder().setPosition(10.0, 0.0, 0.0).build()
		val pointWest = Location.builder().setPosition(-10.0, 0.0, 0.0).build()
		val pointSouth = Location.builder().setPosition(0.0, 0.0, -10.0).build()
		Assertions.assertEquals(0.0, origin.getHeadingTo(pointNorth), 1E-7)
		Assertions.assertEquals(0.0, pointSouth.getHeadingTo(pointNorth), 1E-7)
		Assertions.assertEquals(270.0, origin.getHeadingTo(pointEast), 1E-7)
		Assertions.assertEquals(180.0, origin.getHeadingTo(pointSouth), 1E-7)
		Assertions.assertEquals(90.0, origin.getHeadingTo(pointWest), 1E-7)
		Assertions.assertEquals(0.0, Location.builder(origin).setHeading(0.0).build().yaw, 1E-7)
		Assertions.assertEquals(90.0, Location.builder(origin).setHeading(90.0).build().yaw, 1E-7)
		Assertions.assertEquals(180.0, Location.builder(origin).setHeading(180.0).build().yaw, 1E-7)
		Assertions.assertEquals(270.0, Location.builder(origin).setHeading(270.0).build().yaw, 1E-7)
	}
}
