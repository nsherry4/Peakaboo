package peakaboo.datasource.plugin;

import net.sciencestudio.bolt.plugin.java.BoltJavaPlugin;

public interface JavaDataSourcePlugin extends DataSourcePlugin, BoltJavaPlugin {

	//TODO: Remove for Peakaboo 6
	@Override
	default String pluginUUID() {
		return pluginName(); 
	}
	
}
