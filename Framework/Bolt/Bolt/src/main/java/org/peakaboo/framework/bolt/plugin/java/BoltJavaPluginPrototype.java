package org.peakaboo.framework.bolt.plugin.java;

import java.util.logging.Level;

import org.peakaboo.framework.bolt.Bolt;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginManager;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginPrototype;
import org.peakaboo.framework.bolt.plugin.core.container.BoltContainer;

public class BoltJavaPluginPrototype<T extends BoltJavaPlugin> implements BoltPluginPrototype<T> {

	private Class<T> pluginClass;
	private Class<? extends T> implClass;
	private BoltContainer<T> container;
	private T instance;
	private BoltPluginManager<T> manager;
	
	public BoltJavaPluginPrototype(BoltPluginManager<T> manager, Class<T> pluginClass, Class<? extends T> implClass, BoltContainer<T> container) {
		this.pluginClass = pluginClass;
		this.implClass = implClass;
		this.container = container;
		this.manager = manager;
		instance = create();
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
	public T create()
	{
		try
		{
			return implClass.newInstance();
		}
		catch (InstantiationException e)
		{
			Bolt.logger().log(Level.WARNING, "Unable to create new plugin instance for " + implClass, e);
			return null;
		}
		catch (IllegalAccessException e)
		{
			Bolt.logger().log(Level.WARNING, "Unable to create new plugin instance for " + implClass, e);
			return null;
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
	public BoltPluginManager<T> getManager() {
		return manager;
	}
	
	
}
