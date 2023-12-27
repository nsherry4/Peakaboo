package org.peakaboo.framework.bolt.plugin.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.logging.Level;

import org.peakaboo.framework.bolt.Bolt;
import org.peakaboo.framework.bolt.plugin.config.container.BoltConfigContainer;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginRegistry;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginPrototype;
import org.peakaboo.framework.bolt.plugin.core.container.BoltContainer;

public class BoltConfigPluginPrototype<T extends BoltConfigPlugin> implements BoltPluginPrototype<T> {

	private BoltConfigPluginBuilder<T> builder;
	private Class<T> pluginClass;
	private T reference;
	private BoltConfigContainer<T> container;
	private BoltPluginRegistry<T> registry;
	
	public BoltConfigPluginPrototype(BoltPluginRegistry<T> registry, BoltConfigPluginBuilder<T> builder, Class<T> pluginClass, BoltConfigContainer<T> container) {
		this.builder = builder;
		this.pluginClass = pluginClass;
		this.container = container;
		this.registry = registry;
		this.reference = create();
	}
	
	@Override
	public Class<? extends T> getImplementationClass() {
		return (Class<? extends T>) reference.getClass();
	}

	@Override
	public Class<T> getPluginClass() {
		return pluginClass;
	}

	@Override
	public T create() {
		
		try (InputStream stream = container.openStream()) {
			
			T plugin;
			Scanner s = new Scanner(stream).useDelimiter("\\A");
			if (s.hasNext()) {
				String config = s.next();
				plugin = builder.build(config);
			} else {
				s.close();
				throw new IOException("Could not read file contents");
			}
			s.close();
			return plugin;
		} catch (IOException e) {
			Bolt.logger().log(Level.WARNING, "Could not create plugin instance: " + container.getSourceName(), e);
			return null;
		}

		
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public T getReferenceInstance() {
		return reference;
	}

	@Override
	public String getName() {
		if (reference == null) {
			return null;
		}
		return reference.pluginName();
	}

	@Override
	public String getDescription() {
		if (reference == null) {
			return null;
		}
		return reference.pluginDescription();
	}

	@Override
	public String getVersion() {
		if (reference == null) {
			return null;
		}
		return reference.pluginVersion();
	}

	@Override
	public String getUUID() {
		if (reference == null) {
			return null;
		}
		return reference.pluginUUID();
	}

	@Override
	public BoltContainer<T> getContainer() {
		return container;
	}
	
	@Override
	public String toString() {
		return getName();
	}

	@Override
	public BoltPluginRegistry<T> getRegistry() {
		return this.registry;
	}

}
