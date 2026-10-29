package org.peakaboo.framework.cyclops.visualization.backend.awt.surfaces;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

import org.peakaboo.framework.cyclops.log.CyclopsLog;
import org.peakaboo.framework.cyclops.visualization.ChartFonts;
import org.peakaboo.framework.cyclops.visualization.backend.awt.surfaces.png.PNGDescriptor;
import org.peakaboo.framework.cyclops.visualization.backend.awt.surfaces.svg.SVGDescriptor;
import org.peakaboo.framework.cyclops.visualization.descriptor.SurfaceExporterRegistry;

public class CyclopsSurface {

	private CyclopsSurface() {}

	private static boolean initted = false;
	public static synchronized void init() {
		if (initted) return;
		initted = true;
		// Register our custom, cross-platform fonts
		if (registerFont(ChartFonts.CHART_SANS_FAMILY, ChartFonts.CHART_SANS_PATH)) AbstractGraphicsSurface.FONT_SANS = ChartFonts.CHART_SANS_FAMILY;
		if (registerFont(ChartFonts.CHART_MONO_FAMILY, ChartFonts.CHART_MONO_PATH)) AbstractGraphicsSurface.FONT_MONO = ChartFonts.CHART_MONO_FAMILY;
		SurfaceExporterRegistry.registerExporter(new PNGDescriptor());
		SurfaceExporterRegistry.registerExporter(new SVGDescriptor());
	}

	// Registers the regular, bold and italic styles of a given bundled font, returning false
	// if any of them can't be loaded.
	private static boolean registerFont(String family, String pathFormat) {
		try {
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			for (String style : ChartFonts.STYLES) {
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
