package org.peakaboo.framework.bolt.repository;

import java.util.List;

public abstract class AbstractPluginRepository implements PluginRepository {

	private String repositoryName = "Manually Installed";
	private String repositoryUrl = "";
	private List<PluginMetadata> pluginCache;
	private boolean generating = false;

	protected AbstractPluginRepository(String name, String url) {
		this.repositoryName = name;
		this.repositoryUrl = url;
	}

	protected abstract List<PluginMetadata> generatePluginList();

	@Override
	public List<PluginMetadata> listAvailablePlugins() throws PluginRepositoryException {
		if (pluginCache == null) {
			pluginCache = generate();
		}
		return pluginCache;
	}

	@Override
	public void refresh() {
		pluginCache = generate();
	}

	/**
	 * Generates the plugin list with a guard against becoming part of a cycle.
	 * Throws an exception which aggregators should handle gracefully.
	 */
	private List<PluginMetadata> generate() throws PluginRepositoryException {
		if (generating) {
			throw new PluginRepositoryException("Cycle detected in '" + repositoryName);
		}
		generating = true;
		try {
			return generatePluginList();
		} finally {
			generating = false;
		}
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
