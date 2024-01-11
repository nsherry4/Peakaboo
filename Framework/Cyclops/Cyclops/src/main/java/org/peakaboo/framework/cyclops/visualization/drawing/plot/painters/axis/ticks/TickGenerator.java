package org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.axis.ticks;

import java.util.List;

public interface TickGenerator {

	List<Integer> getTicks(float maxValue, int maxTicks, boolean includeMinorTicks);
	
}
