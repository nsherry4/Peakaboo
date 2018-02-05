package peakaboo.controller.mapper.mapdisplay;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import peakaboo.curvefit.model.transitionseries.TransitionSeries;
import peakaboo.mapping.colours.OverlayColour;


public class MapDisplaySettings {
	
	public Map<TransitionSeries, Integer> ratioSide;
	public Map<TransitionSeries, OverlayColour> overlayColour;
	public Map<TransitionSeries, Boolean> visible;
	
	public MapScaleMode mapScaleMode;
	
	public MapDisplayMode displayMode;
	
	
	public MapDisplaySettings(List<TransitionSeries> tss){
					
		displayMode = MapDisplayMode.COMPOSITE;
		mapScaleMode = MapScaleMode.ABSOLUTE;
		
		ratioSide = new HashMap<TransitionSeries, Integer>();
		overlayColour = new HashMap<TransitionSeries, OverlayColour>();
		visible = new HashMap<TransitionSeries, Boolean>();
		
		for (TransitionSeries ts : tss)
		{
			ratioSide.put(ts, 1);
			overlayColour.put(ts, OverlayColour.RED);
			visible.put(ts, true);
		}
		
	}

	

	
}
