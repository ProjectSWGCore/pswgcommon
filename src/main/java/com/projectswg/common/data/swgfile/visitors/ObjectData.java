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

import com.projectswg.common.data.EnumLookup;
import com.projectswg.common.data.encodables.oob.StringId;
import com.projectswg.common.data.swgfile.ClientData;
import com.projectswg.common.data.swgfile.ClientFactory;
import com.projectswg.common.data.swgfile.IffNode;
import com.projectswg.common.data.swgfile.SWGFile;
import com.projectswg.common.network.packets.swg.zone.baselines.Baseline.BaselineType;
import me.joshlarson.jlcommon.log.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ObjectData extends ClientData {

	private final Map<ObjectDataAttribute, Object> attributes = new HashMap<>();
	private final List<String> parsedFiles = new ArrayList<>();
	
	public enum ObjectDataAttribute {
		ACCELERATION							("acceleration"),
		ANIMATION_MAP_FILENAME					("animationMapFilename"),
		APPEARANCE_FILENAME						("appearanceFilename"),
		ARRANGEMENT_DESCRIPTOR_FILENAME			("arrangementDescriptorFilename"),
		ATTACK_TYPE								("attackType"),
		ATTRIBUTES								("attributes"),
		CAMERA_HEIGHT							("cameraHeight"),
		CERTIFICATIONS_REQUIRED					("certificationsRequired"),
		CLEAR_FLORA_RADIUS						("clearFloraRadius"),
		CLIENT_DATA_FILE						("clientDataFile"),
		CLIENT_VISIBILITY_FLAG					("clientVisabilityFlag"),
		COCKPIT_FILENAME						("cockpitFilename"),
		COLLISION_ACTION_BLOCK_FLAGS			("collisionActionBlockFlags"),
		COLLISION_ACTION_FLAGS					("collisionActionFlags"),
		COLLISION_ACTION_PASS_FLAGS				("collisionActionPassFlags"),
		COLLISION_HEIGHT						("collisionHeight"),
		COLLISION_LENGTH						("collisionLength"),
		COLLISION_MATERIAL_BLOCK_FLAGS			("collisionMaterialBlockFlags"),
		COLLISION_MATERIAL_FLAGS				("collisionMaterialFlags"),
		COLLISION_MATERIAL_PASS_FLAGS			("collisionMaterialPassFlags"),
		COLLISION_OFFSET_X						("collisionOffsetX"),
		COLLISION_OFFSET_Z						("collisionOffsetZ"),
		COLLISION_RADIUS						("collisionRadius"),
		CONST_STRING_CUSTOMIZATION_VARIABLES	("constStringCustomizationVariables"),
		CONTAINER_TYPE							("containerType"),
		CONTAINER_VOLUME_LIMIT					("containerVolumeLimit"),
		CRAFTED_SHARED_TEMPLATE					("craftedSharedTemplate"),
		CUSTOMIZATION_VARIABLE_MAPPING			("customizationVariableMapping"),
		DETAILED_DESCRIPTION					("detailedDescription"),
		FORCE_NO_COLLISION						("forceNoCollision"),
		GAME_OBJECT_TYPE						("gameObjectType"),
		GENDER									("gender"),
		HAS_WINGS								("hasWings"),
		INTERIOR_LAYOUT_FILENAME				("interiorLayoutFileName"),
		LOCATION_RESERVATION_RADIUS				("locationReservationRadius"),
		LOOK_AT_TEXT							("lookAtText"),
		MOVEMENT_DATATABLE						("movementDatatable"),
		NICHE									("niche"),
		NO_BUILD_RADIUS							("noBuildRadius"),
		OBJECT_NAME								("objectName"),
		ONLY_VISIBLE_IN_TOOLS					("onlyVisibleInTools"),
		PALETTE_COLOR_CUSTOMIZATION_VARIABLES	("paletteColorCustomizationVariables"),
		PLAYER_CONTROLLED						("playerControlled"),
		PORTAL_LAYOUT_FILENAME					("portalLayoutFilename"),
		POSTURE_ALIGN_TO_TERRAIN				("postureAlignToTerrain"),
		RACE									("race"),
		RANGED_INT_CUSTOMIZATION_VARIABLES		("rangedIntCustomizationVariables"),
		SCALE									("scale"),
		SCALE_THRESHOLD_BEFORE_EXTENT_TEST		("scaleThresholdBeforeExtentTest"),
		SEND_TO_CLIENT							("sendToClient"),
		SLOPE_MOD_ANGLE							("slopeModAngle"),
		SLOPE_MOD_PERCENT						("slopeModPercent"),
		SLOTS									("slots"),
		SLOT_DESCRIPTOR_FILENAME				("slotDescriptorFilename"),
		SNAP_TO_TERRAIN							("snapToTerrain"),
		SOCKET_DESTINATIONS						("socketDestinations"),
		SPECIES									("species"),
		SPEED									("speed"),
		STEP_HEIGHT								("stepHeight"),
		STRUCTURE_FOOTPRINT_FILENAME			("structureFootprintFileName"),
		SURFACE_TYPE							("surfaceType"),
		SWIM_HEIGHT								("swimHeight"),
		TARGETABLE								("targetable"),
		TERRAIN_MODIFICATION_FILENAME			("terrainModificationFileName"),
		TINT_PALETTE							("tintPalette"),
		TURN_RADIUS								("turnRate"),
		USE_STRUCTURE_FOOTPRINT_OUTLINE			("useStructureFootprintOutline"),
		WARP_TOLERANCE							("warpTolerance"),
		WATER_MOD_PERCENT						("waterModPercent"),
		WEAPON_EFFECT							("weaponEffect"),
		WEAPON_EFFECT_INDEX						("weaponEffectIndex"),
		
		// Holocore
		HOLOCORE_BASELINE_TYPE					("holocoreBaselineType"),
		
		UNKNOWN									("");
		
		private static final EnumLookup<String, ObjectDataAttribute> LOOKUP = new EnumLookup<>(ObjectDataAttribute.class, ObjectDataAttribute::getName);
		
		private final String name;
		
		ObjectDataAttribute(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
		public static ObjectDataAttribute getForName(String name) {
			return LOOKUP.getEnum(name, UNKNOWN);
		}
	}
	
	public ObjectData() {}

	@Override
	public void readIff(SWGFile iff) {
		readNextForm(iff);
		if (!attributes.containsKey(ObjectDataAttribute.HOLOCORE_BASELINE_TYPE))
			System.err.println("Unknown baseline type: " + iff.getFileName());
	}
	
	private void readNextForm(SWGFile iff) {
		BaselineType type;
		switch (iff.getCurrentForm().getTag()) {
			case "SBMK": type = BaselineType.BMRK; break; // Battlefield Marker
			case "SBOT": type = BaselineType.BUIO; break; // Building
			case "CCLT": type = BaselineType.SCLT; break; // Cell
			case "SCNC": type = BaselineType.CONC; break; // Construction Contraction
			case "SCOU": type = null; break; // Counting
			case "SCOT": type = BaselineType.CREO; break; // Creature / Mobile
			case "SDSC": type = BaselineType.DSCO; break; // Draft Schematic
			case "SFOT": type = BaselineType.FCYT; break; // Factory
			case "SGRP": type = BaselineType.GRUP; break; // Group
			case "SGLD": type = BaselineType.GILD; break; // Guild
			case "SIOT": type = BaselineType.INSO; break; // Installation
			case "SITN": type = BaselineType.ITNO; break; // Intangible
			case "SJED": type = BaselineType.JEDI; break; // Jedi Manager
			case "SMSC": type = BaselineType.MSCO; break; // Manufacture Schematic
			case "SMSO": type = BaselineType.MISO; break; // Mission
			case "SHOT": type = null; break; // Object
			case "STOT": type = BaselineType.TANO; break; // Tangible / Path Waypoint
			case "SPLY": type = BaselineType.PLAY; break; // Player
			case "SPQO": type = BaselineType.PQOS; break; // Player Quest
			case "RCCT": type = BaselineType.RCNO; break; // Resource Container
			case "SSHP": type = BaselineType.SHIP; break; // Ship
			case "STAT": type = BaselineType.STAO; break; // Sound Object / Static
			case "STOK": type = BaselineType.TOKN; break; // Token
			case "SUNI": type = null; break; // Universe
			case "SWAY": type = BaselineType.WAYP; break; // Waypoint
			case "SWOT": type = BaselineType.WEAO; break; // Weapon
			default:
				System.err.println("Unknown type: " + iff.getCurrentForm().getTag() + " for " + iff.getFileName());
				type = null; break;
		}
		if (type != null)
			attributes.putIfAbsent(ObjectDataAttribute.HOLOCORE_BASELINE_TYPE, type.name());
		
		IffNode next;
		while ((next = iff.enterNextForm()) != null) {
			String tag = next.getTag();
			if (tag.equals("DERV"))
				readExtendedAttributes(iff);
			else if (tag.contains("0"))
				readVersionForm(iff);
			else if (!tag.isEmpty())
				readNextForm(iff);
			iff.exitForm();
		}
	}
	
	private void readVersionForm(SWGFile iff) {
		IffNode attributeChunk;
		while ((attributeChunk = iff.enterChunk("XXXX")) != null) {
			parseAttributeChunk(iff, attributeChunk);
		}
	}

	private void readExtendedAttributes(SWGFile iff) {
		IffNode chunk = iff.enterNextChunk();
		String file = chunk.readString();

		if (parsedFiles.contains(file)) // some repeated and we do not want to replace any attributes unless they're overriden by a more specific obj
			return;

		ClientData attrData = ClientFactory.getInfoFromFile(file);
		if (!(attrData instanceof ObjectData)) {
			Log.w("Could not load attribute data from file " + file + "!");
			return; // break out of whole method as we should only continue if we have all the extended attributes
		}

		// Put all the extended attributes in this map so it's accessible. Note that some of these are overridden.
		Object prevBaselineType = attributes.get(ObjectDataAttribute.HOLOCORE_BASELINE_TYPE);
		attributes.putAll(((ObjectData)attrData).getAttributes());
		if (prevBaselineType != null)
			attributes.put(ObjectDataAttribute.HOLOCORE_BASELINE_TYPE, prevBaselineType);

		parsedFiles.add(file);
	}

	// Try and parse the attribute to map w/ appropriate Object type.
	private void parseAttributeChunk(SWGFile iff, IffNode chunk) {
		String str = chunk.readString();
		if (str.isEmpty())
			return;
		ObjectDataAttribute attr = ObjectDataAttribute.getForName(str);
		if (attr == ObjectDataAttribute.UNKNOWN) {
			Log.e("Unknown ObjectData attribute: %s", str);
			return;
		}
		parseObjectAttribute(iff, chunk, attr);
	}
	
	private void parseObjectAttribute(SWGFile iff, IffNode chunk, ObjectDataAttribute attr) {
		switch (attr) {
			case ACCELERATION:						putFloat(chunk, attr); break;
			case ANIMATION_MAP_FILENAME:			putString(chunk, attr); break;
			case APPEARANCE_FILENAME:				putString(chunk, attr); break;
			case ARRANGEMENT_DESCRIPTOR_FILENAME:	putString(chunk, attr); break;
			case ATTACK_TYPE:						putInt(chunk, attr); break;
			case ATTRIBUTES:						putStructList(iff, chunk, attr, ObjectData::readAttributes); break;
			case CAMERA_HEIGHT:						putFloat(chunk, attr); break;
			case CERTIFICATIONS_REQUIRED:			putList(chunk, attr, ObjectData::readStringIfPresent); break;
			case CLEAR_FLORA_RADIUS:				putFloat(chunk, attr); break;
			case CLIENT_DATA_FILE:					putString(chunk, attr); break;
			case CLIENT_VISIBILITY_FLAG:			putBoolean(chunk, attr); break;
			case COCKPIT_FILENAME: 					putString(chunk, attr); break;
			case COLLISION_ACTION_BLOCK_FLAGS:		putInt(chunk, attr); break;
			case COLLISION_ACTION_FLAGS:			putInt(chunk, attr); break;
			case COLLISION_ACTION_PASS_FLAGS:		putInt(chunk, attr); break;
			case COLLISION_HEIGHT:					putFloat(chunk, attr); break;
			case COLLISION_LENGTH:					putFloat(chunk, attr); break;
			case COLLISION_MATERIAL_BLOCK_FLAGS:	putInt(chunk, attr); break;
			case COLLISION_MATERIAL_FLAGS:			putInt(chunk, attr); break;
			case COLLISION_MATERIAL_PASS_FLAGS:		putInt(chunk, attr); break;
			case COLLISION_OFFSET_X:				putFloat(chunk, attr); break;
			case COLLISION_OFFSET_Z:				putFloat(chunk, attr); break;
			case COLLISION_RADIUS:					putFloat(chunk, attr); break;
			case CONST_STRING_CUSTOMIZATION_VARIABLES:putStructList(iff, chunk, attr, ObjectData::readConstStringCustomizationVariables); break;
			case CONTAINER_TYPE:					putInt(chunk, attr); break;
			case CONTAINER_VOLUME_LIMIT:			putInt(chunk, attr); break;
			case CRAFTED_SHARED_TEMPLATE:			putString(chunk, attr); break;
			case CUSTOMIZATION_VARIABLE_MAPPING:	putStructList(iff, chunk, attr, ObjectData::readCustomizationVariableMapping); break;
			case DETAILED_DESCRIPTION:				putStfString(chunk, attr); break;
			case FORCE_NO_COLLISION:				putBoolean(chunk, attr); break;
			case GAME_OBJECT_TYPE:					putInt(chunk, attr); break;
			case GENDER:							putInt(chunk, attr); break;
			case HAS_WINGS: 						putBoolean(chunk, attr); break;
			case INTERIOR_LAYOUT_FILENAME:			putString(chunk, attr); break;
			case LOCATION_RESERVATION_RADIUS:		putFloat(chunk, attr); break;
			case LOOK_AT_TEXT:						putString(chunk, attr); break;
			case MOVEMENT_DATATABLE:				putString(chunk, attr); break;
			case NICHE:								putInt(chunk, attr); break;
			case NO_BUILD_RADIUS:					putFloat(chunk, attr); break;
			case OBJECT_NAME:						putStfString(chunk, attr); break;
			case ONLY_VISIBLE_IN_TOOLS:				putBoolean(chunk, attr); break;
			case PALETTE_COLOR_CUSTOMIZATION_VARIABLES:putStructList(iff, chunk, attr, ObjectData::readPaletteColorCustomizationVariable); break;
			case PLAYER_CONTROLLED:					putBoolean(chunk, attr); break;
			case PORTAL_LAYOUT_FILENAME:			putString(chunk, attr); break;
			case POSTURE_ALIGN_TO_TERRAIN:			putBoolean(chunk, attr); break;
			case RACE:								putInt(chunk, attr); break;
			case RANGED_INT_CUSTOMIZATION_VARIABLES:putStructList(iff, chunk, attr, ObjectData::readRangedIntCustomizationVariable); break;
			case SCALE:								putFloat(chunk, attr); break;
			case SCALE_THRESHOLD_BEFORE_EXTENT_TEST:putFloat(chunk, attr); break;
			case SEND_TO_CLIENT:					putBoolean(chunk, attr); break;
			case SLOPE_MOD_ANGLE:					putFloat(chunk, attr); break;
			case SLOPE_MOD_PERCENT:					putFloat(chunk, attr); break;
			case SLOTS:								putStructList(iff, chunk, attr, ObjectData::readSlots); break;
			case SLOT_DESCRIPTOR_FILENAME:			putString(chunk, attr); break;
			case SNAP_TO_TERRAIN:					putBoolean(chunk, attr); break;
			case SOCKET_DESTINATIONS:				putList(chunk, attr, ObjectData::readIntIfPresent); break;
			case SPECIES:							putInt(chunk, attr); break;
			case SPEED:								putFloat(chunk, attr); break;
			case STEP_HEIGHT:						putFloat(chunk, attr); break;
			case STRUCTURE_FOOTPRINT_FILENAME:		putString(chunk, attr); break;
			case SURFACE_TYPE:						putInt(chunk, attr); break;
			case SWIM_HEIGHT:						putFloat(chunk, attr); break;
			case TARGETABLE:						putBoolean(chunk, attr); break;
			case TERRAIN_MODIFICATION_FILENAME:		putString(chunk, attr); break;
			case TINT_PALETTE:						putString(chunk, attr); break;
			case TURN_RADIUS:						putFloat(chunk, attr); break;
			case USE_STRUCTURE_FOOTPRINT_OUTLINE:	putBoolean(chunk, attr); break;
			case WARP_TOLERANCE:					putFloat(chunk, attr); break;
			case WATER_MOD_PERCENT:					putFloat(chunk, attr); break;
			case WEAPON_EFFECT:						putString(chunk, attr); break;
			case WEAPON_EFFECT_INDEX:				putInt(chunk, attr); break;
			default: break;
		}
	}
	
	public Object getAttribute(ObjectDataAttribute attribute) {
		return attributes.get(attribute);
	}
	
	public Map<ObjectDataAttribute, Object> getAttributes() {
		return attributes;
	}
	
	private void putStructList(SWGFile iff, IffNode chunk, ObjectDataAttribute attr, Function<IffNode, ?> transform) {
		boolean append = chunk.readBoolean();
		int size = chunk.readInt();
		if (size <= 0)
			return;
		chunk.readByte();
		String chunkName = new StringBuilder(chunk.readString()).reverse().toString();
		@SuppressWarnings("unchecked")
		List<Object> data = append ? (List<Object>) attributes.getOrDefault(attr, new ArrayList<>()) : new ArrayList<>();
		
		for (int i = 0; i < size; i++) {
			IffNode child = iff.enterForm(chunkName);
			data.add(transform.apply(child));
			iff.exitForm();
		}
		
		attributes.put(attr, data);
	}
	
	private void putList(IffNode chunk, ObjectDataAttribute attr, Function<IffNode, ?> transform) {
		boolean append = chunk.readBoolean();
		int size = chunk.readInt();
		@SuppressWarnings("unchecked")
		List<Object> data = append ? (List<Object>) attributes.getOrDefault(attr, new ArrayList<>()) : new ArrayList<>();
		for (int i = 0; i < size; i++) {
			data.add(transform.apply(chunk));
		}
		attributes.put(attr, data);
	}
	
	private void putStfString(IffNode chunk, ObjectDataAttribute attr) {
		StringId stf = readStfIfPresent(chunk);
		if (stf != null)
			attributes.put(attr, stf);
	}
	
	private void putString(IffNode chunk, ObjectDataAttribute attr) {
		if (chunk.readByte() == 0)
			return;
		String s = chunk.readString();
		if (s.isEmpty())
			return;
		
		attributes.put(attr, s);
	}
	
	private void putInt(IffNode chunk, ObjectDataAttribute attr) {
		if (chunk.readByte() == 0)
			return; // This should always be 1 if there is an int (note that 0x20 follows after this even if it's 0)
		chunk.readByte(); // 0x20 byte for all it seems, unsure what it means
		attributes.put(attr, chunk.readInt());
	}
	
	private void putFloat(IffNode chunk, ObjectDataAttribute attr) {
		if (chunk.readByte() == 0)
			return; // This should always be 1 if there is an int (note that 0x20 follows after this even if it's 0)
		chunk.readByte(); // 0x20 byte for all it seems, unsure what it means
		attributes.put(attr, chunk.readFloat());
	}
	
	private void putBoolean(IffNode chunk, ObjectDataAttribute attr) {
		attributes.put(attr, (chunk.readByte() == 1));
	}
	
	private static StringId readStfIfPresent(IffNode chunk) {
		if (chunk.readByte() == 0 || chunk.readByte() == 0)
			return null;
		String file = chunk.readString();
		if (file.isEmpty())
			return null;
		if (chunk.readByte() == 0)
			return new StringId(file, "");
		return new StringId(file, chunk.readString());
	}
	
	private static String readStringIfPresent(IffNode chunk) {
		if (chunk.readByte() == 0)
			return null;
		String s = chunk.readString();
		if (s.isEmpty())
			return null;
		return s;
	}
	
	private static Integer readIntIfPresent(IffNode chunk) {
		if (chunk.readByte() == 0)
			return null; // This should always be 1 if there is an int (note that 0x20 follows after this even if it's 0)
		chunk.readByte(); // 0x20 byte for all it seems, unsure what it means
		return chunk.readInt();
	}
	
	private static Map<String, Object> readAttributes(IffNode form) {
		IffNode pcnt = form.getNextUnreadChunk();
		assert pcnt.readInt() == 3;
		StringId name = readStructItem(form, "name", ObjectData::readStfIfPresent);
		StringId experiment = readStructItem(form, "experiment", ObjectData::readStfIfPresent);
		Integer value = readStructItem(form, "value", ObjectData::readIntIfPresent);
		return createMap("name", name, "experiment", experiment, "value", value == null ? 0 : value);
	}
	
	private static Map<String, Object> readSlots(IffNode form) {
		IffNode pcnt = form.getNextUnreadChunk();
		assert pcnt.readInt() == 2;
		StringId name = readStructItem(form, "name", ObjectData::readStfIfPresent);
		String hardpoint = readStructItem(form, "hardpoint", ObjectData::readStringIfPresent);
		return createMap("name", name, "hardpoint", hardpoint);
	}
	
	private static Map<String, Object> readConstStringCustomizationVariables(IffNode form) {
		IffNode pcnt = form.getNextUnreadChunk();
		assert pcnt.readInt() == 2;
		String variableName = readStructItem(form, "variableName", ObjectData::readStringIfPresent);
		String constValue = readStructItem(form, "constValue", ObjectData::readStringIfPresent);
		return createMap("variableName", variableName, "constValue", constValue);
	}
	
	private static Map<String, Object> readCustomizationVariableMapping(IffNode form) {
		IffNode pcnt = form.getNextUnreadChunk();
		assert pcnt.readInt() == 2;
		String source = readStructItem(form, "sourceVariable", ObjectData::readStringIfPresent);
		String dependent = readStructItem(form, "dependentVariable", ObjectData::readStringIfPresent);
		return createMap("source", source, "dependent", dependent);
	}
	
	private static Map<String, Object> readPaletteColorCustomizationVariable(IffNode form) {
		IffNode pcnt = form.getNextUnreadChunk();
		assert pcnt.readInt() == 3;
		String variableName = readStructItem(form, "variableName", ObjectData::readStringIfPresent);
		String palettePathName = readStructItem(form, "palettePathName", ObjectData::readStringIfPresent);
		Integer defaultPaletteIndex = readStructItem(form, "defaultPaletteIndex", ObjectData::readIntIfPresent);
		return createMap("variableName", variableName, "palettePathName", palettePathName, "defaultPaletteIndex", defaultPaletteIndex == null ? 0 : defaultPaletteIndex);
	}
	
	private static Map<String, Object> readRangedIntCustomizationVariable(IffNode form) {
		IffNode pcnt = form.getNextUnreadChunk();
		assert pcnt.readInt() == 4;
		String variableName = readStructItem(form, "variableName", ObjectData::readStringIfPresent);
		Integer minValueInclusive = readStructItem(form, "minValueInclusive", ObjectData::readIntIfPresent);
		Integer defaultValue = readStructItem(form, "defaultValue", ObjectData::readIntIfPresent);
		Integer maxValueExclusive = readStructItem(form, "maxValueExclusive", ObjectData::readIntIfPresent);
		if (minValueInclusive == null)
			minValueInclusive = 0;
		if (defaultValue == null)
			defaultValue = 0;
		if (maxValueExclusive == null)
			maxValueExclusive = 0;
		return createMap("variableName", variableName, "minValueInclusive", minValueInclusive, "defaultValue", defaultValue, "maxValueExclusive", maxValueExclusive);
	}
	
	private static <T> T readStructItem(IffNode form, String expectedName, Function<IffNode, T> transform) {
		IffNode chunk = form.getNextUnreadChunk();
		assert chunk.getTag().equals("XXXX");
		String key = chunk.readString();
		assert key.equals(expectedName);
		return transform.apply(chunk);
	}
	
	private static Map<String, Object> createMap(Object ... items) {
		assert items.length % 2 == 0;
		Map<String, Object> map = new HashMap<>();
		for (int i = 0; i < items.length; i+=2) {
			assert items[i] instanceof String;
			map.put((String) items[i], items[i+1]);
		}
		return map;
	}
	
}
