package net.sciencestudio.bolt.plugin.java;


import java.io.File;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.logging.Level;

import net.sciencestudio.bolt.Bolt;
import net.sciencestudio.bolt.plugin.core.BoltPluginController;
import net.sciencestudio.bolt.plugin.core.BoltPluginSet;




public class BoltJavaPluginLoader<T extends BoltJavaPlugin>
{

	private BoltPluginSet<? super T>		plugins;
	private Class<T> 						target;
	
	private boolean 						isTargetInterface;
	
	
	/**
	 * Creates a PluginLoader which will locate any plugins which are subclasses or implementations of the target
	 * @param target
	 * @throws ClassInheritanceException
	 */
	public BoltJavaPluginLoader(BoltPluginSet<? super T> pluginset, final Class<T> target) throws ClassInheritanceException
	{
		
		this.plugins = pluginset;
		this.target = target;
		isTargetInterface = Modifier.isInterface(target.getModifiers());
		
		if (!checkImplementsInterface(target, BoltJavaPlugin.class)) {
			throw new ClassInheritanceException();
		}
		
	}
	

	public BoltPluginController<T>  registerPlugin(Class<? extends T> loadedClass) throws ClassInstantiationException
	{
		URL url = null;
		try {
			url = BoltJar.getJarForClass(loadedClass).toURI().toURL();
		} catch (MalformedURLException | NullPointerException e) {
		}
		return registerPlugin(loadedClass, url);
	}
	
	public BoltPluginController<T> registerPlugin(Class<? extends T> loadedClass, URL source) throws ClassInstantiationException
	{
		
		try 
		{
			if (!test(loadedClass)) { return null; } 
			
			BoltPluginController<T> plugin = new IBoltJavaPluginController<>(target, loadedClass, source);
			
			if (plugin.isEnabled()) {
				plugins.addPlugin(plugin);
				return plugin;
			}
			
			return null;

		}
		catch (ServiceConfigurationError e)
		{
			Bolt.logger().log(Level.WARNING, "Unable to load plugin", e);
			throw new ClassInstantiationException(e);
		}
	}
	
	private boolean test(Class<? extends T> clazz) {
		//make sure its not an interface or an abstract class
		if (!isActualPlugin(clazz)) return false;
						
		//if target is an interface, c must implement it
		if (isTargetInterface)
		{
			//make sure the class implements the target interface
			if (!checkImplementsInterface(clazz, target)) return false;
		}
		//if target is a class, c must extend it
		else
		{
			//make sure the plugin is a subclass of the given class
			if (!checkSuperclasses(clazz, target)) return false;
		}
		
		return true;
	}
	

	private boolean checkSuperclasses(Class<?> c, Class<?> target)
	{
		
		if (c == null) return false;
		
		if (Modifier.isInterface(	c.getModifiers()  )) return false;	
		
		return target.isAssignableFrom(c);
				
	}
	
	private boolean checkImplementsInterface(Class<?> c, Class<?> targetInterface)
	{
		if (c == null) return false;
		
		while (c != Object.class) {
		
			Class<?> ifaces[] = c.getInterfaces();
			for (Class<?> iface : ifaces)
			{
				if (iface.equals(targetInterface)) return true;
			}
						
			c = c.getSuperclass();
			
		}
		return false;
		
	}
	
	private boolean isActualPlugin(Class<?> c)
	{
		if (c.isInterface()) return false;
		if (c.isAnnotation()) return false;
		if (Modifier.isAbstract(	c.getModifiers()  )) return false;
		
		return true;
	}
	
	
	
	
	
	
	
	public void register(File file)
	{
		File[] files;
		if (file.isDirectory())	{
			files = file.listFiles((dir, name) -> name.toLowerCase().endsWith(".jar"));
		} else {
			files = new File[1];
			files[0] = file;
		}
		
		for (int i = 0; i < files.length; i++) {
			try	{
				register(files[i].toURI().toURL());
			} catch (Exception e) {
				Bolt.logger().log(Level.WARNING, "Unable to load plugin at " + files[i], e);
			}
		}
		
	}
	
	public void register(URL url) throws ClassInstantiationException
	{
		
		URLClassLoader urlLoader = new URLClassLoader(new URL[]{url});
		ServiceLoader<T> loader = ServiceLoader.load(target, urlLoader);
		loader.reload();
			
		try {
			for (T t : loader) {
				registerPlugin((Class<? extends T>) t.getClass(), url);
			}
		} catch (ServiceConfigurationError | NoClassDefFoundError e) {
			Bolt.logger().log(Level.WARNING, "Unable to load plugin", e);
		} 
	}
	
	public void register()
	{
		if (BoltJar.isClassInJar(target)) {
			register(BoltJar.getJarForClass(target).getParentFile());
		} else {
			register(new File(".").getAbsoluteFile());
		}
	}
	
	
}
