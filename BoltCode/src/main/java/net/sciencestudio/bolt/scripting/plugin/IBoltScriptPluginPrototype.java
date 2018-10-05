package net.sciencestudio.bolt.scripting.plugin;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;

import net.sciencestudio.bolt.Bolt;
import net.sciencestudio.bolt.plugin.core.BoltPluginPrototype;


public class IBoltScriptPluginPrototype<T extends BoltScriptPlugin> implements BoltPluginPrototype<T> {

	private URL scriptFile;
	private Class<T> runnerClass;
	private T instance;
	
	public IBoltScriptPluginPrototype(URL url, Class<T> runner) {
		this.scriptFile = url;
		this.runnerClass = runner;
		instance = create();
	}

	@Override
	public Class<? extends T> getImplementationClass() {
		return runnerClass;
	}

	@Override
	public Class<T> getPluginClass() {
		return runnerClass;
	}

	@Override
	public T create()
	{

		try
		{
			T inst = runnerClass.newInstance();
			inst.setScriptFile(scriptFile);
			return inst;
		}
		catch (InstantiationException e)
		{
			Bolt.logger().log(Level.SEVERE, "Failed to create new plugin instance of " + runnerClass, e);
			return null;
		}
		catch (IllegalAccessException e)
		{
			Bolt.logger().log(Level.SEVERE, "Failed to create new plugin instance of " + runnerClass, e);
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
		return scriptFile;
	}

	@Override
	public T getReferenceInstance() {
		return instance;
	}
	
	public String toString() {
		return getName();
	}

	/**
	 * Script files get a pass on generating a UUID, since we want to keep things simple
	 */
	@Override
	public String getUUID() {
		return getName();
	}
	
}
