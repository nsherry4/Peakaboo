package net.sciencestudio.bolt.plugin.config;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Function;
import java.util.logging.Level;

import net.sciencestudio.bolt.Bolt;
import net.sciencestudio.bolt.plugin.config.container.BoltConfigContainer;
import net.sciencestudio.bolt.plugin.core.BoltPluginPrototype;
import net.sciencestudio.bolt.plugin.core.container.BoltContainer;

public class IBoltConfigPluginPrototype<T extends BoltConfigPlugin> implements BoltPluginPrototype<T> {

	private Function<String, T> builder;
	private Class<T> pluginClass;
	private T reference;
	private BoltConfigContainer<T> container;
	
	public IBoltConfigPluginPrototype(Function<String, T> builder, Class<T> pluginClass, BoltConfigContainer<T> container) {
		this.builder = builder;
		this.pluginClass = pluginClass;
		this.container = container;
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
		
		InputStream stream;
		try {
			stream = container.openStream();
			T plugin;
			Scanner s = new Scanner(stream).useDelimiter("\\A");
			if (s.hasNext()) {
				String config = s.next();
				plugin = builder.apply(config);
			} else {
				throw new IOException("Could not read file contents");
			}
			s.close();
			stream.close();
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

}
