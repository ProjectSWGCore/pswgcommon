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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.projectswg.common.data.EnumLookup;
import com.projectswg.common.encoding.Encodable;
import com.projectswg.common.network.NetBuffer;

public class IngridientSlot implements Encodable{
	
	private final List<DraftSlotDataOption> slotOptions;
	
	private String name;
	private boolean optional;
	private String hardPoint;
	
	public IngridientSlot(String name, boolean optional) {
		this.name = name;
		this.optional = optional;
		this.slotOptions = new ArrayList<>();
		this.hardPoint = "";
	}
	
	public IngridientSlot(){
		this.name = "";
		this.optional = false;
		this.slotOptions = new ArrayList<>();
		this.hardPoint = "";
	}
	
	public String getName() {
		return name;
	}

	public boolean isOptional() {
		return optional;
	}

	public void addSlotDataOption(DraftSlotDataOption object){
		synchronized (slotOptions) {
			slotOptions.add(object);	
		}	
	}

	public List<DraftSlotDataOption> getFromSlotDataOption(){
		return Collections.unmodifiableList(slotOptions);
	}
	
	public String getHardPoint() {
		return hardPoint;
	}

	@Override
	public void decode(NetBuffer data) {
		name = data.getAscii();
		optional = data.getBoolean();
		slotOptions.clear();
		slotOptions.addAll(data.getList(DraftSlotDataOption.class));
		hardPoint = data.getAscii();	
	}
	
	@Override
	public byte[] encode() {
		NetBuffer data = NetBuffer.allocate(getLength());
		data.addAscii(name);
		data.addBoolean(optional);
		data.addList(slotOptions);
		data.addAscii(hardPoint);
		return data.array();
	}
	
	@Override
	public int getLength() {
		int length = 0;
		for (DraftSlotDataOption draftSlotDataOption : slotOptions) {
			length += draftSlotDataOption.getLength();
		}
		return 9 + name.length() + hardPoint.length() + length;
	}

	public enum IngridientType {
		UNDEFINED					(Integer.MIN_VALUE),
		IT_NONE						(0),
		IT_ITEM						(1),
		IT_TEMPLATE					(2),
		IT_RESOURCE_TYPE			(3),
		IT_RESOURCE_CLASS			(4),
		IT_TEMPLATE_GENERIC			(5),
		IT_SCHEMATIC				(6),
		IT_SCHEMATIC_GENERIC		(7);
		
		private static final EnumLookup<Integer, IngridientType> LOOKUP = new EnumLookup<>(IngridientType.class, i -> i.getId());
		
		private int id;
		
		IngridientType(int id) {
			this.id = id;
		}	
		
		public int getId() {
			return id;
		}
		
		public static IngridientType getTypeForInt(int id) {
			return LOOKUP.getEnum(id, UNDEFINED);
		}
	}
}
