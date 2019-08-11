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
package com.projectswg.common.data.swgfile;

import com.projectswg.common.data.swgfile.visitors.*;
import com.projectswg.common.data.swgfile.visitors.appearance.*;
import com.projectswg.common.data.swgfile.visitors.shader.CustomizableShaderData;
import com.projectswg.common.data.swgfile.visitors.shader.StaticShaderData;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class ClientFactory extends DataFactory {
	
	private static final ClientFactory INSTANCE = new ClientFactory();
	private static final Map <String, ClientData> DATA_MAP = new ConcurrentHashMap<>();
	private static final Map <String, ClientFactoryType> TYPE_MAP = new HashMap<>();
	
	static {
		populateTypeMap();
	}
	
	/**
	 * Creates a new instance of ClientFactory.
	 * <br>
	 * <br>
	 * In order to add parsing for an IFF type which is not yet parsed:
	 * <OL>
	 * <LI>Create a new class which extends {@link ClientData}.
	 * <LI>Perform the parsing of each node within the parse method using switch-case statements for different node names.
	 * <LI>Add a new entry to the typeMap through populateTypeMap() method by adding in the name of the folder/node you're parsing 
	 * as the Key and the name of the class that was just created as the Value.
	 * <LI>Add in a case statement in the createDataObject method returning a new instance of the class, the case being the Value 
	 * that was entered in Step 3.
	 * </OL>
	 */
	private ClientFactory() {
		
	}
	
	public static void freeMemory() {
		DATA_MAP.clear();
	}

	/**
	 * Retrieves information from a client file used by SWG. Parsing of the file is done internally using {@link ClientData} which also
	 * stores the variables and is the returned type. Retrieving info from this file puts a reference of the returned 
	 * {@link ClientData} into a {@link HashMap}. Future calls for this file will try and obtain this reference if it's not null to prevent
	 * the file from being parsed multiple times if the save variable is true.
	 * @param file The SWG file you wish to get information from which resides in the ./clientdata/ folder.
	 * Example: creation/profession_defaults_combat_brawler.iff
	 * @return Specific visitor type of {@link ClientData} relating to the chosen file. For example, loading the file
	 * creation/profession_defaults_combat_brawler.iff would return an instance of {@link ProfTemplateData} extended from {@link ClientData}.
	 * A null instance of {@link ClientData} means that parsing for the type of file is not done, or a file was entered that doesn't exist on the
	 * file system.
	 */
	public static ClientData getInfoFromFile(String file) {
		String fileInterned = file.intern();
		ClientData data = DATA_MAP.get(fileInterned);
		if (data != null)
			return data;
		data = getInstance().readFile(fileInterned);
		if (data != null)
			DATA_MAP.putIfAbsent(fileInterned, data);
		return data;
	}

	/**
	 * Retrieves information from a client file used by SWG. Parsing of the file is done internally using {@link ClientData} which also
	 * stores the variables and is the returned type. Retrieving info from this file puts a reference of the returned 
	 * {@link ClientData} into a {@link HashMap}. Future calls for this file will try and obtain this reference if it's not null to prevent
	 * the file from being parsed multiple times if the save variable is true.
	 * @param file The SWG file you wish to get information from which resides in the ./clientdata/ folder.
	 * @return Specific visitor type of {@link ClientData} relating to the chosen file. For example, loading the file
	 * creation/profession_defaults_combat_brawler.iff would return an instance of {@link ProfTemplateData} extended from {@link ClientData}.
	 * A null instance of {@link ClientData} means that parsing for the type of file is not done, or a file was entered that doesn't exist on the
	 * file system.
	 */
	public static ClientData getInfoFromFile(File file) {
		return getInstance().readFile(file);
	}

	public static String formatToSharedFile(String original) {
		int index = original.lastIndexOf('/');
		if (original.indexOf("shared_", index+1) != -1)
			return original.intern();
		
		return (original.substring(0, index) + "/shared_" + original.substring(index+1)).intern();
	}

	// Any time a new DataObject is coded for parsing a file, it will need to be added in populateTypeMap() along with a new return 
	// of that instance so the file can be parsed. The type is the name of the folder/node which is then used to get the value associated
	// with it in the typeMap (value being the name of the Class preferably). If populateTypeMap() does not contain that node, then null is returned
	// and getFileType method will print out what the type is along with a "not implemented!" message.
	@Override
	protected ClientData createDataObject(String type) {
		ClientFactoryType c = TYPE_MAP.get(type);
		if (c != null)
			return c.create();
		return null;
	}

	// The typeMap is used for determining what DataObject class
	private static void populateTypeMap() {
		TYPE_MAP.put("CSTB", ClientFactoryType.CRC_STRING_TABLE_DATA);
		TYPE_MAP.put("DTII", ClientFactoryType.DATATABLE_DATA);
		TYPE_MAP.put("PRTO", ClientFactoryType.PORTAL_LAYOUT_DATA);
		TYPE_MAP.put("PRFI", ClientFactoryType.PROFILE_TEMPLATE_DATA);
		TYPE_MAP.put("ARGD", ClientFactoryType.SLOT_ARRANGEMENT_DATA);
		TYPE_MAP.put("0006", ClientFactoryType.SLOT_DEFINITION_DATA);
		TYPE_MAP.put("SLTD", ClientFactoryType.SLOT_DESCRIPTOR_DATA);
		TYPE_MAP.put("WSNP", ClientFactoryType.WORLD_SNAPSHOT_DATA);
		TYPE_MAP.put("CIDM", ClientFactoryType.CUSTOMIZATION_ID_MANAGER_DATA);
		TYPE_MAP.put("FOOT", ClientFactoryType.FOOTPRINT_DATA);
		// Appearance Related Data
		TYPE_MAP.put("APPR", ClientFactoryType.APPEARANCE_TEMPLATE_DATA);
		TYPE_MAP.put("APT ", ClientFactoryType.APPEARANCE_TEMPLATE_LIST);
		TYPE_MAP.put("CSHD", ClientFactoryType.CUSTOMIZABLE_SHADER_DATA);
		TYPE_MAP.put("DTLA", ClientFactoryType.DETAILED_APPEARANCE_TEMPLATE_DATA);
		TYPE_MAP.put("SKTM", ClientFactoryType.BASIC_SKELETON_TEMPLATE);
		TYPE_MAP.put("MESH", ClientFactoryType.MESH_APPEARANCE_TEMPLATE);
		TYPE_MAP.put("MLOD", ClientFactoryType.LOD_MESH_GENERATOR_TEMPLATE_DATA);
		TYPE_MAP.put("SLOD", ClientFactoryType.LOD_SKELETON_TEMPLATE_DATA);
		TYPE_MAP.put("SMAT", ClientFactoryType.SKELETAL_APPEARANCE_DATA);
		TYPE_MAP.put("SKMG", ClientFactoryType.SKELETAL_MESH_GENERATOR_TEMPLATE_DATA);
		TYPE_MAP.put("SSHT", ClientFactoryType.STATIC_SHADER_DATA);
		// Objects
		TYPE_MAP.put("SBMK", ClientFactoryType.OBJECT_DATA); // object/battlefield_marker
		TYPE_MAP.put("SBOT", ClientFactoryType.OBJECT_DATA); // object/building
		TYPE_MAP.put("CCLT", ClientFactoryType.OBJECT_DATA); // object/cell
		TYPE_MAP.put("SCNC", ClientFactoryType.OBJECT_DATA); // object/construction_contract
		TYPE_MAP.put("SCOU", ClientFactoryType.OBJECT_DATA); // object/counting
		TYPE_MAP.put("SCOT", ClientFactoryType.OBJECT_DATA); // object/creature && object/mobile
		TYPE_MAP.put("SDSC", ClientFactoryType.OBJECT_DATA); // object/draft_schematic
		TYPE_MAP.put("SFOT", ClientFactoryType.OBJECT_DATA); // object/factory
		TYPE_MAP.put("SGRP", ClientFactoryType.OBJECT_DATA); // object/group
		TYPE_MAP.put("SGLD", ClientFactoryType.OBJECT_DATA); // object/guild
		TYPE_MAP.put("SIOT", ClientFactoryType.OBJECT_DATA); // object/installation
		TYPE_MAP.put("SITN", ClientFactoryType.OBJECT_DATA); // object/intangible
		TYPE_MAP.put("SJED", ClientFactoryType.OBJECT_DATA); // object/jedi_manager
		TYPE_MAP.put("SMSC", ClientFactoryType.OBJECT_DATA); // object/manufacture_schematic
		TYPE_MAP.put("SMSO", ClientFactoryType.OBJECT_DATA); // object/mission
		TYPE_MAP.put("SHOT", ClientFactoryType.OBJECT_DATA); // object/object
		TYPE_MAP.put("STOT", ClientFactoryType.OBJECT_DATA); // object/path_waypoint && object/tangible
		TYPE_MAP.put("SPLY", ClientFactoryType.OBJECT_DATA); // object/player
		TYPE_MAP.put("SPQO", ClientFactoryType.OBJECT_DATA); // object/player_quest
		TYPE_MAP.put("RCCT", ClientFactoryType.OBJECT_DATA); // object/resource_container
		TYPE_MAP.put("SSHP", ClientFactoryType.OBJECT_DATA); // object/ship
		TYPE_MAP.put("STAT", ClientFactoryType.OBJECT_DATA); // object/soundobject && object/static
		TYPE_MAP.put("STOK", ClientFactoryType.OBJECT_DATA); // object/token
		TYPE_MAP.put("SUNI", ClientFactoryType.OBJECT_DATA); // object/universe
		TYPE_MAP.put("SWAY", ClientFactoryType.OBJECT_DATA); // object/waypoint
		TYPE_MAP.put("SWOT", ClientFactoryType.OBJECT_DATA); // object/weapon
		//
	}
	
	@Override
	protected String getFolder() {
		return "clientdata/";
	}
	
	private static ClientFactory getInstance() {
		return INSTANCE;
	}
	
	private enum ClientFactoryType {
		APPEARANCE_TEMPLATE_DATA				(AppearanceTemplateData::new),
		APPEARANCE_TEMPLATE_LIST				(AppearanceTemplateList::new),
		BASIC_SKELETON_TEMPLATE					(BasicSkeletonTemplate::new),
		CRC_STRING_TABLE_DATA					(CrcStringTableData::new),
		CUSTOMIZABLE_SHADER_DATA				(CustomizableShaderData::new),
		CUSTOMIZATION_ID_MANAGER_DATA			(CustomizationIDManagerData::new),
		DATATABLE_DATA							(DatatableData::new),
		DETAILED_APPEARANCE_TEMPLATE_DATA		(DetailedAppearanceTemplateData::new),
		FOOTPRINT_DATA							(FootprintData::new),
		LOD_MESH_GENERATOR_TEMPLATE_DATA		(LodMeshGeneratorTemplateData::new),
		LOD_SKELETON_TEMPLATE_DATA				(LodSkeletonTemplateData::new),
		MESH_APPEARANCE_TEMPLATE				(MeshAppearanceTemplate::new),
		OBJECT_DATA								(ObjectData::new),
		PORTAL_LAYOUT_DATA						(PortalLayoutData::new),
		PROFILE_TEMPLATE_DATA					(ProfTemplateData::new),
		SLOT_DESCRIPTOR_DATA					(SlotDescriptorData::new),
		SLOT_DEFINITION_DATA					(SlotDefinitionData::new),
		SLOT_ARRANGEMENT_DATA					(SlotArrangementData::new),
		SKELETAL_APPEARANCE_DATA				(SkeletalAppearanceData::new),
		SKELETAL_MESH_GENERATOR_TEMPLATE_DATA	(SkeletalMeshGeneratorTemplateData::new),
		STATIC_SHADER_DATA						(StaticShaderData::new),
		WORLD_SNAPSHOT_DATA						(WorldSnapshotData::new);
		
		private final Supplier<ClientData> supplier;
		
		ClientFactoryType(Supplier<ClientData> supplier) {
			this.supplier = supplier;
		}
		
		public ClientData create() {
			return supplier.get();
		}
	}
}
