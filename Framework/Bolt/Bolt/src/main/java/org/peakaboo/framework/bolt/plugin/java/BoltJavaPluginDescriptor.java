package org.peakaboo.framework.bolt.plugin.java;

import java.util.Optional;
import java.util.logging.Level;

import org.peakaboo.framework.accent.log.OneLog;
import org.peakaboo.framework.bolt.plugin.core.PluginDescriptor;
import org.peakaboo.framework.bolt.plugin.core.PluginRegistry;
import org.peakaboo.framework.bolt.plugin.core.container.BoltContainer;
import org.peakaboo.framework.bolt.plugin.core.exceptions.BoltException;

public class BoltJavaPluginDescriptor<T extends BoltJavaPlugin> implements PluginDescriptor<T> {

	private Class<T> pluginClass;
	private Class<? extends T> implClass;
	private BoltContainer<T> container;
	private T instance;
	private PluginRegistry<T> registry;
	private int weight = PluginDescriptor.WEIGHT_MEDIUM;
	
	public BoltJavaPluginDescriptor(PluginRegistry<T> registry, Class<T> pluginClass, Class<? extends T> implClass, BoltContainer<T> container, int weight) throws BoltException {
		this.pluginClass = pluginClass;
		this.implClass = implClass;
		this.container = container;
		this.registry = registry;
		this.weight = weight;
		
		var creation = create();
		if (creation.isPresent()) {
			this.instance = creation.get();
		} else {
			throw new BoltException("Coult not create reference instance for plugin " + pluginClass.getName());
		}
		
	}
	
	public Class<? extends T> getImplementationClass() {
		return implClass;
	}
	
	public Class<T> getPluginClass() {
		return pluginClass;
	}
	
	/**
	 * Returns an instance of this plugin which is to be used for reference only. 
	 * Do not use this instance of the plugin directly.
	 */
	@Override
	public T getReferenceInstance() {
		return instance;
	}
	
	
	@Override
	public Optional<T> create()
	{
		try
		{
			return Optional.of(implClass.newInstance());
		}
		catch (InstantiationException | IllegalAccessException e)
		{
			OneLog.log(Level.WARNING, "Unable to create new plugin instance for " + implClass, e);
			return Optional.empty();
		}

	}
	
	@Override
	public boolean isEnabled() {
		return (instance != null && instance.pluginEnabled());
	}
	
	/**
	 * A short, descriptive name for this plugin. If the plugin cannot be loaded, returns null.
	 */
	@Override
	public String getName() {
		if (instance == null) return null;
		return instance.pluginName();
	}

	/**
	 * A longer description of what this plugin is and what it does. If the plugin cannot be loaded, returns null.
	 * @return
	 */
	@Override
	public String getDescription() {
		if (instance == null) return null;
		return instance.pluginDescription();
	}
	
	/**
	 * A version string for this plugin. If the plugin cannot be loaded, returns null.
	 */
	@Override
	public String getVersion() {
		if (instance == null) return null;
		return instance.pluginVersion();
	}
	

	public String toString() {
		return getName();
	}

	@Override
	public String getUUID() {
		if (instance == null) return null;
		return instance.pluginUUID();
	}

	@Override
	public BoltContainer<T> getContainer() {
		return container;
	}

	@Override
	public PluginRegistry<T> getRegistry() {
		return registry;
	}

	@Override
	public int getWeight() {
		return weight;
	}
	

}
