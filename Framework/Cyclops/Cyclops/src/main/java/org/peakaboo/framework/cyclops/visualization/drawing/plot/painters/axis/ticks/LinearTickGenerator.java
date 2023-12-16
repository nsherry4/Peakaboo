package org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.axis.ticks;

import java.util.ArrayList;
import java.util.List;

public class LinearTickGenerator implements TickGenerator {

	private static final float[] STEPS = new float[]{1f, 2f, 2.5f, 5f};
	
	@Override
	public List<Integer> getTicks(float maxValue, int maxTicks, boolean includeMinorTicks) {
		int magnitude = (int) Math.ceil(Math.log10(1+maxValue));
		
		int stepsDown = 1;
		List<Integer> ticks = null;
		while (stepsDown < STEPS.length * 3) {
			var maybeTicks = getTicksByMagitude(magnitude, stepsDown, maxValue);
			if (maybeTicks.size() <= maxTicks || ticks == null) {
				ticks = maybeTicks;
			} else if (maybeTicks.size() > maxTicks) {
				return ticks;
			}
			stepsDown++;
		}
		float x = 1;
		return ticks;

	}
	
	private List<Integer> getTicksByMagitude(int magnitude, int stepsDown, float maxValue) {
		float mult = calcMultiplier(magnitude, stepsDown);
		
		List<Integer> ticks = new ArrayList<>();
		int i = 0;
		while (true) {
			int value = (int)(Math.round(mult * i));
			if (value > maxValue) {
				break;
			}
			ticks.add(value);
			i++;
		}
		return ticks;
	}
	
	private float calcMultiplier(int magnitude, int stepsDown) {
		int ooms = (int) Math.ceil(stepsDown / (float)STEPS.length);
		int partial = STEPS.length - (stepsDown % STEPS.length);
		if (partial == STEPS.length) {
			partial = 0;
		}
		float multOom = (float) Math.pow(10, magnitude - ooms);
		float mult = multOom * STEPS[partial];
		return mult;
		
	}
	
	
	

}
