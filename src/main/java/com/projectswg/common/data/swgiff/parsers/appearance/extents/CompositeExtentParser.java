package com.projectswg.common.data.swgiff.parsers.appearance.extents;

import com.projectswg.common.data.math.extents.*;
import com.projectswg.common.data.swgiff.IffForm;
import com.projectswg.common.data.swgiff.IffNode;
import com.projectswg.common.data.swgiff.parsers.SWGParser;

import java.util.ArrayList;
import java.util.List;

public class CompositeExtentParser implements ExtentParser {
	
	private CompositeExtent composite;
	
	public CompositeExtentParser() {
		this.composite = null;
	}
	
	public CompositeExtentParser(CompositeExtent composite) {
		this.composite = composite;
	}
	
	@Override
	public CompositeExtent getExtent() {
		return composite;
	}
	
	@Override
	public void read(IffForm form) {
		assert form.getTag().equals("CPST");
		assert form.getVersion() == 0;
		
		IffForm child;
		List<Extent> extents = new ArrayList<>();
		while ((child = form.readForm()) != null) {
			extents.add(((ExtentParser) SWGParser.parseNotNull(child)).getExtent());
			child.close();
		}
		
		this.composite = new CompositeExtent(extents);
	}
	
	@Override
	public IffForm write() {
		List<IffNode> forms = new ArrayList<>();
		for (Extent e : composite.getExtents()) {
			if (e instanceof BoxExtent)
				forms.add(new BoxExtentParser((BoxExtent) e).write());
			else if (e instanceof CompositeExtent)
				forms.add(new CompositeExtentParser((CompositeExtent) e).write());
			else if (e instanceof OrientedCylinderExtent)
				forms.add(new OrientedCylinderExtentParser((OrientedCylinderExtent) e).write());
			else if (e instanceof CylinderExtent)
				forms.add(new CylinderExtentParser((CylinderExtent) e).write());
			else if (e instanceof MeshExtent)
				forms.add(new MeshExtentParser((MeshExtent) e).write());
			else if (e instanceof SphereExtent)
				forms.add(new SphereExtentParser((SphereExtent) e).write());
		}
		
		return IffForm.of("CPST", 0, forms);
	}
}
