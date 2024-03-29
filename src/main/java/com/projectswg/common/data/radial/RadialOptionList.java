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
package com.projectswg.common.data.radial;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.joshlarson.jlcommon.log.Log;
import com.projectswg.common.encoding.Encodable;
import com.projectswg.common.network.NetBuffer;

public class RadialOptionList implements Encodable {
	
	private final List<RadialOption> options;
	
	public RadialOptionList() {
		options = new ArrayList<>();
	}
	
	public RadialOptionList(List<RadialOption> options) {
		this();
		this.options.addAll(options);
	}
	
	public void addOption(RadialOption option) {
		options.add(option);
	}
	
	public void setOptions(List<RadialOption> options) {
		this.options.clear();
		this.options.addAll(options);
	}
	
	public List<RadialOption> getOptions() {
		return Collections.unmodifiableList(options);
	}
	
	@Override
	public void decode(NetBuffer data) {
		int optionsCount = data.getInt();
		Map<Integer, RadialOption> optionMap = new HashMap<>();
		for (int i = 0; i < optionsCount; i++) {
			int opt = data.getByte(); // option number
			int parent = data.getByte(); // parentId
			int radialType = data.getByte(); // radialType
			byte flags = data.getByte(); // optionType
			String label = data.getUnicode(); // text
			RadialItem item = RadialItem.getFromId(radialType);
			if (item == null) {
				Log.e("No radial item found for: %04X");
				continue;
			}
			RadialOption option = RadialOption.createRaw(item, label, flags);
			optionMap.put(opt, option);
			if (parent == 0) {
				options.add(option);
			} else {
				RadialOption parentOpt = optionMap.get(parent);
				if (parentOpt == null) {
					Log.e("Parent not found! Parent=%d  Option=%s", parent, option);
				} else {
					List<RadialOption> children = new ArrayList<>(parentOpt.getChildren());
					children.add(option);
					optionMap.put(parent, RadialOption.createRaw(parentOpt, children));
				}
			}
		}
	}
	
	@Override
	public byte [] encode() {
		NetBuffer data = NetBuffer.allocate(4 + getOptionSize());
		data.addInt(getOptionCount());
		addOptions(data);
		return data.array();
	}
	
	@Override
	public int getLength() {
		return 4 + getOptionSize();
	}
	
	public int getSize() {
		return 4 + getOptionSize();
	}
	
	private int getOptionCount() {
		int count = 0;
		for (RadialOption option : options) {
			count += getOptionCount(option);
		}
		return count;
	}
	
	private int getOptionSize() {
		int size = 0;
		for (RadialOption option : options) {
			size += getOptionSize(option);
		}
		return size;
	}
	
	private void addOptions(NetBuffer data) {
		int index = 1;
		for (RadialOption option : options) {
			index = addOption(data, option, 0, index);
		}
	}
	
	private int getOptionCount(RadialOption parent) {
		int count = 1;
		for (RadialOption child : parent.getChildren()) {
			count += getOptionCount(child);
		}
		return count;
	}
	
	private int getOptionSize(RadialOption parent) {
		int size = 8 + parent.getLabel().length() * 2;
		for (RadialOption child : parent.getChildren()) {
			size += getOptionSize(child);
		}
		return size;
	}
	
	private int addOption(NetBuffer data, RadialOption parent, int parentIndex, int index) {
		int myIndex = index++;
		data.addByte(myIndex);
		data.addByte(parentIndex);
		data.addByte(parent.getType().getType());
		data.addByte(parent.getFlags());

		data.addUnicode(parent.getLabel());

		for (RadialOption option : parent.getChildren()) {
			index = addOption(data, option, myIndex, index);
		}
		return index;
	}
	
}
