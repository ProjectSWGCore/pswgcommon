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
package com.projectswg.common.data.encodables.mongo

import org.bson.Document
import org.bson.types.Binary
import org.bson.types.ObjectId
import java.lang.reflect.InvocationTargetException
import java.time.Instant
import java.util.*
import java.util.function.Function
import java.util.function.Supplier

class MongoData(doc: Document?) : MutableMap<String, Any?> {
	private val doc = doc ?: Document()

	@JvmOverloads
	constructor(version: Int = 0) : this(null) {
		if (version != 0) doc["_ver"] = version
	}

	fun toDocument(): Document {
		return Document(doc)
	}

	override fun toString(): String {
		return doc.toString()
	}

	override fun hashCode(): Int {
		return doc.hashCode()
	}

	override fun equals(other: Any?): Boolean {
		return (other is MongoData && doc == other.doc) || (other is Document && doc == other)
	}

	override val size: Int
		get() { return doc.size }
	override val entries: MutableSet<MutableMap.MutableEntry<String, Any?>>
		get() { return doc.entries }
	override val keys: MutableSet<String>
		get() { return doc.keys }
	override val values: MutableCollection<Any?>
		get() { return doc.values }

	fun version(): Int {
		return doc.getInteger("_ver", 0)
	}

	fun getInteger(key: String?): Int? {
		return doc.getInteger(key)
	}

	fun getInteger(key: String?, defaultValue: Int): Int {
		return doc.getInteger(key, defaultValue)
	}

	fun getLong(key: String?): Long? {
		return doc.getLong(key)
	}

	fun getLong(key: String?, defaultValue: Long): Long {
		val l = doc.getLong(key)
		return l ?: defaultValue
	}

	fun getFloat(key: String?): Float? {
		val f = doc.getDouble(key)
		return f?.toFloat()
	}

	fun getFloat(key: String?, defaultValue: Float): Float {
		val f = doc.getDouble(key)
		return f?.toFloat() ?: defaultValue
	}

	fun getDouble(key: String?): Double? {
		return doc.getDouble(key)
	}

	fun getDouble(key: String?, defaultValue: Double): Double {
		val d = doc.getDouble(key)
		return d ?: defaultValue
	}

	fun getString(key: String?): String? {
		return doc.getString(key)
	}

	fun getString(key: String?, defaultValue: String): String {
		val str = doc.getString(key)
		return str ?: defaultValue
	}

	fun getBoolean(key: String?): Boolean? {
		return doc.getBoolean(key)
	}

	fun getBoolean(key: String?, defaultValue: Boolean): Boolean {
		return doc.getBoolean(key, defaultValue)
	}

	fun getObjectId(key: String?): ObjectId? {
		return doc.getObjectId(key)
	}

	fun getDate(key: String?, def: Instant): Instant {
		return doc.getDate(key)?.toInstant() ?: def
	}

	fun getByteArray(key: String?): ByteArray? {
		return getByteArray(key, null)
	}

	fun getByteArray(key: String?, defaultValue: ByteArray?): ByteArray? {
		val b = doc.get(key, Binary::class.java) ?: return defaultValue
		return b.data
	}

	fun <T> getArray(key: String?, klass: Class<T>): List<T> {
		val mdbArray = doc.get(key, List::class.java) ?: return ArrayList()
		val ret: MutableList<T> = ArrayList(mdbArray.size)
		for (o in mdbArray) {
			ret.add(translateGet(o, klass) ?: continue)
		}
		return ret
	}

	fun <T : MongoPersistable> getArray(key: String?, generator: Supplier<T>): List<T> {
		val mdbArray = doc.get(key, List::class.java) ?: return ArrayList()
		val ret: MutableList<T> = ArrayList(mdbArray.size)
		for (o in mdbArray) {
			ret.add(translateGet(o, generator) ?: continue)
		}
		return ret
	}

	fun <T : MongoPersistable> getArray(key: String?, generator: Function<MongoData, T>): List<T> {
		val mdbArray = doc.get(key, List::class.java) ?: return ArrayList()
		val ret: MutableList<T> = ArrayList(mdbArray.size)
		for (o in mdbArray) {
			ret.add(translateGet(o, generator) ?: continue)
		}
		return ret
	}

	fun getDocument(key: String?): MongoData {
		if (!doc.containsKey(key)) {
			val mongoDoc = Document()
			doc[key] = mongoDoc
			return MongoData(mongoDoc)
		}
		return MongoData(doc.get(key, Document::class.java))
	}

