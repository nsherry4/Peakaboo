package org.peakaboo.framework.bolt.repository;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

public class AggregatingPluginRepository implements PluginRepository {

	private final PluginRepository[] repositories;

	public AggregatingPluginRepository(PluginRepository... repositories) {
		this.repositories = repositories;
	}
	
	public AggregatingPluginRepository(List<PluginRepository> repositories) {
		this.repositories = repositories.toArray(new PluginRepository[0]);
	}

	@Override
	public List<PluginMetadata> listAvailablePlugins() throws PluginRepositoryException {
		List<PluginMetadata> allPlugins = new java.util.ArrayList<>();
		for (PluginRepository repo : repositories) {
			allPlugins.addAll(repo.listAvailablePlugins());
		}
		return allPlugins;
	}
	
	@Override
	public InputStream downloadPlugin(PluginMetadata metadata) throws PluginRepositoryException {
		throw new PluginRepositoryException("This aggregating implementation does not implement download");
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
