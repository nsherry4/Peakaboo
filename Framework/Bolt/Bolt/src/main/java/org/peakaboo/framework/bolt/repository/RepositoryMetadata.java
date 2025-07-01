package org.peakaboo.framework.bolt.repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.peakaboo.framework.bolt.Bolt;
import org.peakaboo.framework.druthers.DruthersStorable;

public class RepositoryMetadata implements DruthersStorable {

	public int specVersion = 0; // 0 will represent uninitialized or repository data unavailable 
	public List<PluginMetadata> plugins = new ArrayList<>();
	public Map<String, String> properties = new LinkedHashMap<>();
	public String applicationName = "<No Application Name>";
	public String repositoryName = "<No Repository Name>";
	public String repositoryUrl = "<No URL>";
	public String repositoryDescription = "<No Description>";
	
    public boolean validate(String repoUrl) {
    	// Spec version must match
    	if (this.specVersion != 1) {
    		Bolt.logger().log(Level.SEVERE, "Invalid repository contents: Unsupported spec version " + this.specVersion + ". Expected 1.");
    	}
    	
    	// Repository Name may not contain the protected term "Built" to prevent confusion with built-in plugins
    	if (this.repositoryName.toLowerCase().contains("built")) {
			Bolt.logger().log(Level.SEVERE, "Invalid repository contents: Repository name may not contain the term 'Built'.");
			return false;
		}
    	
    	// Repo must report the same url as we have for it
    	if (! this.repositoryUrl.equals(repoUrl)) {
			Bolt.logger().log(Level.SEVERE, "Invalid repository contents: Repository URL mismatch. Expected " + repoUrl + " but got " + this.repositoryUrl);
			return false;
		}
    	
    	// Only allow the term "Official" in the repository name if the repository URL matches an official Peakaboo repository prefix
    	if (this.repositoryName.toLowerCase().contains("official") && !this.repositoryUrl.startsWith("https://github.com/PeakabooLabs/")) {
    		Bolt.logger().log(Level.SEVERE, "Invalid repository contents: Repository name contains 'Official' but URL does not match official Peakaboo repository prefix.");
    		return false;
    	}
    	
    	// Don't accept repository names that are too long, or which contain characters other than alphanumerics and hyphens
    	if (this.repositoryName.length() > 50 || !this.repositoryName.matches("^[a-zA-Z0-9_ ]+$")) {
    		Bolt.logger().log(Level.SEVERE, "Invalid repository contents: Repository name exceeds 50 characters.");
			return false;
		}
    	
    	
    	// Examine each plugin
		for (var plugin : this.plugins) {
			
			// Ensure the plugin's download url starts with the repository URL
			if (! plugin.downloadUrl.startsWith(this.repositoryUrl)) {
				Bolt.logger().log(Level.SEVERE, "Plugin '" + plugin.name + "' has an invalid download URL: " + plugin.downloadUrl + ". It must begin with same path as the repository inventory file.");
				return false;
			}
			
			// Ensure the plugin's reference to the repository URL is correct
			if (! plugin.repositoryUrl.equals(this.repositoryUrl)) {
				Bolt.logger().log(Level.SEVERE, "Plugin '" + plugin.name + "' has an invalid repository URL: " + plugin.repositoryUrl + ". It must match the repository inventory file URL.");
				return false;
			}			
		}
		return true;
	}
}
