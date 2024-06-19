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
package com.projectswg.common.data.sui

import com.projectswg.common.encoding.Encodable
import com.projectswg.common.network.NetBuffer
import me.joshlarson.jlcommon.log.Log
import java.nio.charset.StandardCharsets

open class SuiBaseWindow : Encodable {
	var id: Int = 0
	var suiScript: String? = null
	var rangeObjId: Long = 0
	var maxDistance: Float = 0f
	private var components: MutableList<SuiComponent> = ArrayList()
	private var callbacks: MutableMap<String, ISuiCallback>? = null
	private var hasSubscriptionComponent = false

	fun clearDataSource(dataSource: String) {
		val component = SuiComponent(SuiComponent.Type.CLEAR_DATA_SOURCE, dataSource)
		components.add(component)
	}

	fun addChildWidget(type: String, childWidget: String, parentWidget: String) {
		val component = SuiComponent(SuiComponent.Type.ADD_CHILD_WIDGET, parentWidget)

		component.addNarrowParam(type)
		component.addNarrowParam(childWidget)

		components.add(component)
	}

	fun setProperty(widget: String, property: String, value: String) {
		val component = SuiComponent(SuiComponent.Type.SET_PROPERTY, widget)

		component.addNarrowParam(property)
		component.addWideParam(value)

		components.add(component)
	}

	fun addDataItem(dataSource: String, name: String, value: String) {
		val component = SuiComponent(SuiComponent.Type.ADD_DATA_ITEM, dataSource)

		component.addNarrowParam(name)
		component.addWideParam(value)

		components.add(component)
	}

	protected fun subscribeToEvent(event: Int, widgetSource: String, callback: String) {
		var component = getSubscriptionForEvent(event, widgetSource)
		if (component != null) {
			Log.i("Added event callback %d to %s when the event is already subscribed to, replacing callback to %s", event, widgetSource, callback)
			component.setNarrowParam(1, callback) // Replaces the callback for a SUBSCRIBE_TO_EVENT component that's set 4 lines below
		} else {
			component = SuiComponent(SuiComponent.Type.SUBSCRIBE_TO_EVENT, widgetSource)
			component.addNarrowParam(getWrappedEventString(event))
			component.addNarrowParam(callback)

			components.add(component)
		}
		if (!hasSubscriptionComponent()) hasSubscriptionComponent = true
	}

	protected fun subscribeToPropertyEvent(event: Int, widgetSource: String, propertyWidget: String, propertyName: String) {
		var component = getSubscriptionForEvent(event, widgetSource)
		if (component != null) {
			// This component already has the trigger and source param, just need to add the widget and property
			// for client to return the value to the server
			component.addNarrowParam(propertyWidget)
			component.addNarrowParam(propertyName)
		} else {
			component = SuiComponent(SuiComponent.Type.SUBSCRIBE_TO_EVENT, widgetSource)
			component.addNarrowParam(getWrappedEventString(event))
			component.addNarrowParam("")
			component.addNarrowParam(propertyWidget)
			component.addNarrowParam(propertyName)
			components.add(component)
		}
		if (!hasSubscriptionComponent()) hasSubscriptionComponent = true
	}

	fun addDataSourceContainer(dataSourceContainer: String, name: String, value: String) {
		val component = SuiComponent(SuiComponent.Type.ADD_DATA_SOURCE_CONTAINER, dataSourceContainer)

		component.addNarrowParam(name)
		component.addWideParam(value)

		components.add(component)
	}

	fun clearDataSourceContainer(dataSourceContainer: String) {
		val component = SuiComponent(SuiComponent.Type.CLEAR_DATA_SOURCE_CONTAINER, dataSourceContainer)
		components.add(component)
	}

	fun addDataSource(dataSource: String, name: String, value: String) {
		val component = SuiComponent(SuiComponent.Type.ADD_DATA_SOURCE, dataSource)

		component.addNarrowParam(name)
		component.addWideParam(value)

		components.add(component)
	}

	fun addReturnableProperty(event: SuiEvent, source: String, widget: String, property: String) {
		subscribeToPropertyEvent(event.value, source, widget, property)
	}

	fun addReturnableProperty(event: SuiEvent, widget: String, property: String) {
		addReturnableProperty(event, "", widget, property)
	}

	fun addReturnableProperty(widget: String, property: String) {
		subscribeToPropertyEvent(SuiEvent.OK_PRESSED.value, "", widget, property)
		subscribeToPropertyEvent(SuiEvent.CANCEL_PRESSED.value, "", widget, property)
	}

	fun addCallback(event: SuiEvent, source: String, name: String, callback: ISuiCallback) {
		subscribeToEvent(event.value, source, name)
		addJavaCallback(name, callback)
	}

	fun addCallback(event: SuiEvent, name: String, callback: ISuiCallback) {
		addCallback(event, "", name, callback)
	}

	fun addCallback(source: String, name: String, callback: ISuiCallback) {
		subscribeToEvent(SuiEvent.OK_PRESSED.value, source, name)
		subscribeToEvent(SuiEvent.CANCEL_PRESSED.value, source, name)
		addJavaCallback(name, callback)
	}

	fun addCallback(name: String, callback: ISuiCallback) {
		addCallback("", name, callback)
	}

	fun getSubscriptionForEvent(event: Int, widget: String): SuiComponent? {
		for (component in components) {
			if (component.type != SuiComponent.Type.SUBSCRIBE_TO_EVENT) continue

			val eventType = component.subscribedToEventType

			if (eventType == event && component.target == widget) return component
		}
		return null
	}

	fun getSubscriptionByIndex(index: Int): SuiComponent? {
		var count = 0
		for (component in components) {
			if (component.type == SuiComponent.Type.SUBSCRIBE_TO_EVENT) {
				if (index == count) return component
				else count++
			}
		}
		return null
	}

	fun getComponents(): List<SuiComponent> {
		return components
	}

	fun getJavaCallback(name: String?): ISuiCallback? {
		return callbacks?.get(name)
	}

	fun hasJavaCallback(name: String?): Boolean {
		return callbacks?.containsKey(name) ?: false
	}

	fun hasSubscriptionComponent(): Boolean {
		return hasSubscriptionComponent
	}

	private fun addJavaCallback(name: String, callback: ISuiCallback) {
		if (callbacks == null)
			callbacks = HashMap()

		callbacks!![name] = callback
	}

	private fun getWrappedEventString(event: Int): String {
		return String(byteArrayOf(event.toByte()), StandardCharsets.UTF_8)
	}

	override fun encode(): ByteArray {
		val data = NetBuffer.allocate(length)
		data.addInt(id)
		data.addAscii(suiScript)
		data.addList(components)
		data.addLong(rangeObjId)
		data.addFloat(maxDistance)
		data.addLong(0) // Window Location?
		data.addInt(0)
		return data.array()
	}

	override fun decode(data: NetBuffer) {
		id = data.int
		suiScript = data.ascii
		components = data.getList(SuiComponent::class.java)
		rangeObjId = data.long
		maxDistance = data.float
		// unk long
		// unk int
	}

	override val length: Int
		get() {
			var listSize = 0
			for (component in components) {
				listSize += component.length
			}
			return 34 + suiScript!!.length + listSize
		}
}
