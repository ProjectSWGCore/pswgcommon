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
package com.projectswg.common.data.swgfile.visitors;

import com.projectswg.common.data.swgfile.ClientData;
import com.projectswg.common.data.swgfile.IffNode;
import com.projectswg.common.data.swgfile.SWGFile;

import java.util.HashMap;
import java.util.Map;

public class SlotDefinitionData extends ClientData {
	
	private final Map<String, SlotDefinition> definitions;
	
	public SlotDefinitionData() {
		this.definitions = new HashMap<>();
	}
	
	@Override
	public void readIff(SWGFile iff) {
		IffNode data = iff.enterChunk("DATA");
		while (data.remaining() > 0) {
			SlotDefinition sd = new SlotDefinition(data.readString(), data.readBoolean(), data.readBoolean(), data.readBoolean(), data.readString(), data.readShort(), data.readBoolean(), data.readBoolean());
			
			definitions.put(sd.getName(), sd);
		}
	}
	
	public Map<String, SlotDefinition> getDefinitions() {
		return definitions;
	}
	
	public SlotDefinition getDefinition(String name) {
		return definitions.get(name);
	}
	
	public static class SlotDefinition {
		
		private final String name;
		private final boolean global;
		private final boolean modifiable;
		private final boolean hasHardpoint;
		private final String hardpointName;
		private final short combatBoneId;
		private final boolean observeWithParent;
		private final boolean exposeToWorld;
		
		public SlotDefinition(String name, boolean global, boolean modifiable, boolean hasHardpoint, String hardpointName, short combatBoneId, boolean observeWithParent, boolean exposeToWorld) {
			this.name = name;
			this.global = global;
			this.modifiable = modifiable;
			this.hasHardpoint = hasHardpoint;
			this.hardpointName = hardpointName;
			this.combatBoneId = combatBoneId;
			this.observeWithParent = observeWithParent;
			this.exposeToWorld = exposeToWorld;
		}
		
		public String getName() {
			return name;
		}
		
		public boolean isGlobal() {
			return global;
		}
		
		public boolean isModifiable() {
			return modifiable;
		}
		
		public boolean isHasHardpoint() {
			return hasHardpoint;
		}
		
		public String getHardpointName() {
			return hardpointName;
		}
		
		public short getCombatBoneId() {
			return combatBoneId;
		}
		
		public boolean isObserveWithParent() {
			return observeWithParent;
		}
		
		public boolean isExposeToWorld() {
			return exposeToWorld;
		}
		
	}
}
