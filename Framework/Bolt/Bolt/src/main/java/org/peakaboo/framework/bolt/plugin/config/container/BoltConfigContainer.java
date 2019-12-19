package org.peakaboo.framework.bolt.plugin.config.container;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.function.Function;

import org.peakaboo.framework.bolt.plugin.config.BoltConfigPlugin;
import org.peakaboo.framework.bolt.plugin.config.BoltConfigPluginPrototype;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginPrototype;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginSet;
import org.peakaboo.framework.bolt.plugin.core.container.BoltURLContainer;
import org.peakaboo.framework.bolt.plugin.core.issue.BoltIssue;

public class BoltConfigContainer<T extends BoltConfigPlugin> extends BoltURLContainer<T>{

	private BoltPluginSet<T> plugins;
	
	public BoltConfigContainer(URL url, Class<T> pluginClass, Function<String, T> builder) {
		super(url);
		this.url = url;
		
		plugins = new BoltPluginSet<>();
		BoltConfigPluginPrototype<T> plugin = new BoltConfigPluginPrototype<>(builder, pluginClass, this);
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
	
}
