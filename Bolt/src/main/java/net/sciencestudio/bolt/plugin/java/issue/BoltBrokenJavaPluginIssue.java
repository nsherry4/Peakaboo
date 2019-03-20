package net.sciencestudio.bolt.plugin.java.issue;

import java.io.PrintWriter;
import java.io.StringWriter;

import net.sciencestudio.bolt.plugin.core.issue.BoltBrokenPluginIssue;
import net.sciencestudio.bolt.plugin.java.container.BoltJavaContainer;

public class BoltBrokenJavaPluginIssue extends BoltBrokenPluginIssue {

	private BoltJavaContainer<?> container;
	private String message;
	private Class<?> cls;
	
	public BoltBrokenJavaPluginIssue(Class<?> cls, BoltJavaContainer<?> container, String message) {
		this.container = container;
		this.message = message;
		this.cls = cls;
	}
	
	public BoltBrokenJavaPluginIssue(Class<?> cls, BoltJavaContainer<?> container, Throwable e) {
		this.container = container;
		this.cls = cls;
		
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
		return "The plugin " + (cls!=null ? cls.getSimpleName() : "'Unknown'") + (container!=null ? " from " + shortSource() : "") + " could not be loaded: " + message;
	}



	@Override
	public String longSource() {
		return container.getSourcePath();
	}

	@Override
	public String shortSource() {
		return container.getSourceName();
	}
	
}
