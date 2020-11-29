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
import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MongoData implements Map<String, Object> {
	
	private final @NotNull Document doc;
	
	public MongoData() {
		this(0);
	}
	
	public MongoData(int version) {
		this(null);
		if (version != 0)
			doc.put("_ver", version);
	}
	
	public MongoData(Document doc) {
		this.doc = doc == null ? new Document() : doc;
	}
	
	public Document toDocument() {
		return new Document(doc);
	}
	
	@Override
	public String toString() {
		return doc.toString();
	}
	
	@Override
	public int hashCode() {
		return doc.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		return (o instanceof MongoData && doc.equals(((MongoData) o).doc)) || (o instanceof Document && doc.equals(o));
	}
	
	public boolean containsKey(String key) {
		return doc.containsKey(key);
	}
	
	public int version() {
		return doc.getInteger("_ver", 0);
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
		return getDate(key, null);
	}
	
	public Instant getDate(String key, Instant def) {
		@SuppressWarnings("UseOfObsoleteDateTimeApi") // forced to by MongoDB
		Date d = doc.getDate(key);
		return d == null ? def : d.toInstant();
	}
	
	public byte [] getByteArray(String key) {
		return getByteArray(key, null);
	}
	
	public byte [] getByteArray(String key, byte [] defaultValue) {
		Binary b = doc.get(key, Binary.class);
		if (b == null)
			return defaultValue;
		return b.getData();
	}
	
	@NotNull
	public <T> List<T> getArray(String key, Class<T> klass) {
		List<?> mdbArray = doc.get(key, List.class);
		if (mdbArray == null)
			return new ArrayList<>();
		List<T> ret = new ArrayList<>(mdbArray.size());
		for (Object o : mdbArray) {
			ret.add(translateGet(o, klass));
		}
		return ret;
	}
	
	@NotNull
	public <T extends MongoPersistable> List<T> getArray(String key, Supplier<T> generator) {
		List<?> mdbArray = doc.get(key, List.class);
		if (mdbArray == null)
			return new ArrayList<>();
		List<T> ret = new ArrayList<>(mdbArray.size());
		for (Object o : mdbArray) {
			ret.add(translateGet(o, generator));
		}
		return ret;
	}
	
	@NotNull
	public <T extends MongoPersistable> List<T> getArray(String key, Function<MongoData, T> generator) {
		List<?> mdbArray = doc.get(key, List.class);
		if (mdbArray == null)
			return new ArrayList<>();
		List<T> ret = new ArrayList<>(mdbArray.size());
		for (Object o : mdbArray) {
			ret.add(translateGet(o, generator));
		}
		return ret;
	}
	
	@NotNull
	public MongoData getDocument(String key) {
		if (!doc.containsKey(key)) {
			var mongoDoc = new Document();
			doc.put(key, mongoDoc);
			return new MongoData(mongoDoc);
		}
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
			dataInstance.readMongo(new MongoData(doc));
		return dataInstance;
	}
	
	/**
	 * Attempts to parse the document at the specified key.  If the document exists, an instance of the specified class will be created to parse the document
	 * @param key the key to look up
	 * @param dataGenerator the data generator to use
	 * @param <T> the parser type
	 * @return the parsed data, or null if the value does not exist
	 */
	@Nullable
	public <T extends MongoPersistable> T getDocument(String key, Supplier<T> dataGenerator) {
		Document doc = this.doc.get(key, Document.class);
		if (doc != null) {
			T data = dataGenerator.get();
			data.readMongo(new MongoData(doc));
			return data;
		}
		return null;
	}
	
	/**
	 * Attempts to parse the document at the specified key.  If the document exists, the specified function will be called to parse the data
	 * @param key the key to look up
	 * @param dataParser the data parser to use
	 * @param <T> the parser type
	 * @return the parsed data, or null if the value does not exist
	 */
	@Nullable
	public <T extends MongoPersistable> T getDocument(String key, Function<MongoData, T> dataParser) {
		Document doc = this.doc.get(key, Document.class);
		return doc == null ? null : dataParser.apply(new MongoData(doc));
	}
	
	@NotNull
	public <T, V> Map<T, V> getMap(String key, Class<T> keyClass, Class<V> valueClass) {
		Map<T, V> ret = new LinkedHashMap<>();
		for (MongoData val : getArray(key, MongoData.class)) {
			ret.put(translateGet(val.get("key"), keyClass), translateGet(val.get("val"), valueClass));
		}
		return ret;
	}
	
	@NotNull
	public <T extends MongoPersistable, V> Map<T, V> getMap(String key, Supplier<T> keyGen, Class<V> valueClass) {
		Map<T, V> ret = new LinkedHashMap<>();
		for (MongoData val : getArray(key, MongoData.class)) {
			ret.put(translateGet(val.get("key"), keyGen), translateGet(val.get("val"), valueClass));
		}
		return ret;
	}
	
	@NotNull
	public <T extends MongoPersistable, V> Map<T, V> getMap(String key, Function<MongoData, T> keyGen, Class<V> valueClass) {
		Map<T, V> ret = new LinkedHashMap<>();
		for (MongoData val : getArray(key, MongoData.class)) {
			ret.put(translateGet(val.get("key"), keyGen), translateGet(val.get("val"), valueClass));
		}
		return ret;
	}
	
	@NotNull
	public <T, V extends MongoPersistable> Map<T, V> getMap(String key, Class<T> keyClass, Supplier<V> valueGen) {
		Map<T, V> ret = new LinkedHashMap<>();
		for (MongoData val : getArray(key, MongoData.class)) {
			ret.put(translateGet(val.get("key"), keyClass), translateGet(val.get("val"), valueGen));
		}
		return ret;
	}
	
	@NotNull
	public <T, V extends MongoPersistable> Map<T, V> getMap(String key, Class<T> keyClass, Function<MongoData, V> valueGen) {
		Map<T, V> ret = new LinkedHashMap<>();
		for (MongoData val : getArray(key, MongoData.class)) {
			ret.put(translateGet(val.get("key"), keyClass), translateGet(val.get("val"), valueGen));
		}
		return ret;
	}
	
	@NotNull
	public <T extends MongoPersistable, V extends MongoPersistable> Map<T, V> getMap(String key, Supplier<T> keyGen, Supplier<V> valueGen) {
		Map<T, V> ret = new LinkedHashMap<>();
		for (MongoData val : getArray(key, MongoData.class)) {
			ret.put(translateGet(val.get("key"), keyGen), translateGet(val.get("val"), valueGen));
		}
		return ret;
	}
	
	@NotNull
	public <T extends MongoPersistable, V extends MongoPersistable> Map<T, V> getMap(String key, Function<MongoData, T> keyGen, Supplier<V> valueGen) {
		Map<T, V> ret = new LinkedHashMap<>();
		for (MongoData val : getArray(key, MongoData.class)) {
			ret.put(translateGet(val.get("key"), keyGen), translateGet(val.get("val"), valueGen));
		}
		return ret;
	}
	
	@NotNull
	public <T extends MongoPersistable, V extends MongoPersistable> Map<T, V> getMap(String key, Supplier<T> keyGen, Function<MongoData, V> valueGen) {
		Map<T, V> ret = new LinkedHashMap<>();
		for (MongoData val : getArray(key, MongoData.class)) {
			ret.put(translateGet(val.get("key"), keyGen), translateGet(val.get("val"), valueGen));
		}
		return ret;
	}
	
	@NotNull
	public <T extends MongoPersistable, V extends MongoPersistable> Map<T, V> getMap(String key, Function<MongoData, T> keyGen, Function<MongoData, V> valueGen) {
		Map<T, V> ret = new LinkedHashMap<>();
		for (MongoData val : getArray(key, MongoData.class)) {
			ret.put(translateGet(val.get("key"), keyGen), translateGet(val.get("val"), valueGen));
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
		doc.put(key, (double) f);
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
	
	public void putByteArray(String key, byte [] array) {
		assert !containsKey(key) : "key already exists";
		doc.put(key, new Binary(array));
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
	
	public void putArray(String key, Collection<?> array) {
		assert !containsKey(key) : "key already exists";
		List<Object> mdbArray = new ArrayList<>(array.size());
		for (Object o : array) {
			mdbArray.add(translatePut(o));
		}
		doc.put(key, mdbArray);
	}
	
	public <T> void putArray(String key, Collection<T> array, Function<T, ?> converter) {
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
			data.saveMongo(dataDoc);
			doc.put(key, dataDoc.doc);
		}
	}
	
	public void putMap(String key, Map<?, ?> data) {
		List<MongoData> map = new ArrayList<>();
		for (Entry<?, ?> e : data.entrySet()) {
			MongoData val = new MongoData();
			val.doc.put("key", translatePut(e.getKey()));
			val.doc.put("val", translatePut(e.getValue()));
			map.add(val);
		}
		putArray(key, map);
	}
	
	public <T, V> void putMap(String key, Map<T, V> data, Function<T, ?> keyTransform, Function<V, ?> valTransform) {
		List<MongoData> map = new ArrayList<>();
		for (Entry<T, V> e : data.entrySet()) {
			MongoData val = new MongoData();
			val.doc.put("key", translatePut(keyTransform.apply(e.getKey())));
			val.doc.put("val", translatePut(valTransform.apply(e.getValue())));
			map.add(val);
		}
		putArray(key, map);
	}
	
	private <T> T translateGet(Object input, Class<T> klass) {
		if (input == null)
			return null;
		else if (klass == Instant.class)
			//noinspection UseOfObsoleteDateTimeApi
			return klass.cast(((Date) input).toInstant());
		else if (klass == MongoData.class)
			return klass.cast(new MongoData((Document) input));
		else if (MongoPersistable.class.isAssignableFrom(klass)) {
			try {
				Constructor<T> smartConstructor = klass.getConstructor(MongoData.class);
				return smartConstructor.newInstance(new MongoData((Document) input));
			} catch (NoSuchMethodException e) {
				try {
					Constructor<T> defaultConstructor = klass.getConstructor();
					T instance = defaultConstructor.newInstance();
					((MongoPersistable) instance).readMongo(new MongoData((Document) input));
					return instance;
				} catch (NoSuchMethodException e1) {
					throw new IllegalArgumentException("Unable to find suitable constructor for: " + klass.getSimpleName());
				} catch (IllegalAccessException | InstantiationException | InvocationTargetException e1) {
					throw new IllegalArgumentException("Unable to create new default instance of " + klass.getSimpleName(), e1);
				}
			} catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
				throw new IllegalArgumentException("Unable to create new smart instance of " + klass.getSimpleName(), e);
			}
		} else
			return klass.cast(input);
	}
	
	private <T extends MongoPersistable> T translateGet(Object input, Supplier<T> generator) {
		if (input == null)
			return null;
		
		T t = generator.get();
		t.readMongo(new MongoData((Document) input));
		return t;
	}
	
	private <T extends MongoPersistable> T translateGet(Object input, Function<MongoData, T> generator) {
		return input == null ? null : generator.apply(new MongoData((Document) input));
	}
	
	@SuppressWarnings("UseOfObsoleteDateTimeApi")
	private Object translatePut(Object input) {
		if (input instanceof Instant) {
			return Date.from((Instant) input);
		} else if (input instanceof String || input instanceof Boolean || input instanceof Long || input instanceof Date || input instanceof Document) {
			return input;
		} else if (input instanceof MongoData) {
			return ((MongoData) input).toDocument();
		} else if (input instanceof Float || input instanceof Double) {
			return ((Number) input).doubleValue();
		} else if (input instanceof Number) {
			return ((Number) input).intValue();
		} else if (input instanceof MongoPersistable) {
			return store((MongoPersistable) input).toDocument();
		}
		assert false : "bad object type: " + input;
		return null;
	}
	
	/* Map functions */
	
	@Override
	public int size() {
		return doc.size();
	}
	
	@Override
	public boolean isEmpty() {
		return doc.isEmpty();
	}
	
	@Override
	public boolean containsKey(Object key) {
		return doc.containsKey(key);
	}
	
	@Override
	public boolean containsValue(Object value) {
		return doc.containsValue(value);
	}
	
	@Override
	public Object get(Object key) {
		return doc.get(key);
	}
	
	@Nullable
	@Override
	public Object put(String key, Object value) {
		throw new UnsupportedOperationException("must use another put method");
	}
	
	@Override
	public Object remove(Object key) {
		return doc.remove(key);
	}
	
	@Override
	public void putAll(@NotNull Map<? extends String, ?> m) {
		throw new UnsupportedOperationException("must use another put method");
	}
	
	@Override
	public void clear() {
		doc.clear();
	}
	
	@NotNull
	@Override
	public Set<String> keySet() {
		return new HashSet<>(doc.keySet());
	}
	
	@NotNull
	@Override
	public Collection<Object> values() {
		return new ArrayList<>(doc.values());
	}
	
	@NotNull
	@Override
	public Set<Entry<String, Object>> entrySet() {
		return doc.entrySet().stream().map(e -> Map.entry(e.getKey(), e.getValue())).collect(Collectors.toSet());
	}
	
	public static MongoData store(MongoPersistable obj) {
		MongoData data = new MongoData();
		obj.saveMongo(data);
		return data;
	}
	
	public static <T extends MongoPersistable> T create(Document doc, Supplier<T> generator) {
		return create(new MongoData(doc), generator);
	}
	
	public static <T extends MongoPersistable> T create(MongoData data, Supplier<T> generator) {
		T ret = generator.get();
		ret.readMongo(data);
		return ret;
	}
	
}
