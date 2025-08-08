package org.peakaboo.framework.bolt.repository;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.peakaboo.framework.bolt.plugin.core.ExtensionPointRegistry;

public class ManualInstallPluginRepository extends AbstractPluginRepository {

	private ExtensionPointRegistry registries;
	private Supplier<List<PluginMetadata>> inventory;
	
	public ManualInstallPluginRepository(ExtensionPointRegistry registries, Supplier<List<PluginMetadata>> inventory) {
		super("Manually Installed", "builtin://manual");
		if (registries == null) {
			throw new IllegalArgumentException("ExtensionPointRegistry cannot be null");
		}
		this.registries = registries;
		this.inventory = inventory;
		refresh();
	}

	protected List<PluginMetadata> generatePluginList() {	
		// get local plugins, filter for undeletable containers and plugins from this own repo. 
		// Then transform the descriptor into PluginMetadata
		var accountedFor = inventory.get();
		List<PluginMetadata> plugins = new ArrayList<>();
		for (var registry : this.registries.getRegistries()) {
		
			 var results = registry.getPlugins().stream()
					// Manual installs should be deletable, these are not builtins
					.filter(p -> p.getContainer().isDeletable())
					// The source path should exist
					.filter(p -> p.getContainer().getSourcePath() != null)
					// Looking for plugins that dont appear in the inventory of all plugins found in repos
					.filter(p -> accountedFor.stream().filter(o -> {
							if (o.sourceRepository() == this) return true;
							return p.getUUID().equals(o.uuid);
						}).toList().isEmpty())
					// Build our local metadata instance
					.map(p -> {
						var meta = PluginMetadata.fromPluginDescriptor(p, true);
						meta.downloadUrl = ""; // Can't download a local plugin
						meta.repositoryUrl = this.getRepositoryName();
						meta.category = registry.getInterfaceName();
						meta.pluginRepository = this;
				    	meta.author = "";
				    	meta.releaseNotes = ""; // We can't know this from the descriptor
						return meta;
					}).toList();
			 
			 plugins.addAll(results);
			 
		}
		
		return plugins;
	}
	
	@Override
	public InputStream downloadPlugin(PluginMetadata metadata) throws PluginRepositoryException {
		throw new PluginRepositoryException("Cannot download plugins from the local repository. Use the plugin directly instead.");
	}
}
