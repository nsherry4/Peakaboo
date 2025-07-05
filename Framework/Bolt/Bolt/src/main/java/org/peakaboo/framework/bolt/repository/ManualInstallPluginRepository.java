package org.peakaboo.framework.bolt.repository;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.peakaboo.framework.bolt.plugin.core.BoltPlugin;
import org.peakaboo.framework.bolt.plugin.core.PluginRegistry;

public class ManualInstallPluginRepository implements PluginRepository {

	private PluginRegistry<? extends BoltPlugin> registry;
	private String repositoryName = "Manually Installed";
	private Supplier<List<PluginMetadata>> inventory;
	
	// We have to cache the plugin list, otherwise we'll get a circular call when trying to list all plugins from the inventory
	private List<PluginMetadata> metadataCache;
	
	
	public ManualInstallPluginRepository(PluginRegistry<? extends BoltPlugin> registry, Supplier<List<PluginMetadata>> inventory) {
		if (registry == null) {
			throw new IllegalArgumentException("PluginRegistry cannot be null");
		}
		this.registry = registry;
		this.inventory = inventory;
		init();
	}

	protected List<PluginMetadata> generateRepoContents() {	
		// get local plugins, filter for undeletable containers and plugins from this own repo. 
		// Then transform the descriptor into PluginMetadata
		var accountedFor = inventory.get();
		return registry.getPlugins().stream()
				// Manual installs should be deletable, these are not builtins
				.filter(p -> p.getContainer().isDeletable())
				// The source path should exist
				.filter(p -> p.getContainer().getSourcePath() != null)
				// Looking for plugins that dont appear in the inventory of all plugins found in repos
				.filter(p -> accountedFor.stream().filter(o -> {
						if (o.sourceRepository() == this) return true;
						return p.getUUID().equals(o.uuid);
					}).toList().isEmpty())
				// Build our local metadata instance
				.map(p -> {
					var meta = PluginMetadata.fromPluginDescriptor(p, true);
					meta.downloadUrl = ""; // Can't download a local plugin
					meta.repositoryUrl = repositoryName;
					meta.category = registry.getInterfaceName();
					meta.pluginRepository = this;
			    	meta.author = "";
			    	meta.releaseNotes = ""; // We can't know this from the descriptor
					return meta;
				}).toList();
	}


	@Override
	public List<PluginMetadata> listAvailablePlugins() throws PluginRepositoryException {
		return new ArrayList<>(metadataCache);
	}
	
	@Override
	public void refresh() {
		metadataCache = generateRepoContents();
	}

	private void init() {
		if (metadataCache == null) {
			refresh();
		}
	}
	
	
	@Override
	public InputStream downloadPlugin(PluginMetadata metadata) throws PluginRepositoryException {
		throw new PluginRepositoryException("Cannot download plugins from the local repository. Use the plugin directly instead.");
	}

	@Override
	public String getRepositoryName() {
		return repositoryName;
	}

	@Override
	public String getRepositoryUrl() {
		// Repository URL is used for identifying the repos, we don't return null here.
		return repositoryName;
	}
	
}
