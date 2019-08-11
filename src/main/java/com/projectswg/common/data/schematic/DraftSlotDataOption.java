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
package com.projectswg.common.data.schematic;

import com.projectswg.common.data.schematic.IngridientSlot.IngridientType;
import com.projectswg.common.encoding.Encodable;
import com.projectswg.common.network.NetBuffer;

public class DraftSlotDataOption implements Encodable {
	
	private String stfName;
	private String ingredientName;
	private IngridientType ingredientType;
	private int amount;
	
	public DraftSlotDataOption(String stfName, String ingredientName, IngridientType ingredientType, int amount) {
		this.stfName = stfName;
		this.ingredientName = ingredientName;
		this.ingredientType = ingredientType;
		this.amount = amount;
	}

	public DraftSlotDataOption(){
		this.stfName = "";
		this.ingredientName = "";
		this.ingredientType = IngridientType.IT_NONE;
		this.amount = 0;
	}
	
	public String getStfName() {
		return stfName;
	}
	
	public String getIngredientName() {
		return ingredientName;
	}
	
	public IngridientType getIngredientType() {
		return ingredientType;
	}
	
	public int getAmount() {
		return amount;
	}
	
	@Override
	public void decode(NetBuffer data) {
		stfName = data.getAscii();
		ingredientName = data.getUnicode();
		ingredientType =  IngridientType.getTypeForInt(data.getInt());
		amount = data.getInt();		
	}
	
	@Override
	public byte[] encode() {
		NetBuffer data = NetBuffer.allocate(getLength());
		data.addAscii(stfName);
		data.addUnicode(ingredientName);
		data.addInt(ingredientType.getId());
		data.addInt(amount);
		return data.array();
	}
	
	@Override
	public int getLength() {
		return 14 + stfName.length() + ingredientName.length() * 2;
	}	
}
