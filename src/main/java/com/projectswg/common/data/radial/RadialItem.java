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
	UNKNOWN														,
	COMBAT_TARGET												,
	COMBAT_UNTARGET												,
	COMBAT_ATTACK												,
	COMBAT_PEACE												,
	COMBAT_DUEL													,
	COMBAT_DEATH_BLOW											,
	EXAMINE														,
	TRADE_START													,
	TRADE_ACCEPT												,
	ITEM_PICKUP													,
	ITEM_EQUIP													,
	ITEM_UNEQUIP												,
	ITEM_DROP													,
	ITEM_DESTROY												,
	ITEM_TOKEN													,
	ITEM_OPEN													,
	ITEM_OPEN_NEW_WINDOW										,
	ITEM_ACTIVATE												,
	ITEM_DEACTIVATE												,
	ITEM_USE													,
	ITEM_USE_SELF												,
	ITEM_USE_OTHER												,
	ITEM_SIT													,
	ITEM_MAIL													,
	CONVERSE_START												,
	CONVERSE_RESPOND											,
	CONVERSE_RESPONSE											,
	CONVERSE_STOP												,
	CRAFT_OPTIONS												,
	CRAFT_START													,
	CRAFT_HOPPER_INPUT											,
	CRAFT_HOPPER_OUTPUT											,
	MISSION_TERMINAL_LIST										,
	MISSION_DETAILS												,
	LOOT														,
	LOOT_ALL													,
	GROUP_INVITE												,
	GROUP_JOIN													,
	GROUP_LEAVE													,
	GROUP_KICK													,
	GROUP_DISBAND												,
	GROUP_DECLINE												,
	EXTRACT_OBJECT												,
	PET_CALL													,
	TERMINAL_AUCTION_USE										,
	CREATURE_FOLLOW												,
	CREATURE_STOP_FOLLOW										,
	SPLIT														,
	IMAGEDESIGN													,
	SET_NAME													,
	ITEM_ROTATE													,
	ITEM_ROTATE_RIGHT											,
	ITEM_ROTATE_LEFT											,
	ITEM_MOVE													,
	ITEM_MOVE_FORWARD											,
	ITEM_MOVE_BACK												,
	ITEM_MOVE_UP												,
	ITEM_MOVE_DOWN												,
	PET_STORE													,
	VEHICLE_GENERATE											,
	VEHICLE_STORE												,
	MISSION_ABORT												,
	MISSION_END_DUTY											,
	SHIP_MANAGE_COMPONENTS										,
	WAYPOINT_AUTOPILOT											,
	PROGRAM_DROID												,
	VEHICLE_OFFER_RIDE											,
	SERVER_DIVIDER												,
	SERVER_MENU1												,
	SERVER_MENU2												,
	SERVER_MENU3												,
	SERVER_MENU4												,
	SERVER_MENU5												,
	SERVER_MENU6												,
	SERVER_MENU7												,
	SERVER_MENU8												,
	SERVER_MENU9												,
	SERVER_MENU10												,
	SERVER_HARVESTER_MANAGE										,
	SERVER_HOUSE_MANAGE											,
	SERVER_FACTION_HALL_MANAGE									,
	SERVER_HUE													,
	SERVER_OBSERVE												,
	SERVER_STOP_OBSERVING										,
	SERVER_TRAVEL_OPTIONS										,
	SERVER_BAZAAR_OPTIONS										,
	SERVER_SHIPPING_OPTIONS										,
	SERVER_HEAL_WOUND											,
	SERVER_HEAL_WOUND_HEALTH									,
	SERVER_HEAL_WOUND_ACTION									,
	SERVER_HEAL_WOUND_STRENGTH									,
	SERVER_HEAL_WOUND_CONSTITUTION								,
	SERVER_HEAL_WOUND_QUICKNESS									,
	SERVER_HEAL_WOUND_STAMINA									,
	SERVER_HEAL_DAMAGE											,
	SERVER_HEAL_STATE											,
	SERVER_HEAL_STATE_STUNNED									,
	SERVER_HEAL_STATE_BLINDED									,
	SERVER_HEAL_STATE_DIZZY										,
	SERVER_HEAL_STATE_INTIMIDATED								,
	SERVER_HEAL_ENHANCE											,
	SERVER_HEAL_ENHANCE_HEALTH									,
	SERVER_HEAL_ENHANCE_ACTION									,
	SERVER_HEAL_ENHANCE_STRENGTH								,
	SERVER_HEAL_ENHANCE_CONSTITUTION							,
	SERVER_HEAL_ENHANCE_QUICKNESS								,
	SERVER_HEAL_ENHANCE_STAMINA									,
	SERVER_HEAL_FIRSTAID										,
	SERVER_HEAL_CURE_POISON										,
	SERVER_HEAL_CURE_DISEASE									,
	SERVER_HEAL_APPLY_POISON									,
	SERVER_HEAL_APPLY_DISEASE									,
	SERVER_HARVEST_CORPSE										,
	SERVER_PERFORMANCE_LISTEN									,
	SERVER_PERFORMANCE_WATCH									,
	SERVER_PERFORMANCE_LISTEN_STOP								,
	SERVER_PERFORMANCE_WATCH_STOP								,
	SERVER_TERMINAL_PERMISSIONS									,
	SERVER_TERMINAL_MANAGEMENT									,
	SERVER_TERMINAL_PERMISSIONS_ENTER							,
	SERVER_TERMINAL_PERMISSIONS_BANNED							,
	SERVER_TERMINAL_PERMISSIONS_ADMIN							,
	SERVER_TERMINAL_PERMISSIONS_VENDOR							,
	SERVER_TERMINAL_PERMISSIONS_HOPPER							,
	SERVER_TERMINAL_MANAGEMENT_STATUS							,
	SERVER_TERMINAL_MANAGEMENT_PRIVACY							,
	SERVER_TERMINAL_MANAGEMENT_TRANSFER							,
	SERVER_TERMINAL_MANAGEMENT_RESIDENCE						,
	SERVER_TERMINAL_MANAGEMENT_DESTROY							,
	SERVER_TERMINAL_MANAGEMENT_PAY								,
	SERVER_TERMINAL_CREATE_VENDOR								,
	SERVER_GIVE_VENDOR_MAINTENANCE								,
	SERVER_ITEM_OPTIONS											,
	SERVER_SURVEY_TOOL_RANGE									,
	SERVER_SURVEY_TOOL_RESOLUTION								,
	SERVER_SURVEY_TOOL_CLASS									,
	SERVER_PROBE_DROID_TRACK_TARGET								,
	SERVER_PROBE_DROID_FIND_TARGET								,
	SERVER_PROBE_DROID_ACTIVATE									,
	SERVER_PROBE_DROID_BUY										,
	SERVER_TEACH												,
	PET_COMMAND													,
	PET_FOLLOW													,
	PET_STAY													,
	PET_GUARD													,
	PET_FRIEND													,
	PET_ATTACK													,
	PET_PATROL													,
	PET_GET_PATROL_POINT										,
	PET_CLEAR_PATROL_POINTS										,
	PET_ASSUME_FORMATION_1										,
	PET_ASSUME_FORMATION_2										,
	PET_TRANSFER												,
	PET_RELEASE													,
	PET_TRICK_1													,
	PET_TRICK_2													,
	PET_TRICK_3													,
	PET_TRICK_4													,
	PET_GROUP													,
	PET_TAME													,
	PET_FEED													,
	PET_SPECIAL_ATTACK_ONE										,
	PET_SPECIAL_ATTACK_TWO										,
	PET_RANGED_ATTACK											,
	DICE_ROLL													,
	DICE_TWO_FACE												,
	DICE_THREE_FACE												,
	DICE_FOUR_FACE												,
	DICE_FIVE_FACE												,
	DICE_SIX_FACE												,
	DICE_SEVEN_FACE												,
	DICE_EIGHT_FACE												,
	DICE_COUNT_ONE												,
	DICE_COUNT_TWO												,
	DICE_COUNT_THREE											,
	DICE_COUNT_FOUR												,
	CREATE_BALLOT												,
	VOTE														,
	BOMBING_RUN													,
	SELF_DESTRUCT												,
	THIRTY_SEC													,
	FIFTEEN_SEC													,
	SERVER_CAMP_DISBAND											,
	SERVER_CAMP_ASSUME_OWNERSHIP								,
	SERVER_PROBE_DROID_PROGRAM									,
	SERVER_GUILD_CREATE											,
	SERVER_GUILD_INFO											,
	SERVER_GUILD_MEMBERS										,
	SERVER_GUILD_SPONSORED										,
	SERVER_GUILD_ENEMIES										,
	SERVER_GUILD_SPONSOR										,
	SERVER_GUILD_DISBAND										,
	SERVER_GUILD_NAMECHANGE										,
	SERVER_GUILD_GUILD_MANAGEMENT								,
	SERVER_GUILD_MEMBER_MANAGEMENT								,
	SERVER_MANF_HOPPER_INPUT									,
	SERVER_MANF_HOPPER_OUTPUT									,
	SERVER_MANF_STATION_SCHEMATIC								,
	ELEVATOR_UP													,
	ELEVATOR_DOWN												,
	SERVER_PET_OPEN												,
	SERVER_PET_DPAD												,
	SERVER_MED_TOOL_DIAGNOSE									,
	SERVER_MED_TOOL_TENDWOUND									,
	SERVER_MED_TOOL_TENDDAMAGE									,
	SERVER_PET_MOUNT											,
	SERVER_PET_DISMOUNT											,
	SERVER_PET_TRAIN_MOUNT										,
	SERVER_VEHICLE_ENTER										,
	SERVER_VEHICLE_EXIT											,
	OPEN_NAVICOMP_DPAD											,
	INIT_NAVICOMP_DPAD											,
	CITY_STATUS													,
	CITY_CITIZENS												,
	CITY_STRUCTURES												,
	CITY_TREASURY												,
	CITY_MANAGEMENT												,
	CITY_NAME													,
	CITY_MILITIA												,
	CITY_TAXES													,
	CITY_TREASURY_DEPOSIT										,
	CITY_TREASURY_WITHDRAW										,
	CITY_REGISTER												,
	CITY_RANK													,
	CITY_ADMIN_1												,
	CITY_ADMIN_2												,
	CITY_ADMIN_3												,
	CITY_ADMIN_4												,
	CITY_ADMIN_5												,
	CITY_ADMIN_6												,
	MEMORY_CHIP_PROGRAM											,
	MEMORY_CHIP_TRANSFER										,
	MEMORY_CHIP_ANALYZE											,
	EQUIP_DROID_ON_SHIP											,
	BIO_LINK													,
	LANDMINE_DISARM												,
	LANDMINE_REVERSE_TRIGGER									;
	
	private static final EnumLookup<Integer, RadialItem> LOOKUP = new EnumLookup<>(RadialItem.class, RadialItem::getType);
	
	private final int type;
	
	RadialItem() {
		this.type = RadialItemInit.getNextItemId();
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
