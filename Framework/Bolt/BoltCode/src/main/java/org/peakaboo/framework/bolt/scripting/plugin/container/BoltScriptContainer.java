package org.peakaboo.framework.bolt.scripting.plugin.container;

import java.net.URL;
import java.util.List;

import org.peakaboo.framework.bolt.scripting.plugin.BoltScriptPlugin;
import org.peakaboo.framework.bolt.scripting.plugin.BoltScriptPluginPrototype;

import org.peakaboo.framework.bolt.plugin.core.BoltPluginPrototype;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginSet;
import org.peakaboo.framework.bolt.plugin.core.container.BoltURLContainer;
import org.peakaboo.framework.bolt.plugin.core.issue.BoltIssue;

public class BoltScriptContainer<T extends BoltScriptPlugin> extends BoltURLContainer<T> {

	private BoltPluginSet<T> plugins;
	
	public BoltScriptContainer(URL url, Class<T> runner) {
		super(url);
		
		plugins = new BoltPluginSet<>();
		BoltScriptPluginPrototype<T> plugin = new BoltScriptPluginPrototype<T>(this, runner);
		plugins.addPlugin(plugin);
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
