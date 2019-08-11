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
package com.projectswg.common.data.radial;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class RadialOption {
	
	private static final int FLAG_ENABLED		= 0x01;
	private static final int FLAG_SERVER_NOTIFY	= 0x02;
	
	private final List<RadialOption> children;
	private final RadialItem type;
	private final String label;
	private final byte flags;
	
	private RadialOption(@NotNull List<RadialOption> children, @NotNull RadialItem type, @NotNull String label, int flags) {
		for (RadialOption child : children)
			Objects.requireNonNull(child, "child cannot be null");
		this.children = children;
		this.type = type;
		this.label = label;
		this.flags = (byte) flags;
	}
	
	@NotNull
	public List<RadialOption> getChildren() {
		return Collections.unmodifiableList(children);
	}
	
	@NotNull
	public RadialItem getType() {
		return type;
	}
	
	@NotNull
	public String getLabel() {
		return label;
	}
	
	public byte getFlags() {
		return flags;
	}
	
	@Override
	public String toString() { 
		return String.format("RadialOption[%s label='%s' flags=%d children=%s]", type, label, flags, children); 
	}
	
	@Override
	public boolean equals(Object o) {
		return o instanceof RadialOption && ((RadialOption) o).type == type;
	}
	
	@Override
	public int hashCode() {
		return type.hashCode();
	}
	
	/**
	 * Creates a radial option based on the specified option, with a different set of children options
	 * @param option the base option
	 * @param children the children of this radial option
	 * @return a new radial option with the new children list
	 */
	@NotNull
	public static RadialOption createRaw(@NotNull RadialOption option, @NotNull List<RadialOption> children) {
		return new RadialOption(new ArrayList<>(children), option.getType(), option.getLabel(), option.getFlags());
	}
	
	/**
	 * Creates a radial option based on the specified option, with a different set of children options
	 * @param option the base option
	 * @param children the children of this radial option
	 * @return a new radial option with the new children list
	 */
	@NotNull
	public static RadialOption createRaw(@NotNull RadialOption option, RadialOption ... children) {
		return new RadialOption(List.of(children), option.getType(), option.getLabel(), option.getFlags());
	}
	
	/**
	 * Creates a radial option that is enabled and will not send a selection notification to the server when selected
	 * @param type the action type
	 * @param label the override label
	 * @param flags the option flags (enabled/notify/out of range)
	 * @param children the children of this radial option
	 * @return a new radial option with the specified properties
	 */
	@NotNull
	public static RadialOption createRaw(@NotNull RadialItem type, @NotNull String label, byte flags, RadialOption ... children) {
		return new RadialOption(List.of(children), type, label, flags);
	}
	
	/**
	 * Creates a radial option that is enabled and will not send a selection notification to the server when selected
	 * @param type the action type
	 * @param children the children of this radial option
	 * @return a new radial option with the specified properties
	 */
	@NotNull
	public static RadialOption createSilent(@NotNull RadialItem type, @NotNull Collection<RadialOption> children) {
		return new RadialOption(new ArrayList<>(children), type, "", FLAG_ENABLED);
	}
	
	/**
	 * Creates a radial option that is enabled and will not send a selection notification to the server when selected
	 * @param type the action type
	 * @param children the children of this radial option
	 * @return a new radial option with the specified properties
	 */
	@NotNull
	public static RadialOption createSilent(@NotNull RadialItem type, RadialOption ... children) {
		return new RadialOption(List.of(children), type, "", FLAG_ENABLED);
	}
	
	/**
	 * Creates a radial option that is enabled and will send a selection notification to the server when selected
	 * @param type the action type
	 * @param children the children of this radial option
	 * @return a new radial option with the specified properties
	 */
	@NotNull
	public static RadialOption create(@NotNull RadialItem type, @NotNull Collection<RadialOption> children) {
		return new RadialOption(new ArrayList<>(children), type, "", FLAG_ENABLED | FLAG_SERVER_NOTIFY);
	}
	
	/**
	 * Creates a radial option that is enabled and will send a selection notification to the server when selected
	 * @param type the action type
	 * @param children the children of this radial option
	 * @return a new radial option with the specified properties
	 */
	@NotNull
	public static RadialOption create(@NotNull RadialItem type, RadialOption ... children) {
		return new RadialOption(List.of(children), type, "", FLAG_ENABLED | FLAG_SERVER_NOTIFY);
	}
	
	/**
	 * Creates a radial option that is disabled and will not send a selection notification to the server when selected
	 * @param type the action type
	 * @param children the children of this radial option
	 * @return a new radial option with the specified properties
	 */
	@NotNull
	public static RadialOption createDisabled(@NotNull RadialItem type, @NotNull Collection<RadialOption> children) {
		return new RadialOption(new ArrayList<>(children), type, "", 0);
	}
	
	/**
	 * Creates a radial option that is disabled and will not send a selection notification to the server when selected
	 * @param type the action type
	 * @param children the children of this radial option
	 * @return a new radial option with the specified properties
	 */
	@NotNull
	public static RadialOption createDisabled(@NotNull RadialItem type, RadialOption ... children) {
		return new RadialOption(List.of(children), type, "", 0);
	}
	
	/**
	 * Creates a radial option that is enabled and will not send a selection notification to the server when selected
	 * @param type the action type
	 * @param label the override label
	 * @param children the children of this radial option
	 * @return a new radial option with the specified properties
	 */
	@NotNull
	public static RadialOption createSilent(@NotNull RadialItem type, @NotNull String label, @NotNull Collection<RadialOption> children) {
		return new RadialOption(new ArrayList<>(children), type, label, FLAG_ENABLED);
	}
	
	/**
	 * Creates a radial option that is enabled and will not send a selection notification to the server when selected
	 * @param type the action type
	 * @param label the override label
	 * @param children the children of this radial option
	 * @return a new radial option with the specified properties
	 */
	@NotNull
	public static RadialOption createSilent(@NotNull RadialItem type, @NotNull String label, RadialOption ... children) {
		return new RadialOption(List.of(children), type, label, FLAG_ENABLED);
	}
	
	/**
	 * Creates a radial option that is enabled and will send a selection notification to the server when selected
	 * @param type the action type
	 * @param label the override label
	 * @param children the children of this radial option
	 * @return a new radial option with the specified properties
	 */
	@NotNull
	public static RadialOption create(@NotNull RadialItem type, @NotNull String label, @NotNull Collection<RadialOption> children) {
		return new RadialOption(new ArrayList<>(children), type, label, FLAG_ENABLED | FLAG_SERVER_NOTIFY);
	}
	
	/**
	 * Creates a radial option that is enabled and will send a selection notification to the server when selected
	 * @param type the action type
	 * @param label the override label
	 * @param children the children of this radial option
	 * @return a new radial option with the specified properties
	 */
	@NotNull
	public static RadialOption create(@NotNull RadialItem type, @NotNull String label, RadialOption ... children) {
		return new RadialOption(List.of(children), type, label, FLAG_ENABLED | FLAG_SERVER_NOTIFY);
	}
	
	/**
	 * Creates a radial option that is disabled and will not send a selection notification to the server when selected
	 * @param type the action type
	 * @param label the override label
	 * @param children the children of this radial option
	 * @return a new radial option with the specified properties
	 */
	@NotNull
	public static RadialOption createDisabled(@NotNull RadialItem type, @NotNull String label, @NotNull Collection<RadialOption> children) {
		return new RadialOption(new ArrayList<>(children), type, label, 0);
	}
	
	/**
	 * Creates a radial option that is disabled and will not send a selection notification to the server when selected
	 * @param type the action type
	 * @param label the override label
	 * @param children the children of this radial option
	 * @return a new radial option with the specified properties
	 */
	@NotNull
	public static RadialOption createDisabled(@NotNull RadialItem type, @NotNull String label, RadialOption ... children) {
		return new RadialOption(List.of(children), type, label, 0);
	}
	
}
