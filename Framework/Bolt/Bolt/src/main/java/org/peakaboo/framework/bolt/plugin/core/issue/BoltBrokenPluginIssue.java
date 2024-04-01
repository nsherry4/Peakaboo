package org.peakaboo.framework.bolt.plugin.core.issue;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.peakaboo.framework.bolt.plugin.core.BoltPlugin;
import org.peakaboo.framework.bolt.plugin.core.container.BoltContainer;

/**
 * This plugin is broken in some way
 */
public abstract class BoltBrokenPluginIssue<T extends BoltPlugin> implements BoltPluginIssue<T> {
	
	private BoltContainer<T> container;
	private String message;
	
	protected BoltBrokenPluginIssue(BoltContainer<T> container, String message) {
		this.container = container;
		this.message = message;
	}
	
	protected BoltBrokenPluginIssue(BoltContainer<T> container, Throwable e) {
		this.container = container;
		
		StringWriter s = new StringWriter();
		PrintWriter w = new PrintWriter(s);
		e.printStackTrace(w);
		w.close();
		this.message = s.getBuffer().toString();
	}
	
	
	
	
	@Override
	public String title() {
		return "Broken Plugin";
	}
	
	@Override
	public String description() {
		return "The plugin " + getPluginName() + (container!=null ? " from " + shortSource() : "") + " could not be loaded: " + message;
	}

	
	@Override
	public String longSource() {
		return container.getSourcePath();
	}

	@Override
	public String shortSource() {
		return container.getSourceName();
	}

	
	
	protected abstract String getPluginName();
	
}