	/**
	 * Attempts to parse the document at the specified key.  If the document exists, it will be parsed by the specified data instance.
	 * @param key the key to look up
	 * @param dataInstance the data instance to use
	 * @param <T> the parser type
	 * @return the parsed data, or null if the value does not exist
	</T> */
	fun <T : MongoPersistable?> getDocument(key: String?, dataInstance: T): T? {
		val doc = doc.get(key, Document::class.java)
		if (doc != null) dataInstance!!.readMongo(MongoData(doc))
		return dataInstance
	}

	/**
	 * Attempts to parse the document at the specified key.  If the document exists, an instance of the specified class will be created to parse the document
	 * @param key the key to look up
	 * @param dataGenerator the data generator to use
	 * @param <T> the parser type
	 * @return the parsed data, or null if the value does not exist
	</T> */
	fun <T : MongoPersistable?> getDocument(key: String?, dataGenerator: Supplier<T>): T? {
		val doc = doc.get(key, Document::class.java)
		if (doc != null) {
			val data = dataGenerator.get()
			data!!.readMongo(MongoData(doc))
			return data
		}
		return null
	}

	/**
	 * Attempts to parse the document at the specified key.  If the document exists, the specified function will be called to parse the data
	 * @param key the key to look up
	 * @param dataParser the data parser to use
	 * @param <T> the parser type
	 * @return the parsed data, or null if the value does not exist
	</T> */
	fun <T : MongoPersistable?> getDocument(key: String?, dataParser: Function<MongoData?, T>): T? {
		val doc = doc.get(key, Document::class.java)
		return if (doc == null) null else dataParser.apply(MongoData(doc))
	}

	fun <T, V> getMap(key: String?, keyClass: Class<T>, valueClass: Class<V>): Map<T, V> {
		val ret: MutableMap<T, V> = LinkedHashMap()
		for (value in getArray(key, MongoData::class.java)) {
			ret[translateGet(value["key"], keyClass) ?: continue] = translateGet(value["val"], valueClass) ?: continue
		}
		return ret
	}

	fun <T : MongoPersistable?, V> getMap(key: String?, keyGen: Supplier<T>, valueClass: Class<V>): Map<T, V> {
		val ret: MutableMap<T, V> = LinkedHashMap()
		for (value in getArray(key, MongoData::class.java)) {
			ret[translateGet(value["key"], keyGen) ?: continue] = translateGet(value["val"], valueClass) ?: continue
		}
		return ret
	}

	fun <T : MongoPersistable, V> getMap(key: String?, keyGen: Function<MongoData, T>, valueClass: Class<V>): Map<T, V?> {
		val ret: MutableMap<T, V?> = LinkedHashMap()
		for (value in getArray(key, MongoData::class.java)) {
			ret[translateGet(value["key"], keyGen) ?: continue] = translateGet(value["val"], valueClass)
		}
		return ret
	}

	fun <T, V : MongoPersistable> getMap(key: String?, keyClass: Class<T>, valueGen: Supplier<V>): Map<T, V?> {
		val ret: MutableMap<T, V?> = LinkedHashMap()
		for (value in getArray(key, MongoData::class.java)) {
			ret[translateGet(value["key"], keyClass) ?: continue] = translateGet(value["val"], valueGen)
		}
		return ret
	}

	fun <T, V : MongoPersistable> getMap(key: String?, keyClass: Class<T>, valueGen: Function<MongoData, V>): Map<T, V?> {
		val ret: MutableMap<T, V?> = LinkedHashMap()
		for (value in getArray(key, MongoData::class.java)) {
			ret[translateGet(value["key"], keyClass) ?: continue] = translateGet(value["val"], valueGen)
		}
		return ret
	}

	fun <T : MongoPersistable, V : MongoPersistable> getMap(key: String?, keyGen: Supplier<T>, valueGen: Supplier<V>): Map<T, V?> {
		val ret: MutableMap<T, V?> = LinkedHashMap()
		for (value in getArray(key, MongoData::class.java)) {
			ret[translateGet(value["key"], keyGen) ?: continue] = translateGet(value["val"], valueGen)
		}
		return ret
	}

	fun <T : MongoPersistable, V : MongoPersistable> getMap(key: String?, keyGen: Function<MongoData, T>, valueGen: Supplier<V>): Map<T, V?> {
		val ret: MutableMap<T, V?> = LinkedHashMap()
		for (value in getArray(key, MongoData::class.java)) {
			ret[translateGet(value["key"], keyGen) ?: continue] = translateGet(value["val"], valueGen)
		}
		return ret
	}

	fun <T : MongoPersistable, V : MongoPersistable> getMap(key: String?, keyGen: Supplier<T>, valueGen: Function<MongoData, V>): Map<T, V?> {
		val ret: MutableMap<T, V?> = LinkedHashMap()
		for (value in getArray(key, MongoData::class.java)) {
			ret[translateGet(value["key"], keyGen) ?: continue] = translateGet(value["val"], valueGen)
		}
		return ret
	}

