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

import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.projectswg.common.encoding.StringType;

public class NetBufferStream extends OutputStream {
	
	private final Object expansionMutex;
	private final Object bufferMutex;
	private NetBuffer buffer;
	private int capacity;
	private int size;
	private int mark;
	
	public NetBufferStream() {
		this(1024);
	}
	
	public NetBufferStream(int size) {
		if (size <= 0)
			throw new NegativeArraySizeException("Size cannot be less than or equal to 0!");
		this.expansionMutex = new Object();
		this.bufferMutex = new Object();
		this.buffer = NetBuffer.allocate(size);
		this.capacity = size;
		this.size = 0;
		this.mark = 0;
	}
	
	@Override
	public void close() {
		reset();
	}
	
	@Override
	public void flush() {
		
	}
	
	/**
	 * Sets the mark to the buffer's current position
	 */
	public void mark() {
		synchronized (bufferMutex) {
			mark = buffer.position();
		}
	}
	
	/**
	 * Rewinds the buffer to the previously set mark
	 */
	public void rewind() {
		synchronized (bufferMutex) {
			buffer.position(mark);
			mark = 0;
		}
	}
	
	/**
	 * Resets the buffer to the default capacity and clears all data
	 */
	public void reset() {
		synchronized (expansionMutex) {
			synchronized (bufferMutex) {
				buffer = NetBuffer.allocate(1024);
				capacity = 1024;
				size = 0;
				mark = 0;
			}
		}
	}
	
	@Override
	public void write(int b) {
		ensureCapacity(size + 1);
		synchronized (bufferMutex) {
			buffer.array()[size] = (byte) b;
			size++;
		}
	}
	
	@Override
	public void write(byte [] data) {
		write(data, 0, data.length);
	}
	
	@Override
	public void write(byte [] data, int offset, int length) {
		ensureCapacity(size + length);
		synchronized (bufferMutex) {
			System.arraycopy(data, offset, buffer.array(), size, length);
			size += length;
		}
	}
	
	public void write(ByteBuffer data) {
		ensureCapacity(size + data.remaining());
		synchronized (bufferMutex) {
			while (data.hasRemaining()) {
				buffer.array()[size++] = data.get();
			}
		}
	}
	
	/**
	 * Moves all data from the buffer's current position to position 0. This
	 * method also adjusts the mark to be pointing to the same data
	 */
	public void compact() {
		synchronized (bufferMutex) {
			byte [] data = buffer.array();
			for (int i = buffer.position(), j = 0; i < size; ++i, ++j) {
				data[j] = data[i];
			}
			size -= buffer.position();
			mark -= buffer.position();
			buffer.position(0);
		}
	}
	
	public int remaining() {
		synchronized (bufferMutex) {
			return size - buffer.position();
		}
	}
	
	public boolean hasRemaining() {
		return remaining() > 0;
	}
	
	public int position() {
		synchronized (bufferMutex) {
			return buffer.position();
		}
	}
	
	public void position(int position) {
		synchronized (bufferMutex) {
			buffer.position(position);
		}
	}
	
	public void seek(int relative) {
		synchronized (bufferMutex) {
			buffer.seek(relative);
		}
	}
	
	public ByteBuffer getBuffer() {
		synchronized (bufferMutex) {
			return buffer.getBuffer();
		}
	}
	
	public boolean getBoolean() {
		synchronized (bufferMutex) {
			return buffer.getBoolean();
		}
	}
	
	public Object getGeneric(Class<?> type) {
		synchronized (bufferMutex) {
			return buffer.getGeneric(type);
		}
	}
	
	public String getString(StringType type) {
		switch (type) {
			case ASCII:
				return getAscii();
			case UNICODE:
				return getUnicode();
			default:
				throw new IllegalArgumentException("Unknown string type: " + type);
		}
	}
	
	public String getAscii() {
		synchronized (bufferMutex) {
			return buffer.getAscii();
		}
	}
	
	public String getUnicode() {
		synchronized (bufferMutex) {
			return buffer.getUnicode();
		}
	}
	
	public byte getByte() {
		synchronized (bufferMutex) {
			return buffer.getByte();
		}
	}
	
	public short getShort() {
		synchronized (bufferMutex) {
			return buffer.getShort();
		}
	}
	
	public int getInt() {
		synchronized (bufferMutex) {
			return buffer.getInt();
		}
	}
	
	public float getFloat() {
		synchronized (bufferMutex) {
			return buffer.getFloat();
		}
	}
	
	public double getDouble() {
		synchronized (bufferMutex) {
			return buffer.getDouble();
		}
	}
	
	public long getLong() {
		synchronized (bufferMutex) {
			return buffer.getLong();
		}
	}
	
	public short getNetShort() {
		synchronized (bufferMutex) {
			return buffer.getNetShort();
		}
	}
	
