package org.peakaboo.framework.bolt.repository;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

public class AggregatingPluginRepository implements PluginRepository {

	private final PluginRepository[] repositories;

	public AggregatingPluginRepository(PluginRepository... repositories) {
		this.repositories = repositories;
	}

	@Override
	public List<PluginMetadata> listAvailablePlugins() throws PluginRepositoryException {
		List<PluginMetadata> allPlugins = new java.util.ArrayList<>();
		for (PluginRepository repo : repositories) {
			allPlugins.addAll(repo.listAvailablePlugins());
		}
		return allPlugins;
	}

	/**
	 * Finds the originating repository for the specified plugin metadata.
	 * 
	 * @param metadata The plugin metadata to search for.
	 * @return An Optional containing the PluginRepository if found, or empty if not found.
	 * @throws PluginRepositoryException If an error occurs while accessing the repositories.
	 */
	public Optional<PluginRepository> findRepositoryForPlugin(PluginMetadata metadata) throws PluginRepositoryException {
		for (PluginRepository repo : repositories) {
			var repoUrl = repo.getRepositoryUrl();
			var pluginUrl = metadata.repositoryUrl;
			
			if (repoUrl.equals(pluginUrl)) {
				return Optional.of(repo);
			}
		}
		return Optional.empty(); // No repository found for this plugin
	}
	
	@Override
	public InputStream downloadPlugin(PluginMetadata metadata) throws PluginRepositoryException {
		var repo = findRepositoryForPlugin(metadata);
		if (repo.isPresent()) {
			return repo.get().downloadPlugin(metadata);
		}
		throw new PluginRepositoryException("Plugin not found in any repository: " + metadata.name);
	}

	@Override
	public boolean isAvailable() {
		return true; // Aggregating repository is always available
	}

	@Override
	public String getRepositoryName() {
		return "Aggregate Plugin Repository Name";
	}

	@Override
	public String getRepositoryUrl() {
		return "Aggregate Plugin Repository URL";
	}




}
