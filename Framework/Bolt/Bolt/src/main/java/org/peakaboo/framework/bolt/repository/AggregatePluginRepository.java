package org.peakaboo.framework.bolt.repository;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AggregatePluginRepository implements PluginRepository {

	private final List<PluginRepository> repositories;

	public AggregatePluginRepository(PluginRepository... repositories) {
		this(List.of(repositories));
	}
	
	public AggregatePluginRepository(List<PluginRepository> repositories) {
		this.repositories = new ArrayList<>(repositories);
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
	public String getRepositoryName() {
		return "Aggregate Plugin Repository Name";
	}

	@Override
	public String getRepositoryUrl() {
		return "Aggregate Plugin Repository URL";
	}

	public List<PluginRepository> getRepositories() {
		return List.copyOf(repositories);
	}
	
	public void addRepository(PluginRepository newRepo) {
		this.repositories.add(newRepo);
	}

}
