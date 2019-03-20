package net.sciencestudio.bolt.plugin.java.issue;

import net.sciencestudio.bolt.plugin.core.issue.BoltBrokenPluginIssue;
import net.sciencestudio.bolt.plugin.java.container.BoltJavaContainer;

public class BoltBrokenJavaPluginIssue extends BoltBrokenPluginIssue {

	private Class<?> cls;
	
	public BoltBrokenJavaPluginIssue(Class<?> cls, BoltJavaContainer<?> container, String message) {
		super(container, message);
		this.cls = cls;
	}
	
	public BoltBrokenJavaPluginIssue(Class<?> cls, BoltJavaContainer<?> container, Throwable e) {
		super(container, e);
		this.cls = cls;		
	}
	

	@Override
	protected String getPluginName() {
		return cls!=null ? cls.getSimpleName() : "'Unknown'";
	}
	
}
