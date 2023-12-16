package org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.axis.ticks;

import java.util.ArrayList;
import java.util.List;

public class LogTickGenerator implements TickGenerator {

	@Override
	public List<Integer> getTicks(float maxValue, int maxTicks, boolean includeMinorTicks) {
		int magnitude = (int) Math.ceil(Math.log10(1+maxValue));
		
		//minor value has to start at 1 for the first series of minor ticks because there's no major line below 1  
		float minorValueStart = 1;
		List<Integer> ticks = new ArrayList<>();
		for (int oom = 0; oom <= magnitude; oom++) {
			
			//Optionally generate minor ticks below the major one
			if (includeMinorTicks) {
				for (float minorValue = minorValueStart; minorValue <= 9; minorValue++) {
					ticks.add((int)(  Math.pow(10, oom-1) * minorValue  ));
				}
				minorValueStart = 2;
			}
			
			int value = (int)Math.pow(10, oom);
			ticks.add(value);
		}
		
		return ticks;
		
	}
	
	
}
