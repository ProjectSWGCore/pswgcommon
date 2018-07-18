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
package com.projectswg.common.data.info;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import me.joshlarson.jlcommon.log.Log;

public class RelationalServerFactory {
	
	private static final RelationalServerFactory INSTANCE = new RelationalServerFactory();
	private static final AtomicReference<String> BASE_PATH = new AtomicReference<>("");
	private static final Map <String, Object> FILE_LOAD_LOCKING = new HashMap<>();
	
	public static RelationalServerData getServerData(String file, String ... tables) {
		return INSTANCE.getData(file, tables);
	}
	
	public static RelationalServerData getServerDatabase(String file) {
		return INSTANCE.getDatabase(file);
	}
	
	public static void setBasePath(String basePath) {
		BASE_PATH.set(basePath);
	}
	
	private RelationalServerData getData(String file, String ... tables) {
		if (!file.endsWith(".db"))
			throw new IllegalArgumentException("File path for database must end in .db!");
		file = file.replace('/', File.separatorChar);
		final Object lock = getFileLocking(file);
		synchronized (lock) {
			File f = new File(BASE_PATH + file);
			RelationalServerData data = new RelationalServerData(BASE_PATH + file);
			if (loadServerData(data, f, tables))
				return data;
			return null;
		}
	}
	
	private boolean loadServerData(RelationalServerData data, File file, String ... tables) {
		File parent = file.getParentFile();
		try {
			if (loadTables(data, parent, tables))
				return true;
			data.close();
		} catch (Exception e) {
			Log.e(e);
			data.close();
		}
		return false;
	}
	
	private RelationalServerData getDatabase(String file) {
		if (!file.endsWith(".db"))
			throw new IllegalArgumentException("File path for database must end in .db!");
		final Object lock = getFileLocking(file);
		synchronized (lock) {
			File f = new File(BASE_PATH + file);
			RelationalServerData data = new RelationalServerData(BASE_PATH + file);
			try {
				String [] commands = getCommandsFromSchema(f.getPath().substring(0, f.getPath().length()-3) + ".sql");
				ParserData parserData = new ParserData();
				for (String command : commands)
					executeCommand(data, command, parserData);
				return data;
			} catch (Exception e) {
				Log.e(e);
			}
			data.close();
			return null;
		}
	}
	
	private boolean loadTables(RelationalServerData data, File parent, String [] tables) {
		for (String table : tables) {
			table = table.replace('/', File.separatorChar);
			String path = generatePath(parent, table);
			table = path.substring(path.lastIndexOf(File.separatorChar)+1, path.lastIndexOf('.'));
			if (!data.linkTableWithSdb(table, path))
				return false;
		}
		return true;
	}
	
	private String generatePath(File parent, String table) {
		String base;
		if (table.contains(File.separator))
			base = BASE_PATH + table;
		else
			base = parent.getPath() + File.separator + table;
		if (new File(base + ".msdb").isFile())
			return base + ".msdb";
		return base + ".sdb";
	}
	
	private void executeCommand(RelationalServerData data, String command, ParserData parserData) {
		command = command.trim();
		if (command.startsWith("SELECT") && parserData.getConditional()) {
			try (ResultSet set = data.executeQuery(command)) {
				
			} catch (SQLException e) {
				Log.e(e);
			}
		} else if (command.startsWith("IF")) { // VERY SIMPLE 'if' logic, no parenthesis and no AND/OR's - expects 3 arguments: <num/var> <op> <num/var>
			parserData.addConditional(evaluateIf(data, command.substring(2).trim()));
		} else if (command.startsWith("ENDIF")) {
			parserData.unwrapLastConditional();
		} else if (parserData.getConditional()) {
			data.updateQuery(command);
		}
	}
	
	private boolean evaluateIf(RelationalServerData data, String statement) {
		String [] args = statement.split(" ", 3);
		if (args.length != 3) {
			Log.e("Invalid IF statement: %s", statement);
			return false;
		}
		double num1 = parseToNumber(data, args[0]);
		String comparator = args[1];
		double num2 = parseToNumber(data, args[2]);
		switch (comparator) {
			case "<": return num1 < num2;
			case ">": return num1 > num2;
			case "=": return num1 == num2;
			case "==": return num1 == num2;
			case "<=": return num1 <= num2;
			case ">=": return num1 >= num2;
			default:
				Log.e("Invalid comparator: %s", comparator);
				return false;
		}
	}
	
	private double parseToNumber(RelationalServerData data, String str) {
		if (str.equals("user_version")) {
			ResultSet set = data.executeQuery("PRAGMA user_version");
			try {
				if (set.next())
					return set.getDouble(1);
				else
					Log.e("Variable 'user_version' has not been set!");
			} catch (SQLException e) {
				Log.e(e);
			}
		} else {
			try {
				return Double.parseDouble(str);
			} catch (NumberFormatException e) {
				Log.e("Number '%s' is not a valid number!", str);
			}
		}
		return Double.NaN;
	}
	
	private String [] getCommandsFromSchema(String schema) throws IOException {
		String command;
		try (InputStream input = new FileInputStream(new File(schema))) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream(input.available());
			byte [] block = new byte[1024];
			while (input.available() > 0) {
				int size = input.read(block);
				baos.write(block, 0, size);
			}
			command = new String(baos.toByteArray(), Charset.forName("ASCII"));
		}
		return command.split(";");
	}
	
	private Object getFileLocking(String file) {
		synchronized (FILE_LOAD_LOCKING) {
			Object o = FILE_LOAD_LOCKING.get(file);
			if (o == null)
				FILE_LOAD_LOCKING.put(file, o = new Object());
			return o;
		}
	}
	
	private static class ParserData {
		
		private RecursiveBoolean conditionalValue;
		
		public ParserData() {
			conditionalValue = null;
		}
		
		public void addConditional(boolean val) {
			if (conditionalValue == null)
				conditionalValue = new RecursiveBoolean(val);
			else
				conditionalValue.createRecursive(val);
		}
		
		public void unwrapLastConditional() {
			if (conditionalValue != null && conditionalValue.unwrapLast())
				conditionalValue = null;
		}
		
		public boolean getConditional() {
			if (conditionalValue == null)
				return true;
			return conditionalValue.get();
		}
		
	}
	
	private static class RecursiveBoolean {
		
		private final boolean val;
		private RecursiveBoolean recursive;
		
		public RecursiveBoolean(boolean val) {
			this.val = val;
			this.recursive = null;
		}
		
		public boolean get() {
			if (recursive != null)
				return val && recursive.get();
			return val;
		}
		
		public RecursiveBoolean createRecursive(boolean val) {
			if (recursive != null)
				return recursive.createRecursive(val);
			return (recursive = new RecursiveBoolean(val));
		}
		
		public boolean unwrapLast() {
			RecursiveBoolean recur = this;
			while (recur.recursive != null && recur.recursive.recursive != null)
				recur = recur.recursive;
			if (recur != null && recur.recursive != null) {
				recur.recursive = null;
				return true;
			}
			return true;
		}
		
	}
	
}
