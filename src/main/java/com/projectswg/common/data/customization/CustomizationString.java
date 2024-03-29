/***********************************************************************************
 * Copyright (c) 2023 /// Project SWG /// www.projectswg.com                       *
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
package com.projectswg.common.data.customization;

import com.projectswg.common.data.encodables.mongo.MongoData;
import com.projectswg.common.data.encodables.mongo.MongoPersistable;
import com.projectswg.common.encoding.Encodable;
import com.projectswg.common.network.NetBuffer;
import me.joshlarson.jlcommon.log.Log;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;

/**
 * The Customization string is used to set special properties
 * on objects. This can be lightsaber color, vehicle speed,
 * facial hair style...
 */
public class CustomizationString implements Encodable, MongoPersistable {

	private static final Map<String, Short> VAR_NAME_TO_ID = new HashMap<>();
	private static final Map<Short, String> VAR_ID_TO_NAME = new HashMap<>();

	static {
		try (BufferedInputStream bis = new BufferedInputStream(CustomizationString.class.getResourceAsStream("customization_variables.sdb"))) {
			StringBuilder buffer = new StringBuilder();
			String key = null;
			int c;
			while ((c = bis.read()) != -1) {
				switch (c) {
					case '\t':
						key = buffer.toString();
						buffer.setLength(0);
						break;
					//noinspection HardcodedLineSeparator
					case '\r':
						//noinspection HardcodedLineSeparator
					case '\n':
						if (buffer.length() <= 0)
							continue;
						assert key != null;
						VAR_NAME_TO_ID.put(key, Short.valueOf(buffer.toString()));
						VAR_ID_TO_NAME.put(Short.valueOf(buffer.toString()), key);
						buffer.setLength(0);
						break;
					default:
						buffer.append((char) c);
						break;
				}
			}
			if (buffer.length() > 0) {
				assert key != null;
				VAR_NAME_TO_ID.put(key, Short.valueOf(buffer.toString()));
				VAR_ID_TO_NAME.put(Short.valueOf(buffer.toString()), key);
			}
		} catch (IOException e) {
			throw new RuntimeException("could not load customization variables from resources", e);
		}
	}

	private final Map<String, Integer> variables;

	public CustomizationString() {
		this.variables = Collections.synchronizedMap(new LinkedHashMap<>());    // Ordered and synchronized
	}

	boolean isEmpty() {
		return variables.isEmpty();
	}

	@Override
	public void saveMongo(MongoData data) {
		data.putMap("variables", variables);
	}

	@Override
	public void readMongo(MongoData data) {
		variables.clear();
		variables.putAll(data.getMap("variables", String.class, Integer.class));
	}

	public Integer put(String name, int value) {
		return variables.put(name, value);
	}

	public Integer remove(String name) {
		return variables.remove(name);
	}

	public Integer get(String name) {
		return variables.get(name);
	}

	public Map<String, Integer> getVariables() {
		return Collections.unmodifiableMap(variables);
	}

	public void forEach(BiConsumer<? super String, ? super Integer> consumer) {
		variables.forEach(consumer);
	}

	public void clear() {
		variables.clear();
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		boolean first = true;
		for (Entry<String, Integer> e : variables.entrySet()) {
			if (!first)
				str.append(", ");
			first = false;
			str.append(e.getKey()).append('=').append(e.getValue());
		}
		return str.toString();
	}

