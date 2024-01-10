package org.peakaboo.framework.bolt.plugin.config.container;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.peakaboo.framework.bolt.plugin.config.BoltConfigPlugin;
import org.peakaboo.framework.bolt.plugin.config.BoltConfigPluginBuilder;
import org.peakaboo.framework.bolt.plugin.config.BoltConfigPluginDescriptor;
import org.peakaboo.framework.bolt.plugin.core.PluginDescriptor;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginSet;
import org.peakaboo.framework.bolt.plugin.core.PluginRegistry;
import org.peakaboo.framework.bolt.plugin.core.container.BoltURLContainer;
import org.peakaboo.framework.bolt.plugin.core.issue.BoltIssue;

public class BoltConfigContainer<T extends BoltConfigPlugin> extends BoltURLContainer<T>{

	private BoltPluginSet<T> plugins;
	private PluginRegistry<T> manager;
	
	public BoltConfigContainer(PluginRegistry<T> manager, URL url, Class<T> pluginClass, BoltConfigPluginBuilder<T> builder, boolean deletable) {
		super(url, deletable);
		if (url == null) {
			throw new NullPointerException("URL for config plugin was null");
		}
		this.url = url;
		this.manager = manager;
		
		plugins = new BoltPluginSet<>(manager);
		BoltConfigPluginDescriptor<T> plugin = new BoltConfigPluginDescriptor<>(this.manager, builder, pluginClass, this);
		plugins.addPlugin(plugin);
	}
	
	public InputStream openStream() throws IOException {
		return url.openStream();
	}

	@Override
	public List<PluginDescriptor<? extends T>> getPlugins() {
		return plugins.getPlugins();
	}

	@Override
	public List<BoltIssue<? extends T>> getIssues() {
		return plugins.getIssues();
	}

	@Override
	public PluginRegistry<T> getManager() {
		return this.manager;
	}
	
}
