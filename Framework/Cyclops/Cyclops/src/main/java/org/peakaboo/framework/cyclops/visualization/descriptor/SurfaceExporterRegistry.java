package org.peakaboo.framework.cyclops.visualization.descriptor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SurfaceExporterRegistry {

	private SurfaceExporterRegistry() {}
	
	private static Map<String, SurfaceDescriptor> exporters = new LinkedHashMap<>();
	
	public static void registerExporter(SurfaceDescriptor exporter) {
		exporters.put(exporter.extension().toLowerCase(), exporter);
	}
	
	public static List<SurfaceDescriptor> exporters() {
		return new ArrayList<>(exporters.values());
	}
		
}
