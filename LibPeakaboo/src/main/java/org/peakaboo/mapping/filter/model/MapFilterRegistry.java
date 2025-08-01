package org.peakaboo.mapping.filter.model;

import java.io.File;
import java.util.logging.Level;

import org.peakaboo.app.PeakabooLog;
import org.peakaboo.app.PeakabooPluginRegistry;
import org.peakaboo.framework.bolt.plugin.java.loader.BoltJarDirectoryLoader;
import org.peakaboo.framework.bolt.plugin.java.loader.BoltJavaBuiltinLoader;
import org.peakaboo.mapping.filter.plugin.MapFilterPlugin;
import org.peakaboo.mapping.filter.plugin.plugins.clipping.SignalCapMapFilter;
import org.peakaboo.mapping.filter.plugin.plugins.clipping.SignalOutlierCorrectionMapFilter;
import org.peakaboo.mapping.filter.plugin.plugins.clipping.WeakSignalRemovalMapFilter;
import org.peakaboo.mapping.filter.plugin.plugins.enhancing.SharpenMapFilter;
import org.peakaboo.mapping.filter.plugin.plugins.mathematical.AdditionMapFilter;
import org.peakaboo.mapping.filter.plugin.plugins.mathematical.ElementMultiplyFilter;
import org.peakaboo.mapping.filter.plugin.plugins.mathematical.LogMapFilter;
import org.peakaboo.mapping.filter.plugin.plugins.mathematical.MultiplyMapFilter;
import org.peakaboo.mapping.filter.plugin.plugins.mathematical.NormalizationMapFilter;
import org.peakaboo.mapping.filter.plugin.plugins.mathematical.PowerMapFilter;
import org.peakaboo.mapping.filter.plugin.plugins.sizing.BinningMapFilter;
import org.peakaboo.mapping.filter.plugin.plugins.sizing.EnlargeMapFilter;
import org.peakaboo.mapping.filter.plugin.plugins.smoothing.DenoiseMapFilter;
import org.peakaboo.mapping.filter.plugin.plugins.smoothing.FastAverageMapFilter;
import org.peakaboo.mapping.filter.plugin.plugins.smoothing.WeightedAverageMapFilter;
import org.peakaboo.mapping.filter.plugin.plugins.transforming.DeskewMapFilter;
import org.peakaboo.mapping.filter.plugin.plugins.transforming.HFlipMapFilter;
import org.peakaboo.mapping.filter.plugin.plugins.transforming.Rotate180MapFilter;
import org.peakaboo.mapping.filter.plugin.plugins.transforming.Rotate270MapFilter;
import org.peakaboo.mapping.filter.plugin.plugins.transforming.Rotate90MapFilter;
import org.peakaboo.mapping.filter.plugin.plugins.transforming.VFlipMapFilter;

public class MapFilterRegistry extends PeakabooPluginRegistry<MapFilterPlugin> {

	private static MapFilterRegistry SYSTEM;
	public static void init(File filterDir) {
		try {
			if (SYSTEM == null) {
				SYSTEM = new MapFilterRegistry(filterDir);
				SYSTEM.load();
			}
		} catch (Exception e) {
			PeakabooLog.get().log(Level.SEVERE, "Failed to load map filter plugins", e);
		}
	}
	public static MapFilterRegistry system() {
		return SYSTEM;
	}
	
	//--------------------------------
	
	private MapFilterRegistry(File directories) {
		super("mapfilter");
		
		addLoader(new BoltJarDirectoryLoader<>(this, MapFilterPlugin.class, directories));
		addLoader(new BoltJarDirectoryLoader<>(this, MapFilterPlugin.class));
		
		
		
		var builtins = new BoltJavaBuiltinLoader<>(this, MapFilterPlugin.class);
		
		builtins.load(BinningMapFilter.class);
		builtins.load(EnlargeMapFilter.class);
		
		builtins.load(FastAverageMapFilter.class);
		builtins.load(WeightedAverageMapFilter.class);
		builtins.load(DenoiseMapFilter.class);
		
		builtins.load(SharpenMapFilter.class);
		
		builtins.load(WeakSignalRemovalMapFilter.class);
		builtins.load(SignalOutlierCorrectionMapFilter.class);
		builtins.load(SignalCapMapFilter.class);
					
		builtins.load(MultiplyMapFilter.class);
		builtins.load(AdditionMapFilter.class);
		builtins.load(NormalizationMapFilter.class);
		builtins.load(LogMapFilter.class);
		builtins.load(PowerMapFilter.class);
		builtins.load(ElementMultiplyFilter.class);
		
		builtins.load(VFlipMapFilter.class);
		builtins.load(HFlipMapFilter.class);
		builtins.load(Rotate90MapFilter.class);
		builtins.load(Rotate180MapFilter.class);
		builtins.load(Rotate270MapFilter.class);
		
		builtins.load(DeskewMapFilter.class);
		
		addLoader(builtins);
	}

	@Override
	public String getInterfaceName() {
		return "Map Filter";
	}

	@Override
	public String getInterfaceDescription() {
		return "Map Filters are ways to process or transform map data. This can be used to do things like smooth noise, shrink or enlarge maps, or correct for errors and outliers.";
	}

}
