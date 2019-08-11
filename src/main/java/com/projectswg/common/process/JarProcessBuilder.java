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

package com.projectswg.common.process;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class JarProcessBuilder {
	
	private final ProcessBuilder builder;
	private final String javaPath;
	private final String jarPath;
	private final List<String> vmArguments;
	private final List<String> arguments;
	private long minMemory;
	private long maxMemory;
	
	public JarProcessBuilder(File java, File jar) {
		this.builder = new ProcessBuilder();
		String javaPath, jarPath;
		try {
			javaPath = java.getCanonicalPath();
			jarPath = jar.getCanonicalPath();
		} catch (IOException e) {
			javaPath = java.getAbsolutePath();
			jarPath = jar.getAbsolutePath();
		}
		this.javaPath = javaPath;
		this.jarPath = jarPath;
		this.vmArguments = new CopyOnWriteArrayList<>();
		this.arguments = new CopyOnWriteArrayList<>();
		this.minMemory = Long.MIN_VALUE;
		this.maxMemory = Long.MIN_VALUE;
	}
	
	public JarProcessBuilder setMinMemory(long minMemory, MemoryUnit unit) {
		if (unit != MemoryUnit.KILOBYTES && unit != MemoryUnit.MEGABYTES && unit != MemoryUnit.GIGABYTES)
			throw new IllegalArgumentException("Unsupported memory unit!");
		this.minMemory = unit.getBytes(minMemory);
		return this;
	}
	
	public JarProcessBuilder setMaxMemory(long maxMemory, MemoryUnit unit) {
		if (unit != MemoryUnit.KILOBYTES && unit != MemoryUnit.MEGABYTES && unit != MemoryUnit.GIGABYTES)
			throw new IllegalArgumentException("Unsupported memory unit!");
		this.maxMemory = unit.getBytes(maxMemory);
		return this;
	}
	
	public JarProcessBuilder setMemory(long minMemory, long maxMemory, MemoryUnit unit) {
		if (unit != MemoryUnit.KILOBYTES && unit != MemoryUnit.MEGABYTES && unit != MemoryUnit.GIGABYTES)
			throw new IllegalArgumentException("Unsupported memory unit!");
		this.minMemory = unit.getBytes(minMemory);
		this.maxMemory = unit.getBytes(maxMemory);
		return this;
	}
	
	public JarProcessBuilder setVMArguments(String ... args) {
		return setVMArguments(Arrays.asList(args));
	}
	
	public JarProcessBuilder setVMArguments(List<String> args) {
		this.vmArguments.clear();
		this.vmArguments.addAll(args);
		return this;
	}
	
	public JarProcessBuilder setArguments(String ... args) {
		return setArguments(Arrays.asList(args));
	}
	
	public JarProcessBuilder setArguments(List<String> args) {
		this.arguments.clear();
		this.arguments.addAll(args);
		return this;
	}
	
	public List<String> command() {
		return builder.command();
	}
	
	public Map<String, String> environment() {
		return builder.environment();
	}
	
	public File directory() {
		return builder.directory();
	}
	
	public JarProcessBuilder directory(File directory) {
		builder.directory(directory);
		return this;
	}
	
	public JarProcessBuilder redirectInput(Redirect source) {
		builder.redirectInput(source);
		return this;
	}
	
	public JarProcessBuilder redirectOutput(Redirect destination) {
		builder.redirectOutput(destination);
		return this;
	}
	
	public JarProcessBuilder redirectError(Redirect destination) {
		builder.redirectError(destination);
		return this;
	}
	
	public JarProcessBuilder redirectInput(File file) {
		builder.redirectInput(file);
		return this;
	}
	
	public JarProcessBuilder redirectOutput(File file) {
		builder.redirectOutput(file);
		return this;
	}
	
	public JarProcessBuilder redirectError(File file) {
		builder.redirectError(file);
		return this;
	}
	
	public Redirect redirectInput() {
		return builder.redirectInput();
	}
	
	public Redirect redirectOutput() {
		return builder.redirectOutput();
	}
	
	public Redirect redirectError() {
		return builder.redirectError();
	}
	
	public JarProcessBuilder inheritIO() {
		builder.inheritIO();
		return this;
	}
	
	public boolean redirectErrorStream() {
		return builder.redirectErrorStream();
	}
	
	public JarProcessBuilder redirectErrorStream(boolean redirectErrorStream) {
		builder.redirectErrorStream(redirectErrorStream);
		return this;
	}
	
	public Process start() throws IOException {
		return builder.command(createCommand()).start();
	}
	
	@Override
	public int hashCode() {
		return builder.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return builder.equals(obj);
	}
	
	@Override
	public String toString() {
		return builder.toString();
	}
	
	private List<String> createCommand() {
		List<String> command = new ArrayList<>();
		command.add(javaPath);
		if (minMemory > 0) {
			MemoryUnit unit = MemoryUnit.getAppropriateUnit(minMemory);
			command.add("-Xms" + minMemory/unit.getFactor() + getJavaChar(unit));
		}
		if (maxMemory > 0) {
			MemoryUnit unit = MemoryUnit.getAppropriateUnit(maxMemory);
			command.add("-Xmx" + maxMemory/unit.getFactor() + getJavaChar(unit));
		}
		command.addAll(vmArguments);
		command.add("-jar");
		command.add(jarPath);
		command.addAll(arguments);
		return command;
	}
	
	private static char getJavaChar(MemoryUnit unit) {
		switch (unit) {
			case BYTES:
				return 'b';
			case KILOBYTES:
			default:
				return 'k';
			case MEGABYTES:
				return 'm';
			case GIGABYTES:
				return 'g';
			case TERABYTES:
				return 't';
		}
	}
	
	public static File getJavaPath() {
		return new File(System.getProperty("java.home"), "bin/java");
	}
	
	public enum MemoryUnit {
		BYTES		("B",  1),
		KILOBYTES	("kB", 1024),
		MEGABYTES	("MB", 1048576),
		GIGABYTES	("GB", 1073741824),
		TERABYTES	("TB", 1099511627776L);
		
		private final String suffix;
		private final long factor;
		
		MemoryUnit(String suffix, long factor) {
			this.suffix = suffix;
			this.factor = factor;
		}
		
		public String getSuffix() {
			return suffix;
		}
		
		public long getFactor() {
			return factor;
		}
		
		public long getBytes(double val) {
			return (long) (val * factor);
		}
		
		public long getBytes(long val) {
			return val * factor;
		}
		
		public double convert(double val, MemoryUnit unit) {
			long bytes = getBytes(val);
			return bytes / (double) unit.getFactor();
		}
		
		public static MemoryUnit getAppropriateUnit(long val) {
			if (val < KILOBYTES.getFactor())
				return MemoryUnit.BYTES;
			if (val < MEGABYTES.getFactor())
				return MemoryUnit.KILOBYTES;
			if (val < GIGABYTES.getFactor())
				return MemoryUnit.MEGABYTES;
			if (val < TERABYTES.getFactor())
				return MemoryUnit.GIGABYTES;
			return MemoryUnit.TERABYTES;
		}
		
	}
	
}