	@Override
	public void decode(NetBuffer data) {
		byte[] stringData = data.getArray();

		if (stringData.length == 0) {
			return;
		}

		String string = new String(stringData, StandardCharsets.UTF_8);
		int[] codePoints = string.codePoints().toArray();    // Replaces 0xC3BF with 0xFF, because 0xFF is reserved as an escape flag
		int position = 0;
		byte startOfText = (byte) codePoints[position++];

		if (startOfText != 0x02) {
			Log.w("Expected UTF8 start-of-text in CustomizationString, assuming corruption!");
			return;
		}

		short variableCount = (short) codePoints[position++];

		if (variableCount == 0xFF) {
			// SOE decided not to include a variable count for the Twi'lek lekku. No object has anywhere near 255 variables, so we'll be alright.
			return;
		}

		for (short i = 0; i < variableCount; i++) {
			short combinedVariableInfo = (short) codePoints[position++];
			int variableSize = ((combinedVariableInfo & 0x80) != 0) ? 2 : 1;
			short variableId = (short) (combinedVariableInfo & 0x7F);
			int current = codePoints[position++];    // Read 8-bit value
			int variable;

			switch (variableSize) {
				case 2:
					// Read 16-bit value
					byte lowByte = (byte) current;
					byte hiByte = (byte) codePoints[position++];
					position++;    // Skip escape

					current = ((hiByte << 8) | lowByte) & 0xFF;
					break;
			}

			if (current == 0xFF) {    // This marks an escaped character to follow
				int next = codePoints[position++];

				switch (next) {
					case 0x01:    // Value is 0
					default:
						variable = 0;
						break;
					case 0x02:    // Value is 255
						variable = 0xFF;
						break;
					case 0x03:    // We shouldn't be meeting an end here. Malformed input.
						Log.w("Unexpected end of text in CustomizationString, assuming corruption!");
						clear();    // In this case, we'll want to clear whatever we've loaded as it might be corrupted
						return;
				}
			} else {
				variable = current;
			}

			String variableName = VAR_ID_TO_NAME.get(variableId);

			if (variableName == null) {    // Variable ID matched no variable name.
				Log.w("Variable ID %d had no name associated", variableId);
				position++;    // Skip the associated value
				continue;
			}

			variables.put(variableName, variable);
		}

		if ((position + 2) < codePoints.length) {
			Log.w("Too much data remaining in CustomizationString, assuming corruption!");
			clear();    // In this case, we'll want to clear whatever we've loaded as it might be corrupted
			return;
		}

		int escapeFlag = codePoints[position++];
		int endOfText = codePoints[position];

		if (escapeFlag == 0xFF && endOfText != 0x03) {
			Log.w("Invalid UTF-8 ending for CustomizationString, assuming corruption!");
			clear();    // In this case, we'll want to clear whatever we've loaded as it might be corrupted
		}
	}

	@Override
	public byte @NotNull [] encode() {
		if (isEmpty()) {
			// No need to send more than an empty array in this case
			return ByteBuffer.allocate(Short.BYTES).array();
		}

		int encodableLength = getLength();
		ByteArrayOutputStream out = new ByteArrayOutputStream(encodableLength - Short.BYTES);

		try {
			out.write(2);    // Marks start of text
			addCustomizationStringByte(out, variables.size());

			variables.forEach((variableName, variableValue) -> {
				int variableId = VAR_NAME_TO_ID.get(variableName);
				boolean variableValueOneByte = variableValue >= 0 && variableValue < 128;
				if (!variableValueOneByte)
					variableId |= 0x80;

				try {
					out.write(variableId);
					if (variableValueOneByte) {
						addCustomizationStringByte(out, variableValue);
					} else {
						addCustomizationStringByte(out, variableValue & 0xFF);
						addCustomizationStringByte(out, (variableValue >> 8) & 0xFF);
					}
				} catch (Exception e) {
					Log.e(e);
				}
			});

			out.write(0xFF);    // Escape
			out.write(3);    // Marks end of text
			out.flush();

			byte[] result = out.toByteArray();
			NetBuffer data = NetBuffer.allocate(encodableLength);

			data.addArray(result);    // This will add the array length in little endian order

			return data.array();
		} catch (IOException e) {
			Log.e(e);
			return NetBuffer.allocate(Short.SIZE).array();    // Returns an array of 0x00, 0x00 indicating that it's empty
		}
	}

	private void addCustomizationStringByte(ByteArrayOutputStream out, int c) throws IOException {
		assert(c >= 0 && c <= 0xFF);
		switch (c) {
			case 0x00 -> {
				out.write(0xFF);    // Escape
				out.write(0x01);    // Put variable value
			}
			case 0xFF -> {
				out.write(0xFF);    // Escape
				out.write(0x02);    // Put variable value
			}
			default -> out.write(c);
		}
	}

	@Override
	public int getLength() {
		int length = Short.BYTES;    // Array size declaration field

		length += 3; // UTF-8 start of text, escape, UTF-8 end of text
		length += getValueLength(variables.size()); // variable count
		for (Integer i : variables.values()) {
			length += 1; // variableId
			int firstByte = i & 0xFF;
			int secondByte = (i >> 8) & 0xFF;
			length += getValueLength(firstByte);
			if (i < 0 || i >= 128) { // signed short
				length += getValueLength(secondByte);
			}
		}

		return length;
	}

	private static int getValueLength(int c) {
		if (c == 0x00 || c == 0xFF)
			return 2;
		return 1;
	}

}
