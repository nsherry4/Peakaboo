package bolt.scripting.plugin;

import java.io.File;

import bolt.plugin.core.BoltPluginSet;

public class IBoltScriptPluginLoader<T extends BoltScriptPlugin> {

	private BoltPluginSet<? super T> plugins;
	private Class<T> runner;
	
	public IBoltScriptPluginLoader(BoltPluginSet<? super T> pluginset, Class<T> runner) {
		this.plugins = pluginset;
		this.runner = runner;
	}
	
	
	public void scanDirectory(File directory, String extension) {
		for (File file : directory.listFiles()) {
			if (! file.getName().endsWith(".js")) continue;
			IBoltScriptPluginController<T> plugin = new IBoltScriptPluginController<>(file, runner);
			plugins.addPlugin(plugin);
		}
	}
	
}
