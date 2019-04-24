package org.peakaboo.framework.bolt.plugin.core.loader;

import java.util.List;

import org.peakaboo.framework.bolt.plugin.core.BoltPlugin;
import org.peakaboo.framework.bolt.plugin.core.container.BoltContainer;

public interface BoltLoader<T extends BoltPlugin> {
	
	List<BoltContainer<T>> getContainers();
	
}
