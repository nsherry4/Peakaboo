package org.peakaboo.framework.bolt.plugin.config.container;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.peakaboo.framework.bolt.plugin.config.BoltConfigPlugin;
import org.peakaboo.framework.bolt.plugin.config.BoltConfigPluginBuilder;
import org.peakaboo.framework.bolt.plugin.config.BoltConfigPluginDescriptor;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginSet;
import org.peakaboo.framework.bolt.plugin.core.PluginDescriptor;
import org.peakaboo.framework.bolt.plugin.core.PluginRegistry;
import org.peakaboo.framework.bolt.plugin.core.container.BoltURLContainer;
import org.peakaboo.framework.bolt.plugin.core.exceptions.BoltException;
import org.peakaboo.framework.bolt.plugin.core.issue.BoltIssue;

/**
 * Implementation of the BoltContainer interface which handles loading a
 * single config plugin from a file.
 */
public class BoltConfigContainer<T extends BoltConfigPlugin> extends BoltURLContainer<T>{

	private BoltPluginSet<T> plugins;
	private PluginRegistry<T> manager;
	private List<BoltIssue<T>> issues;
	
	public BoltConfigContainer(PluginRegistry<T> manager, URL url, Class<T> pluginClass, BoltConfigPluginBuilder<T> builder, boolean deletable) {
		super(url, deletable);
		if (url == null) {
			throw new NullPointerException("URL for config plugin was null");
		}
		this.url = url;
		this.manager = manager;
		this.issues = new ArrayList<>();
		
		plugins = new BoltPluginSet<>(manager);
		try {
			BoltConfigPluginDescriptor<T> plugin = new BoltConfigPluginDescriptor<>(this.manager, builder, pluginClass, this, PluginDescriptor.WEIGHT_MEDIUM);
			plugins.addPlugin(plugin);
		} catch (BoltException e) {
			issues.add(new BoltBrokenConfigfileIssue<>(this, "Could not load plugin from file"));
		}
	}
	
	public InputStream openStream() throws IOException {
		return url.openStream();
	}

	@Override
	public List<PluginDescriptor<T>> getPlugins() {
		return plugins.getPlugins();
	}

	@Override
	public List<BoltIssue<T>> getIssues() {
		var list = new ArrayList<BoltIssue<T>>();
		list.addAll(plugins.getIssues());
		list.addAll(issues);
		return list;
	}

	@Override
	public PluginRegistry<T> getManager() {
		return this.manager;
	}
	
}
