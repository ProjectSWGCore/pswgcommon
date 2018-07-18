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
package com.projectswg.common.network;

import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import me.joshlarson.jlcommon.log.Log;
import com.projectswg.common.encoding.Encodable;
import com.projectswg.common.encoding.StringType;


public class NetBuffer {
	
	public final Charset ASCII   = Charset.forName("UTF-8");
	public final Charset UNICODE = Charset.forName("UTF-16LE");
	
	private final ByteBuffer data;
	private final int size;
	
	private NetBuffer(ByteBuffer data) {
		this.data = data;
		this.size = data.array().length;
	}
	
	public static final NetBuffer allocate(int size) {
		return new NetBuffer(ByteBuffer.allocate(size));
	}
	
	public static final NetBuffer wrap(byte [] data) {
		return new NetBuffer(ByteBuffer.wrap(data));
	}
	
	public static final NetBuffer wrap(ByteBuffer data) {
		return new NetBuffer(data);
	}
	
	public boolean hasRemaining() {
		return data.hasRemaining();
	}
	
	public int remaining() {
		return data.remaining();
	}
	
	public int position() {
		return data.position();
	}
	
	public int limit() {
		return data.limit();
	}
	
	public int capacity() {
		return data.capacity();
	}
	
	public void position(int position) {
		data.position(position);
	}
	
	public void seek(int relative) {
		data.position(data.position()+relative);
	}
	
	public void flip() {
		data.flip();
	}
	
	public ByteBuffer getBuffer() {
		return data;
	}
	
	public void add(NetBuffer buffer) {
		add(buffer.getBuffer());
	}
	
	public void add(ByteBuffer buffer) {
		data.put(buffer);
	}
	
	public void addBoolean(boolean b) {
		data.put(b ? (byte)1 : (byte)0);
	}
	
	public void addAscii(String s) {
		addArray(s.getBytes(ASCII));
	}
	
	public void addUnicode(String s) {
		addInt(s.length());
		data.put(s.getBytes(UNICODE));
	}
	
	public void addLong(long l) {
		data.order(ByteOrder.LITTLE_ENDIAN).putLong(l);
	}
	
	public void addInt(int i) {
		data.order(ByteOrder.LITTLE_ENDIAN).putInt(i);
	}
	
	public void addFloat(float f) {
		data.order(ByteOrder.LITTLE_ENDIAN).putFloat(f);
	}
	
	public void addDouble(double d) {
		data.order(ByteOrder.LITTLE_ENDIAN).putDouble(d);
	}
	
	public void addShort(int i) {
		data.order(ByteOrder.LITTLE_ENDIAN).putShort((short)i);
	}
	
	public void addNetLong(long l) {
		data.order(ByteOrder.BIG_ENDIAN).putLong(l);
	}
	
	public void addNetInt(int i) {
		data.order(ByteOrder.BIG_ENDIAN).putInt(i);
	}
	
	public void addNetShort(int i) {
		data.order(ByteOrder.BIG_ENDIAN).putShort((short)i);
	}
	
	public void addByte(int b) {
		data.put((byte)b);
	}
	
	public void addArray(byte [] b) {
		addShort(b.length);
		data.put(b);
	}
	
	public void addArrayLarge(byte [] b) {
		addInt(b.length);
		data.put(b);
	}
	
	public void addRawArray(byte [] b) {
		data.put(b);
	}
	
	public void addList(Collection<? extends Encodable> list) {
		if (list == null) {
			addInt(0);
			return;
		}
		
		addInt(list.size());
		for (Encodable encodable : list) {
			addEncodable(encodable);
		}
	}
	
	public void addList(List<String> list, StringType type) {
		addInt(list.size());
		
		switch (type) {
			case ASCII:
				for (String s : list) {
					addAscii(s);
				}
				break;
			case UNICODE:
				for (String s : list) {
					addUnicode(s);
				}
				break;
			default:
				Log.e("Cannot encode StringType " + type);
				break;
		}
	}
	
	public void addEncodable(Encodable e) {
		data.put(e.encode());
	}
	
	public boolean getBoolean() {
		return getByte() == 1;
	}
	
	public String getAscii() {
		return new String(getArray(), ASCII);
	}
	
	public String getUnicode() {
		return new String(getArray(getInt() * 2), UNICODE);
	}
	
	public String getString(StringType type) {
		if (type == StringType.ASCII)
			return getAscii();
		if (type == StringType.UNICODE)
			return getUnicode();
		throw new IllegalArgumentException("Unknown StringType: " + type);
	}
	
	public byte getByte() {
		return data.get();
	}
	
