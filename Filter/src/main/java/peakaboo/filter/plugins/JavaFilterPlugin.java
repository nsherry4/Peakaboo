package peakaboo.filter.plugins;

import net.sciencestudio.bolt.plugin.java.BoltJavaPlugin;

public interface JavaFilterPlugin extends FilterPlugin, BoltJavaPlugin {

	//TODO: Remove for Peakaboo 6
	@Override
	default String pluginUUID() {
		return pluginName(); 
	}

}
