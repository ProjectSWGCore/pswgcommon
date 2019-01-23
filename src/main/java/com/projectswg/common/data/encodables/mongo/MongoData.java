/***********************************************************************************
 * Copyright (c) 2019 /// Project SWG /// www.projectswg.com                       *
 *                                                                                 *
 * ProjectSWG is the first NGE emulator for Star Wars Galaxies founded on          *
 * July 7th, 2011 after SOE announced the official shutdown of Star Wars Galaxies. *
 * Our goal is to create an emulator which will provide a server for players to    *
 * continue playing a game similar to the one they used to play. We are basing     *
 * it on the final publish of the game prior to end-game events.                   *
 *                                                                                 *
 * This file is part of Holocore.                                                  *
 *                                                                                 *
 * --------------------------------------------------------------------------------*
 *                                                                                 *
 * Holocore is free software: you can redistribute it and/or modify                *
 * it under the terms of the GNU Affero General Public License as                  *
 * published by the Free Software Foundation, either version 3 of the              *
 * License, or (at your option) any later version.                                 *
 *                                                                                 *
 * Holocore is distributed in the hope that it will be useful,                     *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                  *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                   *
 * GNU Affero General Public License for more details.                             *
 *                                                                                 *
 * You should have received a copy of the GNU Affero General Public License        *
 * along with Holocore.  If not, see <http://www.gnu.org/licenses/>.               *
 ***********************************************************************************/

package com.projectswg.common.data.encodables.mongo;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Function;

public class MongoData {
	
	private final @NotNull Document doc;
	
	public MongoData() {
		this(new Document());
	}
	
	public MongoData(Document doc) {
		this.doc = doc == null ? new Document() : doc;
	}
	
	public Document toDocument() {
		return new Document(doc);
	}
	
	public boolean containsKey(String key) {
		return doc.containsKey(key);
	}
	
	@Nullable
	public Integer getInteger(String key) {
		return doc.getInteger(key);
	}
	
	public int getInteger(String key, int defaultValue) {
		return doc.getInteger(key, defaultValue);
	}
	
	@Nullable
	public Long getLong(String key) {
		return doc.getLong(key);
	}
	
	public long getLong(String key, long defaultValue) {
		Long l = doc.getLong(key);
		return l == null ? defaultValue : l;
	}
	
	@Nullable
	public Float getFloat(String key) {
		Double f = doc.getDouble(key);
		return f == null ? null : f.floatValue();
	}
	
	public float getFloat(String key, float defaultValue) {
		Double f = doc.getDouble(key);
		return f == null ? defaultValue : f.floatValue();
	}
	
	@Nullable
	public Double getDouble(String key) {
		return doc.getDouble(key);
	}
	
	public double getDouble(String key, double defaultValue) {
		Double d = doc.getDouble(key);
		return d == null ? defaultValue : d;
	}
	
	@Nullable
	public String getString(String key) {
		return doc.getString(key);
	}
	
	public String getString(String key, String defaultValue) {
		String str = doc.getString(key);
		return str == null ? defaultValue : str;
	}
	
	@Nullable
	public Boolean getBoolean(String key) {
		return doc.getBoolean(key);
	}
	
	public boolean getBoolean(String key, boolean defaultValue) {
		return doc.getBoolean(key, defaultValue);
	}
	
	@Nullable
	public ObjectId getObjectId(String key) {
		return doc.getObjectId(key);
	}
	
	@Nullable
	public Instant getDate(String key) {
		@SuppressWarnings("UseOfObsoleteDateTimeApi") // forced to by MongoDB
		Date d = doc.getDate(key);
		return d == null ? null : d.toInstant();
	}
	
	@NotNull
	public <T> List<T> getArray(String key, Class<T> klass) {
		List<?> mdbArray = doc.get(key, List.class);
		List<T> ret = new ArrayList<>(mdbArray.size());
		if (klass == Instant.class) {
			for (Object o : mdbArray) {
				if (o == null)
					ret.add(null);
				else
					//noinspection UseOfObsoleteDateTimeApi
					ret.add(klass.cast(((Date) o).toInstant()));
			}
		} else {
			for (Object o : mdbArray) {
				if (o == null)
					ret.add(null);
				else
					ret.add(klass.cast(o));
			}
		}
		return ret;
	}
	
	@NotNull
	public MongoData getDocument(String key) {
		return new MongoData(doc.get(key, Document.class));
	}
	
	/**
	 * Attempts to parse the document at the specified key.  If the document exists, it will be parsed by the specified data instance.
	 * @param key the key to look up
	 * @param dataInstance the data instance to use
	 * @param <T> the parser type
	 * @return the parsed data, or null if the value does not exist
	 */
	@Nullable
	public <T extends MongoPersistable> T getDocument(String key, T dataInstance) {
		Document doc = this.doc.get(key, Document.class);
		if (doc != null)
			dataInstance.read(new MongoData(doc));
		return dataInstance;
	}
	
	@NotNull
	public <T> Map<String, T> getMap(String key, Class<T> klass) {
		Document mdbMap = doc.get(key, Document.class);
		Map<String, T> ret = new HashMap<>(mdbMap.size());
		if (klass == Instant.class) {
			for (Entry<String, Object> e : mdbMap.entrySet()) {
				Object o = e.getValue();
				if (o == null)
					ret.put(e.getKey(), null);
				else
					//noinspection UseOfObsoleteDateTimeApi
					ret.put(e.getKey(), klass.cast(((Date) o).toInstant()));
			}
		} else {
			for (Entry<String, Object> e : mdbMap.entrySet()) {
				Object o = e.getValue();
				if (o == null)
					ret.put(e.getKey(), null);
				else
					ret.put(e.getKey(), klass.cast(o));
			}
		}
		return ret;
	}
	
