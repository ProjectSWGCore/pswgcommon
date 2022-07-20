package com.projectswg.common.network.packets.swg.zone.spatial

import com.projectswg.common.encoding.Encodable
import com.projectswg.common.network.NetBuffer
import java.text.NumberFormat
import java.util.*
import kotlin.collections.LinkedHashMap

class AttributeList : Encodable {

	private val map: MutableMap<String, String> = LinkedHashMap()
	
	fun putPercent(attribute: String, value: Double) {
		val nf = NumberFormat.getInstance(Locale.US)
		nf.maximumFractionDigits = 2
		val formattedValue = nf.format(value)
		
		map[attribute] = formattedValue
	}
	
	fun putText(attribute: String, value: String) {
		map[attribute] = value
	}
	
	fun putNumber(attribute: String, value: Number) {
		putNumber(attribute, value, "")
	}

	fun putNumber(attribute: String, value: Number, suffix: String) {
		if (value == 0) {
			map[attribute] = "0$suffix"
			return
		}
		
		val nf = NumberFormat.getInstance(Locale.US)
		nf.maximumFractionDigits = 2
		nf.isGroupingUsed = false
		val formattedValue = nf.format(value)

		map[attribute] = formattedValue + suffix
	}
	
	override fun decode(data: NetBuffer) {
		val size = data.int
		
		for (i in 0..size) {
			map[data.ascii] = data.unicode
		}
	}

	override fun encode(): ByteArray {
		val data = NetBuffer.allocate(length)

		data.addInt(map.size)
		for (entry in map) {
			data.addAscii(entry.key)
			data.addUnicode(entry.value)
		}
		
		return data.array()
	}

	override val length: Int
		get() {
			var size = 4
			for (entry in map) {
				size += 2 + entry.key.length
				size += 4 + entry.value.length * 2
			}

			return size
		}
}