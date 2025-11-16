package org.peakaboo.framework.bolt.repository;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.peakaboo.framework.bolt.Bolt;

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
			try {
				allPlugins.addAll(repo.listAvailablePlugins());
			} catch (PluginRepositoryException e) {
				// Log error but continue with other repositories
				// This allows local and built-in plugins to be shown even if remote repositories fail
				Bolt.logger().log(Level.WARNING, "Failed to load plugins from repository " + repo.getRepositoryName() + ": " + e.getMessage(), e);
			}
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

	@Override
	public void refresh() {
		for (var repo : repositories) {
			try {
				repo.refresh();
			} catch (Exception e) {
				// Log error but continue refreshing other repositories
				Bolt.logger().log(Level.WARNING, "Failed to refresh repository " + repo.getRepositoryName() + ": " + e.getMessage(), e);
			}
		}
	}

}
