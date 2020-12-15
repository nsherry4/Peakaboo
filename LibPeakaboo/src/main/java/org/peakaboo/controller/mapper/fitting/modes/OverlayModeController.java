package org.peakaboo.controller.mapper.fitting.modes;

import static java.util.stream.Collectors.toList;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.display.map.MapScaleMode;
import org.peakaboo.display.map.modes.overlay.OverlayChannel;
import org.peakaboo.display.map.modes.overlay.OverlayColour;
import org.peakaboo.display.map.modes.overlay.OverlayModeData;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.Pair;
import org.peakaboo.framework.cyclops.Spectrum;
import org.peakaboo.framework.cyclops.SpectrumCalculations;

public class OverlayModeController extends ModeController {

	private Map<ITransitionSeries, OverlayColour> colours = new LinkedHashMap<>();
	
	public OverlayModeController(MappingController map) {
		super(map);
		
		for (ITransitionSeries ts : map.rawDataController.getMapResultSet().getAllTransitionSeries()) {
			colours.put(ts, OverlayColour.RED);
		}
	}


	public OverlayModeData getData()
	{
		
		
		List<Pair<ITransitionSeries, Spectrum>> dataset = getVisible().stream()
				.map(ts -> new Pair<>(ts, sumSingleMap(ts)))
				.collect(toList());
				

		Map<OverlayColour, Spectrum> valueFunctionMaps = new HashMap<>();
		Map<OverlayColour, OverlayChannel> colourChannels = new HashMap<>();
		
		for (OverlayColour colour : OverlayColour.values()) {
			Spectrum colourSpectrum;
			//get the TSs for this colour, and get their combined spectrum
			List<Spectrum> colourSpectrums = dataset.stream()
					.filter(e -> (this.colours.get(e.first) == colour))
					.map(e -> e.second)
					.collect(toList());

			List<ITransitionSeries> colourTS = dataset.stream()
					.filter(e -> (this.colours.get(e.first) == colour))
					.map(e -> e.first)
					.collect(toList());
			
			if (colourSpectrums != null && !colourSpectrums.isEmpty()) {
				colourSpectrum = colourSpectrums.stream().reduce(SpectrumCalculations::addLists).get();
				valueFunctionMaps.put(colour, colourSpectrum);
			} else {
				colourSpectrum = null;
			}
			
			if (getMap().getFitting().getMapScaleMode() == MapScaleMode.RELATIVE && colourSpectrum != null) {
				SpectrumCalculations.normalize_inplace(colourSpectrum);
			}
						
			colourChannels.put(colour, new OverlayChannel(colourSpectrum, colourTS));
			
		}

		int w = getMap().getFiltering().getFilteredDataWidth();
		int h = getMap().getFiltering().getFilteredDataHeight();
		Coord<Integer> size = new Coord<>(w, h);
		boolean relative = getMap().getFitting().getMapScaleMode() == MapScaleMode.RELATIVE;
		return new OverlayModeData(colourChannels, size, relative);
		
	}
	
	
	
	@Override
	public String longTitle() {
		return "Overlay of " + getDatasetTitle(getVisible());	
	}
	
	


	public OverlayColour getColour(ITransitionSeries ts)
	{
		return this.colours.get(ts);
	}
	public void setColour(ITransitionSeries ts, OverlayColour c)
	{
		this.colours.put(ts, c);
		updateListeners();
	}

	@Override
	public boolean isTranslatableToSpatial() {
		return true;
	}
	
	//This is not comparable because each pixel has more than one value
	@Override
	public boolean isComparable() {
		return true;
	}
	
}
