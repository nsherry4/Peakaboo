package peakaboo.controller.mapper.maptab;

import java.util.List;
import java.util.Map;


import peakaboo.curvefit.peaktable.TransitionSeries;
import peakaboo.datatypes.DataTypeFactory;
import peakaboo.mapping.colours.OverlayColour;


public class TabModel {
	
	public Map<TransitionSeries, Integer> ratioSide;
	public Map<TransitionSeries, OverlayColour> overlayColour;
	public Map<TransitionSeries, Boolean> visible;
	
	public MapScaleMode mapScaleMode;
	
	public MapDisplayMode displayMode;
	
	
	public TabModel(List<TransitionSeries> tss){
					
		displayMode = MapDisplayMode.COMPOSITE;
		mapScaleMode = MapScaleMode.ABSOLUTE;
		
		ratioSide = DataTypeFactory.<TransitionSeries, Integer>map();
		overlayColour = DataTypeFactory.<TransitionSeries, OverlayColour>map();
		visible = DataTypeFactory.<TransitionSeries, Boolean>map();
		
		for (TransitionSeries ts : tss)
		{
			ratioSide.put(ts, 1);
			overlayColour.put(ts, OverlayColour.RED);
			visible.put(ts, true);
		}
		
	}

	

	
}
