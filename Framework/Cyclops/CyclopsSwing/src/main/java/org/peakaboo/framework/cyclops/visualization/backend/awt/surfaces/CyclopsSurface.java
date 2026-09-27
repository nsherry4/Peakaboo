package org.peakaboo.framework.cyclops.visualization.backend.awt.surfaces;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;

import org.peakaboo.framework.cyclops.log.CyclopsLog;
import org.peakaboo.framework.cyclops.visualization.backend.awt.surfaces.png.PNGDescriptor;
import org.peakaboo.framework.cyclops.visualization.backend.awt.surfaces.svg.SVGDescriptor;
import org.peakaboo.framework.cyclops.visualization.descriptor.SurfaceExporterRegistry;

public class CyclopsSurface {

	private static final List<String> STYLES = List.of("Regular", "Bold", "Italic", "BoldItalic");

	// TeX Gyre Heros, renamed; see the README next to the fonts
	private static final String CHART_SANS_FAMILY = "Peakaboo Chart Sans";
	private static final String CHART_SANS_PATH = "/org/peakaboo/framework/cyclops/fonts/peakaboo-chart-sans/PeakabooChartSans-%s.otf";

	// JetBrains Mono, de-hinted and renamed. This font is shared
	// with Stratus, so they need to be shipped together.
	private static final String CHART_MONO_FAMILY = "Peakaboo Mono";
	private static final String CHART_MONO_PATH = "/stratus/fonts/peakaboo-mono/PeakabooMono-%s.ttf";

	private CyclopsSurface() {}

	private static boolean initted = false;
	public static synchronized void init() {
		if (initted) return;
		initted = true;
		// Register our custom, cross-platform fonts
		if (registerFont(CHART_SANS_FAMILY, CHART_SANS_PATH)) AbstractGraphicsSurface.FONT_SANS = CHART_SANS_FAMILY;
		if (registerFont(CHART_MONO_FAMILY, CHART_MONO_PATH)) AbstractGraphicsSurface.FONT_MONO = CHART_MONO_FAMILY;
		SurfaceExporterRegistry.registerExporter(new PNGDescriptor());
		SurfaceExporterRegistry.registerExporter(new SVGDescriptor());
	}

	// Registers the regular, bold and italic styles of a given bundled font, returning false
	// if any of them can't be loaded.
	private static boolean registerFont(String family, String pathFormat) {
		try {
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			for (String style : STYLES) {
				String path = String.format(pathFormat, style);
				try (InputStream in = CyclopsSurface.class.getResourceAsStream(path)) {
					if (in == null) throw new IOException("Missing font resource " + path);
					ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, in));
				}
			}
			return true;
		} catch (FontFormatException | IOException e) {
			CyclopsLog.get().log(Level.WARNING, "Failed to load chart font " + family + ", falling back to the system font", e);
			return false;
		}
	}

}
