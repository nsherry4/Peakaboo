package org.peakaboo.framework.bolt.repository;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.peakaboo.framework.bolt.plugin.core.BoltPlugin;
import org.peakaboo.framework.bolt.plugin.core.ExtensionPointRegistry;
import org.peakaboo.framework.bolt.plugin.core.issue.BoltIssue;

public class IssuePluginRepository extends AbstractPluginRepository {

	public static final String CATEGORY = "Issues";
	
	private ExtensionPointRegistry extensionPoints;
	private Map<String, BoltIssue<? extends BoltPlugin>> issueLookup = new LinkedHashMap<>();
		
	public IssuePluginRepository(ExtensionPointRegistry extensionPoints) {
		super("Broken Plugins", "builtin://issues");
		this.extensionPoints = extensionPoints;
	}

	@Override
	public InputStream downloadPlugin(PluginMetadata metadata) throws PluginRepositoryException {
		throw new PluginRepositoryException("Cannot download this plugin, it represents a broken local plugin container file");
	}

	@Override
	protected List<PluginMetadata> generatePluginList() {
		var plugins = new ArrayList<PluginMetadata>();
		for (var reg : extensionPoints.getRegistries()) {
			for (var issue : reg.getIssues()) {
				var plugin = new PluginMetadata();
				plugin.author = "None";
				plugin.category = IssuePluginRepository.CATEGORY;
				// We could checksum the container file, but the checksum is for verifying downloads, so it's not needed
				plugin.checksum = "None";
				plugin.description = issue.description();
				plugin.downloadUrl = "";
				plugin.minAppVersion = 0;
				plugin.name = issue.shortSource() + ": " + issue.title();
				plugin.pluginRepository = this;
				plugin.releaseNotes = issue.longSource();
				plugin.repositoryUrl = getRepositoryUrl();
				plugin.version = "0";
				plugin.uuid = UUID.nameUUIDFromBytes(issue.longSource().getBytes()).toString();
				plugins.add(plugin);
				issueLookup.put(plugin.uuid, issue);
			}
		}
		return plugins;
	}
	
	public BoltIssue<? extends BoltPlugin> getIssueForPlugin(String uuid) {
		return issueLookup.getOrDefault(uuid, null);
	}


}
