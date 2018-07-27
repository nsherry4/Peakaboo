package peakaboo.datasink.plugin;

import net.sciencestudio.bolt.plugin.java.BoltJavaPlugin;

public interface JavaDataSinkPlugin extends DataSinkPlugin, BoltJavaPlugin {

	//TODO: Remove for Peakaboo 6
	@Override
	default String pluginUUID() {
		return pluginName(); 
	}
	
}
