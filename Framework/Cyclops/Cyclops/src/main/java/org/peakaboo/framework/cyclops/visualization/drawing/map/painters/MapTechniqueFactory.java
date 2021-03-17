package org.peakaboo.framework.cyclops.visualization.drawing.map.painters;

import java.util.ArrayList;
import java.util.List;

import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.cyclops.visualization.palette.palettes.AbstractPalette;
import org.peakaboo.framework.cyclops.visualization.palette.palettes.ThermalScalePalette;


public class MapTechniqueFactory
{

	public static SpectrumMapPainter getTechnique(List<AbstractPalette> colourRules, Spectrum data, int contourSteps)
	{
		return new RasterSpectrumMapPainter(colourRules, data);
	}
	
	public static SpectrumMapPainter getTechnique(AbstractPalette colourRule, Spectrum data, int contourSteps)
	{
		List<AbstractPalette> colourRules = new ArrayList<AbstractPalette>();
		colourRules.add(colourRule);
		
		return getTechnique(colourRules, data, contourSteps);
	}
	
	public static SpectrumMapPainter getDefaultTechnique(Spectrum data)
	{
		AbstractPalette palette = new ThermalScalePalette();
		List<AbstractPalette> paletteList = new ArrayList<AbstractPalette>();
		paletteList.add(palette);
		return new RasterSpectrumMapPainter(paletteList, data);
	}
	
}