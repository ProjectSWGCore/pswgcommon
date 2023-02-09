/***********************************************************************************
 * Copyright (c) 2018 /// Project SWG /// www.projectswg.com                       *
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
package com.projectswg.common.data.encodables.tangible;

import com.projectswg.common.data.CRC;
import com.projectswg.common.data.EnumLookup;
import com.projectswg.common.data.swgfile.ClientFactory;

public enum Race {
	HUMAN_MALE			("human_male", "Human"),
	HUMAN_FEMALE		("human_female", "Human"),
	TRANDOSHAN_MALE		("trandoshan_male", "Trandoshan"),
	TRANDOSHAN_FEMALE	("trandoshan_female", "Trandoshan"),
	TWILEK_MALE			("twilek_male", "Twi'lek"),
	TWILEK_FEMALE		("twilek_female", "Twi'lek"),
	BOTHAN_MALE			("bothan_male", "Bothan"),
	BOTHAN_FEMALE		("bothan_female", "Bothan"),
	ZABRAK_MALE			("zabrak_male", "Zabrak"),
	ZABRAK_FEMALE		("zabrak_female", "Zabrak"),
	RODIAN_MALE			("rodian_male", "Rodian"),
	RODIAN_FEMALE		("rodian_female", "Rodian"),
	MONCAL_MALE			("moncal_male", "MonCal"),
	MONCAL_FEMALE		("moncal_female", "MonCal"),
	WOOKIEE_MALE		("wookiee_male", "Wookiee"),
	WOOKIEE_FEMALE		("wookiee_female", "Wookiee"),
	SULLUSTAN_MALE		("sullustan_male", "Sullustan"),
	SULLUSTAN_FEMALE	("sullustan_female", "Sullustan"),
	ITHORIAN_MALE		("ithorian_male", "Ithorian"),
	ITHORIAN_FEMALE		("ithorian_female", "Ithorian");
	
	private static final EnumLookup<Integer, Race> CRC_TO_RACE = new EnumLookup<>(Race.class, Race::getCrc);
	private static final EnumLookup<String, Race> SPECIES_TO_RACE = new EnumLookup<>(Race.class, r -> r.species);
	private static final EnumLookup<String, Race> FILE_TO_RACE = new EnumLookup<>(Race.class, Race::getFilename);
	
	private final int crc;
	private final String species;
	private final String displayName;
	
	Race(String species, String displayName) {
		this.crc = CRC.getCrc("object/creature/player/"+species+".iff");
		this.species = species;
		this.displayName = displayName;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public int getCrc() { return crc; }
	public String getFilename() { return "object/creature/player/shared_"+species+".iff"; }
	public String getSpecies() { return species.substring(0, species.indexOf('_')); }
	
	public static Race getRace(int crc) {
		return CRC_TO_RACE.getEnum(crc, null);
	}
	
	public static Race getRace(String species) {
		return SPECIES_TO_RACE.getEnum(species, HUMAN_MALE);
	}
	
	public static Race getRaceByFile(String iffFile) {
		return FILE_TO_RACE.getEnum(ClientFactory.formatToSharedFile(iffFile), HUMAN_MALE);
	}
	
}
