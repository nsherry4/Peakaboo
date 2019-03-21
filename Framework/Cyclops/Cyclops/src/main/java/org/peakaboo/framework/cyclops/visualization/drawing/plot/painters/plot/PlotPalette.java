package org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.plot;

import org.peakaboo.framework.cyclops.visualization.palette.PaletteColour;

public class PlotPalette {
	public PaletteColour labelText;
	public PaletteColour labelBackground;
	public PaletteColour labelStroke;
	
	public PaletteColour fitFill;
	public PaletteColour fitStroke;
	public PaletteColour sumStroke;
	
	public PaletteColour markings;
	
	public static PlotPalette blackOnWhite() {
		PlotPalette p = new PlotPalette();
		p.fitFill = new PaletteColour(0x50000000);
		p.fitStroke = new PaletteColour(0x80000000);
		p.sumStroke = new PaletteColour(0xD0000000);
		p.labelText = p.fitStroke;
		p.labelBackground = new PaletteColour(0xffffffff);
		p.labelStroke = p.labelText;
		p.markings = p.fitStroke;
		
		return p;
	}
}