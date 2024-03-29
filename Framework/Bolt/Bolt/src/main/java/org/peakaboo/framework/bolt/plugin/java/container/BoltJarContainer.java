package org.peakaboo.framework.bolt.plugin.java.container;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.jar.JarInputStream;
import java.util.logging.Level;

import org.peakaboo.framework.bolt.Bolt;
import org.peakaboo.framework.bolt.plugin.core.PluginDescriptor;
import org.peakaboo.framework.bolt.plugin.core.PluginRegistry;
import org.peakaboo.framework.bolt.plugin.java.BoltJavaPlugin;
import org.peakaboo.framework.bolt.plugin.java.issue.BoltBrokenJarIssue;
import org.peakaboo.framework.bolt.plugin.java.issue.BoltEmptyJarIssue;

public class BoltJarContainer<T extends BoltJavaPlugin> extends BoltJavaContainer<T> {

	private URL url;
	private PluginRegistry<T> manager;
	
	public BoltJarContainer(PluginRegistry<T> manager, Class<T> targetClass, URL url) {
		super(manager, targetClass);
		this.url = url;
		this.manager = manager;

		populate();

	}

	private void populate() {

		try {

			if (!isValidJar()) {
				plugins.addIssue(new BoltBrokenJarIssue<>(this, "It does not appear to be a valid jar file"));
				return;
			}

			URLClassLoader urlLoader = new URLClassLoader(new URL[] { url });
			ServiceLoader<T> loader = ServiceLoader.load(targetClass, urlLoader);
			loader.reload();


			// odd structure is used here because hasNext() will throw an exception if the
			// next plugin cannot be loaded. We want to make sure these kinds of errors are
			// treated as issues with the plugin rather than issues with tha jar.
			Iterator<T> iter = loader.iterator();
			boolean empty = true;
			while (true) {
				try {
					if (!iter.hasNext()) {
						break;
					}
					T t = iter.next();
					
					/*
					 * Any built-in plugins will show up in this URLClassLoader, so we do a check
					 * and only accept plugins loaded by this classloader, and not some parent
					 * loader.
					 */
					if (t.getClass().getClassLoader() != urlLoader) {
						continue;
					}
					
					empty = false;
					add((Class<? extends T>) t.getClass(), PluginDescriptor.WEIGHT_MEDIUM);
					
				} catch (Throwable e) {
					plugins.addIssue(new BoltBrokenJarIssue<>(this, "Failed to load plugins"));
					Bolt.logger().log(Level.WARNING, "Unable to load plugin", e);
					empty = false;
				}
			}

			if (empty) {
				plugins.addIssue(new BoltEmptyJarIssue<>(this));
			}

		} catch (Throwable e) {
			plugins.addIssue(new BoltBrokenJarIssue<>(this, e.getMessage()));
			Bolt.logger().log(Level.WARNING, "Unable to load plugins from jar", e);
		}
		
	}

	private boolean isValidJar() {
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

	@Override
	public String getSourcePath() {
		try {
			File f = new File(url.toURI());
			return f.getAbsolutePath();
		} catch (URISyntaxException e) {
			return url.getPath();
		}
	}

	@Override
	public String getSourceName() {
		try {
			File f = new File(url.toURI());
			return f.getName();
		} catch (URISyntaxException e) {
			String[] parts = url.getPath().split("{/,\\}");
			return parts[parts.length - 1];
		}
	}

	@Override
	public boolean delete() {
		try {
			File f = new File(url.toURI());
			return f.delete();
		} catch (URISyntaxException e) {
			return false;
		}
	}

	@Override
	public boolean isDeletable() {
		return true;
	}

	@Override
	public PluginRegistry<T> getManager() {
		return this.manager;
	}

}


