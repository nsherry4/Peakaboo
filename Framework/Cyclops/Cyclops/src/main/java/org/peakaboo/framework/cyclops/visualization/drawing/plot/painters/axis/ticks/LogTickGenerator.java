package org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.axis.ticks;

import java.util.ArrayList;
import java.util.List;

public class LogTickGenerator implements TickGenerator {

	@Override
	public List<Integer> getTicks(float maxValue, int maxTicks, boolean includeMinorTicks) {
		int magnitude = (int) Math.ceil(Math.log10(1+maxValue));
		
		List<Integer> ticks = new ArrayList<>();
		for (int oom = 0; oom <= magnitude; oom++) {

			// Optionally generate minor ticks below the major one. First major value is at 1,
			// and the minor ticks start at 2 (1 is the previous major)
			if (includeMinorTicks && oom > 0) {
				for (float minorValue = 2; minorValue <= 9; minorValue++) {
					ticks.add((int)(  Math.pow(10, oom-1) * minorValue  ));
				}
			}
			
			int value = (int)Math.pow(10, oom);
			ticks.add(value);
		}
		
		return ticks;
		
	}
	
	
}
