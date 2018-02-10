package com.projectswg.common.utilities;

import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

public class LocalUtilities {
	
	private static final AtomicReference<String> APP_NAME = new AtomicReference<>(".projectswg");
	
	public static void setApplicationName(String name) {
		APP_NAME.set(name);
	}
	
	public static File getSubApplicationDirectory(String ... names) {
		File dir = getApplicationDirectory();
		for (String name : names) {
			dir = new File(dir, name);
			if (!dir.exists())
				dir.mkdir();
		}
		return dir;
	}
	
	public static File getApplicationDirectory() {
		String home = getHomeDirectory();
		if (home == null)
			throw new IllegalStateException("Unknown home directory for OS '"+System.getProperty("os.name")+"'");
		
		File dir = new File(home, APP_NAME.get());
		if (!dir.exists())
			dir.mkdirs();
		return dir;
	}
	
	private static String getHomeDirectory() {
		String os = System.getProperty("os.name").toUpperCase();
		
		if (os.contains("WIN"))
			return System.getenv("APPDATA");
		
		if (os.contains("MAC"))
			return System.getProperty("user.home") + "/Library/Application Support";
		
		if (os.contains("NUX"))
			return System.getProperty("user.home");
		
		return System.getProperty("user.dir");
	}
	
}
