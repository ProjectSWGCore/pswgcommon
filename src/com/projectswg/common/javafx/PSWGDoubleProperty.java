/***********************************************************************************
* Copyright (c) 2015 /// Project SWG /// www.projectswg.com                        *
*                                                                                  *
* ProjectSWG is the first NGE emulator for Star Wars Galaxies founded on           *
* July 7th, 2011 after SOE announced the official shutdown of Star Wars Galaxies.  *
* Our goal is to create an emulator which will provide a server for players to     *
* continue playing a game similar to the one they used to play. We are basing      *
* it on the final publish of the game prior to end-game events.                    *
*                                                                                  *
* This file is part of Holocore.                                                   *
*                                                                                  *
* -------------------------------------------------------------------------------- *
*                                                                                  *
* Holocore is free software: you can redistribute it and/or modify                 *
* it under the terms of the GNU Affero General Public License as                   *
* published by the Free Software Foundation, either version 3 of the               *
* License, or (at your option) any later version.                                  *
*                                                                                  *
* Holocore is distributed in the hope that it will be useful,                      *
* but WITHOUT ANY WARRANTY; without even the implied warranty of                   *
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                    *
* GNU Affero General Public License for more details.                              *
*                                                                                  *
* You should have received a copy of the GNU Affero General Public License         *
* along with Holocore.  If not, see <http://www.gnu.org/licenses/>.                *
*                                                                                  *
***********************************************************************************/
package com.projectswg.common.javafx;

import java.util.HashSet;
import java.util.Set;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;

public class PSWGDoubleProperty extends SimpleDoubleProperty {
	
	private final Set<ChangeListener<? super Number>> listeners;
	private final Set<Property<Number>> properties;
	
	public PSWGDoubleProperty() {
		super();
		listeners = new HashSet<>();
		properties = new HashSet<>();
	}
	
	public PSWGDoubleProperty(double initialValue) {
		super(initialValue);
		listeners = new HashSet<>();
		properties = new HashSet<>();
	}
	
	@Override
	public void addListener(ChangeListener<? super Number> listener) {
		listeners.add(listener);
		super.addListener(listener);
	}
	
	@Override
	public void removeListener(ChangeListener<? super Number> listener) {
		listeners.remove(listener);
		super.removeListener(listener);
	}
	
	public void removeAllListeners() {
		for (ChangeListener<? super Number> listener : listeners) {
			super.removeListener(listener);
		}
	}
	
	@Override
	public void bindBidirectional(Property<Number> property) {
		super.bindBidirectional(property);
		properties.add(property);
	}
	
	public void unbindAll() {
		unbind();
		for (Property<Number> property : properties) {
			super.unbindBidirectional(property);
		}
	}
	
}
