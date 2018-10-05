package net.sciencestudio.bolt.plugin.config;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Function;
import java.util.logging.Level;

import net.sciencestudio.bolt.Bolt;
import net.sciencestudio.bolt.plugin.core.BoltPluginPrototype;

public class IBoltConfigPluginPrototype<T extends BoltConfigPlugin> implements BoltPluginPrototype<T> {

	private Function<String, T> builder;
	private Class<T> pluginClass;
	private T reference;
	private URL source;
	
	public IBoltConfigPluginPrototype(Function<String, T> builder, Class<T> pluginClass, URL source) {
		this.builder = builder;
		this.pluginClass = pluginClass;
		this.source = source;
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
			stream = source.openStream();
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
			Bolt.logger().log(Level.WARNING, "Could not create plugin instance: " + source, e);
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
	public URL getSource() {
		return source;
	}
	
	@Override
	public String toString() {
		return getName();
	}

}
