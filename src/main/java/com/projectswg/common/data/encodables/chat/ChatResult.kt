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
package com.projectswg.common.data.encodables.chat

import com.projectswg.common.data.EnumLookup

enum class ChatResult(val code: Int) {
	NONE(-1),  // The client will just display an "unknown error code" if this is used.
	SUCCESS(0),
	TARGET_AVATAR_DOESNT_EXIST(4),
	ROOM_INVALID_ID(5),
	ROOM_INVALID_NAME(6),
	CUSTOM_FAILURE(9),
	ROOM_AVATAR_BANNED(12),
	ROOM_PRIVATE(13),
	ROOM_AVATAR_NO_PERMISSION(16),
	IGNORED(23),
	ROOM_ALREADY_EXISTS(24),
	ROOM_ALREADY_JOINED(36),
	CHAT_SERVER_UNAVAILABLE(1000000),
	ROOM_DIFFERENT_FACTION(1000001),
	ROOM_NOT_GCW_DEFENDER_FACTION(1000005);

	companion object {
		private val LOOKUP = EnumLookup(ChatResult::class.java) { obj: ChatResult -> obj.code }

		fun fromInteger(code: Int): ChatResult {
			return LOOKUP.getEnum(code, NONE)
		}
	}
}
