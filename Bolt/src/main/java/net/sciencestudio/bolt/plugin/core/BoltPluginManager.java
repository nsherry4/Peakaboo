package net.sciencestudio.bolt.plugin.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import net.sciencestudio.bolt.plugin.core.container.BoltContainer;
import net.sciencestudio.bolt.plugin.core.exceptions.BoltImportException;
import net.sciencestudio.bolt.plugin.core.loader.BoltLoader;
import net.sciencestudio.bolt.plugin.core.loader.BoltManagedLoader;


/**
 * High-level manager for a plugin system. Provides functionality 
 * for loading, reloading, importing, deleting, etc.
 * @author NAS
 *
 * @param <P>
 */
public abstract class BoltPluginManager<P extends BoltPlugin> {

	private boolean loaded = false;
	private List<BoltContainer<P>> containers = new ArrayList<>();
	private BoltPluginSet<P> plugins = new BoltPluginSet<>();
	private List<BoltLoader<P>> loaders = new ArrayList<>();
	
	private Class<P> pluginClass;
	
	public BoltPluginManager(Class<P> pluginClass) {
		this.pluginClass = pluginClass;
	}
	
	public void addLoader(BoltLoader<? extends P> loader) {
		loaders.add((BoltLoader<P>) loader);
	}
	
	public synchronized final void reload() {
		clear();
		load();
	}
	
	public synchronized final void clear() {
		plugins = new BoltPluginSet<>();
		loaded = false;
	}
	
	public synchronized final void load() {
		if (loaded == false) {
			loaded = true;
			
			containers.clear();
			for (BoltLoader<P> loader : loaders) {
				List<BoltContainer<P>> loaderContainers = loader.getContainers();
				containers.addAll(loaderContainers);
			}
			
			plugins = new BoltPluginSet<>();
			for (BoltContainer<P> container : containers) {
				plugins.loadFrom(container.getPlugins());
			}
			
		}
	}
	
	
	public synchronized final BoltPluginSet<P> getPlugins() {
		load();
		return plugins;
	}

	
	public boolean isImportable(File file) {
		BoltManagedLoader<P> loader = importer(file);
		if (loader == null) { return false; }
		return true;
	}

	//TODO
	public BoltContainer<? extends BoltPlugin> importOrUpgradeFile(File file) throws BoltImportException {
	
		// TODO: check if it's an upgrade for any of the containers in any of the other
		// loaders? Do we care?
		
		BoltManagedLoader<P> loader = importer(file);
		
		//all else being equal, we choose to import to the first loader in the list
		if (loader == null) {
			throw new BoltImportException("No import destinations found");
		}
		BoltContainer<P> container = loader.build(file);
		loader.doImport(file);
		reload();
		return container;

	}

	
	private List<BoltManagedLoader<P>> managedLoaders() {
		return loaders.stream()
				.filter(l -> (l instanceof BoltManagedLoader))
				.map(l -> (BoltManagedLoader<P>)l)
				.collect(Collectors.toList());
	}
	
	
	
	
	/**
	 * These are the loaders which can import the file and are able to load a
	 * non-empty container from it
	 */
	private List<BoltManagedLoader<P>> importCandidates(File file) {
		return managedLoaders().stream()
				.filter(l -> l.canImport(file))
				.filter(l -> {
					BoltContainer<P> container = l.build(file);
					if (container == null) { return false; }
					return !container.getPlugins().getAll().isEmpty();
				})
				.collect(Collectors.toList());
	}
	
	private BoltManagedLoader<P> importer(File file) {
		List<BoltManagedLoader<P>> candidates = importCandidates(file);
		if (candidates.isEmpty()) { return null; }
		return candidates.get(0);
	}
	
	
	/**
	 * Provides a name for the kind of plugins managed by this manager
	 * @return
	 */
	public abstract String getInterfaceName();
	/**
	 * Provides a description for the kind of plugins managed by this manager
	 */
	public abstract String getInterfaceDescription();

	
	
}


