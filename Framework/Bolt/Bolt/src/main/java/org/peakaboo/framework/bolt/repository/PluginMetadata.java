package org.peakaboo.framework.bolt.repository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;

import org.peakaboo.framework.bolt.Bolt;
import org.peakaboo.framework.bolt.plugin.core.AlphaNumericComparitor;
import org.peakaboo.framework.bolt.plugin.core.BoltPlugin;
import org.peakaboo.framework.bolt.plugin.core.ExtensionPointRegistry;
import org.peakaboo.framework.bolt.plugin.core.PluginDescriptor;
import org.peakaboo.framework.bolt.plugin.core.PluginRegistry;
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
    
    // This will be populated just after deserialization and validation of the repo contents
    PluginRepository pluginRepository;
    
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
	public boolean isUpgradeFor(PluginDescriptor<? extends BoltPlugin> other) {
		if (!uuid.equals(other.getUUID())) {
			return false;
		}
		return AlphaNumericComparitor.compareVersions(version, other.getVersion()) > 0;
	}
	
	/**
	 * Finds the installed plugin for which this repo plugin represents an upgrade, if one exists. 
	 * @param reg The plugin registry in which to check for a match
	 * @return The plugin descriptor, if one is found
	 */
	public <T extends BoltPlugin> Optional<PluginDescriptor<T>> getUpgradeTarget(PluginRegistry<T> reg) {
		if (!reg.getInterfaceName().equals(category)) {
			return Optional.empty();
		}
		Optional<PluginDescriptor<T>> maybeInstalled = reg.getByUUID(uuid);
		if (maybeInstalled.isEmpty()) {
			return maybeInstalled;
		}
		PluginDescriptor<T> installed = maybeInstalled.get();
		if (this.isUpgradeFor(installed)) {
			return maybeInstalled;
		}
		return Optional.empty();
		
	}
	
	/**
	 * Finds the installed plugin for which this repo plugin represents an upgrade, if one exists. 
	 * @param reg The extension point registry (~ plugin registry set) in which to check for a match
	 * @return The plugin descriptor, if one is found
	 */
	public <T extends BoltPlugin> Optional<PluginDescriptor<T>> getUpgradeTarget(ExtensionPointRegistry reg) {
		for (var subreg : reg.getRegistries()) {
			var result = getUpgradeTarget(subreg);
			if (result.isPresent()) {
				return (Optional<PluginDescriptor<T>>) result;
			}
		}
		return Optional.empty();
	}
	
	
	public static PluginMetadata fromPluginDescriptor(PluginDescriptor<? extends BoltPlugin> desc) {
		return fromPluginDescriptor(desc, false);
	}
	
    public static PluginMetadata fromPluginDescriptor(PluginDescriptor<? extends BoltPlugin> desc, boolean localRepo) {
    	var meta = new PluginMetadata();
    	meta.name = desc.getName();
    	meta.category = desc.getRegistry().getInterfaceName();
    	meta.version = desc.getVersion();
    	meta.minAppVersion = 0; // We can't know this from the descriptor
    	meta.uuid = desc.getUUID();
    	meta.downloadUrl = null; // We can't know this from the descriptor
    	meta.repositoryUrl = null; // We can't know this from the descriptor
    	meta.description = desc.getDescription();
    	meta.author = null; // We can't know this from the descriptor
    	if (!localRepo) {
    		meta.checksum = checksum(desc.getContainer().getSourcePath());
    	}
    	meta.releaseNotes = null; // We can't know this from the descriptor
    	return meta;
    	
    }
	

	/**
	 * This method tries to look up the plugin descriptor based on the category and the uuid.	
	 * @return 
	 */
	public <T extends BoltPlugin> Optional<PluginDescriptor<T>> lookupPluginDescriptor(PluginRegistry<T> registry) {
		// TODO do we have a way to look up all registries? Maybe it has to be done at the application level?

		if (this.category == null || this.uuid == null) {
			return Optional.empty(); // Can't look up without category and uuid
		}

		if (! registry.getInterfaceName().equals(category)) {
			return Optional.empty(); // Not the right category
		}
		for (var desc : registry.getPlugins()) {
			if (desc.getUUID().equals(this.uuid)) {
				return Optional.of(desc);
			}
		}

		return Optional.empty();
	}
	
	public static String checksum(String filename) {
		// Do an md5sumn from the filename
		try {
			byte[] data = Files.readAllBytes(Paths.get(filename));
			byte[] hash = MessageDigest.getInstance("MD5").digest(data);
			return new BigInteger(1, hash).toString(16);
		} catch (IOException | NoSuchAlgorithmException e) {
			Bolt.logger().log(Level.WARNING, "Failed to calculate MD5SUM for " + filename, e);
			return null;
		}
	}
	
	public boolean validateChecksum(String filename) {
		String md5sum = checksum(filename);
		if (md5sum == null) {
			Bolt.logger().log(Level.WARNING, "Checksum failed to match for " + filename);
			return false;
		}
		boolean matched = this.checksum.equalsIgnoreCase(md5sum);
		if (!matched) {
			Bolt.logger().log(Level.WARNING, "Checksum failed to match for " + filename);
		}
		return matched;
	}
	

	/**
	 * Downloads a plugin from an InputStream, typically from a repository.
	 * This will save the stream to a temporary file which will be deleted on exit.
	 * @param stream the InputStream containing the plugin data
	 * @return a File object pointing to the temporary file containing the downloaded plugin
	 * @throws IOException 
	 */
	public Optional<File> download() {
		try {
			InputStream inStream = pluginRepository.downloadPlugin(this);
			
			Path tempDir = Files.createTempDirectory("peakaboo_plugin_");
			String tempPath = tempDir.toFile().getAbsolutePath();
			
			
			// Use the URL to determine the local filename
			String basename;
			try {
				basename = getBasename(this.downloadUrl);
			} catch (URISyntaxException e) {
				throw new IOException("Failed to parse URL");
			}
			// Build the filename out of the tempdir and the extracted basename
			File tempFile = new File(tempPath + File.separator + basename);
			tempFile.deleteOnExit();
			// Download to the file
	        try (FileOutputStream out = new FileOutputStream(tempFile)) {
	            byte[] buffer = new byte[8192];
	            int bytesRead;
	            while ((bytesRead = inStream.read(buffer)) != -1) {
	                out.write(buffer, 0, bytesRead);
	            }
	        }
	        
			if (!validateChecksum(tempFile.getAbsolutePath())) {
				throw new IOException("Checksum did not match for: " + name);
			}
	        
	        return Optional.of(tempFile);
		} catch (IOException e) {
			Bolt.logger().log(Level.WARNING, "Failed to download plugin", e);
			return Optional.empty();
		}

	}
	
    private static String getBasename(String urlString) throws URISyntaxException {
        if (urlString == null || urlString.isEmpty()) {
            return "";
        }
        
        URI url = new URI(urlString);
        String path = url.getPath();
        
        // Handle empty path
        if (path == null || path.isEmpty() || path.equals("/")) {
            return "";
        }
        
        // Use Paths.get() to extract the filename
        return Paths.get(path).getFileName().toString();
    }
	
	// We name this method differently than the property to throw off the serializer
	public PluginRepository sourceRepository() {
		return this.pluginRepository;
	}
	
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (obj instanceof PluginMetadata other) {
        	return 	this.uuid.equals(other.uuid) &&
        			this.name.equals(other.name) &&
        			this.repositoryUrl.equals(other.repositoryUrl) &&
        			this.downloadUrl.equals(other.downloadUrl);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(uuid, name, repositoryUrl, downloadUrl);
    }
	
}
