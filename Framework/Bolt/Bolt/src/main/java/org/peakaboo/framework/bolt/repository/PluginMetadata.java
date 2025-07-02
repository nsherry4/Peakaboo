package org.peakaboo.framework.bolt.repository;

import org.peakaboo.framework.bolt.plugin.core.AlphaNumericComparitor;
import org.peakaboo.framework.bolt.plugin.core.BoltPlugin;
import org.peakaboo.framework.bolt.plugin.core.PluginDescriptor;
import org.peakaboo.framework.druthers.DruthersStorable;

public class PluginMetadata implements DruthersStorable {

    public String name;
    public String category; // Category of the plugin, e.g., "DataSource", "DataSink", "Filter", etc.
    public String version; // Plugin version
    public int minAppVersion; // Minimum app version required, as an integer (eg 6.1 is 601)
    public String uuid; //uuid of the plugin
    
    public String downloadUrl; // URL to download the plugin
    public String repositoryUrl; // Url of the repository where this plugin is hosted, if applicable
    
    public String description;
    public String author;
    
    public String checksum; //md5sum for now, but leave room for checksumType in the future
    public String releaseNotes;
    
    public PluginMetadata() {
		// Default constructor for deserialization
	}
    
    @Override
    public String toString() {
        return String.format("%s v%s by %s", name, version, author);
    }
    
	/**
	 * Returns true if (and only if) the UUID of the other plugin matches 
	 * this one, and the version of this plugin is the same or greater than the other one 
	 * @param other the plugin to test against
	 * @return true if this plugin's version is newer or the same, false if this plugin version is older, or if the UUIDs don't match
	 */
	public boolean isUpgradeFor(PluginDescriptor<?> other) {
		if (!uuid.equals(other.getUUID())) {
			return false;
		}
		return AlphaNumericComparitor.compareVersions(version, other.getVersion()) > 0;
	}
    
    
    public static PluginMetadata fromPluginDescriptor(PluginDescriptor<? extends BoltPlugin> desc) {
    	var meta = new PluginMetadata();
    	meta.name = desc.getName();
    	meta.category = null; // TODO we need to find a way to map between the metadata categories and the plugin registries
    	meta.version = desc.getVersion();
    	meta.minAppVersion = 600;
    	meta.uuid = desc.getUUID();
    	meta.downloadUrl = null; // We can't know this from the descriptor
    	meta.repositoryUrl = null; // We can't know this from the descriptor
    	meta.description = desc.getDescription();
    	meta.author = null; // We can't know this from the descriptor
    	meta.checksum = null; // We can't know this from the descriptor
    	meta.releaseNotes = null; // We can't know this from the descriptor
    	return meta;
    	
    }
	

	/**
	 * This method tries to look up the plugin descriptor based on the category and the uuid.	
	 * @return 
	 */
	public Optional<PluginDescriptor<? extends BoltPlugin>> lookupPluginDescriptor(PluginRegistry registry) {
		// TODO do we have a way to look up all registries? Maybe it has to be done at the application level?

		if (this.category == null || this.uuid == null) {
			return Optional.empty(); // Can't look up without category and uuid
		}

		if (! registry.getInterfaceName().equals(category)) {
			continue; // Not the right category
		}
		for (var desc : registry.getPlugins()) {
			if (desc.getUUID().equals(uuid)) {
				return Optional.of(desc);
			}
		}

		return Optional.empty();
	}

}
