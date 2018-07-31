package com.projectswg.common.data.swgiff.parsers;

import com.projectswg.common.data.swgiff.IffForm;
import me.joshlarson.jlcommon.log.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Objects;

public interface SWGParser {
	
	void read(IffForm form);
	IffForm write();
	
	static <T extends SWGParser> T parse(String file) {
		return parse(new File("clientdata/"+file));
	}
	
	static <T extends SWGParser> T parse(File file) {
		return SWGParserCache.parseIfAbsent(file);
	}
	
	@Nullable
	static <T extends SWGParser> T parse(IffForm form) {
		SWGParser parser = SWGParserFactory.createParser(form.getTag());
		if (parser == null) {
			Log.w("Unknown FORM for SWGParser: %s", form.getTag());
			return null;
		}
		parser.read(form);
		{
			@SuppressWarnings("unchecked")
			T ret = (T) parser;
			return ret;
		}
	}
	
	@NotNull
	static <T extends SWGParser> T parseNotNull(IffForm form) {
		T ret = parse(form);
		Objects.requireNonNull(ret, "Unknown form");
		return ret;
	}
	
}
