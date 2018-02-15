package bolt.plugin.java;

import java.net.URL;

import bolt.plugin.core.BoltPluginController;

public class IBoltPluginController<T extends BoltJavaPlugin> implements BoltPluginController<T> {

	private Class<T> pluginClass;
	private Class<? extends T> implClass;
	private URL source;
	private T instance;
	
	public IBoltPluginController(Class<T> pluginClass, Class<? extends T> implClass, URL source) {
		this.pluginClass = pluginClass;
		this.implClass = implClass;
		this.source = source;
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
			e.printStackTrace();
			System.out.println(implClass);
			return null;
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
			System.out.println(implClass);
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
	
	@Override
	public URL getSource() {
		return source;
	}
	
	public String toString() {
		return getName();
	}
	
	
}
