package org.peakaboo.framework.cyclops.visualization.palette;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a set of colour stops and the fading transition of colours between them
 */
public class Gradient {

	public static final int DEFAULT_STEPS = 1000;
	
	/**
	 * Represents a single colour stop in a gradient. The position value's range is 0 <= position <= 1 
	 */
	public static record Stop(PaletteColour colour, float position) {
		public Stop(int argb, float position) {
			this(new PaletteColour(argb), position);
		}
	};

	private List<Stop> stops;
	private String name;

	public Gradient(String name, List<Stop> stops) {
		this.stops = stops;
		this.name = name;
	}

	public Gradient(String name, Stop... stops) {
		this.stops = List.of(stops);
		this.name = name;
	}
	
	public List<PaletteColour> toList(int steps) {
		return extrapolateStops(steps, this);
	}
	
	public List<Stop> getStops() {
		return Collections.unmodifiableList(this.stops);
	}
	
	
	
	
	public static List<PaletteColour> extrapolateStops(float totalSteps, Gradient gradient) {
		return extrapolateStops(totalSteps, gradient.getStops());
	}
	
	/**
	 * Generates a list of {@link PaletteColour}s from gradient stops
	 */
	public static List<PaletteColour> extrapolateStops(float totalSteps, List<Stop> stops)
	{
		
		// Sort the input stops by position
		List<Stop> sortedStops = new ArrayList<>(stops);
		sortedStops.sort((a, b) -> Float.compare(a.position, b.position));
		stops = sortedStops;
		
		// If there aren't stops at the start and end of the gradient, add implicit stops
		// at those locations 
		Stop firstStop = stops.get(0);
		Stop lastStop = stops.get(stops.size()-1);
		if (firstStop.position > 0f) {
			stops.add(0, new Gradient.Stop(firstStop.colour, 0f));
		}
		if (lastStop.position < 1f) {
			stops.add(new Gradient.Stop(lastStop.colour, 1f));
		}
		
		// Output list for computed colour steps
		List<PaletteColour> spectrum = new ArrayList<>();
		

		// Number of steps in the output list between each gradient stop
		int[] stepsBetween = new int[stops.size() - 1];
		
		
		/*
		 * Fill the stepsBetween array with the number of steps between each colour stop.
		 * To eliminate rounding error, we count the number of steps taken so far, and
		 * adjust the final segment in order to make the real total equal the desired
		 * number of steps
		 */
		int stepsUsed = 0;
		for (int i = 0; i < stepsBetween.length - 1;  i++) {
			int nextStopStep = Math.round(stops.get(i+1).position * totalSteps);
			int newSteps = nextStopStep - stepsUsed;
			stepsBetween[i] = newSteps;
			stepsUsed += stepsBetween[i]; 
		}
		stepsBetween[stepsBetween.length - 1] += (totalSteps - stepsUsed);


		
		/*
		 * For each entry in the stepcount array, we create the required number of
		 * intermediate colours
		 */
		int steps;
		double percent;
		for (int stage = 0; stage < stops.size() - 1; stage++) {

			PaletteColour previous = stops.get(stage).colour;
			PaletteColour next = stops.get(stage+1).colour;
			
			steps = stepsBetween[stage];

			//create 'steps' intermediate colours
			for (int step = 0; step < steps; step++) {
				//how far along from the start colour to the end colour are we?
				percent = (float) step / (float) steps;
				PaletteColour blend = previous.blend(next, percent);
				spectrum.add(blend); 
			}

		}

		return spectrum;
	
	}

	public String getName() {
		return name;
	}
	
	
}
