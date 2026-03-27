package org.peakaboo.display.plot;

import org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.plot.PlotPalette;
import org.peakaboo.framework.cyclops.visualization.palette.PaletteColour;

public class PlotPalettes {


	public static final PlotPalette SELECTION_COLOUR = new PlotPalette() {{
		this.fitFill = new PaletteColour(0x801c71d8);
		this.fitStroke = new PaletteColour(0xff1a5fb4);
		this.sumStroke = new PaletteColour(0xff1a5fb4);
		this.labelText = new PaletteColour(0xffffffff);
		this.labelBackground = this.fitStroke;
		this.labelStroke = this.fitStroke;
		this.markings = this.fitStroke;
	}};

	public static final PlotPalette SELECTION_MONO = new PlotPalette() {{
		this.fitFill = new PaletteColour(0x60ffffff);
		this.fitStroke = new PaletteColour(0x80ffffff);
		this.sumStroke = new PaletteColour(0xFF777777);
		this.labelText = new PaletteColour(0xffffffff);
		this.labelBackground = new PaletteColour(0x80000000);
		this.labelStroke = new PaletteColour(0xA0000000);
		this.markings = this.fitStroke;
	}};


	public static final PlotPalette PROPOSAL_COLOUR = new PlotPalette() {{
		this.fitFill = new PaletteColour(0xA09141ac);
		this.fitStroke = new PaletteColour(0xA0613583);
		this.sumStroke = new PaletteColour(0xD0613583);
		this.labelText = new PaletteColour(0xffffffff);
		this.labelBackground = new PaletteColour(0xFF613583);
		this.labelStroke = this.labelBackground;
		this.markings = this.fitStroke;
	}};

	public static final PlotPalette PROPOSAL_MONO = new PlotPalette() {{
		this.fitFill = new PaletteColour(0x40ffffff);
		this.fitStroke = new PaletteColour(0x80ffffff);
		this.sumStroke = new PaletteColour(0xD0ffffff);
		this.labelText = new PaletteColour(0xFF444444);
		this.labelBackground = new PaletteColour(0xffffffff);
		this.labelStroke = this.labelText;
		this.markings = this.fitStroke;
	}};



	public static final PlotPalette FITTING_LIGHT = new PlotPalette() {{
		this.fitFill = new PaletteColour(0x40000000);
		this.fitStroke = new PaletteColour(0xA0000000);
		this.sumStroke = new PaletteColour(0xD0000000);
		this.labelText = new PaletteColour(0xC0000000);
		this.labelBackground = new PaletteColour(0xffffffff);
		this.labelStroke = this.labelText;
		this.markings = this.fitStroke;
	}};

	public static final PlotPalette FITTING_DARK = new PlotPalette() {{
		this.fitFill = new PaletteColour(0x40ffffff);
		this.fitStroke = new PaletteColour(0xA0ffffff);
		this.sumStroke = new PaletteColour(0xD0ffffff);
		this.labelText = new PaletteColour(0xC0ffffff);
		this.labelBackground = new PaletteColour(0xff000000);
		this.labelStroke = this.labelText;
		this.markings = this.fitStroke;
	}};

	public static final PlotPalette FITTING_MONO = new PlotPalette() {{
		this.fitFill = new PaletteColour(0x40000000);
		this.fitStroke = new PaletteColour(0x80000000);
		this.sumStroke = new PaletteColour(0xD0000000);
		this.labelText = new PaletteColour(0xFF000000);
		this.labelBackground = new PaletteColour(0xffffffff);
		this.labelStroke = this.labelText;
		this.markings = this.fitStroke;
	}};

}