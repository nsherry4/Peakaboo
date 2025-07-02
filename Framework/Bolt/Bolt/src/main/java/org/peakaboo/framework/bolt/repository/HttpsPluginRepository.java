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
import org.peakaboo.framework.druthers.serialize.DruthersSerializer;

public class HttpsPluginRepository implements PluginRepository {

	private String repoUrl;
	private RepositoryMetadata contents;
	
	public HttpsPluginRepository(String repositoryBaseUrl) {
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
            while ((line = in.readLine()) != null) {
                response.append(line + "\n");
            }
            return response.toString();
        }
    }

    private Optional<RepositoryMetadata> fetchRepoContents() {
    	try {
	    	String contentsUrl = repoUrl + "contents.yaml";
	    	String contentsYaml = fetchTextFromUrl(contentsUrl);
	    	RepositoryMetadata fetchedContents = DruthersSerializer.deserialize(contentsYaml, false, RepositoryMetadata.class);
	    	if (!fetchedContents.validate(repoUrl)) {
	    		return Optional.empty();
	    	}
	    	return Optional.of(fetchedContents);
        } catch (Exception e) {
            Bolt.logger().log(Level.WARNING, "Failed to retrieve plugin list from server", e);
        }
    	return Optional.empty();
    }
    


	private void fetchRepoContentsAsNeeded() {
		if (contents == null) {
			contents = fetchRepoContents().orElse(new RepositoryMetadata());
		}
	}
    
	@Override
	public boolean isAvailable() {
		fetchRepoContentsAsNeeded();
		return contents != null && !contents.plugins.isEmpty();
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
	
}