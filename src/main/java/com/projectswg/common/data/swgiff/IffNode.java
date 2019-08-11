package com.projectswg.common.data.swgiff;

import java.util.regex.Pattern;

public abstract class IffNode implements AutoCloseable {
	
	private static final Pattern VERSION_PATTERN = Pattern.compile("\\d{4}");
	
	public boolean isVersionForm() {
		return VERSION_PATTERN.matcher(getTag()).matches();
	}
	
	public int calculateVersionFromTag() {
		return Integer.parseUnsignedInt(getTag());
	}
	
	public boolean isForm() {
		return false;
	}
	
	public abstract String getTag();
	public abstract boolean isRead();
	
}
