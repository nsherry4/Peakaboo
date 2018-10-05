package net.sciencestudio.bolt.scripting.plugin;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;

import net.sciencestudio.bolt.Bolt;
import net.sciencestudio.bolt.plugin.core.BoltFilesytstemPluginLoader;
import net.sciencestudio.bolt.plugin.core.BoltPluginSet;

public class IBoltScriptPluginLoader<T extends BoltScriptPlugin> implements BoltFilesytstemPluginLoader<T> {

	private BoltPluginSet<? super T> plugins;
	private Class<T> runner;
	private String extension;
	
	public IBoltScriptPluginLoader(BoltPluginSet<? super T> pluginset, String extension, Class<T> runner) {
		this.plugins = pluginset;
		this.runner = runner;
		this.extension = extension;
	}
	
	
	@Override
	public void scanDirectory(File directory) {
		for (File file : directory.listFiles()) {
			if (! file.getName().endsWith(extension)) continue;
			try {
				registerURL(file.toURI().toURL());
			} catch (MalformedURLException e) {
				Bolt.logger().log(Level.WARNING, "Failed to load plguin " + file.toString(), e);
			}
		}
	}


	@Override
	public void registerURL(URL url) {
		IBoltScriptPluginPrototype<T> plugin = new IBoltScriptPluginPrototype<>(url, runner);
		plugins.addPlugin(plugin);
	}
	
}
