package org.peakaboo.framework.bolt.repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.peakaboo.framework.bolt.Bolt;
import org.peakaboo.framework.druthers.serialize.DruthersSerializer;

public class GitHubPluginRepository implements PluginRepository {

	private String repositoryName;
	private int applicationVersion;
	private String projectUrl;
	private List<PluginMetadata> plugins;
	
	public GitHubPluginRepository(String repositoryName, String projectUrl, int applicationVersion) {
		this.projectUrl = projectUrl;
		this.applicationVersion = applicationVersion;
		this.repositoryName = repositoryName;
	}
	
	@Override
	public List<PluginMetadata> listAvailablePlugins() throws PluginRepositoryException {
		fetchPluginsAsNeeded();
		// Return a copy of the list to prevent external modification
		return new ArrayList<>(plugins);
	}

	@Override
	public PluginMetadata getPluginMetadata(String pluginName, int version) throws PluginRepositoryException {
		fetchPluginsAsNeeded();
		for (PluginMetadata plugin : plugins) {
			if (plugin.name != null && plugin.name.equalsIgnoreCase(pluginName) && plugin.version == version) {
				return plugin;
			}
		}
		return null;
	}

	@Override
	public InputStream downloadPlugin(PluginMetadata metadata) throws PluginRepositoryException {
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

	@Override
	public List<PluginMetadata> searchPlugins(String query, int limit) throws PluginRepositoryException {
		fetchPluginsAsNeeded();
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
	
    private String fetchTextFromUrl(String urlString) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlString).openConnection();
        conn.setRequestMethod("GET");
        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new IOException("HTTP returned " + responseCode);
        }
        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }

    private List<PluginMetadata> fetchPluginsFromGitHub() {
        List<PluginMetadata> fetchedPlugins = new ArrayList<>();
        try {
            String apiUrl = projectUrl.replace("https://github.com/", "https://api.github.com/repos/") + "/releases/tags/" + applicationVersion;
            String json = fetchTextFromUrl(apiUrl);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode release = mapper.readTree(json);
            JsonNode assets = release.get("assets");
            if (assets != null && assets.isArray()) {
                for (JsonNode asset : assets) {
                    String name = asset.path("name").asText("");
                    String downloadUrl = asset.path("browser_download_url").asText("");
                    if (name.endsWith(".json")) {
                        try {
                            String metadataJson = fetchTextFromUrl(downloadUrl);
                            PluginMetadata meta = DruthersSerializer.deserialize(metadataJson, false, PluginMetadata.class);
                            // We don't set the download URL directly in the metadata, so we set it here
                            meta.downloadUrl = downloadUrl.replace(".json", ".jar"); // By convention, the JAR is named similarly
                            // Filter the plugins based on application version
                            if (meta.minAppVersion > applicationVersion) {
								continue; // Skip plugins that require a higher app version
							}
                            fetchedPlugins.add(meta);
                        } catch (IOException e) {
                        	Bolt.logger().log(Level.WARNING, "Failed to retrieve plugin from server: " + name, e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Bolt.logger().log(Level.WARNING, "Failed to retrieve plugin list from server", e);
        }
        return fetchedPlugins;
    }

    private void fetchPluginsAsNeeded() {
		if (plugins == null) {
			try {
				plugins = fetchPluginsFromGitHub();
			} catch (Exception e) {
				Bolt.logger().log(Level.WARNING, "Failed to fetch plugins from GitHub", e);
				plugins = new ArrayList<>(); // Ensure we have an empty list to return
			}
		}
	}
    
	@Override
	public boolean isAvailable() {
		fetchPluginsAsNeeded();
		return plugins != null && !plugins.isEmpty();
	}

	@Override
	public String getRepositoryName() {
		return repositoryName;
	}
	
	
	public static void main(String[] args) {
		var repo = new GitHubPluginRepository("Official Peakaboo Plugins", "https://github.com/PeakabooLabs/peakaboo-plugins", 600);
		System.out.println("Listing available plugins from: " + repo.getRepositoryName());
		repo.listAvailablePlugins().forEach(plugin -> {
			System.out.println("Found plugin: " + plugin);
			System.out.println("Download URL: " + plugin.downloadUrl);
		});
	}
	
}