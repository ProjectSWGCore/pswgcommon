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
package com.projectswg.common.data.customization;

import com.projectswg.common.data.encodables.mongo.MongoData;
import com.projectswg.common.data.encodables.mongo.MongoPersistable;
import com.projectswg.common.encoding.Encodable;
import com.projectswg.common.network.NetBuffer;
import com.projectswg.common.network.NetBufferStream;
import com.projectswg.common.persistable.Persistable;
import me.joshlarson.jlcommon.log.Log;

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
public class CustomizationString implements Encodable, Persistable, MongoPersistable {
	
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
		this.variables = Collections.synchronizedMap(new LinkedHashMap<>());	// Ordered and synchronized
	}
	
	boolean isEmpty() {
		return variables.isEmpty();
	}
	
	/**
	 *
	 * @return the amount of characters that would require escaping to be valid UTF-8
	 */
	int valuesToEscape() {
		return (int) variables.values().stream().filter(CustomizationString::isReserved).count();
	}
	
	@Override
	public void save(NetBufferStream stream) {
		stream.addByte(0);
		stream.addMap(variables, (entry) -> {
			stream.addAscii(entry.getKey());
			stream.addInt(entry.getValue());
		});
	}
	
	@Override
	public void read(NetBufferStream stream) {
		stream.getByte();
		stream.getList((i) -> variables.put(stream.getAscii(), stream.getInt()));
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
		int[] codePoints = string.codePoints().toArray();	// Replaces 0xC3BF with 0xFF, because 0xFF is reserved as an escape flag
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
			int current = codePoints[position++];	// Read 8-bit value
			int variable;
			
			switch (variableSize) {
				case 2:
					// Read 16-bit value
					byte lowByte = (byte) current;
					byte hiByte = (byte) codePoints[position++];
					position++;	// Skip escape
					
					current = ((hiByte << 8) | lowByte) & 0xFF;
					break;
			}
			
			if (current == 0xFF) {	// This marks an escaped character to follow
				int next = codePoints[position++];
				
				switch (next) {
					case 0x01:	// Value is 0
					default:
						variable = 0;
						break;
					case 0x02:	// Value is 255
						variable = 0xFF;
						break;
					case 0x03:	// We shouldn't be meeting an end here. Malformed input.
						Log.w("Unexpected end of text in CustomizationString, assuming corruption!");
						clear();	// In this case, we'll want to clear whatever we've loaded as it might be corrupted
						return;
				}
			} else {
				variable = current;
			}
			
			String variableName = VAR_ID_TO_NAME.get(variableId);
			
			if (variableName == null) {	// Variable ID matched no variable name.
				Log.w("Variable ID %d had no name associated", variableId);
				position++;	// Skip the associated value
				continue;
			}
			
			variables.put(variableName, variable);
		}
		
		if ((position + 2) < codePoints.length) {
			Log.w("Too much data remaining in CustomizationString, assuming corruption!");
			clear();	// In this case, we'll want to clear whatever we've loaded as it might be corrupted
			return;
		}
		
		int escapeFlag = codePoints[position++];
		int endOfText = codePoints[position];
		
		if (escapeFlag == 0xFF && endOfText != 0x03) {
			Log.w("Invalid UTF-8 ending for CustomizationString, assuming corruption!");
			clear();	// In this case, we'll want to clear whatever we've loaded as it might be corrupted
		}
	}

	@Override
	public byte[] encode() {
		if (isEmpty()) {
			// No need to send more than an empty array in this case
			return ByteBuffer.allocate(Short.BYTES).array();
		}
		
		ByteArrayOutputStream out = new ByteArrayOutputStream(getLength());
		Writer writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
		
		try {
			out.write(2);	// Marks start of text
			out.write(variables.size());
			
			variables.forEach((variableName, variable) -> {
				short combinedVariable = VAR_NAME_TO_ID.get(variableName);
				
				try {
					writer.write(combinedVariable);    // Put variable
					
					switch (variable) {
						case 0x00:
							writer.write(0xFF);    // Escape
							writer.write(0x01);    // Put variable value
							break;
						case 0xFF:
							writer.write(0xFF);    // Escape
							writer.write(0x02);    // Put variable value
							break;
						default:
							writer.write(variable);
							break;
					}
				} catch (Exception e) {
					Log.e(e);
				}
			});
			
			writer.write(0x0FF);	// Escape
			writer.write(3);	// Marks end of text
			writer.flush();
			
			byte[] result = out.toByteArray();
			NetBuffer data = NetBuffer.allocate(Short.BYTES + result.length);
			
			data.addArray(result);	// This will add the array length in little endian order
			
			return data.array();
		} catch (IOException e) {
			Log.e(e);
			return NetBuffer.allocate(Short.SIZE).array();	// Returns an array of 0x00, 0x00 indicating that it's empty
		}
	}
	
	@Override
	public int getLength() {
		int length = Short.BYTES;	// Array size declaration field
		
		if (isEmpty()) {	// No need to send more than an empty array in this case
			return length;
		}
		
		length += 1;	// UTF-8 start of text
		length += 1;	// Variable count
		length += variables.size() * 2;	// Amount of variable IDs and their value
		length += valuesToEscape() * 2;	// If there are escaped values in there, there will be 0xC3 0xBF to indicate escape
		length += 2;	//	Escape flag
		length += 1;	//	UTF-8 end of text
		
		return length;
	}
	
	/**
	 *
	 * @param value the value to check
	 * @return {@code true} if the value is reserved by UTF-8 and escaping it
	 * would be necessary for proper compatibility.
	 */
	private static boolean isReserved(int value) {
		return value == 0x00 || value == 0xFF;
	}
	
}
