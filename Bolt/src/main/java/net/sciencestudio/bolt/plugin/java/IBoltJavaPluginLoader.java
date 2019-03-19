package net.sciencestudio.bolt.plugin.java;


import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.jar.JarInputStream;
import java.util.logging.Level;

import net.sciencestudio.bolt.Bolt;
import net.sciencestudio.bolt.plugin.core.BoltClassloaderPluginLoader;
import net.sciencestudio.bolt.plugin.core.BoltPluginPrototype;
import net.sciencestudio.bolt.plugin.core.BoltPluginSet;
import net.sciencestudio.bolt.plugin.core.issue.BoltBrokenContainerIssue;
import net.sciencestudio.bolt.plugin.core.issue.BoltBrokenPluginIssue;
import net.sciencestudio.bolt.plugin.java.issue.BoltBrokenJarIssue;
import net.sciencestudio.bolt.plugin.java.issue.BoltBrokenJavaPluginIssue;
import net.sciencestudio.bolt.plugin.java.issue.BoltEmptyJarIssue;




public class IBoltJavaPluginLoader<T extends BoltJavaPlugin> implements BoltClassloaderPluginLoader<T>
{

	private BoltPluginSet<? super T>		plugins;
	private Class<T> 						target;
	
	private boolean 						isTargetInterface;
	
	
	/**
	 * Creates a PluginLoader which will locate any plugins which are subclasses or implementations of the target
	 * @param target
	 * @throws ClassInheritanceException
	 */
	public IBoltJavaPluginLoader(BoltPluginSet<? super T> pluginset, final Class<T> target) throws ClassInheritanceException
	{
		
		this.plugins = pluginset;
		this.target = target;
		isTargetInterface = Modifier.isInterface(target.getModifiers());
		
		if (!checkImplementsInterface(target, BoltJavaPlugin.class)) {
			throw new ClassInheritanceException("Does not implement plugin interface");
		}
		
	}
	


	@Override
	public BoltPluginPrototype<T>  registerPlugin(Class<? extends T> loadedClass) throws ClassInstantiationException
	{
		URL url = null;
		try {
			if (BoltJar.isClassInJar(loadedClass)) {
				File jarfile = BoltJar.getJarForClass(loadedClass);
				if (jarfile!= null) { url = jarfile.toURI().toURL(); }
			}
		} catch (MalformedURLException | NullPointerException e) {
			String msg = "Failed to register plugin " + loadedClass.getSimpleName();
			plugins.addIssue(new BoltBrokenJavaPluginIssue(loadedClass, null, msg));
			Bolt.logger().log(Level.WARNING, msg, e);
		}
		return registerPlugin(loadedClass, url);
	}
	

	@Override
	public BoltPluginPrototype<T> registerPlugin(Class<? extends T> loadedClass, URL source) throws ClassInstantiationException
	{
		try 
		{
			if (!checkPluginCriteria(loadedClass)) {
				plugins.addIssue(new BoltBrokenJavaPluginIssue(loadedClass, source, "It does not appear to be a valid plugin"));
				return null; 
			} 
			
			BoltPluginPrototype<T> plugin = new IBoltJavaPluginPrototype<>(target, loadedClass, source);
			
			if (plugin.isEnabled()) {
				plugins.addPlugin(plugin);
				return plugin;
			}
			
			return null;

		}
		catch (Throwable e)
		{
			plugins.addIssue(new BoltBrokenJavaPluginIssue(loadedClass, source, e));
			Bolt.logger().log(Level.WARNING, "Unable to load plugin", e);
			throw new ClassInstantiationException("Unable to load plugin", e);
		}
	}
	
	private boolean checkPluginCriteria(Class<? extends T> clazz) {
		//make sure its not an interface or an abstract class
		if (!checkIsActualPlugin(clazz)) return false;
						
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
	
	private boolean checkIsActualPlugin(Class<?> c)
	{
		if (c.isInterface()) return false;
		if (c.isAnnotation()) return false;
		if (Modifier.isAbstract(	c.getModifiers()  )) return false;
		
		return true;
	}
	
	private boolean checkIsValidJar(URL url) {
		JarInputStream jin = null;
		try {
			jin = new JarInputStream(url.openStream());
			jin.getNextJarEntry().getName();
			return true;
		} catch (Throwable e) {
			return false;
		} finally {
			if (jin != null) { 
				try {
					jin.close();
				} catch (IOException e) {}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see net.sciencestudio.bolt.plugin.java.BoltJavaPluginLoader#register(java.net.URL)
	 */
	@Override
	public void registerURL(URL url)
	{
		
		try {

			if (!checkIsValidJar(url)) {
				plugins.addIssue(new BoltBrokenJarIssue(url, "It does not appear to be a valid jar file"));
				return;
			}
			
			URLClassLoader urlLoader = new URLClassLoader(new URL[]{url});
			ServiceLoader<T> loader = ServiceLoader.load(target, urlLoader);
			loader.reload();
			
			// odd structure is used here because hasNext() will throw an exception if the
			// next plugin cannot be loaded. We want to make sure these kinds of errors are
			// treated as issues with the plugin rather than issues with tha jar.
			Iterator<T> iter = loader.iterator();
			boolean empty = true;
			while (true) {
				try {
					if (!iter.hasNext()) { break; }
					empty = false;
					T t = iter.next();
					registerPlugin((Class<? extends T>) t.getClass(), url);
				} catch (Throwable e) {
					plugins.addIssue(new BoltBrokenJavaPluginIssue(null, url, e));
					Bolt.logger().log(Level.WARNING, "Unable to load plugin", e);
					empty = false;
				}
			}
			
			if (empty) {
				plugins.addIssue(new BoltEmptyJarIssue(url));
			}
			
			
		} catch (Throwable e) {
			plugins.addIssue(new BoltBrokenJarIssue(url, e.getMessage()));
			Bolt.logger().log(Level.WARNING, "Unable to load plugins from jar", e);
		} 
	}
	
	/* (non-Javadoc)
	 * @see net.sciencestudio.bolt.plugin.java.BoltJavaPluginLoader#register()
	 */
	@Override
	public void registerBuiltIn()
	{
		if (BoltJar.isClassInJar(target)) {
			register(BoltJar.getJarForClass(target).getParentFile());
		} else {
			register(new File(".").getAbsoluteFile());
		}
	}
	
	
}
