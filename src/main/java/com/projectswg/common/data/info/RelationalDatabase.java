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

import java.io.Closeable;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;

import me.joshlarson.jlcommon.log.Log;

public abstract class RelationalDatabase implements Closeable {
	
	private DatabaseMetaData metaData;
	private Connection connection;
	private boolean online;
	
	protected RelationalDatabase(String jdbcClass, String url) {
		try {
			Class.forName(jdbcClass);
			initialize(url);
		} catch (ClassNotFoundException e) {
			Log.e(e);
			online = false;
		}
	}
	
	protected RelationalDatabase(String jdbcClass, String url, String user, String pass) {
		try {
			Class.forName(jdbcClass);
			initialize(url, user, pass);
		} catch (ClassNotFoundException e) {
			Log.e(e);
			online = false;
		}
	}
	
	public RelationalDatabase(String jdbcClass, String url, String user, String pass, String params) {
		try {
			Class.forName(jdbcClass);
			if (params != null && params.length() > 0)
				url += "?" + params;
			initialize(url, user, pass);
		} catch (ClassNotFoundException e) {
			Log.e(e);
			online = false;
		}
	}
	
	public RelationalDatabase(String jdbcClass, String type, String host, String db, String user, String pass, String params) {
		try {
			Class.forName(jdbcClass);
			String url = "jdbc:" + type + "://" + host + "/" + db;
			if (params != null && params.length() > 0)
				url += "?" + params;
			initialize(url, user, pass);
		} catch (ClassNotFoundException e) {
			Log.e(e);
			online = false;
		}
	}
	
	private void initialize(String url) {
		try {
			connection = DriverManager.getConnection(url);
			metaData = connection.getMetaData();
			online = true;
		} catch (SQLException e) {
			Log.e("Failed to initialize relational database! %s - %s", e.getClass().getSimpleName(), e.getMessage());
			online = false;
		}
	}
	
	private void initialize(String url, String user, String pass) {
		try {
			connection = DriverManager.getConnection(url, user, pass);
			metaData = connection.getMetaData();
			online = true;
		} catch (SQLException e) {
			Log.e("Failed to initialize relational database! %s - %s", e.getClass().getSimpleName(), e.getMessage());
			online = false;
		}
	}
	
	public void close() {
		try {
			connection.close();
			online = false;
		} catch (SQLException e) {
			Log.e(e);
		}
	}
	
	public boolean isOnline() {
		if (connection == null)
			return false;
		try {
			return online && !connection.isClosed();
		} catch (SQLException e) {
			return online;
		}
	}
	
	public PreparedStatement prepareStatement(String sql) {
		if (connection == null) {
			Log.e("Cannot prepare statement! Connection is null");
			return null;
		}
		try {
			return connection.prepareStatement(sql);
		} catch (SQLException e) {
			Log.e(e);
			return null;
		}
	}
	
	public ResultSet executeQuery(String query) {
		if (connection == null)
			return null;
		Statement s = null;
		try {
			s = connection.createStatement();
			s.execute(query);
			try {
				s.closeOnCompletion();
			} catch (SQLFeatureNotSupportedException e) {
				// It was worth a shot
			}
			return s.getResultSet();
		} catch (SQLException e) {
			Log.e(e);
			if (s != null) {
				try { s.close(); } catch (SQLException ex) { }
			}
			return null;
		}
	}
	
	public int updateQuery(String query) {
		if (connection == null)
			return 0;
		try {
			try (Statement s = connection.createStatement()) {
				return s.executeUpdate(query);
			}
		} catch (SQLException e) {
			Log.e(e);
			return 0;
		}
	}
	
	public boolean isTable(String name) {
		if (metaData == null)
			return false;
		try {
			return metaData.getTables(null, null, name, null).next();
		} catch (SQLException e) {
			return false;
		}
	}
	
}
