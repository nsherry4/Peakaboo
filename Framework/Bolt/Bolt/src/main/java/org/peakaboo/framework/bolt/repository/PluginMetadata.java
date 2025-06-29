package org.peakaboo.framework.bolt.repository;

public class PluginMetadata {

    public String name;
    public String category; // Category of the plugin, e.g., "DataSource", "DataSink", "Filter", etc.
    public int version; // Plugin version as an int -- plugin versioning should be simple
    public int minAppVersion; // Minimum app version required, as an integer (eg 6.1 is 601)
    public String uuid; //uuid of the plugin
    
    public String downloadUrl; // URL to download the plugin
    public String repositoryName; // Name of the repository where this plugin is hosted, if applicable
    
    public String description;
    public String author;
    
    public String checksum; //md5sum for now, but leave room for checksumType in the future
    public String releaseNotes;
    
    public PluginMetadata() {
		// Default constructor for deserialization
	}
    
    @Override
    public String toString() {
        return String.format("%s v%d by %s", name, version, author);
    }
	
}