	public int getNetInt() {
		synchronized (bufferMutex) {
			return buffer.getNetInt();
		}
	}
	
	public long getNetLong() {
		synchronized (bufferMutex) {
			return buffer.getNetLong();
		}
	}
	
	public byte[] getArray() {
		synchronized (bufferMutex) {
			return buffer.getArray();
		}
	}
	
	public byte[] getArrayLarge() {
		synchronized (bufferMutex) {
			return buffer.getArrayLarge();
		}
	}
	
	public byte[] getArray(int size) {
		synchronized (bufferMutex) {
			return buffer.getArray(size);
		}
	}
	
	public void getList(NumericalIterator ni) {
		int size = getInt();
		for (int i = 0; i < size; i++)
			ni.onItem(i);
	}
	
	public void addBoolean(boolean b) {
		ensureCapacity(size+1);
		synchronized (bufferMutex) {
			buffer.addBoolean(b);
			size++;
		}
	}
	
	public void addString(String s, StringType type) {
		switch (type) {
			case ASCII:
				addAscii(s);
				break;
			case UNICODE:
				addUnicode(s);
				break;
			default:
				throw new IllegalArgumentException("Unknown string type: " + type);
		}
	}
	
	public void addAscii(String s) {
		ensureCapacity(size+2+s.length());
		synchronized (bufferMutex) {
			buffer.addAscii(s);
			size += 2 + s.length();
		}
	}
	
	public void addUnicode(String s) {
		ensureCapacity(size+4+s.length()*2);
		synchronized (bufferMutex) {
			buffer.addUnicode(s);
			size += 4 + s.length()*2;
		}
	}
	
	public void addLong(long l) {
		ensureCapacity(size+8);
		synchronized (bufferMutex) {
			buffer.addLong(l);
			size += 8;
		}
	}
	
	public void addInt(int i) {
		ensureCapacity(size+4);
		synchronized (bufferMutex) {
			buffer.addInt(i);
			size += 4;
		}
	}
	
	public void addFloat(float f) {
		ensureCapacity(size+4);
		synchronized (bufferMutex) {
			buffer.addFloat(f);
			size += 4;
		}
	}
	
	public void addDouble(double d) {
		ensureCapacity(size+8);
		synchronized (bufferMutex) {
			buffer.addDouble(d);
			size += 8;
		}
	}
	
	public void addShort(int i) {
		ensureCapacity(size+2);
		synchronized (bufferMutex) {
			buffer.addShort(i);
			size += 2;
		}
	}
	
	public void addNetLong(long l) {
		ensureCapacity(size+8);
		synchronized (bufferMutex) {
			buffer.addNetLong(l);
			size += 8;
		}
	}
	
	public void addNetInt(int i) {
		ensureCapacity(size+4);
		synchronized (bufferMutex) {
			buffer.addNetInt(i);
			size += 4;
		}
	}
	
	public void addNetShort(int i) {
		ensureCapacity(size+2);
		synchronized (bufferMutex) {
			buffer.addNetShort(i);
			size += 2;
		}
	}
	
	public void addByte(int b) {
		ensureCapacity(size+1);
		synchronized (bufferMutex) {
			buffer.addByte(b);
			size++;
		}
	}
	
	public void addArray(byte[] b) {
		ensureCapacity(size+2+b.length);
		synchronized (bufferMutex) {
			buffer.addArray(b);
			size += 2 + b.length;
		}
	}
	
	public <T> void addList(List<T> l, ListIterable<T> r) {
		addInt(l.size());
		for (T t : l)
			r.onItem(t);
	}
	
	public <T> void addList(Set<T> s, ListIterable<T> r) {
		addInt(s.size());
		for (T t : s)
			r.onItem(t);
	}
	
	public <K, V> void addMap(Map<K, V> m, MapIterable<K, V> r) {
		addInt(m.size());
		for (Entry<K, V> e : m.entrySet())
			r.onItem(e);
	}
	
	public void addRawArray(byte[] b) {
		write(b);
	}
	
	public byte [] array() {
		synchronized (bufferMutex) {
			return buffer.array();
		}
	}
	
	public int size() {
		return size;
	}
	
	public int capacity() {
		return capacity;
	}
	
	private void ensureCapacity(int size) {
		if (size <= capacity)
			return;
		synchronized (expansionMutex) {
			while (size > capacity)
				capacity <<= 2;
			synchronized (bufferMutex) {
				NetBuffer buf = NetBuffer.allocate(capacity);
				System.arraycopy(buffer.array(), 0, buf.array(), 0, this.size);
				buf.position(buffer.position());
				this.buffer = buf;
			}
		}
	}
	
	public static interface ListIterable<T> {
		void onItem(T item);
	}
	
	public static interface MapIterable<K, V> {
		void onItem(Entry<K, V> item);
	}
	
	public static interface NumericalIterator {
		void onItem(int index);
	}
	
}
