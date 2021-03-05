package org.peakaboo.controller.mapper.fitting.modes;

import static java.util.stream.Collectors.toList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.controller.mapper.fitting.modes.components.GroupState;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.display.map.MapScaleMode;
import org.peakaboo.display.map.modes.overlay.OverlayChannel;
import org.peakaboo.display.map.modes.overlay.OverlayColour;
import org.peakaboo.display.map.modes.overlay.OverlayModeData;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.Pair;
import org.peakaboo.framework.cyclops.Spectrum;
import org.peakaboo.framework.cyclops.SpectrumCalculations;

public class OverlayModeController extends SimpleModeController {

	private GroupState groups;
		
	public OverlayModeController(MappingController map) {
		super(map);
		this.groups = new GroupState(this); 
	}


	public OverlayModeData getData() {
		
		
		List<Pair<ITransitionSeries, Spectrum>> dataset = getVisible().stream()
				.map(ts -> new Pair<>(ts, sumSingleMap(ts)))
				.collect(toList());
				

		Map<OverlayColour, Spectrum> valueFunctionMaps = new HashMap<>();
		Map<OverlayColour, OverlayChannel> colourChannels = new HashMap<>();
		
		for (OverlayColour colour : OverlayColour.values()) {
			Spectrum colourSpectrum;
			//get the TSs for this colour, and get their combined spectrum
			List<Spectrum> colourSpectrums = dataset.stream()
					.filter(e -> (this.getColour(e.first) == colour))
					.map(e -> e.second)
					.collect(toList());

			List<ITransitionSeries> colourTS = dataset.stream()
					.filter(e -> (this.getColour(e.first) == colour))
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

		boolean relative = getMap().getFitting().getMapScaleMode() == MapScaleMode.RELATIVE;
		return new OverlayModeData(colourChannels, getSize(), relative);
		
	}
	
	public Coord<Integer> getSize() {
		int w = getMap().getFiltering().getFilteredDataWidth();
		int h = getMap().getFiltering().getFilteredDataHeight();
		Coord<Integer> size = new Coord<>(w, h);
		return size;
	}
	
	
	@Override
	public String longTitle() {
		return "Overlay of " + getDatasetTitle(getVisible());	
	}

	
	///// Group delegators /////
	public OverlayColour getColour(ITransitionSeries ts) {
		//groups start at 1, but arrays start at 0
		return OverlayColour.values()[groups.getGroup(ts)-1];
	}
	public void setColour(ITransitionSeries ts, OverlayColour c) {
		//ordinal values start at 0, but groups start at 1
		groups.setGroup(ts, c.ordinal()+1);
	}

	
}
