package org.peakaboo.display.plot;

import org.peakaboo.framework.cyclops.visualization.palette.PaletteColour;

/**
 * Structural/decorative colours for the plot that are not specific to a
 * fitting role: background, axes, gridlines, and data fill/stroke.
 */
public record PlotChrome(
	PaletteColour background,
	PaletteColour axisForeground,
	PaletteColour gridHMajor,
	PaletteColour gridHMinor,
	PaletteColour gridVMajor,
	PaletteColour dataFill,
	PaletteColour dataStroke,
	PaletteColour originalDataColour,
	PaletteColour filterPreviewFill,
	PaletteColour filterPreviewStroke
) {

	private static final PlotChrome COLOUR_LIGHT = new PlotChrome(
		new PaletteColour(0xffffffff),
		new PaletteColour(0xff000000),
		new PaletteColour(0x28000000),
		new PaletteColour(0x10000000),
		new PaletteColour(0x08000000),
		new PaletteColour(0xff4ba67c),
		new PaletteColour(0xff186642),
		new PaletteColour(0x60D32F2F),
		new PaletteColour(0x7f5C3666),
		new PaletteColour(0xff5C3666)
	);

	private static final PlotChrome COLOUR_DARK = new PlotChrome(
		new PaletteColour(0xff202020),
		new PaletteColour(0xffe0e0e0),
		new PaletteColour(0x28ffffff),
		new PaletteColour(0x10ffffff),
		new PaletteColour(0x08ffffff),
		new PaletteColour(0xff4ba67c),
		new PaletteColour(0xff186642),
		new PaletteColour(0x60D32F2F),
		new PaletteColour(0x7f5C3666),
		new PaletteColour(0xff5C3666)
	);

	private static final PlotChrome MONO_LIGHT = new PlotChrome(
		new PaletteColour(0xffffffff),
		new PaletteColour(0xff000000),
		new PaletteColour(0x28000000),
		new PaletteColour(0x10000000),
		new PaletteColour(0x08000000),
		new PaletteColour(0xffa0a0a0),
		new PaletteColour(0xff202020),
		new PaletteColour(0x7f000000),
		new PaletteColour(0x7f000000),
		new PaletteColour(0xff000000)
	);

	private static final PlotChrome MONO_DARK = new PlotChrome(
		new PaletteColour(0xff202020),
		new PaletteColour(0xffe0e0e0),
		new PaletteColour(0x28ffffff),
		new PaletteColour(0x10ffffff),
		new PaletteColour(0x08ffffff),
		new PaletteColour(0xff606060),
		new PaletteColour(0xffc0c0c0),
		new PaletteColour(0x7fffffff),
		new PaletteColour(0x7f303030),
		new PaletteColour(0xff303030)
	);

	public static PlotChrome forMode(PlotMode mode) {
		return switch (mode) {
			case COLOUR_LIGHT -> COLOUR_LIGHT;
			case COLOUR_DARK -> COLOUR_DARK;
			case MONO_LIGHT -> MONO_LIGHT;
			case MONO_DARK -> MONO_DARK;
		};
	}

}
