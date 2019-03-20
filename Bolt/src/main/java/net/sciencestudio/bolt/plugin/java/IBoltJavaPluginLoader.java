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
import net.sciencestudio.bolt.plugin.core.container.BoltContainer;
import net.sciencestudio.bolt.plugin.core.issue.BoltBrokenContainerIssue;
import net.sciencestudio.bolt.plugin.core.issue.BoltBrokenPluginIssue;
import net.sciencestudio.bolt.plugin.java.container.BoltClassContainer;
import net.sciencestudio.bolt.plugin.java.container.BoltJarContainer;
import net.sciencestudio.bolt.plugin.java.issue.BoltBrokenJarIssue;
import net.sciencestudio.bolt.plugin.java.issue.BoltBrokenJavaPluginIssue;
import net.sciencestudio.bolt.plugin.java.issue.BoltEmptyJarIssue;




public class IBoltJavaPluginLoader<T extends BoltJavaPlugin> implements BoltClassloaderPluginLoader<T>
{

	private BoltPluginSet<? super T> plugins;
	private Class<T> target;

	/**
	 * Creates a PluginLoader which will locate any plugins which are subclasses or implementations of the target
	 * @param target
	 * @throws ClassInheritanceException
	 */
	public IBoltJavaPluginLoader(BoltPluginSet<? super T> pluginset, final Class<T> target) throws ClassInheritanceException
	{
		
		this.plugins = pluginset;
		this.target = target;
		
		if (!BoltJar.checkImplementsInterface(target, BoltJavaPlugin.class)) {
			throw new ClassInheritanceException("Does not implement plugin interface");
		}
		
	}
	


	@Override
	public void registerPlugin(Class<? extends T> loadedClass) {
		BoltContainer<T> container = new BoltClassContainer<>(target, loadedClass);
		plugins.loadFrom(container.getPlugins());
	}
	
	@Override
	public void registerURL(URL url) {
		BoltContainer<T> container = new BoltJarContainer<>(target, url);
		plugins.loadFrom(container.getPlugins());
	}
	

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