	fun <T : MongoPersistable, V : MongoPersistable> getMap(key: String?, keyGen: Function<MongoData, T>, valueGen: Function<MongoData, V>): Map<T, V> {
		val ret: MutableMap<T, V> = LinkedHashMap()
		for (value in getArray(key, MongoData::class.java)) {
			ret[translateGet(value["key"], keyGen) ?: continue] = translateGet(value["val"], valueGen) ?: continue
		}
		return ret
	}

	fun putInteger(key: String?, i: Int) {
		assert(!containsKey(key)) { "key already exists" }
		doc[key] = i
	}

	fun putLong(key: String?, l: Long) {
		assert(!containsKey(key)) { "key already exists" }
		doc[key] = l
	}

	fun putFloat(key: String?, f: Float) {
		assert(!containsKey(key)) { "key already exists" }
		doc[key] = f.toDouble()
	}

	fun putDouble(key: String?, d: Double) {
		assert(!containsKey(key)) { "key already exists" }
		doc[key] = d
	}

	fun putString(key: String?, str: String?) {
		assert(!containsKey(key)) { "key already exists" }
		doc[key] = str
	}

	fun putBoolean(key: String?, bool: Boolean) {
		assert(!containsKey(key)) { "key already exists" }
		doc[key] = bool
	}

	fun putObjectId(key: String?, id: ObjectId?) {
		assert(!containsKey(key)) { "key already exists" }
		doc[key] = id
	}

	fun putDate(key: String?, date: Instant?) {
		assert(!containsKey(key)) { "key already exists" }
		doc[key] = Date.from(date)
	}

	fun putByteArray(key: String?, array: ByteArray?) {
		assert(!containsKey(key)) { "key already exists" }
		doc[key] = Binary(array)
	}

	fun putArray(key: String?, array: ShortArray) {
		assert(!containsKey(key)) { "key already exists" }
		val mdbArray: MutableList<Int> = ArrayList(array.size)
		for (s in array) mdbArray.add(s.toInt())
		doc[key] = mdbArray
	}

	fun putArray(key: String?, array: IntArray) {
		val list: MutableList<Int> = ArrayList()
		for (element in array) {
			list.add(element)
		}
		putArray(key, list)
	}

	fun putArray(key: String?, array: LongArray) {
		assert(!containsKey(key)) { "key already exists" }
		doc[key] = listOf(array)
	}

	fun putArray(key: String?, array: Array<String?>) {
		assert(!containsKey(key)) { "key already exists" }
		doc[key] = listOf(*array)
	}

	fun putArray(key: String?, array: Array<ObjectId?>) {
		assert(!containsKey(key)) { "key already exists" }
		doc[key] = listOf(*array)
	}

	fun putArray(key: String?, array: Array<Instant?>) {
		assert(!containsKey(key)) { "key already exists" }
		val mdbArray:  // forced to by MongoDB
				MutableList<Date> = ArrayList(array.size)
		for (i in array) mdbArray.add(Date.from(i))
		doc[key] = mdbArray
	}

	fun putArray(key: String?, array: Collection<*>) {
		assert(!containsKey(key)) { "key already exists" }
		val mdbArray: MutableList<Any?> = ArrayList(array.size)
		for (o in array) {
			mdbArray.add(if (o == null) null else translatePut(o))
		}
		doc[key] = mdbArray
	}

	fun <T> putArray(key: String?, array: Collection<T>, converter: (T) -> Any) {
		assert(!containsKey(key)) { "key already exists" }
		val mdbArray: MutableList<Any?> = ArrayList(array.size)
		for (o in array) {
			mdbArray.add(translatePut(converter(o)))
		}
		doc[key] = mdbArray
	}

	fun putDocument(key: String?, doc: MongoData) {
		assert(!containsKey(key)) { "key already exists" }
		this.doc[key] = doc.doc
	}

	fun <T : MongoPersistable?> putDocument(key: String?, data: T?) {
		assert(!containsKey(key)) { "key already exists" }
		if (data != null) {
			val dataDoc = MongoData()
			data.saveMongo(dataDoc)
			doc[key] = dataDoc.doc
		}
	}

	fun putMap(key: String?, data: Map<*, *>) {
		val map: MutableList<MongoData> = ArrayList()
		for ((key1, value) in data) {
			val `val` = MongoData()
			`val`.doc["key"] = translatePut(key1!!)
			`val`.doc["val"] = translatePut(value!!)
			map.add(`val`)
		}
		putArray(key, map)
	}

