package org.peakaboo.framework.cyclops.visualization;

import java.util.List;

public class ChartFonts {

	public static final List<String> STYLES = List.of("Regular", "Bold", "Italic", "BoldItalic");

	// TeX Gyre Heros, renamed; see the README next to the fonts
	public static final String CHART_SANS_FAMILY = "Peakaboo Chart Sans";
	public static final String CHART_SANS_PATH = "/org/peakaboo/framework/cyclops/fonts/peakaboo-chart-sans/PeakabooChartSans-%s.otf";

	// JetBrains Mono, de-hinted and renamed. Stratus bundles its own copy.
	public static final String CHART_MONO_FAMILY = "Peakaboo Mono";
	public static final String CHART_MONO_PATH = "/org/peakaboo/framework/cyclops/fonts/peakaboo-mono/PeakabooMono-%s.ttf";

	private ChartFonts() {}

}
