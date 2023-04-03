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
package com.projectswg.common.data.persistable

import com.projectswg.common.network.NetBufferStream
import com.projectswg.common.persistable.InputPersistenceStream
import com.projectswg.common.persistable.OutputPersistenceStream
import com.projectswg.common.persistable.Persistable
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.*

class TestSimplePersistable {
	@Test
	@Throws(IOException::class)
	fun testInputPersistenceStream() {
		InputPersistenceStream(ByteArrayInputStream(createBuffer())).use { `is` ->
			val po = `is`.read { stream: NetBufferStream -> PersistableObject.create(stream) }
			Assertions.assertEquals(13, po.someInt)
		}
	}

	@Test
	@Throws(IOException::class)
	fun testOutputPersistenceStream() {
		val baos = ByteArrayOutputStream(8)
		val po = PersistableObject(13)
		OutputPersistenceStream(baos).use { os ->
			os.write(po)
			Assertions.assertArrayEquals(createBuffer(), baos.toByteArray())
		}
	}

	@Test
	@Throws(IOException::class)
	fun testPersistenceStreams() {
		val tmp = File.createTempFile("PersistableTest", "odb")
		val original = PersistableObject(17)
		OutputPersistenceStream(FileOutputStream(tmp)).use { os ->
			os.write(original)
		}
		InputPersistenceStream(FileInputStream(tmp)).use { `is` ->
			val recreated = `is`.read { stream: NetBufferStream -> PersistableObject.create(stream) }
			Assertions.assertEquals(original.someInt, recreated.someInt)
		}
	}

	@Test
	@Throws(IOException::class)
	fun testPersistenceStreamMultiple() {
		val tmp = File.createTempFile("PersistableTest", "odb")
		val original = PersistableObject(17)
		OutputPersistenceStream(FileOutputStream(tmp)).use { os ->
			os.write(original)
			os.write(original)
			os.write(original)
		}
		InputPersistenceStream(FileInputStream(tmp)).use { persistenceStream ->
			Assertions.assertEquals(
				original.someInt,
				persistenceStream.read { stream: NetBufferStream -> PersistableObject.create(stream) }.someInt)
			Assertions.assertEquals(
				original.someInt,
				persistenceStream.read { stream: NetBufferStream -> PersistableObject.create(stream) }.someInt)
			Assertions.assertEquals(
				original.someInt,
				persistenceStream.read { stream: NetBufferStream -> PersistableObject.create(stream) }.someInt)
		}
	}

	private fun createBuffer(): ByteArray {
		val stream = NetBufferStream(8)
		stream.addInt(4)
		stream.addInt(13)
		val array = stream.array()
		stream.close()
		return array
	}

	private class PersistableObject(val someInt: Int) : Persistable {

		override fun save(stream: NetBufferStream) {
			stream.addInt(someInt)
		}

		override fun read(stream: NetBufferStream) {}

		companion object {
			fun create(stream: NetBufferStream): PersistableObject {
				return PersistableObject(stream.int)
			}
		}
	}
}
