package org.peakaboo.controller.mapper.selection;

import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.framework.cyclops.Coord;

public abstract class AbstractSelection implements Selection {

	protected MappingController map;
	
	public AbstractSelection(MappingController map) {
		this.map = map;
	}
	
	protected Coord<Integer> mapSize() {
		return map.getFitting().getActiveMode().getSize();
	}
	
}
