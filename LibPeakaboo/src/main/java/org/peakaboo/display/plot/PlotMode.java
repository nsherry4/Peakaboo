package org.peakaboo.display.plot;

/**
 * Represents the four visual states for plot rendering, combining
 * colour/monochrome with light/dark background modes.
 */
public enum PlotMode {

	COLOUR_LIGHT,
	COLOUR_DARK,
	MONO_LIGHT,
	MONO_DARK;

	public static PlotMode from(PlotSettings settings) {
		if (settings.monochrome) {
			return settings.darkmode ? MONO_DARK : MONO_LIGHT;
		}
		return settings.darkmode ? COLOUR_DARK : COLOUR_LIGHT;
	}

	public boolean isDark() {
		return this == COLOUR_DARK || this == MONO_DARK;
	}

	public boolean isMono() {
		return this == MONO_LIGHT || this == MONO_DARK;
	}

}
