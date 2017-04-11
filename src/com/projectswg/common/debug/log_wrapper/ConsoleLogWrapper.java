package com.projectswg.common.debug.log_wrapper;

import com.projectswg.common.debug.Log.LogLevel;
import com.projectswg.common.debug.LogWrapper;

public class ConsoleLogWrapper implements LogWrapper {
	
	private final LogLevel level;
	
	public ConsoleLogWrapper(LogLevel level) {
		this.level = level;
	}
	
	@Override
	public void onLog(LogLevel level, String str) {
		if (this.level.compareTo(level) > 0)
			return;
		if (level.compareTo(LogLevel.WARN) >= 0)
			System.err.println(str);
		else
			System.out.println(str);
	}
	
}
