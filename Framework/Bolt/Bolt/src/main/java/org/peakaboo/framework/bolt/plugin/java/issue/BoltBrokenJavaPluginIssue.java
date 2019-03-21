package org.peakaboo.framework.bolt.plugin.java.issue;

import org.peakaboo.framework.bolt.plugin.core.issue.BoltBrokenPluginIssue;
import org.peakaboo.framework.bolt.plugin.java.BoltJavaPlugin;
import org.peakaboo.framework.bolt.plugin.java.container.BoltJavaContainer;

public class BoltBrokenJavaPluginIssue<T extends BoltJavaPlugin> extends BoltBrokenPluginIssue<T> {

	private Class<? extends T> cls;
	
	public BoltBrokenJavaPluginIssue(Class<? extends T> cls, BoltJavaContainer<T> container, String message) {
		super(container, message);
		this.cls = cls;
	}
	
	public BoltBrokenJavaPluginIssue(Class<? extends T> cls, BoltJavaContainer<T> container, Throwable e) {
		super(container, e);
		this.cls = cls;		
	}
	

	@Override
	protected String getPluginName() {
		return cls!=null ? cls.getSimpleName() : "'Unknown'";
	}
	
}
