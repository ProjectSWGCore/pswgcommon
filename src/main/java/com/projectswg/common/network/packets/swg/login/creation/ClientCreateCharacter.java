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
package com.projectswg.common.network.packets.swg.login.creation;

import com.projectswg.common.data.customization.CustomizationString;
import com.projectswg.common.network.NetBuffer;
import com.projectswg.common.network.packets.SWGPacket;
import com.projectswg.common.utilities.ByteUtilities;

import java.util.List;

public class ClientCreateCharacter extends SWGPacket {
	public static final int CRC = getCrc("ClientCreateCharacter");

	private CustomizationString charCustomization	= new CustomizationString();
	private String name					= "";
	private String race					= "";
	private String start				= "";
	private String hair					= "";
	private CustomizationString hairCustomization	= new CustomizationString();
	private String clothes				= "";
	private boolean jedi				= false;
	private float height				= 0;
	private String biography			= "";
	private boolean tutorial			= false;
	
	public ClientCreateCharacter() {
		
	}
	
	public void decode(NetBuffer data) {
		if (!super.checkDecode(data, CRC))
			return;
		charCustomization	= data.getEncodable(CustomizationString.class);
		name				= data.getUnicode();
		race				= data.getAscii();
		start				= data.getAscii();
		hair				= data.getAscii();
		hairCustomization	= data.getEncodable(CustomizationString.class);
		clothes				= data.getAscii();
		jedi				= data.getBoolean();
		height				= data.getFloat();
		biography			= data.getUnicode();
		tutorial			= data.getBoolean();
	}
	
	public NetBuffer encode() {
		int extraSize = charCustomization.getLength();
		extraSize += name.length()*2;
		extraSize += race.length() + start.length();
		extraSize += hair.length() + hairCustomization.getLength();
		extraSize += clothes.length();
		NetBuffer data = NetBuffer.allocate(36+extraSize);
		data.addShort(2);
		data.addInt(CRC);
		data.addEncodable(charCustomization);
		data.addUnicode(name);
		data.addAscii(race);
		data.addAscii(start);
		data.addAscii(hair);
		data.addEncodable(hairCustomization);
		data.addAscii(clothes);
		data.addBoolean(jedi);
		data.addFloat(height);
		data.addUnicode(biography);
		data.addBoolean(tutorial);
		return data;
	}
	
	public CustomizationString getCharCustomization() { return charCustomization; }
	public String getName() { return name; }
	public String getRace() { return race; }
	public String getStartLocation() { return start; }
	public String getHair() { return hair; }
	public CustomizationString getHairCustomization() { return hairCustomization; }
	public String getClothes() { return clothes; }
	public float getHeight() { return height; }
	public boolean isTutorial() { return tutorial; }
	
	public void setCharCustomization(CustomizationString data) { this.charCustomization = data; }
	public void setName(String name) { this.name = name; }
	public String getStart() { return start; }
	public void setStart(String start) { this.start = start; }
	
	public boolean isJedi() {
		return jedi;
	}
	
	public void setJedi(boolean jedi) {
		this.jedi = jedi;
	}
	
	public String getBiography() {
		return biography;
	}
	
	public void setBiography(String biography) {
		this.biography = biography;
	}
	
	public void setRace(String race) { this.race = race; }
	public void setHair(String hair) { this.hair = hair; }
	public void setHairCustomization(CustomizationString hairCustomization) { this.hairCustomization = hairCustomization; }
	public void setClothes(String clothes) { this.clothes = clothes; }
	public void setHeight(float height) { this.height = height; }
	public void setTutorial(boolean tutorial) { this.tutorial = tutorial; }
	
}