	public short getShort() {
		return data.order(ByteOrder.LITTLE_ENDIAN).getShort();
	}
	
	public int getInt() {
		return data.order(ByteOrder.LITTLE_ENDIAN).getInt();
	}
	
	public float getFloat() {
		return data.getFloat();
	}
	
	public double getDouble() {
		return data.getDouble();
	}
	
	public long getLong() {
		return data.order(ByteOrder.LITTLE_ENDIAN).getLong();
	}
	
	public short getNetShort() {
		return data.order(ByteOrder.BIG_ENDIAN).getShort();
	}
	
	public int getNetInt() {
		return data.order(ByteOrder.BIG_ENDIAN).getInt();
	}
	
	public long getNetLong() {
		return data.order(ByteOrder.BIG_ENDIAN).getLong();
	}
	
	public byte [] getArray() {
		return getArray(getShort());
	}
	
	public byte [] getArrayLarge() {
		return getArray(getInt());
	}
	
	public byte [] getArray(int size) {
		byte [] bData = new byte[size];
		data.get(bData);
		return bData;
	}
	
	public int [] getIntArray() {
		int [] ints = new int[getInt()];
		for (int i = 0; i < ints.length; i++)
			ints[i] = getInt();
		return ints;
	}
	
	public int [] getIntArray(int size) {
		int [] ints = new int[size];
		for (int i = 0; i < ints.length; i++)
			ints[i] = getInt();
		return ints;
	}
	
	public boolean[] getBooleanArray() {
		boolean[] booleans = new boolean[getInt()];
		for(int i = 0; i < booleans.length; i++)
			booleans[i] = getBoolean();
		return booleans;
	}
	
	public Object getGeneric(Class<?> type) {
		if (Encodable.class.isAssignableFrom(type)) {
			Object instance = null;
			try {
				instance = type.getConstructor().newInstance();
				((Encodable) instance).decode(this);
			} catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
				Log.e(e);
			}

			return instance;
		} else if (Integer.class.isAssignableFrom(type) || Integer.TYPE.isAssignableFrom(type))
			return getInt();
		else if (Long.class.isAssignableFrom(type) || Long.TYPE.isAssignableFrom(type))
			return getLong();
		else if (Float.class.isAssignableFrom(type) || Float.TYPE.isAssignableFrom(type))
			return getFloat();
		else if (StringType.ASCII.getClass().isAssignableFrom(type))
			return getAscii();
		else if (StringType.UNICODE.getClass().isAssignableFrom(type))
			return getAscii();
		return null;
	}
	
	public <T extends Encodable> List<T> getList(Class<T> type) {
		int size = getInt();
		
		if (size < 0) {
			Log.e("Read list with size less than zero!");
			return null;
		} else if (size == 0) {
			return new ArrayList<>();
		}
		
		List<T> list = new ArrayList<>();
		
		try {
			for (int i = 0; i < size; i++) {
				T instance = type.getConstructor().newInstance();
				instance.decode(this);
				list.add(instance);
			}
		} catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
			Log.e(e);
		}
		
		if (size != list.size())
			Log.e("Expected list size %d but only have %d elements in the list", size, list.size());
		return list;
	}
	
	public List<String> getList(StringType type) {
		int size = getInt();
		
		if (size < 0) {
			Log.e("Read list with size less than zero!");
			return null;
		} else if (size == 0) {
			return new ArrayList<>();
		}
		
		List<String> list = new ArrayList<>();
		
		switch (type) {
			case ASCII:
				for (int i = 0; i < size; i++) {
					list.add(getAscii());
				}
				break;
			case UNICODE:
				for (int i = 0; i < size; i++) {
					list.add(getUnicode());
				}
				break;
			default:
				Log.e("Do not know how to read list of StringType " + type);
				break;
		}
		
		return list;
	}
	
	public <T extends Encodable> T getEncodable(Class<T> type) {
		T instance = null;
		try {
			instance = type.getConstructor().newInstance();
			instance.decode(this);
		} catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
			Log.e(e);
		}
		
		return instance;
	}
	
	public byte [] array() {
		return data.array();
	}
	
	public int size() {
		return size;
	}
	
	public byte [] copyArray() {
		return copyArray(0, size);
	}
	
	public byte [] copyArray(int offset, int length) {
		if (length < 0)
			throw new IllegalArgumentException("Length cannot be less than 0!");
		if (offset+length > size)
			throw new IllegalArgumentException("Length extends past the end of the array!");
		byte [] ret = new byte[length];
		System.arraycopy(array(), offset, ret, 0, length);
		return ret;
	}
	
}
