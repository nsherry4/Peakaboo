package net.sciencestudio.bolt.plugin.java.container;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.jar.JarInputStream;
import java.util.logging.Level;

import net.sciencestudio.bolt.Bolt;
import net.sciencestudio.bolt.plugin.java.BoltJavaPlugin;
import net.sciencestudio.bolt.plugin.java.issue.BoltBrokenJarIssue;
import net.sciencestudio.bolt.plugin.java.issue.BoltEmptyJarIssue;

public class BoltJarContainer<T extends BoltJavaPlugin> extends BoltJavaContainer<T> {

	private URL url;

	public BoltJarContainer(Class<T> targetClass, URL url) {
		super(targetClass);
		this.url = url;

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
					empty = false;
					T t = iter.next();
					add((Class<? extends T>) t.getClass());
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
				} catch (IOException e) {
				}
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
			f.delete();
			return true;
		} catch (URISyntaxException e) {
			return false;
		}
	}

	@Override
	public boolean isDeletable() {
		return true;
	}

}
