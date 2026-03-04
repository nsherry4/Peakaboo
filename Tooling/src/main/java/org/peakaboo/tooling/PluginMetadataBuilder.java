package org.peakaboo.tooling;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Callable;

import org.peakaboo.app.Version;
import org.peakaboo.dataset.source.plugin.DataSourceRegistry;
import org.peakaboo.framework.bolt.plugin.core.BoltPlugin;
import org.peakaboo.framework.bolt.plugin.core.PluginDescriptor;
import org.peakaboo.framework.bolt.repository.PluginMetadata;
import org.peakaboo.framework.bolt.repository.RepositoryMetadata;
import org.peakaboo.framework.druthers.serialize.DruthersSerializer;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;


// This class helps build the PluginMetadata objects used in plugin repositories from loaded plugins.
public class PluginMetadataBuilder {

	@Command(
		name = "build",
		description = "Scans a directory of plugin JARs and writes a contents.yaml repository index."
	)
	static class BuildCommand implements Callable<Integer> {

		@Option(names = {"--dir", "-d"}, required = true, description = "Directory containing plugin JARs to scan.")
		String directory;

		@Option(names = {"--url", "-u"}, required = true, description = "Base download URL for the plugin repository.")
		String repositoryUrl;

		@Option(names = {"--author", "-a"}, required = true, description = "Author name to embed in plugin metadata.")
		String author;

		@Override
		public Integer call() throws Exception {
			DataSourceRegistry.init(new File(directory));

			RepositoryMetadata contents = new RepositoryMetadata();
			contents.applicationName = "Peakaboo";
			contents.repositoryName = "Official Plugins";
			contents.repositoryUrl = repositoryUrl;
			contents.specVersion = 1;

			DataSourceRegistry.system().getPlugins().forEach(plugin -> {
				if (!plugin.getContainer().isDeletable()) {
					return;
				}
				var meta = buildFromPlugin(plugin, repositoryUrl, author);
				contents.plugins.add(meta);
			});

			String yaml = DruthersSerializer.serialize(contents);
			String filename = directory + "/contents.yaml";
			try {
				Files.write(Paths.get(filename), yaml.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
				return 1;
			}
			return 0;
		}
	}

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
		meta.minAppVersion = Version.VERSION_MAJOR * 100 + Version.VERSION_MINOR * 10; // Default to the most recent release

		// Do an md5sum from the file
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
		System.exit(new CommandLine(new BuildCommand()).execute(args));
	}
}
