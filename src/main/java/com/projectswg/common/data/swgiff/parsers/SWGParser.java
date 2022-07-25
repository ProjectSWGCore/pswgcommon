package com.projectswg.common.data.swgiff.parsers;

import com.projectswg.common.data.swgiff.IffForm;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public interface SWGParser {
	
	void read(@NotNull IffForm form);
	IffForm write();
	
	static void setBasePath(String basePath) {
		SWGParserFactory.INSTANCE.setBasePath(basePath);
	}
	
	static String getBasePath() {
		return SWGParserFactory.INSTANCE.getBasePath();
	}
	
	static <T extends SWGParser> T parse(String file) {
		return SWGParserFactory.parse(file);
	}
	
	static <T extends SWGParser> T parse(File file) {
		return SWGParserFactory.parse(file);
	}
	
	@Nullable
	static <T extends SWGParser> T parse(IffForm form) {
		return SWGParserFactory.parse(form);
	}
	
	@NotNull
	static <T extends SWGParser> T parseNotNull(IffForm form) {
		T ret = parse(form);
		Objects.requireNonNull(ret, "Unknown form");
		return ret;
	}
	
}
