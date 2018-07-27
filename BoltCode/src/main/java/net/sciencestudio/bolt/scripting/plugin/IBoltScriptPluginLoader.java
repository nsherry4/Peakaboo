package net.sciencestudio.bolt.scripting.plugin;

import java.io.File;

import net.sciencestudio.bolt.plugin.core.BoltFilesytstemPluginLoader;
import net.sciencestudio.bolt.plugin.core.BoltPluginSet;

public class IBoltScriptPluginLoader<T extends BoltScriptPlugin> implements BoltFilesytstemPluginLoader<T> {

	private BoltPluginSet<? super T> plugins;
	private Class<T> runner;
	
	public IBoltScriptPluginLoader(BoltPluginSet<? super T> pluginset, Class<T> runner) {
		this.plugins = pluginset;
		this.runner = runner;
	}
	
	
	/* (non-Javadoc)
	 * @see net.sciencestudio.bolt.scripting.plugin.BoltFilesytstemPluginLoader#scanDirectory(java.io.File, java.lang.String)
	 */
	@Override
	public void scanDirectory(File directory, String extension) {
		for (File file : directory.listFiles()) {
			if (! file.getName().endsWith(".js")) continue;
			IBoltScriptPluginController<T> plugin = new IBoltScriptPluginController<>(file, runner);
			plugins.addPlugin(plugin);
		}
	}
	
}
