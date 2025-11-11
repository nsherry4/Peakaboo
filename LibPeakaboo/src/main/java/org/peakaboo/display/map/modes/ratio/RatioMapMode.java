package org.peakaboo.display.map.modes.ratio;

import java.util.ArrayList;
import java.util.List;

import org.peakaboo.controller.mapper.fitting.modes.RatioModeController.Ratios;
import org.peakaboo.display.map.MapRenderData;
import org.peakaboo.display.map.MapRenderSettings;
import org.peakaboo.display.map.modes.MapMode;
import org.peakaboo.framework.accent.Coord;
import org.peakaboo.framework.accent.Pair;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.cyclops.spectrum.SpectrumCalculations;
import org.peakaboo.framework.cyclops.visualization.Surface;
import org.peakaboo.framework.cyclops.visualization.drawing.ViewTransform;
import org.peakaboo.framework.cyclops.visualization.drawing.map.painters.MapPainter;
import org.peakaboo.framework.cyclops.visualization.drawing.map.painters.MapTechniqueFactory;
import org.peakaboo.framework.cyclops.visualization.drawing.map.painters.SelectionMaskPainter;
import org.peakaboo.framework.cyclops.visualization.drawing.map.painters.SpectrumMapPainter;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.axis.AxisPainter;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.axis.PaddingAxisPainter;
import org.peakaboo.framework.cyclops.visualization.palette.*;

public class RatioMapMode extends MapMode {

	public static String MODE_NAME = "Ratio";
	
	private SpectrumMapPainter ratioMapPainter;
	
	@Override
	public void draw(Coord<Integer> size, MapRenderData data, MapRenderSettings settings, Surface backend, int spectrumSteps) {
		map.setContext(backend);
		
		backend.rectAt(0, 0, (float)size.x, (float)size.y);
		backend.setSource(settings.getBg());
		backend.fill();


		Pair<Spectrum, Spectrum> ratiodata = ((RatioModeData)data.mapModeData).getData();
		
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
		
		
		
		// if this is a valid ratio, make a real colour palette,
		// otherwise, just a transparent black palette
		Palette palette = new SingleColourPalette(new PaletteColour(0x00000000));
		if (validRatio) {
			palette = new RatioPalette(Gradients.RATIO_THERMAL);
		}
		
		
		
		//generate a list of markers to be drawn along the spectrum to indicate the ratio at those points
		List<Pair<Float, String>> spectrumMarkers = new ArrayList<>();

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
		
		
		

		List<AxisPainter> axisPainters = new ArrayList<>();
		
		super.setupTitleAxisPainters(settings, axisPainters);
		axisPainters.add(new PaddingAxisPainter(0, 0, 10, 0));
		axisPainters.add(getDescriptionPainter(settings));
		axisPainters.add(super.getSpectrumPainter(settings, spectrumSteps, palette, true, spectrumMarkers));
		map.setAxisPainters(axisPainters);
		
		
		
		boolean oldVector = dr.drawToVectorSurface;
		dr.drawToVectorSurface = backend.isVectorSurface();

		
		List<MapPainter> mapPainters = new ArrayList<>();
		if (ratioMapPainter == null) {
			ratioMapPainter = MapTechniqueFactory.getTechnique(palette, ratiodata.first);
		} else {
			ratioMapPainter.setData(ratiodata.first);
			ratioMapPainter.setPalette(palette);
		}
		mapPainters.add(ratioMapPainter);
		


		MapPainter invalidPainter = MapTechniqueFactory.getTechnique(new SaturationPalette(new PaletteColour(0xff777777), new PaletteColour(0x00000000)), ratiodata.second);
		mapPainters.add(invalidPainter);
		
		
		//Selection Painter
		SelectionMaskPainter selectionPainter = super.getSelectionPainter(
				new PaletteColour(0x80ffffff), 
				settings.selectedPoints, 
				settings.userDataWidth, 
				settings.userDataHeight);
		mapPainters.add(selectionPainter);
		
		
		map.setPainters(mapPainters);
		map.draw();

		dr.drawToVectorSurface = oldVector;

	}

	@Override
	public void invalidate() {
		map.needsMapRepaint();
		if (ratioMapPainter != null) { ratioMapPainter.clearBuffer(); }
	}

	@Override
	public String mapModeName() {
		return MODE_NAME;
	}
	
}
