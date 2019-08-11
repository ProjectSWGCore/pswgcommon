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
package com.projectswg.common.data.sui;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import me.joshlarson.jlcommon.log.Log;
import com.projectswg.common.encoding.Encodable;
import com.projectswg.common.encoding.StringType;
import com.projectswg.common.network.NetBuffer;

/**
 * @author Waverunner
 */
public class SuiComponent implements Encodable {

	private Type type;
	private List <String> wideParams;
	private List<String> narrowParams;

	public SuiComponent() {
		type = Type.NONE;
		wideParams = new ArrayList<>();
		narrowParams = new ArrayList<>();
	}

	public SuiComponent(Type type, String widget) {
		this.type = type;
		this.wideParams 	= new ArrayList<>(3);
		this.narrowParams	= new ArrayList<>(3);

		narrowParams.add(widget);
	}

	public Type getType() {
		return type;
	}
	public List <String> getNarrowParams() {
		return narrowParams;
	}
	public List <String> getWideParams() {
		return wideParams;
	}

	/**
	 * Retrieve the base widget that this component targets
	 * @return Base widget this component targets
	 */
	public String getTarget() {
		return narrowParams.get(0);
	}

	public void addNarrowParam(String param) {
		narrowParams.add(param);
	}

	public void addWideParam(String param) {
		wideParams.add(param);
	}

	public List<String> getSubscribedProperties() {
		if (type != Type.SUBSCRIBE_TO_EVENT)
			return null;

		int size = narrowParams.size();
		if (size < 3) {
			Log.w("Tried to get subscribed properties when there are none for target %s", getTarget());
		} else {
			List<String> subscribedProperties = new ArrayList<>();

			for (int i = 3; i < size;) {
				String property = narrowParams.get(i++) + "." + narrowParams.get(i++);
				subscribedProperties.add(property);
			}

			return subscribedProperties;
		}
		return null;
	}

	public String getSubscribeToEventCallback() {
		if (type != Type.SUBSCRIBE_TO_EVENT)
			return null;

		int size = narrowParams.size();
		if (size < 3) {
			Log.w("Tried to get subscribed callback when there is none for target %s", getTarget());
		} else {

			return narrowParams.get(2);
		}
		return null;
	}

	public int getSubscribedToEventType() {
		if (type != Type.SUBSCRIBE_TO_EVENT)
			return -1;

		int size = narrowParams.size();
		if (size < 3) {
			Log.w("Tried to get subscribed event type when there is none for target %s", getTarget());
		} else {
			byte[] bytes = narrowParams.get(1).getBytes(StandardCharsets.UTF_8);
			if (bytes.length > 1) {
				Log.w("Tried to get eventType but narrowparams string byte array length is more than 1");
				return -1;
			}

			return bytes[0];
		}
		return -1;
	}

	@Override
	public byte[] encode() {
		NetBuffer data = NetBuffer.allocate(getLength());
		data.addByte(type.getValue());
		data.addList(wideParams, StringType.UNICODE);
		data.addList(narrowParams, StringType.ASCII);
		return data.array();
	}

	@Override
	public void decode(NetBuffer data) {
		type			= Type.valueOf(data.getByte());
		wideParams		= data.getList(StringType.UNICODE);
		narrowParams	= data.getList(StringType.ASCII);
	}
	
	@Override
	public int getLength() {
		int size = 9;
		
		for (String param : wideParams) {
			size += 4 + (param.length() * 2);
		}
		for (String param : narrowParams) {
			size += 2 + param.length();
		}
		
		return size;
	}

	public enum Type {
		NONE(0),
		CLEAR_DATA_SOURCE(1),
		ADD_CHILD_WIDGET(2),
		SET_PROPERTY(3),
		ADD_DATA_ITEM(4),
		SUBSCRIBE_TO_EVENT(5),
		ADD_DATA_SOURCE_CONTAINER(6),
		CLEAR_DATA_SOURCE_CONTAINER(7),
		ADD_DATA_SOURCE(8);

		private byte value;

		Type(int value) {
			this.value = (byte) value;
		}

		public byte getValue() {
			return value;
		}

		public static Type valueOf(byte value) {
			for (Type type : Type.values()) {
				if (type.getValue() == value)
					return type;
			}
			return NONE;
		}
	}
}
