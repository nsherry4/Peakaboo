package org.peakaboo.tooling;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.peakaboo.app.Version;
import org.peakaboo.dataset.source.plugin.DataSourceRegistry;
import org.peakaboo.framework.bolt.plugin.core.BoltPlugin;
import org.peakaboo.framework.bolt.plugin.core.PluginDescriptor;
import org.peakaboo.framework.bolt.repository.PluginMetadata;
import org.peakaboo.framework.bolt.repository.RepositoryMetadata;
import org.peakaboo.framework.druthers.serialize.DruthersSerializer;


// This class helps build the PluginMetadata objects used in plugin repositories from loaded plugins.
public class PluginMetadataBuilder {

	

	public static PluginMetadata buildFromPlugin(
			PluginDescriptor<? extends BoltPlugin> descriptor,
			String downloadRoot,
			String author
		) {
		
		
		var filename = descriptor.getContainer().getSourceName();
		
		var meta = PluginMetadata.fromPluginDescriptor(descriptor);
		meta.category = descriptor.getRegistry().getInterfaceName(); // Use the registry's interface name as the category
		meta.downloadUrl = downloadRoot + filename;
		meta.repositoryUrl = downloadRoot;
		meta.author = author;
		meta.releaseNotes = "No Release Notes"; // TODO how to get the release notes for the plugin?
		meta.minAppVersion = Version.VERSION_MAJOR * 100; // Default to the beginning of the current major release series

		
		
		
		// Do an md5sumn from the filename
		String filepath = descriptor.getContainer().getSourcePath();
		try {
			byte[] data = Files.readAllBytes(Paths.get(filepath));
			byte[] hash = MessageDigest.getInstance("MD5").digest(data);
			meta.checksum = new BigInteger(1, hash).toString(16);
		} catch (IOException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		
		return meta;
		
	}
	
	public static void main(String[] args) {
		String directory = args[0];
		// Initialize the DataSourceRegistry with the directory provided as the first argument
		DataSourceRegistry.init(new File(directory));
		
		// Create the repository metadata, this is the top-level metadata for the repository
		RepositoryMetadata contents = new RepositoryMetadata();
		contents.applicationName = "Peakaboo";
		contents.repositoryName = "Official Plugins";
		contents.repositoryUrl = "https://github.com/PeakabooLabs/peakaboo-plugins/releases/download/600/";
		contents.specVersion = 1;
		
		// For each plugin we've loaded, build the metadata and add it to the repository contents
		DataSourceRegistry.system().getPlugins().forEach(plugin -> {
			if (!plugin.getContainer().isDeletable()) {
				// Skip plugins that are not deletable (e.g., built-in plugins)
				return;
			}
			var meta = buildFromPlugin(plugin, contents.repositoryUrl, "Nathaniel Sherry");
			contents.plugins.add(meta);
		});

		String yaml = DruthersSerializer.serialize(contents);
		// write the yaml to a file named <slug>.yaml in the current directory
		String filename = directory + "/contents.yaml";
		try {
			Files.write(Paths.get(filename), yaml.getBytes());
		} catch (IOException e) {
		}
	}
}
