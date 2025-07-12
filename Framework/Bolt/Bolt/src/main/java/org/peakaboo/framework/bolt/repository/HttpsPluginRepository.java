package org.peakaboo.framework.bolt.repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

import org.peakaboo.framework.bolt.Bolt;

public class HttpsPluginRepository implements PluginRepository {

	private String repoUrl;
	private RepositoryMetadata contents;
	private int appVersion;
	
	public HttpsPluginRepository(String repositoryBaseUrl, int appVersion) {
		this.appVersion = appVersion;
		if (!RepositoryMetadata.validateString(repositoryBaseUrl, 100)) {
			throw new IllegalArgumentException("Invalid repository url");
		}
		if (!repositoryBaseUrl.startsWith("https://")) {
			throw new IllegalArgumentException("URL must start with https://");
		}
		// Always ensure the URL ends with a slash for consistency
		if (!repositoryBaseUrl.endsWith("/")) {
			repositoryBaseUrl += "/";
		}
		this.repoUrl = repositoryBaseUrl;
	}
	
	@Override
	public List<PluginMetadata> listAvailablePlugins() throws PluginRepositoryException {
		fetchRepoContentsAsNeeded();
		// Return a copy of the list to prevent external modification
		return new ArrayList<>(contents.plugins);
	}

	@Override
	public InputStream downloadPlugin(PluginMetadata metadata) throws PluginRepositoryException {
		return PluginRepository.downloadPluginHttp(metadata);
	}	


	
    private String fetchTextFromUrl(String urlString) throws IOException {
    	var url = new URL(urlString);
    	if (!"https".equals(url.getProtocol())) {
    		throw new IllegalArgumentException("Only HTTPS connections are allowed");
    	}
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new IOException("HTTP returned " + responseCode);
        }
        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            long length = 0;
            while ((line = in.readLine()) != null) {
            	length += line.length();
            	if (length > 50_000_000) {
            		throw new IOException("Too large, 50MB limit");
            	}
                response.append(line + "\n");
            }
            return response.toString();
        }
    }

    private Optional<RepositoryMetadata> fetchRepoContents() {
    	try {
	    	String contentsUrl = repoUrl + "contents.yaml";
	    	String contentsYaml = fetchTextFromUrl(contentsUrl);
	    	
	    	// Handle empty files without an exception
	    	boolean isEmpty = contentsYaml.strip().isEmpty();
	    	if (isEmpty) {
	    		return Optional.empty();
	    	}

	    	// Validate the raw yaml string
	    	if (!validateRawYaml(contentsYaml)) {
	    		Bolt.logger().log(Level.SEVERE, "Yaml failed validation for for repo " + repoUrl);
	    		return Optional.empty();
	    	}
	    	
	    	// Attempt to deserialize the yaml
	    	RepositoryMetadata fetchedContents = SecureRepositoryLoader.loadRepositoryMetadata(contentsYaml);
	    	
	    	// Validate the data we deserialized from the yaml
	    	if (!fetchedContents.validate(repoUrl, this.appVersion)) {
	    		return Optional.empty();
	    	}
	    	

	    	// Populate the PluginMetadata with a reference back to this PluginRepository.
	    	// Being able to refer back to the repository simplifies the design.
	    	for (var plugin : fetchedContents.plugins) {
	    		plugin.pluginRepository = this;
	    	}
	    	
	    	return Optional.of(fetchedContents);
	    	        } catch (Exception e) {
            Bolt.logger().log(Level.WARNING, "Failed to retrieve plugin list from server", e);
        }
    	return Optional.empty();
    }
    

    private boolean validateRawYaml(String yaml) {
    	if (yaml.contains("!!")) {
    		// Do not accept any yaml which tries to deserialize to specific java classes
    		return false;
    	}
    	
    	if (yaml.length() > 102400) {
    		// Do not accept any yaml longer than 100K
    		return false;
    	}
    	
    	return true;
    }

	private void fetchRepoContentsAsNeeded() {
		if (contents == null) {
			refresh();
		}
	}
    
	@Override
	public String getRepositoryName() {
		fetchRepoContentsAsNeeded();
		return contents.repositoryName;
	}

	@Override
	public String getRepositoryUrl() {
		return this.repoUrl;
	}

	@Override
	public void refresh() {
		contents = fetchRepoContents().orElse(new RepositoryMetadata());
	}
	
}