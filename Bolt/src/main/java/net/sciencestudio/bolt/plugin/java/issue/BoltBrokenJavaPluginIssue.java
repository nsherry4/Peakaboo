package net.sciencestudio.bolt.plugin.java.issue;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;

import net.sciencestudio.bolt.plugin.core.issue.BoltBrokenPluginIssue;

public class BoltBrokenJavaPluginIssue extends BoltBrokenPluginIssue {

	private URL url;
	private String message;
	private Class<?> cls;
	
	public BoltBrokenJavaPluginIssue(Class<?> cls, URL url, String message) {
		this.url = url;
		this.message = message;
		this.cls = cls;
	}
	
	public BoltBrokenJavaPluginIssue(Class<?> cls, URL url, Throwable e) {
		this.url = url;
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
		
		return "The plugin " + (cls!=null ? cls.getSimpleName() : "'Unknown'") + (url!=null ? " from " + shortSource() : "") + " could not be loaded: " + message;
	}

	public String jarname() {
		try {
			return new File(this.url.toURI()).getName();
		} catch (Throwable e) {
			String[] parts = this.url.getFile().split("{/,\\}");
			return parts[parts.length-1];
		}
	}

	@Override
	public String longSource() {
		if (cls != null) {
			return cls.getCanonicalName();
		}
		if (url != null) {
			return url.getFile();
		}
		return "Unknown";
	}

	@Override
	public String shortSource() {
		if (cls != null) {
			return cls.getSimpleName();
		}
		if (url != null) {
			return jarname();
		}
		return "Unknown";
	}
	
}