	public void putInteger(String key, int i) {
		assert !containsKey(key) : "key already exists";
		doc.put(key, i);
	}
	
	public void putLong(String key, long l) {
		assert !containsKey(key) : "key already exists";
		doc.put(key, l);
	}
	
	public void putFloat(String key, float f) {
		assert !containsKey(key) : "key already exists";
		doc.put(key, f);
	}
	
	public void putDouble(String key, double d) {
		assert !containsKey(key) : "key already exists";
		doc.put(key, d);
	}
	
	public void putString(String key, String str) {
		assert !containsKey(key) : "key already exists";
		doc.put(key, str);
	}
	
	public void putBoolean(String key, boolean bool) {
		assert !containsKey(key) : "key already exists";
		doc.put(key, bool);
	}
	
	public void putObjectId(String key, ObjectId id) {
		assert !containsKey(key) : "key already exists";
		doc.put(key, id);
	}
	
	public void putDate(String key, Instant date) {
		assert !containsKey(key) : "key already exists";
		doc.put(key, Date.from(date));
	}
	
	public void putArray(String key, byte [] array) {
		assert !containsKey(key) : "key already exists";
		List<Integer> mdbArray = new ArrayList<>(array.length);
		for (byte b : array)
			mdbArray.add((int) b);
		doc.put(key, mdbArray);
	}
	
	public void putArray(String key, short [] array) {
		assert !containsKey(key) : "key already exists";
		List<Integer> mdbArray = new ArrayList<>(array.length);
		for (short s : array)
			mdbArray.add((int) s);
		doc.put(key, mdbArray);
	}
	
	public void putArray(String key, int [] array) {
		assert !containsKey(key) : "key already exists";
		doc.put(key, List.of(array));
	}
	
	public void putArray(String key, long [] array) {
		assert !containsKey(key) : "key already exists";
		doc.put(key, List.of(array));
	}
	
	public void putArray(String key, String [] array) {
		assert !containsKey(key) : "key already exists";
		doc.put(key, List.of(array));
	}
	
	public void putArray(String key, ObjectId [] array) {
		assert !containsKey(key) : "key already exists";
		doc.put(key, List.of(array));
	}
	
	public void putArray(String key, Instant [] array) {
		assert !containsKey(key) : "key already exists";
		@SuppressWarnings("UseOfObsoleteDateTimeApi") // forced to by MongoDB
		List<Date> mdbArray = new ArrayList<>(array.length);
		for (Instant i : array)
			mdbArray.add(Date.from(i));
		doc.put(key, mdbArray);
	}
	
	public void putArray(String key, List<?> array) {
		assert !containsKey(key) : "key already exists";
		List<Object> mdbArray = new ArrayList<>(array.size());
		for (Object o : array) {
			mdbArray.add(translatePut(o));
		}
		doc.put(key, mdbArray);
	}
	
	public <T> void putArray(String key, List<T> array, Function<T, ?> converter) {
		assert !containsKey(key) : "key already exists";
		List<Object> mdbArray = new ArrayList<>(array.size());
		for (T o : array) {
			mdbArray.add(translatePut(converter.apply(o)));
		}
		doc.put(key, mdbArray);
	}
	
	public void putDocument(String key, @NotNull MongoData doc) {
		assert !containsKey(key) : "key already exists";
		this.doc.put(key, doc.doc);
	}
	
	public <T extends MongoPersistable> void putDocument(String key, T data) {
		assert !containsKey(key) : "key already exists";
		if (data != null) {
			MongoData dataDoc = new MongoData();
			data.save(dataDoc);
			doc.put(key, dataDoc.doc);
		}
	}
	
	public void putMap(String key, Map<String, ?> data) {
		MongoData doc = new MongoData();
		for (Entry<String, ?> e : data.entrySet()) {
			doc.doc.put(e.getKey(), translatePut(e.getValue()));
		}
		putDocument(key, doc);
	}
	
	public <T, S> void putMap(String key, Map<T, S> data, Function<T, String> keyExtractor) {
		MongoData doc = new MongoData();
		for (Entry<T, S> e : data.entrySet()) {
			doc.doc.put(keyExtractor.apply(e.getKey()), translatePut(e.getValue()));
		}
		putDocument(key, doc);
	}
	
	public <T, S> void putMap(String key, Map<T, S> data, Function<T, String> keyExtractor, BiFunction<T, S, ?> valueExtractor) {
		MongoData doc = new MongoData();
		for (Entry<T, S> e : data.entrySet()) {
			doc.doc.put(keyExtractor.apply(e.getKey()), translatePut(valueExtractor.apply(e.getKey(), e.getValue())));
		}
		putDocument(key, doc);
	}
	
	@SuppressWarnings("UseOfObsoleteDateTimeApi")
	private Object translatePut(Object input) {
		if (input instanceof Instant) {
			return Date.from((Instant) input);
		} else if (input instanceof String || input instanceof Boolean || input instanceof Long || input instanceof Date || input instanceof Document) {
			return input;
		} else if (input instanceof Float || input instanceof Double) {
			return ((Number) input).doubleValue();
		} else if (input instanceof Number) {
			return ((Number) input).intValue();
		} else
			assert false : "bad object type: " + input;
		return null;
	}
	
}
