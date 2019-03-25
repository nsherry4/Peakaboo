package org.peakaboo.display.map.modes.ratio;

import java.util.ArrayList;
import java.util.List;

import org.peakaboo.display.map.MapRenderData;
import org.peakaboo.display.map.MapRenderSettings;
import org.peakaboo.display.map.modes.MapModes;
import org.peakaboo.display.map.modes.MapMode;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.Pair;
import org.peakaboo.framework.cyclops.Ratios;
import org.peakaboo.framework.cyclops.Spectrum;
import org.peakaboo.framework.cyclops.SpectrumCalculations;
import org.peakaboo.framework.cyclops.visualization.Surface;
import org.peakaboo.framework.cyclops.visualization.drawing.ViewTransform;
import org.peakaboo.framework.cyclops.visualization.drawing.map.painters.MapPainter;
import org.peakaboo.framework.cyclops.visualization.drawing.map.painters.MapTechniqueFactory;
import org.peakaboo.framework.cyclops.visualization.drawing.map.painters.SelectionMaskPainter;
import org.peakaboo.framework.cyclops.visualization.drawing.map.painters.SpectrumMapPainter;
import org.peakaboo.framework.cyclops.visualization.drawing.map.painters.axis.SpectrumCoordsAxisPainter;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.axis.AxisPainter;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.axis.PaddingAxisPainter;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.axis.TitleAxisPainter;
import org.peakaboo.framework.cyclops.visualization.palette.PaletteColour;
import org.peakaboo.framework.cyclops.visualization.palette.palettes.AbstractPalette;
import org.peakaboo.framework.cyclops.visualization.palette.palettes.SaturationPalette;

public class RatioMapMode extends MapMode {

	private SpectrumMapPainter ratioMapPainter;
	private SelectionMaskPainter selectionPainter;
	
	@Override
	public void draw(Coord<Integer> size, MapRenderData data, MapRenderSettings settings, Surface backend, int spectrumSteps) {
		map.setContext(backend);
		
		size = this.setDimensions(settings, size);
		backend.rectAt(0, 0, (float)size.x, (float)size.y);
		backend.setSource(new PaletteColour(0xffffffff));
		backend.fill();
		
		AxisPainter spectrumCoordPainter = null;
		List<AbstractPalette> paletteList = new ArrayList<AbstractPalette>();
		

		Pair<Spectrum, Spectrum> ratiodata = data.ratioData;
		
		dr.uninterpolatedWidth = settings.filteredDataWidth;
		dr.uninterpolatedHeight = settings.filteredDataHeight;
		dr.dataWidth = settings.filteredDataWidth;
		dr.dataHeight = settings.filteredDataHeight;
		//LOG view not supported
		dr.viewTransform = ViewTransform.LINEAR;
		dr.screenOrientation = false;
		
		
		//this is a valid ratio if there is at least 1 visible TS
		boolean validRatio = ratiodata.first.sum() != 0;
		
		
		//how many steps/markings will we display on the spectrum
		float steps = (float) Math.ceil(SpectrumCalculations.abs(ratiodata.first).max());
		dr.maxYIntensity = steps;
		
		
		
		//if this is a valid ratio, make a real colour palette -- otherwise, just a black palette
		if (validRatio)
		{
			paletteList.add(new RatioPalette(spectrumSteps, settings.monochrome));
		}
		
		
		
		//generate a list of markers to be drawn along the spectrum to indicate the ratio at those points
		List<Pair<Float, String>> spectrumMarkers = new ArrayList<Pair<Float, String>>();

		int increment = 1;
		if (steps > 8) increment = (int) Math.ceil(steps / 8);

		if (validRatio)
		{
			for (int i = -(int) steps; i <= (int) steps; i += increment)
			{
				float percent = 0.5f + 0.5f * (i / steps);				
				spectrumMarkers.add(new Pair<Float, String>(percent, Ratios.fromFloat(i, true)));
			}
		}
		
		
		

		List<AxisPainter> axisPainters = new ArrayList<AxisPainter>();
		
		super.setupTitleAxisPainters(settings, axisPainters);
		axisPainters.add(new PaddingAxisPainter(0, 0, 10, 0));
		axisPainters.add(getDescriptionPainter(settings));
		axisPainters.add(super.getSpectrumPainter(settings, spectrumSteps, paletteList, true, spectrumMarkers));
		map.setAxisPainters(axisPainters);
		
		
		
		boolean oldVector = dr.drawToVectorSurface;
		dr.drawToVectorSurface = backend.isVectorSurface();
		map.setDrawingRequest(dr);


		
		List<MapPainter> mapPainters = new ArrayList<MapPainter>();
		if (ratioMapPainter == null) {
			ratioMapPainter = MapTechniqueFactory.getTechnique(paletteList, ratiodata.first, spectrumSteps); 
		} else {
			ratioMapPainter.setData(ratiodata.first);
			ratioMapPainter.setPalettes(paletteList);
		}
		mapPainters.add(ratioMapPainter);
		
		

				
		
		Spectrum invalidPoints = ratiodata.second;
		final float datamax = dr.maxYIntensity;
		
		
		invalidPoints.map_i((Float value) -> {
			if (value == 1f) return datamax;
			return 0f;
		});
		

		MapPainter invalidPainter = MapTechniqueFactory.getTechnique(new SaturationPalette(new PaletteColour(0xff777777), new PaletteColour(0x00000000)), invalidPoints, 0);
		mapPainters.add(invalidPainter);
		
		
		//Selection Painter
		if (selectionPainter == null) {
			selectionPainter = new SelectionMaskPainter(new PaletteColour(0xffffffff), settings.selectedPoints, settings.userDataWidth, settings.userDataHeight);
		} else {
			selectionPainter.configure(settings.userDataWidth, settings.userDataHeight, settings.selectedPoints);
		}
		mapPainters.add(selectionPainter);
		
		
		map.setPainters(mapPainters);
		map.draw();

		dr.drawToVectorSurface = oldVector;

	}

	@Override
	public MapModes getMode() {
		return MapModes.RATIO;
	}

	@Override
	public void invalidate() {
		map.needsMapRepaint();
		if (ratioMapPainter != null) { ratioMapPainter.clearBuffer(); }
	}
	
}
