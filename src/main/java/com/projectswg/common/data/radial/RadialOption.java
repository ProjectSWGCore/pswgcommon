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
import java.util.List;

public class RadialOption {
	
	private RadialItem item;
	private List<RadialOption> children;
	private String overriddenText;
	
	public RadialOption() {
		this.children = new ArrayList<>();
	}
	
	public RadialOption(RadialItem item) {
		this.item = item;
		this.children = new ArrayList<>();
	}
	
	public void setItem(RadialItem item) { this.item = item; }
	public void addChild(RadialOption option) { this.children.add(option); }
	public void addChild(RadialItem item) { addChild(new RadialOption(item)); }
	public void addChildWithOverriddenText(RadialItem item, String overriddenText) {
		RadialOption childOption = new RadialOption(item);
		childOption.setOverriddenText(overriddenText);
		addChild(childOption);
	}
	public void setOverriddenText(String overridenText) { this.overriddenText = overridenText; }
	
	public int getId() { return item.getId(); }
	public int getOptionType() { return item.getOptionType(); }
	public String getText() { return overriddenText != null ? overriddenText : item.getText(); }
	
	public List<RadialOption> getChildren() { return children; }
	
	@Override
	public String toString() { 
		return String.format("ID=%d Option=%d Text=%s", getId(), getOptionType(), getText()); 
	}
	
}
