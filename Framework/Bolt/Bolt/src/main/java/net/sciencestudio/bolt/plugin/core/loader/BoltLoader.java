package net.sciencestudio.bolt.plugin.core.loader;

import java.util.List;

import net.sciencestudio.bolt.plugin.core.BoltPlugin;
import net.sciencestudio.bolt.plugin.core.container.BoltContainer;

public interface BoltLoader<T extends BoltPlugin> {
	
	List<BoltContainer<T>> getContainers();
	
}
