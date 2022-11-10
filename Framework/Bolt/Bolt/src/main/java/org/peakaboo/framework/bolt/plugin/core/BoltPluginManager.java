package org.peakaboo.framework.bolt.plugin.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.peakaboo.framework.bolt.plugin.core.container.BoltContainer;
import org.peakaboo.framework.bolt.plugin.core.exceptions.BoltImportException;
import org.peakaboo.framework.bolt.plugin.core.issue.BoltIssue;
import org.peakaboo.framework.bolt.plugin.core.issue.BoltOldContainerIssue;
import org.peakaboo.framework.bolt.plugin.core.loader.BoltLoader;
import org.peakaboo.framework.bolt.plugin.core.loader.BoltManagedLoader;


/**
 * High-level manager for a plugin system. Provides functionality 
 * for loading, reloading, importing, deleting, etc.
 * @author NAS
 *
 * @param <P>
 */
public abstract class BoltPluginManager<P extends BoltPlugin> implements BoltPluginCollection<P> {

	private boolean loaded = false;
	private List<BoltContainer<P>> containers = new ArrayList<>();
	private BoltPluginSet<P> plugins = new BoltPluginSet<>(this);
	private List<BoltLoader<P>> loaders = new ArrayList<>();
	private String name;

	public BoltPluginManager(String name) {
		this.name = name;
	}
	
	@SuppressWarnings("unchecked")
	public void addLoader(BoltLoader<? extends P> loader) {
		loaders.add((BoltLoader<P>) loader);
	}
	
	public final synchronized void reload() {
		clear();
		load();
	}
	
	public final synchronized void clear() {
		plugins = new BoltPluginSet<>(this);
		loaded = false;
	}
	
	public final synchronized void load() {
		if (!loaded) {
			loaded = true;
			
			containers.clear();
			for (BoltLoader<P> loader : loaders) {
				List<BoltContainer<P>> loaderContainers = loader.getContainers();
				containers.addAll(loaderContainers);
			}
			
			plugins = new BoltPluginSet<>(this);
			for (BoltContainer<P> container : containers) {
				plugins.loadFrom(container);
			}
			
		}
	}
	
	
	public BoltPluginManager<P> getManager() {
		return this;
	}
	
	public String getName() {
		return name;
	}
	
	public String getAssetPath() {
		return "/" + name;
	}
	
	public final synchronized List<BoltPluginPrototype<? extends P>> getPlugins() {
		load();
		return plugins.getPlugins();
	}
	
	public List<BoltIssue<? extends P>> getIssues() {
		/*
		 * Here we not only return the issues generated by containers during loading,
		 * but we also return issues detected from a higher-level view such as one
		 * container having only out-of-date plugins, which we can't know until we see
		 * all the containers at once. This later set is constructed on the fly so that
		 * we're always generating issues based on the current set of containers.
		 */
		List<BoltIssue<? extends P>> allIssues = new ArrayList<>();
		allIssues.addAll(plugins.getIssues());
		allIssues.addAll(findIssues());
		return allIssues;
	}
	
	private List<BoltIssue<? extends P>> findIssues() {
		List<BoltIssue<? extends P>> found = new ArrayList<>();

		//check if a container contains only outdated plugins
		for (BoltContainer<P> container : containers) {
			boolean outdated = false;
			
			//we're not responsible for detecting empty containers
			if (container.isEmpty()) { continue; }
						
			for (BoltPluginPrototype<? extends P> plugin : container.getPlugins()) {
				//look up the newest version of this plugin by UUID
				BoltPluginPrototype<? extends P> newest = getByUUID(plugin.getUUID());
				if (newest.isNewerThan(plugin)) {
					outdated = true;
				}
			}
			if (outdated) {
				found.add(new BoltOldContainerIssue<>(container));
			}
		}
		
		return found;
	}


	
	public boolean isImportable(File file) {
		BoltManagedLoader<P> loader = importer(file);
		if (loader == null) { return false; }
		return true;
	}

	//TODO
	public BoltContainer<P> importOrUpgradeFile(File file) throws BoltImportException {
	
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
					return !container.isEmpty();
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


