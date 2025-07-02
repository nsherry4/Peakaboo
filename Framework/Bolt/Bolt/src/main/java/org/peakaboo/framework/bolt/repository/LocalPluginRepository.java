package org.peakaboo.framework.bolt.repository;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.peakaboo.framework.bolt.plugin.core.BoltPlugin;
import org.peakaboo.framework.bolt.plugin.core.PluginRegistry;

/**
 * This class exposes built-in plugins through the PluginRepository interface.
 */
public class LocalPluginRepository implements PluginRepository {

	private PluginRegistry<? extends BoltPlugin> registry;
	private List<PluginMetadata> metadataCache;
	
	private static final String REPOSITORY_NAME = "Built-in";
	
	public LocalPluginRepository(PluginRegistry<? extends BoltPlugin> registry) {
		if (registry == null) {
			throw new IllegalArgumentException("PluginRegistry cannot be null");
		}
		this.registry = registry;
		init();
	}
	
	@Override
	public List<PluginMetadata> listAvailablePlugins() throws PluginRepositoryException {
		return new ArrayList<>(metadataCache);
	}
	

	
	private void refresh() {
		metadataCache = generateRepoContents();
	}

	private void init() {
		if (metadataCache == null) {
			refresh();
		}
	}
	
	private List<PluginMetadata> generateRepoContents() {
		// get local plugins, filter for undeletable containers, then transform to PluginMetadata
		return registry.getPlugins().stream()
				.filter(p -> !p.getContainer().isDeletable())
				.map(p -> {
					var meta = PluginMetadata.fromPluginDescriptor(p);
					meta.downloadUrl = ""; // Can't download a local plugin
					meta.repositoryUrl = REPOSITORY_NAME;
					meta.category = registry.getInterfaceName();
					System.out.println(meta);
					return meta;
				}).toList();
	}
	
	@Override
	public InputStream downloadPlugin(PluginMetadata metadata) throws PluginRepositoryException {
		throw new PluginRepositoryException("Cannot download plugins from the local repository. Use the plugin directly instead.");
	}

	@Override
	public boolean isAvailable() {
		return true;
	}

	@Override
	public String getRepositoryName() {
		return REPOSITORY_NAME;
	}

	@Override
	public String getRepositoryUrl() {
		// Repository URL is used for identifying the repos, we don't return null here.
		return REPOSITORY_NAME;
	}


}
