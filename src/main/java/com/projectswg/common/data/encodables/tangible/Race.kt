/***********************************************************************************
 * Copyright (c) 2024 /// Project SWG /// www.projectswg.com                       *
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
package com.projectswg.common.data.encodables.tangible

import com.projectswg.common.data.CRC
import com.projectswg.common.data.EnumLookup
import com.projectswg.common.data.swgfile.ClientFactory

enum class Race(private val _species: String, val displayName: String) {
	HUMAN_MALE("human_male", "Human"),
	HUMAN_FEMALE("human_female", "Human"),
	TRANDOSHAN_MALE("trandoshan_male", "Trandoshan"),
	TRANDOSHAN_FEMALE("trandoshan_female", "Trandoshan"),
	TWILEK_MALE("twilek_male", "Twi'lek"),
	TWILEK_FEMALE("twilek_female", "Twi'lek"),
	BOTHAN_MALE("bothan_male", "Bothan"),
	BOTHAN_FEMALE("bothan_female", "Bothan"),
	ZABRAK_MALE("zabrak_male", "Zabrak"),
	ZABRAK_FEMALE("zabrak_female", "Zabrak"),
	RODIAN_MALE("rodian_male", "Rodian"),
	RODIAN_FEMALE("rodian_female", "Rodian"),
	MONCAL_MALE("moncal_male", "MonCal"),
	MONCAL_FEMALE("moncal_female", "MonCal"),
	WOOKIEE_MALE("wookiee_male", "Wookiee"),
	WOOKIEE_FEMALE("wookiee_female", "Wookiee"),
	SULLUSTAN_MALE("sullustan_male", "Sullustan"),
	SULLUSTAN_FEMALE("sullustan_female", "Sullustan"),
	ITHORIAN_MALE("ithorian_male", "Ithorian"),
	ITHORIAN_FEMALE("ithorian_female", "Ithorian");

	val crc: Int = CRC.getCrc("object/creature/player/$_species.iff")

	val filename: String
		get() = "object/creature/player/shared_$_species.iff"

	val species: String = _species.substring(0, _species.indexOf('_'))

	companion object {
		private val CRC_TO_RACE = EnumLookup(Race::class.java) { obj: Race -> obj.crc }
		private val SPECIES_TO_RACE = EnumLookup(Race::class.java) { r: Race -> r._species }
		private val FILE_TO_RACE = EnumLookup(Race::class.java) { obj: Race -> obj.filename }

		fun getRace(crc: Int): Race? {
			return CRC_TO_RACE.getEnum(crc, null)
		}

		fun getRace(species: String): Race {
			return SPECIES_TO_RACE.getEnum(species, HUMAN_MALE)
		}

		fun getRaceByFile(iffFile: String?): Race {
			return FILE_TO_RACE.getEnum(ClientFactory.formatToSharedFile(iffFile), HUMAN_MALE)
		}
	}
}
