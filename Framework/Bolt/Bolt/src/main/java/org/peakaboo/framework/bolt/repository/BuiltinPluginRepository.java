package org.peakaboo.framework.bolt.repository;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.peakaboo.framework.bolt.plugin.core.BoltPlugin;
import org.peakaboo.framework.bolt.plugin.core.PluginRegistry;

/**
 * This class exposes built-in plugins through the PluginRepository interface.
 */
public class BuiltinPluginRepository implements PluginRepository {
	
	private PluginRegistry<? extends BoltPlugin> registry;
	private List<PluginMetadata> metadataCache;
	private String repositoryName = "Built-in";
	
	public BuiltinPluginRepository(PluginRegistry<? extends BoltPlugin> registry) {
		if (registry == null) {
			throw new IllegalArgumentException("PluginRegistry cannot be null");
		}
		this.registry = registry;
		init();
	}

	protected List<PluginMetadata> generateRepoContents() {
		// get local plugins, filter for undeletable containers, then transform to PluginMetadata
		return registry.getPlugins().stream()
				.filter(p -> !p.getContainer().isDeletable())
				.filter(p -> p.getContainer().getSourcePath() != null)
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
		
	private void refresh() {
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