	fun <T, V> putMap(key: String?, data: Map<T, V>, keyTransform: Function<T, *>, valTransform: Function<V, *>) {
		val map: MutableList<MongoData> = ArrayList()
		for ((key1, value) in data) {
			val `val` = MongoData()
			`val`.doc["key"] = translatePut(keyTransform.apply(key1))
			`val`.doc["val"] = translatePut(valTransform.apply(value))
			map.add(`val`)
		}
		putArray(key, map)
	}

	private fun <T> translateGet(input: Any?, klass: Class<T>): T? {
		input ?: return null
		@Suppress("UNCHECKED_CAST") return when {
			klass == Instant::class.java -> klass.cast((input as Date).toInstant()) as T
			klass == MongoData::class.java -> klass.cast(MongoData(input as Document)) as T
			Int::class.java.isAssignableFrom(klass) -> java.lang.Integer::class.java.cast(input).toInt() as T
			Long::class.java.isAssignableFrom(klass) -> java.lang.Long::class.java.cast(input).toLong() as T
			MongoPersistable::class.java.isAssignableFrom(klass) -> {
				try {
					val smartConstructor = klass.getConstructor(MongoData::class.java)
					return smartConstructor.newInstance(MongoData(input as Document?))
				} catch (e: NoSuchMethodException) {
					try {
						val defaultConstructor = klass.getConstructor()
						val instance = defaultConstructor.newInstance()
						(instance as MongoPersistable).readMongo(MongoData(input as Document?))
						return instance
					} catch (e1: NoSuchMethodException) {
						throw IllegalArgumentException("Unable to find suitable constructor for: " + klass.simpleName)
					} catch (e1: IllegalAccessException) {
						throw IllegalArgumentException("Unable to create new default instance of " + klass.simpleName, e1)
					} catch (e1: InstantiationException) {
						throw IllegalArgumentException("Unable to create new default instance of " + klass.simpleName, e1)
					} catch (e1: InvocationTargetException) {
						throw IllegalArgumentException("Unable to create new default instance of " + klass.simpleName, e1)
					}
				} catch (e: IllegalAccessException) {
					throw IllegalArgumentException("Unable to create new smart instance of " + klass.simpleName, e)
				} catch (e: InstantiationException) {
					throw IllegalArgumentException("Unable to create new smart instance of " + klass.simpleName, e)
				} catch (e: InvocationTargetException) {
					throw IllegalArgumentException("Unable to create new smart instance of " + klass.simpleName, e)
				}
			}
			else -> klass.cast(input)
		}
	}

	private fun <T : MongoPersistable?> translateGet(input: Any?, generator: Supplier<T>): T? {
		if (input == null) return null

		val t = generator.get()
		t!!.readMongo(MongoData(input as Document?))
		return t
	}

	private fun <T : MongoPersistable> translateGet(input: Any?, generator: Function<MongoData, T>): T? {
		return generator.apply(MongoData(input as Document? ?: return null))
	}

	private fun translatePut(input: Any): Any? {
		when (input) {
			is Instant                                           -> return Date.from(input)
			is String, is Boolean, is Long, is Date, is Document -> return input
			is MongoData                                         -> return input.toDocument()
			is Double                                            -> return (input as Number).toDouble()
			is Float                                             -> return (input as Number).toFloat()
			is Number                                            -> return input.toInt()
			is MongoPersistable                                  -> return store(input).toDocument()
			else -> {
				assert(false) { "bad object type: $input" }
				return null
			}
		}
	}

	/* Map functions */
	override fun isEmpty(): Boolean {
		return doc.isEmpty()
	}

	override fun containsKey(key: String): Boolean {
		return doc.containsKey(key)
	}

	override fun containsValue(value: Any?): Boolean {
		return doc.containsValue(value)
	}

	override fun get(key: String): Any? {
		return doc[key]
	}

	override fun put(key: String, value: Any?): Any? {
		throw UnsupportedOperationException("must use another put method")
	}

	override fun remove(key: String): Any? {
		return doc.remove(key)
	}

	override fun putAll(from: Map<out String, Any?>) {
		throw UnsupportedOperationException("must use another put method")
	}

	override fun clear() {
		doc.clear()
	}

	companion object {
		fun store(obj: MongoPersistable): MongoData {
			val data = MongoData()
			obj.saveMongo(data)
			return data
		}

		fun <T : MongoPersistable?> create(doc: Document?, generator: Supplier<T>): T {
			return create(MongoData(doc), generator)
		}

		fun <T : MongoPersistable?> create(data: MongoData, generator: Supplier<T>): T {
			val ret = generator.get()
			ret!!.readMongo(data)
			return ret
		}
	}
}
