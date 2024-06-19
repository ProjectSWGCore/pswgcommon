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
package com.projectswg.common.data.sui

enum class SuiEvent(val value: Int) {
	NONE(0),
	BUTTON(1),
	CHECKBOX(2),
	ENABLE_DISABLE(3),
	GENERIC(4),
	SLIDER(5),
	TAB_PANE(6),
	TEXTBOX(7),
	VISIBILITY_CHANGED(8),
	OK_PRESSED(9),
	CANCEL_PRESSED(10);

	companion object {
		fun valueOf(value: Int): SuiEvent {
			return when (value) {
				0    -> NONE
				1    -> BUTTON
				2    -> CHECKBOX
				3    -> ENABLE_DISABLE
				4    -> GENERIC
				5    -> SLIDER
				6    -> TAB_PANE
				7    -> TEXTBOX
				8    -> VISIBILITY_CHANGED
				9    -> OK_PRESSED
				10   -> CANCEL_PRESSED
				else -> NONE
			}
		}
	}
}
