package org.peakaboo.display.plot;

import org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.plot.PlotPalette;
import org.peakaboo.framework.cyclops.visualization.palette.PaletteColour;

/**
 * Centralised palette definitions for plot fitting roles (selection, proposal,
 * fitted). Palettes are derived from a base colour using a shared alpha
 * progression, with hand-tuned monochrome bases for accessibility.
 */
public class PlotPalettes {

	// Colour bases: differentiated by hue
	private static final PaletteColour SELECTION_BASE   = new PaletteColour(0xff2570cc); // blue
	private static final PaletteColour SELECTION_STROKE = new PaletteColour(0xff0d3b6e); // dark blue for fit lines
	private static final PaletteColour PROPOSAL_BASE   = new PaletteColour(0xff613583); // purple

	// Mono bases: hand-tuned for luminance separation between roles.
	// These are deliberately NOT auto-converted from the colour bases —
	// monochrome relies on luminance alone, and automatic conversion would
	// collapse distinct hues into similar grey values.
	private static final PaletteColour SELECTION_MONO_BASE = new PaletteColour(0xffffffff);
	private static final PaletteColour PROPOSAL_MONO_BASE  = new PaletteColour(0xffffffff);


	/**
	 * Build a PlotPalette from a base colour and contrast direction.
	 * The alpha progression is mechanical and shared across all roles.
	 */
	private static PlotPalette build(PaletteColour base, boolean dark) {
		PlotPalette p = new PlotPalette();
		p.fitFill = withAlpha(base, 0x40);
		p.fitStroke = withAlpha(base, 0x80);
		p.sumStroke = withAlpha(base, 0xD0);
		p.markings = p.fitStroke;
		p.labelText = dark ? new PaletteColour(0xC0ffffff) : new PaletteColour(0xC0000000);
		p.labelBackground = dark ? new PaletteColour(0xff000000) : new PaletteColour(0xffffffff);
		p.labelStroke = p.labelText;
		return p;
	}

	/**
	 * Apply tinted label styling — the role's base colour becomes the label
	 * background with white text. Used for selection and proposal roles in
	 * colour mode to give labels a distinctive tint.
	 */
	private static void applyTintedLabels(PlotPalette p, PaletteColour base) {
		p.labelBackground = base;
		p.labelStroke = base;
		p.labelText = new PaletteColour(0xffffffff);
	}


	public static PlotPalette fitting(PlotMode mode) {
		// Fitting uses neutral base: black on light backgrounds, white on dark
		var base = mode.isDark()
			? new PaletteColour(0xffffffff)
			: new PaletteColour(0xff000000);
		return build(base, mode.isDark());
	}

	public static PlotPalette selection(PlotMode mode) {
		var base = mode.isMono() ? SELECTION_MONO_BASE : SELECTION_BASE;
		var p = build(base, mode.isDark());
		// Selection highlights need stronger visibility than fittings.
		// Strokes use a darker blue so fit lines stand out against the
		// green data fill without the semi-transparent area undermining them.
		var stroke = mode.isMono() ? base : SELECTION_STROKE;
		p.fitFill = withAlpha(base, 0x60);
		p.fitStroke = withAlpha(stroke, 0xFF);
		p.sumStroke = withAlpha(stroke, 0xFF);
		p.markings = p.fitStroke;
		if (!mode.isMono()) {
			applyTintedLabels(p, base);
		}
		return p;
	}

	public static PlotPalette proposal(PlotMode mode) {
		var base = mode.isMono() ? PROPOSAL_MONO_BASE : PROPOSAL_BASE;
		var p = build(base, mode.isDark());
		if (!mode.isMono()) {
			applyTintedLabels(p, base);
		}
		return p;
	}


	/**
	 * Create a new PaletteColour with the same RGB channels but a different
	 * alpha value.
	 */
	private static PaletteColour withAlpha(PaletteColour c, int alpha) {
		return new PaletteColour(alpha, c.getRed(), c.getGreen(), c.getBlue());
	}

}
