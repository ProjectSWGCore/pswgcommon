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
package com.projectswg.common.network.packets.swg.zone.object_controller.combat;

import com.projectswg.common.data.combat.AttackInfo;
import com.projectswg.common.data.encodables.oob.StringId;
import com.projectswg.common.data.location.Point3D;
import com.projectswg.common.network.NetBuffer;
import com.projectswg.common.network.packets.swg.zone.object_controller.ObjectController;

public class CombatSpam extends ObjectController {
	
	public static final int CRC = 0x0134;
	
	private long attacker;
	private long defender;
	private long weapon;
	private int damage;
	private StringId spam;
	private byte colorFlag;	// 0=white, 1=auto green/red, 11=yellow. TODO ENUM!
	private String customString;	// String to display in combat spam
	
	public CombatSpam(long objectId) {
		super(objectId, CRC);
	}
	
	/**
	 *
	 * @param receiver object ID of the SWGObject that should receive this packet
	 * @param attacker object ID for attacker
	 * @param defender object ID for defender
	 * @param weapon object ID of attacker's weapon used to attack the defender
	 * @param damage done to defender by attacker
	 * @param spam message to show in combat log
	 * @param colorFlag of damage flytext
	 * @param customString optional string to display in the combat log
	 */
	public CombatSpam(long receiver, long attacker, long defender, long weapon, int damage, StringId spam, byte colorFlag, String customString) {
		super(receiver, CRC);
		this.attacker = attacker;
		this.defender = defender;
		this.weapon = weapon;
		this.damage = damage;
		this.spam = spam;
		this.colorFlag = colorFlag;
		this.customString = customString;
	}
	
	public CombatSpam(NetBuffer data) {
		super(CRC);
		decode(data);
	}
	
	@Override
	public void decode(NetBuffer data) {
		decodeHeader(data);
		attacker = data.getLong();
		defender = data.getLong();
		weapon = data.getLong();
		
		damage = data.getInt();
		spam = data.getEncodable(StringId.class);
		colorFlag = data.getByte();
		customString = data.getUnicode();
	}
	
	@Override
	public NetBuffer encode() {
		NetBuffer data = NetBuffer.allocate(HEADER_LENGTH + 33 + spam.getLength());
		encodeHeader(data);
		data.addLong(attacker);
		data.addLong(defender);
		data.addLong(weapon);
		data.addInt(damage);
		data.addEncodable(spam);
		data.addByte(colorFlag);
		data.addUnicode(customString);
		
		return data;
	}
	
	public StringId getSpam() {
		return spam;
	}
	
	public void setSpam(StringId spam) {
		this.spam = spam;
	}
	
	public byte getColorFlag() {
		return colorFlag;
	}
	
	public void setColorFlag(byte colorFlag) {
		this.colorFlag = colorFlag;
	}
	
	public String getCustomString() {
		return customString;
	}
	
	public void setCustomString(String customString) {
		this.customString = customString;
	}
	
	public long getAttacker() {
		return attacker;
	}
	
	public long getDefender() {
		return defender;
	}
	
	public long getWeapon() {
		return weapon;
	}
	
	public void setAttacker(long attacker) {
		this.attacker = attacker;
	}
	
	public void setDefender(long defender) {
		this.defender = defender;
	}
	
	public void setWeapon(long weapon) {
		this.weapon = weapon;
	}
	
	public int getDamage() {
		return damage;
	}
	
	public void setDamage(int damage) {
		this.damage = damage;
	}
}

