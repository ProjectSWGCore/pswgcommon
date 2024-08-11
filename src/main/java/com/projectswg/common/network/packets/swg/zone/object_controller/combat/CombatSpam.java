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
package com.projectswg.common.network.packets.swg.zone.object_controller.combat;

import com.projectswg.common.data.combat.*;
import com.projectswg.common.data.encodables.oob.OutOfBandPackage;
import com.projectswg.common.data.encodables.oob.StringId;
import com.projectswg.common.network.NetBuffer;
import com.projectswg.common.network.packets.swg.zone.object_controller.ObjectController;

public class CombatSpam extends ObjectController {
	
	public static final int CRC = 0x0134;
	
	private byte dataType;
	private long attacker;
	private long defender;
	private long weapon;
	private StringId weaponName;
	private StringId attackName;
	private AttackInfo info;
	private OutOfBandPackage spamMessage;
	private CombatSpamType spamType;
	
	public CombatSpam(long objectId) {
		super(objectId, CRC);
	}
	
	public CombatSpam(NetBuffer data) {
		super(CRC);
		decode(data);
	}
	
	@Override
	public void decode(NetBuffer data) {
		decodeHeader(data);
		info = new AttackInfo();
		dataType = data.getByte();
		attacker = data.getLong();
		defender = data.getLong();
		if (isAttackDataWeaponObject(dataType) || isAttackWeaponName(dataType)) {
			if (isAttackDataWeaponObject(dataType))
				weapon = data.getLong();
			else
				weaponName = data.getEncodable(StringId.class);
			attackName = data.getEncodable(StringId.class);
			info.setSuccess(data.getBoolean());
			if (info.isSuccess()) {
				info.setArmor(data.getLong());
				info.setRawDamage(data.getInt());
				info.setDamageType(DamageType.Companion.getDamageType(data.getInt()));
				info.setElementalDamage(data.getInt());
				info.setElementalDamageType(DamageType.Companion.getDamageType(data.getInt()));
				info.setBleedDamage(data.getInt());
				info.setCriticalDamage(data.getInt());
				info.setBlockedDamage(data.getInt());
				info.setFinalDamage(data.getInt());
				info.setHitLocation(HitLocation.Companion.getHitLocation(data.getInt()));
			}
		} else if (isMessageData(dataType)) {
			spamMessage = data.getEncodable(OutOfBandPackage.class);
		}
		spamType = CombatSpamType.Companion.getCombatSpamType(data.getInt());
	}
	
	@Override
	public NetBuffer encode() {
		NetBuffer data = NetBuffer.allocate(getEncodeSize());
		encodeHeader(data);
		data.addByte(dataType);
		data.addLong(attacker);
		data.addLong(defender);
		if (isAttackDataWeaponObject(dataType) || isAttackWeaponName(dataType)) {
			if (isAttackDataWeaponObject(dataType))
				data.addLong(weapon);
			else
				data.addEncodable(weaponName);
			data.addEncodable(attackName);
			data.addBoolean(info.isSuccess());
			if (info.isSuccess()) {
				data.addLong(info.getArmor());
				data.addInt(info.getRawDamage());
				data.addInt(info.getDamageType().getNum());
				data.addInt(info.getElementalDamage());
				data.addInt(info.getElementalDamageType().getNum());
				data.addInt(info.getBleedDamage());
				data.addInt(info.getCriticalDamage());
				data.addInt(info.getBlockedDamage());
				data.addInt(info.getFinalDamage());
				data.addInt(info.getHitLocation().getNum());
			}
		} else if (isMessageData(dataType)) {
			data.addEncodable(spamMessage);
		}
		
		data.addInt(spamType.getNum());
		return data;
	}
	
	private int getEncodeSize() {
		int size = HEADER_LENGTH + 21;
		if (isAttackDataWeaponObject(dataType))
			size += 9 + attackName.getLength() + (info.isSuccess() ? 44 : 0);
		else if (isAttackWeaponName(dataType))
			size += 1 + attackName.getLength() + weaponName.getLength() + (info.isSuccess() ? 44 : 0);
		else if (isMessageData(dataType))
			size += spamMessage.getLength();
		return size;
	}
	
	public byte getDataType() {
		return dataType;
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
	
	public StringId getWeaponName() {
		return weaponName;
	}
	
	public StringId getAttackName() {
		return attackName;
	}
	
	public AttackInfo getInfo() {
		return info;
	}
	
	public OutOfBandPackage getSpamMessage() {
		return spamMessage;
	}
	
	public CombatSpamType getSpamType() {
		return spamType;
	}
	
	public void setDataType(byte dataType) {
		this.dataType = dataType;
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
	
	public void setWeaponName(StringId weaponName) {
		this.weaponName = weaponName;
	}
	
	public void setAttackName(StringId attackName) {
		this.attackName = attackName;
	}
	
	public void setInfo(AttackInfo info) {
		this.info = info;
	}
	
	/**
	 * NOTE: This only works when the OutOfBandPackage contains a ProsePackage!
	 * @param spamMessage
	 */
	public void setSpamMessage(OutOfBandPackage spamMessage) {
		this.spamMessage = spamMessage;
	}
	
	/**
	 * Controls the color of the combat log entry shown in the client
	 * @param spamType color to show this entry in
	 */
	public void setSpamType(CombatSpamType spamType) {
		this.spamType = spamType;
	}
	
	private boolean isAttackDataWeaponObject(byte b) {
		return b == 0;
	}
	
	private boolean isAttackWeaponName(byte b) {
		return b == 1;
	}
	
	private boolean isMessageData(byte b) {
		return b == 2;
	}
	
}

