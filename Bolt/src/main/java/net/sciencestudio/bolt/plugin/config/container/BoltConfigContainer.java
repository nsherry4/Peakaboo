package net.sciencestudio.bolt.plugin.config.container;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.function.Function;

import net.sciencestudio.bolt.plugin.config.BoltConfigPlugin;
import net.sciencestudio.bolt.plugin.config.BoltConfigPluginPrototype;
import net.sciencestudio.bolt.plugin.core.BoltPluginSet;
import net.sciencestudio.bolt.plugin.core.BoltPluginSet;
import net.sciencestudio.bolt.plugin.core.container.BoltURLContainer;

public class BoltConfigContainer<T extends BoltConfigPlugin> extends BoltURLContainer<T>{

	private BoltPluginSet<T> plugins;
	
	public BoltConfigContainer(URL url, Class<T> pluginClass, Function<String, T> builder) {
		super(url);
		this.url = url;
		
		plugins = new BoltPluginSet<>();
		BoltConfigPluginPrototype<T> plugin = new BoltConfigPluginPrototype<T>(builder, pluginClass, this);
		plugins.addPlugin(plugin);
	}

	@Override
	public BoltPluginSet<T> getPlugins() {
		return plugins;
	}
	
	public InputStream openStream() throws IOException {
		return url.openStream();
	}
	
}
