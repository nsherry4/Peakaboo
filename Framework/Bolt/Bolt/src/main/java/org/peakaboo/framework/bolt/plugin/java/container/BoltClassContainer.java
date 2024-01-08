package org.peakaboo.framework.bolt.plugin.java.container;

import org.peakaboo.framework.bolt.plugin.core.PluginRegistry;
import org.peakaboo.framework.bolt.plugin.java.BoltJavaPlugin;

public class BoltClassContainer<T extends BoltJavaPlugin> extends BoltJavaContainer<T> {
		
	protected Class<? extends T> implClass;
	private PluginRegistry<T> manager;
	
	public BoltClassContainer(PluginRegistry<T> manager, Class<T> targetClass, Class<? extends T> implClass) {
		super(manager, targetClass);
		this.implClass = implClass;
		add(implClass);
	}
	
	@Override
	public String getSourcePath() {
		return implClass.getCanonicalName();
	}

	@Override
	public String getSourceName() {
		return implClass.getSimpleName();
	}

	@Override
	public boolean delete() {
		return false;
	}

	@Override
	public boolean isDeletable() {
		return false;
	}

	@Override
	public PluginRegistry<T> getManager() {
		return this.manager;
	}
	
}
