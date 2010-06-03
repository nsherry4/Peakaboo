package peakaboo.drawing.map.painters;

import java.util.List;

import peakaboo.datatypes.DataTypeFactory;
import peakaboo.drawing.map.palettes.AbstractPalette;
import peakaboo.drawing.map.palettes.ThermalScalePalette;


public class MapTechniqueFactory
{

	public static MapPainter getTechnique(List<AbstractPalette> colourRules, List<Double> data, boolean contour, int contourSteps)
	{
		if (contour) return new ContourMapPainter(colourRules, data, contourSteps);
		return new ThreadedRasterMapPainter(colourRules, data);
	}
	
	public static MapPainter getTechnique(AbstractPalette colourRule, List<Double> data, boolean contour, int contourSteps)
	{
		List<AbstractPalette> colourRules = DataTypeFactory.<AbstractPalette>list();
		colourRules.add(colourRule);
		
		return getTechnique(colourRules, data, contour, contourSteps);
	}
	
	public static MapPainter getDefaultTechnique(List<Double> data)
	{
		AbstractPalette palette = new ThermalScalePalette();
		List<AbstractPalette> paletteList = DataTypeFactory.<AbstractPalette>list();
		paletteList.add(palette);
		return new ThreadedRasterMapPainter(paletteList, data);
	}
	
}
