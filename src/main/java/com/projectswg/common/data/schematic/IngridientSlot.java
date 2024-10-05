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
package com.projectswg.common.data.schematic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.projectswg.common.data.EnumLookup;
import com.projectswg.common.data.encodables.oob.StringId;
import com.projectswg.common.encoding.Encodable;
import com.projectswg.common.network.NetBuffer;

public class IngridientSlot implements Encodable{
	
	private final List<DraftSlotDataOption> slotOptions;
	
	private StringId name;
	private boolean optional;

	public IngridientSlot(StringId name, boolean optional) {
		this.name = name;
		this.optional = optional;
		this.slotOptions = new ArrayList<>();
	}
	
	public IngridientSlot(){
		this.name = StringId.Companion.getEMPTY();
		this.optional = false;
		this.slotOptions = new ArrayList<>();
	}
	
	public StringId getName() {
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
	
	@Override
	public void decode(NetBuffer data) {
		name = data.getEncodable(StringId.class);
		optional = data.getBoolean();
		slotOptions.clear();
		slotOptions.addAll(data.getList(DraftSlotDataOption.class));
	}
	
	@Override
	public byte[] encode() {
		NetBuffer data = NetBuffer.allocate(getLength());
		data.addEncodable(name);
		data.addBoolean(optional);
		data.addList(slotOptions);
		return data.array();
	}
	
	@Override
	public int getLength() {
		int length = 0;
		for (DraftSlotDataOption draftSlotDataOption : slotOptions) {
			length += draftSlotDataOption.getLength();
		}
		return 5 + name.getLength() + length;
	}

	public enum IngridientType {
		UNDEFINED					(Integer.MIN_VALUE, SlotType.RESOURCES),
		IT_NONE						(0, SlotType.RESOURCES),
		IT_ITEM						(1, SlotType.SIMILAR_COMPONENTS),
		IT_TEMPLATE					(2, SlotType.IDENTICAL),
		IT_RESOURCE_TYPE			(3, SlotType.RESOURCES),
		IT_RESOURCE_CLASS			(4, SlotType.RESOURCES),
		IT_TEMPLATE_GENERIC			(5, SlotType.IDENTICAL),
		IT_SCHEMATIC				(6, SlotType.SIMILAR_COMPONENTS),
		IT_SCHEMATIC_GENERIC		(7, SlotType.SIMILAR_COMPONENTS);
		
		private static final EnumLookup<Integer, IngridientType> LOOKUP = new EnumLookup<>(IngridientType.class, i -> i.getId());
		
		private final int id;
		private final SlotType slotType;
		
		IngridientType(int id, SlotType slotType) {
			this.id = id;
			this.slotType = slotType;
		}	
		
		public int getId() {
			return id;
		}

		public SlotType getSlotType() {
			return slotType;
		}

		public static IngridientType getTypeForInt(int id) {
			return LOOKUP.getEnum(id, UNDEFINED);
		}
	}
}
