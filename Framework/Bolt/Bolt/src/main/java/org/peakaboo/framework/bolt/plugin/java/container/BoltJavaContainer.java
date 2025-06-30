package org.peakaboo.framework.bolt.plugin.java.container;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.logging.Level;

import org.peakaboo.framework.bolt.Bolt;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginSet;
import org.peakaboo.framework.bolt.plugin.core.PluginDescriptor;
import org.peakaboo.framework.bolt.plugin.core.PluginRegistry;
import org.peakaboo.framework.bolt.plugin.core.container.BoltContainer;
import org.peakaboo.framework.bolt.plugin.core.issue.BoltIssue;
import org.peakaboo.framework.bolt.plugin.java.BoltJar;
import org.peakaboo.framework.bolt.plugin.java.BoltJavaPlugin;
import org.peakaboo.framework.bolt.plugin.java.BoltJavaPluginDescriptor;
import org.peakaboo.framework.bolt.plugin.java.issue.BoltBrokenJavaPluginIssue;

public abstract class BoltJavaContainer<T extends BoltJavaPlugin> implements BoltContainer<T> {

	protected Class<T> targetClass;
	protected BoltPluginSet<T> plugins;
	private PluginRegistry<T> manager;
	
	protected BoltJavaContainer(PluginRegistry<T> manager, Class<T> targetClass) {
		this.targetClass = targetClass;
		this.plugins = new BoltPluginSet<>(manager);
		this.manager = manager;
		if (this.manager == null) {
			throw new RuntimeException("Plugin registry cannot be null");
		}
	}

	@Override
	public List<PluginDescriptor<T>> getPlugins() {
		return plugins.getPlugins();
	}

	@Override
	public List<BoltIssue<T>> getIssues() {
		return plugins.getIssues();
	}

	protected void add(Class<? extends T> loadedClass, int weight) {

		try 
		{
			if (!checkPluginCriteria(loadedClass)) {
				plugins.addIssue(new BoltBrokenJavaPluginIssue<>(loadedClass, this, "It does not appear to be a valid plugin"));
				return; 
			} 
			
			PluginDescriptor<T> plugin = new BoltJavaPluginDescriptor<>(this.manager, targetClass, loadedClass, this, weight);
			
			if (plugin.isEnabled()) {
				plugins.addPlugin(plugin);
			}
			
		}
		catch (Throwable e)
		{
			plugins.addIssue(new BoltBrokenJavaPluginIssue<>(loadedClass, this, e));
			Bolt.logger().log(Level.WARNING, "Unable to load plugin", e);
		}
	}
	
	
	private boolean checkPluginCriteria(Class<? extends T> clazz) {
		//make sure its not an interface or an abstract class
		if (!checkIsActualPlugin(clazz)) return false;
						
		//if target is an interface, c must implement it
		if (Modifier.isInterface(targetClass.getModifiers()))
		{
			//make sure the class implements the target interface
			if (!BoltJar.checkImplementsInterface(clazz, targetClass)) return false;
		}
		//if target is a class, c must extend it
		else
		{
			//make sure the plugin is a subclass of the given class
			if (!checkSuperclasses(clazz, targetClass)) return false;
		}
		
		return true;
	}
	

	private boolean checkSuperclasses(Class<?> c, Class<?> target)
	{
		
		if (c == null) return false;
		
		if (Modifier.isInterface(	c.getModifiers()  )) return false;	
		
		return target.isAssignableFrom(c);
				
	}
	
	private boolean checkIsActualPlugin(Class<?> c)
	{
		if (c.isInterface()) return false;
		if (c.isAnnotation()) return false;
		if (Modifier.isAbstract(	c.getModifiers()  )) return false;
		
		return true;
	}
	
	
}
