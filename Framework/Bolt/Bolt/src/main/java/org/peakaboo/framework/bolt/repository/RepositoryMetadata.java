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
	public String applicationName = "No Application Name";
	public String repositoryName = "No Repository Name";
	public String repositoryUrl = "No URL";
	public String repositoryDescription = "No Description";
	
    public boolean validate(String repoUrl, int appVersion) {
    	final String REGEX_A1 = "^[a-zA-Z0-9_ -]+$"; // Alphanumeric, underscores, hyphens, and spaces only
    	final String REGEX_UUID = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}";
    	final String REGEX_VERSION = "^[a-zA-Z0-9_., -]+$";
    	final String REGEX_CHECKSUM = "^[0-9a-fA-F]{32}$";
    	
    	// SPEC VERSION
    	// Spec version must match
    	if (this.specVersion != 1) {
    		Bolt.logger().log(Level.WARNING, "Invalid repository contents: Unsupported spec version " + this.specVersion + ". Expected 1.");
    		return false;
    	}
    	
    	// REPOSITORY NAME
    	// Repository Name may not contain the protected term "Built" to prevent confusion with built-in plugins
    	if (!validateString(this.repositoryName, 50, REGEX_A1)) {
    		Bolt.logger().log(Level.WARNING, "Invalid repository name. 50 alphanumeric characters (plus spaces, hyphens, and underscores) or less");
    		return false;
    	}
    	
    	// No remote repository uses a term like built in (reserved)
    	if (this.repositoryName.toLowerCase().contains("built")) {
			Bolt.logger().log(Level.WARNING, "Invalid repository contents: Repository name may not contain the term 'Built'.");
			return false;
		}
    	// Only allow the term "Official" in the repository name if the repository URL matches an official Peakaboo repository prefix
    	if (this.repositoryName.toLowerCase().contains("official") && !this.repositoryUrl.startsWith("https://github.com/PeakabooLabs/")) {
    		Bolt.logger().log(Level.WARNING, "Invalid repository contents: Repository name contains 'Official' but URL does not match official Peakaboo repository prefix.");
    		return false;
    	}
    	
    	// REPOSITORY URL
    	// Repo must report the same url as we have for it
    	if (!validateString(this.repositoryUrl, 200) || this.repositoryUrl.contains("..") || !this.repositoryUrl.equals(repoUrl)) {
			Bolt.logger().log(Level.WARNING, "Invalid repository contents: Repository URL mismatch. Expected " + repoUrl + " but got " + this.repositoryUrl);
			return false;
		}
    	
    	// REPOSITORY DESCRIPTION
    	if (!validateString(this.repositoryDescription, 500)) {
    		Bolt.logger().log(Level.WARNING, "Invalid repository description. 500 characters or less");
    		return false;
    	}
    	
    	// APPLICATION NAME
    	if (!validateString(this.applicationName, 20, REGEX_A1)) {
    		Bolt.logger().log(Level.WARNING, "Invalid application name. 20 alphanumeric characters (plus spaces, hyphens, and underscores) or less");
    		return false;
    	}
    	
    	
    	// Examine each plugin
		for (var plugin : this.plugins) {
			
			// DOWNLOAD URL
			// Ensure the plugin's download url starts with the repository URL
			if (!validateString(plugin.downloadUrl, 200) || this.repositoryUrl.contains("..") || !plugin.downloadUrl.startsWith(this.repositoryUrl)) {
				Bolt.logger().log(Level.WARNING, "Plugin '" + plugin.name + "' has an invalid download URL: " + plugin.downloadUrl + ". It must begin with same path as the repository inventory file.");
				return false;
			}
			
			// REPOSITORY URL
			// Ensure the plugin's reference to the repository URL is correct
			if (!validateString(plugin.repositoryUrl, 200) || this.repositoryUrl.contains("..") || !plugin.repositoryUrl.equals(this.repositoryUrl)) {
				Bolt.logger().log(Level.WARNING, "Plugin '" + plugin.name + "' has an invalid repository URL: " + plugin.repositoryUrl + ". It must match the repository inventory file URL.");
				return false;
			}
			
			// NAME
			if (!validateString(plugin.name, 50, REGEX_A1)) {
				Bolt.logger().log(Level.WARNING, "Plugin '" + plugin.name + "' has an invalid name. It must only contain alphanumeric characters, underscores, hyphens, and spaces.");
				return false;
			}
			
			// DESCRIPTION
			if (!validateString(plugin.description, 200)) {
				Bolt.logger().log(Level.WARNING, "Plugin '" + plugin.name + "' has an invalid name. It must only contain alphanumeric characters, underscores, hyphens, and spaces.");
				return false;
			}
			
			// CATEGORY
			if (!validateString(plugin.category, 50, REGEX_A1)) {
				Bolt.logger().log(Level.WARNING, "Plugin '" + plugin.name + "' has an invalid description.");
				return false;
			}
			
			
			// UUID
			if (!validateString(plugin.uuid, 36, REGEX_UUID)) {
				Bolt.logger().log(Level.WARNING, "Plugin '" + plugin.name + "' has an invalid UUID.");
				return false;
			}
			
			// RELEASE NOTES
			if (!validateString(plugin.releaseNotes, 1000)) {
				Bolt.logger().log(Level.WARNING, "Plugin '" + plugin.name + "' has invalid Release Notes.");
				return false;
			}
			
			// AUTHOR
			if (!validateString(plugin.author, 200)) {
				Bolt.logger().log(Level.WARNING, "Plugin '" + plugin.name + "' has invalid Author.");
				return false;
			}
			
			// CHECKSUM
			if (!validateString(plugin.checksum, 32, REGEX_CHECKSUM)) {
				Bolt.logger().log(Level.WARNING, "Plugin '" + plugin.name + "' has invalid Checksum.");
				return false;
			}
			// TODO verify checksum
			
			// MINAPPVERSION
			if (plugin.minAppVersion < 1 || plugin.minAppVersion > 10000) {
				Bolt.logger().log(Level.WARNING, "Plugin '" + plugin.name + "' has invalid Minimum App Version.");
				return false;
			}
			if (plugin.minAppVersion > appVersion) {
				Bolt.logger().log(Level.WARNING, "Plugin minimum required version is greater than the version of this application.");
				return false;
			}
			
			// VERSION
			// Just big enough for an ISO 8601 timestamp
			if (!validateString(plugin.version, 10, REGEX_VERSION)) {
				Bolt.logger().log(Level.WARNING, "Plugin '" + plugin.name + "' has invalid Version.");
				return false;
			}

			
			
		}
		return true;
	}
    
    public static boolean validateString(String s, int maxLength) {
    	return validateString(s, maxLength, null);
    }
    public static boolean validateString(String s, int maxLength, String regex) {
    	if (s != null && !s.isEmpty() && s.length() <= maxLength) {
    		if (regex == null) return true;  
    		return s.matches(regex);
    	} else {
    		return false;
    	}
    }
    
}
