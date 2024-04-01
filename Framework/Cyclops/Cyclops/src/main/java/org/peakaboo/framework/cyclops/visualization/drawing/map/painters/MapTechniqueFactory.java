package org.peakaboo.framework.cyclops.visualization.drawing.map.painters;

import java.util.ArrayList;
import java.util.List;

import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.cyclops.visualization.palette.palettes.AbstractPalette;
import org.peakaboo.framework.cyclops.visualization.palette.palettes.ThermalScalePalette;


public class MapTechniqueFactory
{

	private MapTechniqueFactory() {}
	
	public static SpectrumMapPainter getTechnique(List<AbstractPalette> colourRules, Spectrum data)
	{
		return new RasterSpectrumMapPainter(colourRules, data);
	}
	
	public static SpectrumMapPainter getTechnique(AbstractPalette colourRule, Spectrum data)
	{
		List<AbstractPalette> colourRules = new ArrayList<>();
		colourRules.add(colourRule);
		
		return getTechnique(colourRules, data);
	}
	
	public static SpectrumMapPainter getDefaultTechnique(Spectrum data)
	{
		AbstractPalette palette = new ThermalScalePalette();
		List<AbstractPalette> paletteList = new ArrayList<>();
		paletteList.add(palette);
		return new RasterSpectrumMapPainter(paletteList, data);
	}
	
}