package net.sciencestudio.bolt.scripting.plugin.container;

import java.net.URL;

import net.sciencestudio.bolt.plugin.core.BoltPluginSet;
import net.sciencestudio.bolt.plugin.core.BoltPluginSet;
import net.sciencestudio.bolt.plugin.core.container.BoltURLContainer;
import net.sciencestudio.bolt.scripting.plugin.BoltScriptPlugin;
import net.sciencestudio.bolt.scripting.plugin.BoltScriptPluginPrototype;

public class BoltScriptContainer<T extends BoltScriptPlugin> extends BoltURLContainer<T> {

	private BoltPluginSet<T> plugins;
	
	public BoltScriptContainer(URL url, Class<T> runner) {
		super(url);
		
		plugins = new BoltPluginSet<>();
		BoltScriptPluginPrototype<T> plugin = new BoltScriptPluginPrototype<T>(this, runner);
		plugins.addPlugin(plugin);
	}
	
	@Override
	public BoltPluginSet<T> getPlugins() {
		return plugins;
	}

}
