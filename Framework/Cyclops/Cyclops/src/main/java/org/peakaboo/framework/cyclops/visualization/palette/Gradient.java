package org.peakaboo.framework.cyclops.visualization.palette;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
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

	public Gradient(String name, String resourcePath) {
		this.name = name;
		try {
			String csvContent = Files.readString(Paths.get(getClass().getResource(resourcePath).toURI()));
			this.stops = parseCSVContent(csvContent);
		} catch (IOException | URISyntaxException e) {
			throw new RuntimeException(e);
		}

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
	
	private static List<Stop> parseCSVContent(String csvContent) {
		List<Stop> stops = new ArrayList<>();
		String[] lines = csvContent.trim().split("\\r?\\n");
		
		// Auto-detect format based on whether CSV contains decimal points
		boolean useFloatFormat = csvContent.contains(".");
		
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i].trim();
			if (line.isEmpty()) {
				continue;
			}
			
			String[] values = line.split(",");
			if (values.length != 3) {
				throw new IllegalArgumentException("Each line must contain exactly 3 comma-separated values (R,G,B)");
			}
			
			try {
				int r, g, b;
				
				if (useFloatFormat) {
					r = (int) Math.round(Double.parseDouble(values[0].trim()) * 255);
					g = (int) Math.round(Double.parseDouble(values[1].trim()) * 255);
					b = (int) Math.round(Double.parseDouble(values[2].trim()) * 255);
				} else {
					r = Integer.parseInt(values[0].trim());
					g = Integer.parseInt(values[1].trim());
					b = Integer.parseInt(values[2].trim());
				}

                if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255) {
                    throw new IllegalArgumentException("RGB values must be between 0 and 255");
                }
				
				int argb = 0xff000000 | (r << 16) | (g << 8) | b;
				float position = lines.length == 1 ? 0.0f : (float) i / (lines.length - 1);
				
				stops.add(new Stop(argb, position));
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Invalid number format in CSV content: " + line, e);
			}
		}
		
		if (stops.isEmpty()) {
			throw new IllegalArgumentException("CSV content must contain at least one RGB line");
		}
		
		return stops;
	}
	
	
}
