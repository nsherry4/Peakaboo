package peakaboo.drawing.map.painters;


import java.awt.Color;
import java.util.List;

import peakaboo.datatypes.DataTypeFactory;
import peakaboo.drawing.map.Map;
import peakaboo.drawing.map.palettes.AbstractPalette;
import peakaboo.drawing.painters.Painter;
import peakaboo.drawing.painters.PainterData;

/**
 * 
 * A MapPainter is a specific way of drawing the {@link Map}
 * 
 * @author Nathaniel Sherry, 2009
 * @see Map
 *
 */

public abstract class MapPainter extends Painter
{

	protected List<Double>			data;
	protected List<AbstractPalette>	colourRules;


	public MapPainter(List<AbstractPalette> colourRules, List<Double> data)
	{
		this.colourRules = colourRules;
		this.data = data;

	}
	
	public MapPainter(AbstractPalette colourRule, List<Double> data)
	{
		List<AbstractPalette> rules = DataTypeFactory.<AbstractPalette>list();
		rules.add(colourRule);
		this.colourRules = rules;
		this.data = data;
	}


	@Override
	protected double getBaseUnitSize(peakaboo.drawing.DrawingRequest dr)
	{
		// TODO Auto-generated method stub
		return 1;
	}
	
	public Color getColourFromRules(double intensity, double maximum)
	{

		Color c;
		
		for (AbstractPalette r : colourRules) {
			c = r.getFillColour(intensity, maximum);
			if (c != null) return c;
		}

		return new Color(0.0f, 0.0f, 0.0f, 0.0f);

	}
	
	public void setPalette(AbstractPalette palette)
	{
		colourRules.clear();
		colourRules.add(palette);
	}
	public void setPalettes(List<AbstractPalette> palettes)
	{
		colourRules.clear();
		colourRules.addAll(palettes);
	}

	
}
