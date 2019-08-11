package com.projectswg.common.data.swgiff.parsers.appearance;

import com.projectswg.common.data.swgiff.IffChunk;
import com.projectswg.common.data.swgiff.IffForm;
import com.projectswg.common.data.swgiff.parsers.SWGParser;

public class AppearanceTemplateList implements SWGParser {
	
	private String redirectFile;
	private AppearanceTemplate appearance;
	
	public AppearanceTemplateList() {
		this(null, null);
	}
	
	public AppearanceTemplateList(String redirectFile, AppearanceTemplate appearance) {
		this.redirectFile = redirectFile;
		this.appearance = appearance;
	}
	
	public AppearanceTemplate getAppearance() {
		return appearance;
	}
	
	@Override
	public void read(IffForm form) {
		assert form.getTag().equals("APT ");
		assert form.getVersion() == 0;
		
		try (IffChunk name = form.readChunk("NAME")) {
			redirectFile = name.readString();
			SWGParser parser = SWGParser.parse(redirectFile);
			this.appearance = parser instanceof AppearanceTemplateList ? ((AppearanceTemplateList) parser).getAppearance() : (AppearanceTemplate) parser;
		}
	}
	
	@Override
	public IffForm write() {
		IffChunk name = new IffChunk("NAME");
		name.writeString(redirectFile);
		
		return IffForm.of("APT ", 0, name);
	}
	
	@Override
	public String toString() {
		return "AppearanceTemplateList["+appearance+"]";
	}
	
}
