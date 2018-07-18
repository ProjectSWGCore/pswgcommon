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

package com.projectswg.common.data;

import com.projectswg.common.encoding.CachedEncode;
import com.projectswg.common.encoding.Encodable;
import com.projectswg.common.encoding.Encoder;
import com.projectswg.common.encoding.StringType;
import com.projectswg.common.network.NetBuffer;
import com.projectswg.common.network.NetBufferStream;
import com.projectswg.common.persistable.Persistable;
import me.joshlarson.jlcommon.log.Log;

import java.util.Objects;

public class Pair<T, S> implements Encodable, Persistable {
	
	private final StringType leftType;
	private final StringType rightType;
	private final Class<T> leftClass;
	private final Class<S> rightClass;
	private final CachedEncode cache;
	
	private T left;
	private S right;
	
	private Pair(T left, S right, Class<T> leftClass, Class<S> rightClass, StringType leftType, StringType rightType) {
		// Final checks - has to be either one or the other. These ensure other assumptions in the class will succeed
		if ((leftType == null && !(left instanceof String) && !leftClass.equals(String.class)) == (leftType != null && (left instanceof String) && leftClass.equals(String.class)))
			throw new IllegalArgumentException("Invalid left arguments");
		if ((rightType == null && !(right instanceof String) && !rightClass.equals(String.class)) == (rightType != null && (right instanceof String) && rightClass.equals(String.class)))
			throw new IllegalArgumentException("Invalid right arguments");
		this.leftClass = leftClass;
		this.rightClass = rightClass;
		this.leftType = leftType;
		this.rightType = rightType;
		this.cache = new CachedEncode(this::encodeImpl);
		setLeft(left);
		setRight(right);
	}
	
	public T getLeft() {
		return left;
	}
	
	public S getRight() {
		return right;
	}
	
	public void setLeft(T val1) {
		this.left = val1;
		cache.clearCached();
	}
	
	public void setRight(S val2) {
		this.right = val2;
		cache.clearCached();
	}
	
	@Override
	public void decode(NetBuffer data) {
		setLeft(smartDecode(data, leftClass, leftType));
		setRight(smartDecode(data, rightClass, rightType));
	}
	
	@Override
	public byte[] encode() {
		Objects.requireNonNull(left, "left is null in encode()");
		Objects.requireNonNull(right, "right is null in encode()");
		return cache.encode();
	}
	
	@Override
	public int getLength() {
		return cache.encode().length;
	}
	
	@Override
	public void save(NetBufferStream stream) {
		smartSave(stream, left, leftType);
		smartSave(stream, right, rightType);
	}
	
	@Override
	public void read(NetBufferStream stream) {
		smartRead(stream, left, leftClass, leftType);
		smartRead(stream, right, rightClass, rightType);
	}
	
	private byte [] encodeImpl() {
		if (left == null || right == null)
			return new byte[0];
		
		byte [] left = smartEncode(this.left, leftType);
		byte [] right = smartEncode(this.right, rightType);
		byte [] combined = new byte[left.length + right.length];
		System.arraycopy(left, 0, combined, 0, left.length);
		System.arraycopy(right, 0, combined, left.length, right.length);
		return combined;
	}
	
	@SuppressWarnings("unchecked") // should succeed based on checks in constructor
	private static <U> U smartDecode(NetBuffer data, Class<U> klass, StringType strType) {
		if (strType != null)
			return (U) data.getString(strType);
		return (U) data.getGeneric(klass);
	}
	
	private static byte [] smartEncode(Object obj, StringType strType) {
		if (strType != null)
			return Encoder.encode(obj, strType);
		return Encoder.encode(obj);
	}
	
	@SuppressWarnings("unchecked") // should succeed based on checks in constructor
	private static <U> U smartRead(NetBufferStream data, U obj, Class<U> klass, StringType strType) {
		if (strType != null)
			return (U) data.getString(strType);
		
		// Try making our own persistable
		if (obj == null && Persistable.class.isAssignableFrom(klass)) {
			try {
				obj = klass.getConstructor().newInstance();
			} catch (Exception e) {
				Log.e(e);
			}
		}
		
		// If we succeeded, read it - if not, try grabbing some generic
		if (obj instanceof Persistable) {
			((Persistable) obj).read(data);
		} else {
			obj = (U) data.getGeneric(klass);
		}
		return obj;
	}
	
	private static void smartSave(NetBufferStream data, Object obj, StringType strType) {
		if (strType != null) {
			data.addString((String) obj, strType);
		} else if (obj instanceof Persistable) {
			((Persistable) obj).save(data);
		} else {
			data.write(Encoder.encode(obj));
		}
	}
	
	@SuppressWarnings("unchecked") // it's pretty obvious T.getClass() should be Class<T>
	public static <T, S> Pair<T, S> createPair(T left, S right) {
		Objects.requireNonNull(left, "Left cannot be null in this method! Instead call createPair(left, right, leftClass, rightClass)");
		Objects.requireNonNull(right, "Right cannot be null in this method! Instead call createPair(left, right, leftClass, rightClass)");
		return createPair(left, right, (Class<T>) left.getClass(), (Class<S>) right.getClass());
	}
	
	public static <T, S> Pair<T, S> createPair(T left, S right, Class<T> leftClass, Class<S> rightClass) {
		if (!(left instanceof String))
			throw new IllegalArgumentException("Invalid left argument");
		if (!(right instanceof String))
			throw new IllegalArgumentException("Invalid right argument");
		return new Pair<>(left, right, leftClass, rightClass, null, null);
	}
	
	@SuppressWarnings("unchecked") // it's pretty obvious T.getClass() should be Class<T>
	public static <T> Pair<String, T> createPair(String left, T right, StringType type) {
		Objects.requireNonNull(right, "Right cannot be null in this method! Instead call createPair(left, right, type, rightClass)");
		return createPair(left, right, type, (Class<T>) right.getClass());
	}
	
	public static <T> Pair<String, T> createPair(String left, T right, StringType type, Class<T> rightClass) {
		if (!(right instanceof String))
			throw new IllegalArgumentException("Invalid right argument");
		return new Pair<>(left, right, String.class, rightClass, type, null);
	}
	
	@SuppressWarnings("unchecked") // it's pretty obvious T.getClass() should be Class<T>
	public static <T> Pair<T, String> createPair(T left, String right, StringType type) {
		Objects.requireNonNull(left, "Left cannot be null in this method! Instead call createPair(left, right, leftClass, type)");
		return createPair(left, right, (Class<T>) left.getClass(), type);
	}
	
	public static <T> Pair<T, String> createPair(T left, String right, Class<T> leftClass, StringType type) {
		if (!(left instanceof String))
			throw new IllegalArgumentException("Invalid left argument");
		return new Pair<>(left, right, leftClass, String.class, null, type);
	}
	
	/**
	 * Creates a pair with two strings
	 * @param left the left string, can be null
	 * @param right the right string, can be null
	 * @param leftType the type of the left string: ASCII/UNICODE
	 * @param rightType the type of the right string: ASCII/UNICODE
	 * @return the corresponding pair
	 */
	public static Pair<String, String> createPair(String left, String right, StringType leftType, StringType rightType) {
		return new Pair<>(left, right, String.class, String.class, leftType, rightType);
	}
	
}
