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

import com.projectswg.common.data.EnumLookup;

public enum RadialItem {
	UNKNOWN							(0x0000),
	COMBAT_TARGET					(0x0001),
	COMBAT_UNTARGET					(0x0002),
	COMBAT_ATTACK					(0x0003),
	COMBAT_PEACE					(0x0004),
	COMBAT_DUEL						(0x0005),
	COMBAT_DEATH_BLOW				(0x0006),
	EXAMINE							(0x0007),
	EXAMINE_CHARACTERSHEET			(0x0008),
	TRADE_START						(0x0009),
	TRADE_ACCEPT					(0x000A),
	ITEM_PICKUP						(0x000B),
	ITEM_EQUIP						(0x000C),
	ITEM_UNEQUIP					(0x000D),
	ITEM_DROP						(0x000E),
	ITEM_DESTROY					(0x000F),
	ITEM_TOKEN						(0x0010),
	ITEM_OPEN						(0x0011),
	ITEM_OPEN_NEW_WINDOW			(0x0012),
	ITEM_ACTIVATE					(0x0013),
	ITEM_DEACTIVATE					(0x0014),
	ITEM_USE						(0x0015),
	ITEM_USE_SELF					(0x0016),
	ITEM_USE_OTHER					(0x0017),
	ITEM_SIT						(0x0018),
	ITEM_MAIL						(0x0019),
	CONVERSE_START					(0x001A),
	CONVERSE_RESPOND				(0x001B),
	CONVERSE_RESPONSE				(0x001C),
	CONVERSE_STOP					(0x001D),
	CRAFT_OPTIONS					(0x001E),
	CRAFT_START						(0x001F),
	CRAFT_HOPPER_INPUT				(0x0020),
	CRAFT_HOPPER_OUTPUT				(0x0021),
	TERMINAL_MISSION_LIST			(0x0022),
	MISSION_DETAILS					(0x0023),
	LOOT							(0x0024),
	LOOT_ALL						(0x0025),
	GROUP_INVITE					(0x0026),
	GROUP_JOIN						(0x0027),
	GROUP_LEAVE						(0x0028),
	GROUP_KICK						(0x0029),
	GROUP_DISBAND					(0x002A),
	GROUP_DECLINE					(0x002B),
	EXTRACT_OBJECT					(0x002C),
	PET_CALL						(0x002D),
	TERMINAL_AUCTION_USE			(0x002E),
	CREATURE_FOLLOW					(0x002F),
	CREATURE_STOP_FOLLOW			(0x0030),
	SPLIT							(0x0031),
	IMAGEDESIGN						(0x0032),
	SET_NAME						(0x0033),
	ITEM_ROTATE						(0x0034),
	ITEM_ROTATE_RIGHT				(0x0035),
	ITEM_ROTATE_LEFT				(0x0036),
	ITEM_MOVE						(0x0037),
	ITEM_MOVE_FORWARD				(0x0038),
	ITEM_MOVE_BACK					(0x0039),
	ITEM_MOVE_UP					(0x003A),
	ITEM_MOVE_DOWN					(0x003B),
	PET_STORE						(0x003C),
	VEHICLE_GENERATE				(0x003D),
	VEHICLE_STORE					(0x003E),
	MISSION_ABORT					(0x003F),
	MISSION_END_DUTY				(0x0040),
	SHIP_MANAGE_COMPONENTS			(0x0041),
	WAYPOINT_AUTOPILOT				(0x0042),
	PROGRAM_DROID					(0x0043),
	VEHICLE_OFFER_RIDE				(0x0044),
	ITEM_PUBLIC_CONTAINER_USE1		(0x0045),
	COLLECTIONS						(0x0046),
	GROUP_MASTER_LOOTER				(0x0047),
	GROUP_MAKE_LEADER				(0x0048),
	GROUP_LOOT						(0x0049),
	ITEM_ROTATE_FORWARD				(0x004A),
	ITEM_ROTATE_BACKWARD			(0x004B),
	ITEM_ROTATE_CLOCKWISE			(0x004C),
	ITEM_ROTATE_COUNTERCLOCKWISE	(0x004D),
	ITEM_ROTATE_RANDOM				(0x004E),
	ITEM_ROTATE_RANDOM_YAW			(0x004F),
	ITEM_ROTATE_RANDOM_PITCH		(0x0050),
	ITEM_ROTATE_RANDOM_ROLL			(0x0051),
	ITEM_ROTATE_RESET				(0x0052),
	ITEM_ROTATE_COPY				(0x0053),
	ITEM_MOVE_COPY_LOCATION			(0x0054),
	ITEM_MOVE_COPY_HEIGHT			(0x0055),
	GROUP_TELL						(0x0056),
	ITEM_WP_SETCOLOR				(0x0057),
	ITEM_WP_SETCOLOR_BLUE			(0x0058),
	ITEM_WP_SETCOLOR_GREEN			(0x0059),
	ITEM_WP_SETCOLOR_ORANGE			(0x005A),
	ITEM_WP_SETCOLOR_YELLOW			(0x005B),
	ITEM_WP_SETCOLOR_PURPLE			(0x005C),
	ITEM_WP_SETCOLOR_WHITE			(0x005D),
	ITEM_MOVE_LEFT					(0x005E),
	ITEM_MOVE_RIGHT					(0x005F),
	ROTATE_APPLY					(0x0060),
	ROTATE_RESET					(0x0061),
	WINDOW_LOCK						(0x0062),
	WINDOW_UNLOCK					(0x0063),
	GROUP_CREATE_PICKUP_POINT		(0x0064),
	GROUP_USE_PICKUP_POINT			(0x0065),
	GROUP_USE_PICKUP_POINT_NOCAMP	(0x0066),
	VOICE_SHORTLIST_REMOVE			(0x0067),
	VOICE_INVITE					(0x0068),
	VOICE_KICK						(0x0069),
	ITEM_EQUIP_APPEARANCE			(0x006A),
	ITEM_UNEQUIP_APPEARANCE			(0x006B),
	OPEN_STORYTELLER_RECIPE			(0x006C),
	CLIENT_MENU_LAST				(0x006D),
	SERVER_MENU1					(0x006E),
	SERVER_MENU2					(0x006F),
	SERVER_MENU3					(0x0070), // created
	SERVER_MENU4					(0x0071), // created
	SERVER_MENU5					(0x0072), // created
	SERVER_MENU6					(0x0073), // created
	SERVER_MENU7					(0x0074), // created
	SERVER_MENU8					(0x0075), // created
	SERVER_MENU9					(0x0076), // created
	SERVER_MENU10					(0x0077), // created
	SERVER_MENU11					(0x0078), // created
	SERVER_MENU12					(0x0079), // created
	SERVER_MENU13					(0x007A), // created
	SERVER_MENU14					(0x007B), // created
	SERVER_MENU15					(0x007C), // created
	SERVER_MENU16					(0x007D), // created
	SERVER_MENU17					(0x007E), // created
	SERVER_MENU18					(0x007F), // created
	SERVER_MENU19					(0x0080), // created
	SERVER_MENU20					(0x0081), // created
	SERVER_MENU21					(0x0082), // created
	SERVER_MENU22					(0x0083), // created
	SERVER_MENU23					(0x0084), // created
	SERVER_MENU24					(0x0085), // created
	SERVER_MENU25					(0x0086), // created
	SERVER_MENU26					(0x0087), // created
	SERVER_MENU27					(0x0088), // created
	SERVER_MENU28					(0x0089), // created
	SERVER_MENU29					(0x008A), // created
	SERVER_MENU30					(0x008B), // created
	SERVER_MENU31					(0x008C), // created
	SERVER_MENU32					(0x008D), // created
	SERVER_MENU33					(0x008E), // created
	SERVER_MENU34					(0x008F), // created
	SERVER_MENU35					(0x0090), // created
	SERVER_MENU36					(0x0091), // created
	SERVER_MENU37					(0x0092), // created
	SERVER_MENU38					(0x0093), // created
	SERVER_MENU39					(0x0094), // created
	SERVER_MENU40					(0x0095), // created
	SERVER_MENU41					(0x0096), // created
	SERVER_MENU42					(0x0097), // created
	SERVER_MENU43					(0x0098), // created
	SERVER_MENU44					(0x0099), // created
	SERVER_MENU45					(0x009A), // created
	SERVER_MENU46					(0x009B), // created
	SERVER_MENU47					(0x009C), // created
	SERVER_MENU48					(0x009D), // created
	SERVER_MENU49					(0x009E), // created
	SERVER_MENU50					(0x009F), // created
	SERVER_MENU51					(0x00A0), // created
	SERVER_MENU52					(0x00A1), // created
	SERVER_MENU53					(0x00A2), // created
	SERVER_MENU54					(0x00A3), // created
	SERVER_MENU55					(0x00A4), // created
	SERVER_MENU56					(0x00A5), // created
	SERVER_MENU57					(0x00A6), // created
	SERVER_MENU58					(0x00A7), // created
	SERVER_MENU59					(0x00A8), // created
	SERVER_MENU60					(0x00A9), // created
	SERVER_MENU61					(0x00AA), // created
	SERVER_MENU62					(0x00AB), // created
	SERVER_MENU63					(0x00AC), // created
	SERVER_MENU64					(0x00AD), // created
	SERVER_ITEM_OPTIONS				(0x00D6),
	SERVER_SURVEY_TOOL_RANGE		(0x00D7),
	SERVER_PET_MOUNT				(0x011F),
	SERVER_VEHICLE_ENTER_EXIT		(0x0124);
	
	private static final EnumLookup<Integer, RadialItem> LOOKUP = new EnumLookup<>(RadialItem.class, RadialItem::getType);
	
	private final int type;
	
	RadialItem(int type) {
		this.type = type;
	}
	
	public int getType() {
		return type;
	}
	
	/**
	 * Gets the RadialItem from the selection id. If the item is undefined,
	 * then NULL is returned
	 * @param id the selection id that maps to a RadialItem
	 * @return the RadialItem represented by the selection, or NULL if it does
	 * not exist
	 */
	public static RadialItem getFromId(int id) {
		return LOOKUP.getEnum(id, null);
	}
	
}
