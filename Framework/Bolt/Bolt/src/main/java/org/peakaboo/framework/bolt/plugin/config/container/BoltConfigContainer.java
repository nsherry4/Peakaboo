package org.peakaboo.framework.bolt.plugin.config.container;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.function.Function;

import org.peakaboo.framework.bolt.plugin.config.BoltConfigPlugin;
import org.peakaboo.framework.bolt.plugin.config.BoltConfigPluginPrototype;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginManager;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginPrototype;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginSet;
import org.peakaboo.framework.bolt.plugin.core.container.BoltURLContainer;
import org.peakaboo.framework.bolt.plugin.core.issue.BoltIssue;

public class BoltConfigContainer<T extends BoltConfigPlugin> extends BoltURLContainer<T>{

	private BoltPluginSet<T> plugins;
	private BoltPluginManager<T> manager;
	
	public BoltConfigContainer(BoltPluginManager<T> manager, URL url, Class<T> pluginClass, Function<String, T> builder, boolean deletable) {
		super(url, deletable);
		if (url == null) {
			throw new NullPointerException("URL for config plugin was null");
		}
		this.url = url;
		this.manager = manager;
		
		plugins = new BoltPluginSet<>(manager);
		BoltConfigPluginPrototype<T> plugin = new BoltConfigPluginPrototype<>(this.manager, builder, pluginClass, this);
		plugins.addPlugin(plugin);
	}
	
	public InputStream openStream() throws IOException {
		return url.openStream();
	}

	@Override
	public List<BoltPluginPrototype<? extends T>> getPlugins() {
		return plugins.getPlugins();
	}

	@Override
	public List<BoltIssue<? extends T>> getIssues() {
		return plugins.getIssues();
	}

	@Override
	public BoltPluginManager<T> getManager() {
		return this.manager;
	}
	
}
