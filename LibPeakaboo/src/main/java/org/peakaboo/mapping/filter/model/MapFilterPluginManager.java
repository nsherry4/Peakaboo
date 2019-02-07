package org.peakaboo.mapping.filter.model;

import java.io.File;
import java.util.logging.Level;

import org.peakaboo.common.PeakabooLog;
import org.peakaboo.mapping.filter.plugin.JavaMapFilterPlugin;
import org.peakaboo.mapping.filter.plugin.MapFilterPlugin;
import org.peakaboo.mapping.filter.plugin.plugins.clipping.SignalCapMapFilter;
import org.peakaboo.mapping.filter.plugin.plugins.clipping.SignalOutlierCorrectionMapFilter;
import org.peakaboo.mapping.filter.plugin.plugins.clipping.WeakSignalRemovalMapFilter;
import org.peakaboo.mapping.filter.plugin.plugins.mathematical.AdditionMapFilter;
import org.peakaboo.mapping.filter.plugin.plugins.mathematical.PowerMapFilter;
import org.peakaboo.mapping.filter.plugin.plugins.mathematical.LogMapFilter;
import org.peakaboo.mapping.filter.plugin.plugins.mathematical.MultiplyMapFilter;
import org.peakaboo.mapping.filter.plugin.plugins.mathematical.NormalizationMapFilter;
import org.peakaboo.mapping.filter.plugin.plugins.sizing.BinningMapFilter;
import org.peakaboo.mapping.filter.plugin.plugins.sizing.EnlargeMapFilter;
import org.peakaboo.mapping.filter.plugin.plugins.smoothing.FastAverageMapFilter;
import org.peakaboo.mapping.filter.plugin.plugins.smoothing.WeightedAverageMapFilter;
import org.peakaboo.mapping.filter.plugin.plugins.transforming.HFlipMapFilter;
import org.peakaboo.mapping.filter.plugin.plugins.transforming.Rotate180MapFilter;
import org.peakaboo.mapping.filter.plugin.plugins.transforming.Rotate270MapFilter;
import org.peakaboo.mapping.filter.plugin.plugins.transforming.Rotate90MapFilter;
import org.peakaboo.mapping.filter.plugin.plugins.transforming.VFlipMapFilter;

import net.sciencestudio.bolt.plugin.core.BoltClassloaderDirectoryManager;
import net.sciencestudio.bolt.plugin.core.BoltClassloaderPluginLoader;
import net.sciencestudio.bolt.plugin.core.BoltDirectoryManager;
import net.sciencestudio.bolt.plugin.core.BoltFilesytstemPluginLoader;
import net.sciencestudio.bolt.plugin.core.BoltPluginManager;
import net.sciencestudio.bolt.plugin.core.BoltPluginSet;
import net.sciencestudio.bolt.plugin.java.ClassInheritanceException;
import net.sciencestudio.bolt.plugin.java.ClassInstantiationException;
import net.sciencestudio.bolt.plugin.java.IBoltJavaPluginLoader;

public class MapFilterPluginManager extends BoltPluginManager<MapFilterPlugin> {

	public static MapFilterPluginManager SYSTEM;
	
	public static void init(File filterDir) {
		if (SYSTEM == null) {
			SYSTEM = new MapFilterPluginManager(filterDir);
			SYSTEM.load();
		}
	}
	
	private MapFilterPluginManager(File directories) {
		super(directories);
	}

	@Override
	protected void loadCustomPlugins() {
		
		try {
			BoltClassloaderPluginLoader<JavaMapFilterPlugin> loader = classpathLoader(getPlugins());
			
			loader.registerPlugin(BinningMapFilter.class);
			loader.registerPlugin(EnlargeMapFilter.class);
			
			loader.registerPlugin(FastAverageMapFilter.class);
			loader.registerPlugin(WeightedAverageMapFilter.class);
			
			loader.registerPlugin(WeakSignalRemovalMapFilter.class);
			loader.registerPlugin(SignalOutlierCorrectionMapFilter.class);
			loader.registerPlugin(SignalCapMapFilter.class);
						
			loader.registerPlugin(MultiplyMapFilter.class);
			loader.registerPlugin(AdditionMapFilter.class);
			loader.registerPlugin(NormalizationMapFilter.class);
			loader.registerPlugin(LogMapFilter.class);
			loader.registerPlugin(PowerMapFilter.class);
			
			loader.registerPlugin(VFlipMapFilter.class);
			loader.registerPlugin(HFlipMapFilter.class);
			loader.registerPlugin(Rotate90MapFilter.class);
			loader.registerPlugin(Rotate180MapFilter.class);
			loader.registerPlugin(Rotate270MapFilter.class);
			
			
			
		} catch (ClassInheritanceException | ClassInstantiationException e) {
			PeakabooLog.get().log(Level.SEVERE, "Failed to load MapFilter plugins", e);
		}
		
	}

	@Override
	protected BoltClassloaderPluginLoader<JavaMapFilterPlugin> classpathLoader(BoltPluginSet<MapFilterPlugin> pluginset) throws ClassInheritanceException {
		return new IBoltJavaPluginLoader<JavaMapFilterPlugin>(pluginset, JavaMapFilterPlugin.class);
	}

	@Override
	protected BoltFilesytstemPluginLoader<? extends MapFilterPlugin> filesystemLoader(BoltPluginSet<MapFilterPlugin> pluginset) {
		return null;
	}

	@Override
	protected BoltDirectoryManager<MapFilterPlugin> classloaderDirectoryManager() {
		return new BoltClassloaderDirectoryManager<>(this, getDirectory());
	}

	@Override
	protected BoltDirectoryManager<MapFilterPlugin> filesystemDirectoryManager() {
		return null;
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
