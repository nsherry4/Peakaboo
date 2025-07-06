package org.peakaboo.framework.bolt.repository;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.peakaboo.framework.bolt.plugin.core.ExtensionPointRegistry;

public interface PluginRepository {

    /**
     * Lists all available and compatible plugins from the repository.
     * @return List of plugin metadata for all available plugins
     * @throws PluginRepositoryException if unable to fetch plugin list
     */
    List<PluginMetadata> listAvailablePlugins() throws PluginRepositoryException;
    
    /**
     * Gets metadata for a specific plugin version
     * @param pluginName Name of the plugin
     * @param version Version string
     * @return Plugin metadata or null if not found
     * @throws PluginRepositoryException if unable to fetch metadata
     */
	default PluginMetadata getPluginMetadata(String pluginName, String version) throws PluginRepositoryException {
		for (PluginMetadata plugin : listAvailablePlugins()) {
			if (plugin.name != null && plugin.name.equalsIgnoreCase(pluginName) && plugin.version.equals(version)) {
				return plugin;
			}
		}
		return null;
	}
    
    /**
     * Downloads a plugin JAR file
     * @param metadata Plugin metadata containing download information
     * @return InputStream of the plugin JAR file
     * @throws PluginRepositoryException if unable to download plugin
     */
    InputStream downloadPlugin(PluginMetadata metadata) throws PluginRepositoryException;
    	
    
    
    public static InputStream downloadPluginHttp(PluginMetadata metadata) throws PluginRepositoryException {
		if (metadata == null || metadata.downloadUrl == null || metadata.downloadUrl.isBlank()) {
			throw new PluginRepositoryException("No download URL specified for plugin: " + (metadata != null ? metadata.name : "null"));
		}
		try {
			URL url = new URL(metadata.downloadUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			int responseCode = conn.getResponseCode();
			if (responseCode != 200) {
				throw new PluginRepositoryException("Failed to download plugin: HTTP " + responseCode);
			}
			return conn.getInputStream();
		} catch (IOException e) {
			throw new PluginRepositoryException("Error downloading plugin: " + metadata.downloadUrl, e);
		}
    }
    
    /**
     * Searches for plugins matching the given query
     * @param query Search query (searches name, description, author)
     * @param limit Maximum number of results to return. Set to 0 for no limit.
     * @return List of matching plugin metadata
     * @throws PluginRepositoryException if unable to perform search
     */
	default List<PluginMetadata> searchPlugins(String query, int limit) throws PluginRepositoryException {
		if (query == null || query.isBlank()) {
			return listAvailablePlugins();
		}
		String q = query.toLowerCase();
		List<PluginMetadata> allPlugins = listAvailablePlugins();
		List<PluginMetadata> results = new ArrayList<>();
		for (PluginMetadata plugin : allPlugins) {
			if ((plugin.name != null && plugin.name.toLowerCase().contains(q)) ||
				(plugin.description != null && plugin.description.toLowerCase().contains(q)) ||
				(plugin.author != null && plugin.author.toLowerCase().contains(q))) {
				results.add(plugin);
				if (limit > 0 && results.size() >= limit) {
					break;
				}
			}
		}
		return results;
	}
    
    /**
     * Gets the display name of this repository
     * @return Human-readable name for this repository
     */
    String getRepositoryName();

    /**
	 * Gets the URL of this repository
	 * @return URL where this repository is hosted
	 */
	String getRepositoryUrl();
	
	void refresh();

	default boolean hasUpgrades(ExtensionPointRegistry reg) {
		return listAvailablePlugins().stream()
				.map(p -> p.getUpgradeTarget(reg).isPresent())
				.reduce((a, b) -> a || b).orElse(false);
	}
	
}
