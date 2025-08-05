package org.peakaboo.framework.cyclops.visualization.drawing.map.painters;

import java.util.ArrayList;
import java.util.List;

import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.cyclops.visualization.palette.Palette;


public class MapTechniqueFactory {

	private MapTechniqueFactory() {}

	public static SpectrumMapPainter getTechnique(Palette colourRule, Spectrum data) {
		return new RasterSpectrumMapPainter(colourRule, data);
	}
	
}