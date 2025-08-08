package org.peakaboo.framework.bolt.repository;

import java.util.List;

public abstract class AbstractPluginRepository implements PluginRepository {

	private String repositoryName = "Manually Installed";
	private String repositoryUrl = "";
	private List<PluginMetadata> pluginCache;
	
	protected AbstractPluginRepository(String name, String url) {
		this.repositoryName = name;
		this.repositoryUrl = url;
	}
	
	protected abstract List<PluginMetadata> generatePluginList();
	
	@Override
	public List<PluginMetadata> listAvailablePlugins() throws PluginRepositoryException {
		if (pluginCache == null) {
			pluginCache = generatePluginList();
		}
		return pluginCache;
	}

	@Override
	public void refresh() {
		pluginCache = generatePluginList();
	}

	@Override
	public String getRepositoryName() {
		return repositoryName;
	}

	@Override
	public String getRepositoryUrl() {
		return repositoryUrl;
	}


}
