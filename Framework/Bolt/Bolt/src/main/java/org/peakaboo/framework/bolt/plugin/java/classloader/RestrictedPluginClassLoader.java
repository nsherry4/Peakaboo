package org.peakaboo.framework.bolt.plugin.java.classloader;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import org.peakaboo.framework.bolt.Bolt;

/**
 * This class is a custom ClassLoader that restricts access to certain packages
 * and classes, allowing only those that are explicitly allowed. It also prevents
 * local loading of classes from reserved package prefixes to try and limit the
 * damage that a misbehaving plugin can do. Note that this approach alone is not
 * sufficient for security, and plugins should still only be loaded from
 * trusted sources.
 */
public class RestrictedPluginClassLoader extends URLClassLoader {
	private final Set<String> blockedPackages;
	private final Set<String> parentOnlyPackages;
	private final ClassLoader parentLoader;
	
	public RestrictedPluginClassLoader(URL[] urls, ClassLoader parentLoader, Set<String> parentOnlyPackages) {
		super(urls, parentLoader); // No parent - we'll control delegation manually
		this.parentLoader = parentLoader;

		// Explicitly block dangerous packages
		this.blockedPackages = Set.of(
				"java.lang.reflect", 
				"sun.", 
				"com.sun.", 
				"jdk.internal.", 
				"java.security",
				"java.awt", 
				"javax.swing", 
				"javax.management"
		);
		
		this.parentOnlyPackages = new HashSet<>(Set.of("java", "javax"));
		this.parentOnlyPackages.addAll(parentOnlyPackages);

		
	}

	private boolean isListedPackage(String name, Set<String> packageList) {
		return packageList.stream().anyMatch(name::startsWith);
	}
	
	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		
		// Don't load classes from the restricted packages
		if (isListedPackage(name, blockedPackages)) {
			Bolt.logger().log(Level.WARNING, "Restricted access to class: " + name);
			throw new ClassNotFoundException("Access to " + name + " is restricted");
		}
		
		// Don't allow the URLs in this classloader to load classes from the reserved packages
		if (isListedPackage(name, parentOnlyPackages)) {
			return parentLoader.loadClass(name);
		}		
		
		// Otherwise, delegate to the super (not parent) class loader
		return super.loadClass(name, resolve);

	}
	
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {

		// Check if this class is explicitly blocked
    	if (isListedPackage(name, blockedPackages)) {
			throw new ClassNotFoundException("Access to " + name + " is restricted");
		}

		// If not found in allowed packages, delegate to the super method
		return super.findClass(name);

	}
    
    
}
