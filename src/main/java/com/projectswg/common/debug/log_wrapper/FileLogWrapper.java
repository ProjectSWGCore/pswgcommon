package com.projectswg.common.debug.log_wrapper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import com.projectswg.common.debug.Log.LogLevel;
import com.projectswg.common.debug.LogWrapper;

public class FileLogWrapper implements LogWrapper {
	
	private final BufferedWriter writer;
	
	public FileLogWrapper(File file) {
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void onLog(LogLevel level, String str) {
		try {
			writer.write(str);
			writer.newLine();
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
