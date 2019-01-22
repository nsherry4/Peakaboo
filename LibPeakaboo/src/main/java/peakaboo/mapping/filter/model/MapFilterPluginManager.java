package peakaboo.mapping.filter.model;

import java.io.File;
import java.util.logging.Level;

import net.sciencestudio.bolt.plugin.core.BoltClassloaderDirectoryManager;
import net.sciencestudio.bolt.plugin.core.BoltClassloaderPluginLoader;
import net.sciencestudio.bolt.plugin.core.BoltDirectoryManager;
import net.sciencestudio.bolt.plugin.core.BoltFilesytstemPluginLoader;
import net.sciencestudio.bolt.plugin.core.BoltPluginManager;
import net.sciencestudio.bolt.plugin.core.BoltPluginSet;
import net.sciencestudio.bolt.plugin.java.ClassInheritanceException;
import net.sciencestudio.bolt.plugin.java.ClassInstantiationException;
import net.sciencestudio.bolt.plugin.java.IBoltJavaPluginLoader;
import peakaboo.common.PeakabooLog;
import peakaboo.mapping.filter.plugin.JavaMapFilterPlugin;
import peakaboo.mapping.filter.plugin.MapFilterPlugin;
import peakaboo.mapping.filter.plugin.plugins.EnlargeMapFilter;
import peakaboo.mapping.filter.plugin.plugins.FastAverageMapFilter;
import peakaboo.mapping.filter.plugin.plugins.WeightedAverageMapFilter;

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
			
			loader.registerPlugin(FastAverageMapFilter.class);
			loader.registerPlugin(EnlargeMapFilter.class);
			loader.registerPlugin(WeightedAverageMapFilter.class);
			
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
		return "Map Filters are ways to process or transform map data. This can be used to do things like smooth noise.";
	}

}
