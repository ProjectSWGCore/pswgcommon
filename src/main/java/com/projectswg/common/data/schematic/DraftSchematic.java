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
package com.projectswg.common.data.schematic;

import java.util.ArrayList;
import java.util.List;

public class DraftSchematic {

	private final List<IngridientSlot> ingridientSlot; // needed for both

	private int itemsPerContainer; // not needed for Datapadschematic
	private String craftedSharedTemplate; // needed for Datapadschematic
	private int complexity; // needed for Datapadschematic
	private long combinedCrc; // needed for Datapadschematic
	private int volume; // needed for Datapadschematic
	private boolean canManufacture; // not needed for Datapadschematic

	public DraftSchematic() {
		this.ingridientSlot = new ArrayList<>();
		this.itemsPerContainer = 0;
		this.craftedSharedTemplate = "";
		this.complexity = 0;
		this.combinedCrc = 0;
		this.volume = 0;
		this.canManufacture = false;
	}

	public int getItemsPerContainer() {
		return itemsPerContainer;
	}

	public void setItemsPerContainer(int itemsPerContainer) {
		this.itemsPerContainer = itemsPerContainer;
	}

	public String getCraftedSharedTemplate() {
		return craftedSharedTemplate;
	}

	public void setCraftedSharedTemplate(String craftedSharedTemplate) {
		this.craftedSharedTemplate = craftedSharedTemplate;
	}

	public int getComplexity() {
		return complexity;
	}

	public void setComplexity(int complexity) {
		this.complexity = complexity;
	}

	public long getCombinedCrc() {
		return combinedCrc;
	}

	public void setCombinedCrc(long combinedCrc) {
		this.combinedCrc = combinedCrc;
	}

	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}

	public boolean isCanManufacture() {
		return canManufacture;
	}

	public void setCanManufacture(boolean canManufacture) {
		this.canManufacture = canManufacture;
	}

	public List<IngridientSlot> getIngridientSlot() {
		return ingridientSlot;
	}

}
