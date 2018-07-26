package peakaboo.common;

import net.sciencestudio.bolt.plugin.core.BoltPlugin;
import net.sciencestudio.bolt.plugin.core.BoltPluginSet;
import net.sciencestudio.bolt.plugin.core.IBoltPluginSet;



public abstract class PluginManager<P extends BoltPlugin> {

	private boolean loaded = false;
	private BoltPluginSet<P> plugins = new IBoltPluginSet<>();

	
	public synchronized final void reload() {
		clear();
		load();
	}
	
	public synchronized final void clear() {
		plugins = new IBoltPluginSet<>();
		loaded = false;
	}
	
	public synchronized final void load() {
		if (loaded == false) {
			loaded = true;
			loadPlugins();
		}
	}
	

	protected abstract void loadPlugins();
	
	public synchronized final BoltPluginSet<P> getPlugins() {
		load();
		return plugins;
	}
	
	

	
}
