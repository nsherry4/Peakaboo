package org.peakaboo.framework.bolt.plugin.java.issue;

import org.peakaboo.framework.bolt.plugin.core.issue.BoltBrokenContainerIssue;
import org.peakaboo.framework.bolt.plugin.java.BoltJavaPlugin;
import org.peakaboo.framework.bolt.plugin.java.container.BoltJarContainer;

public class BoltBrokenJarIssue<T extends BoltJavaPlugin> extends BoltBrokenContainerIssue<T> {

	private String message;
	
	public BoltBrokenJarIssue(BoltJarContainer<T> container, String message) {
		super(container);
		this.message = message;
	}

	@Override
	public String title() {
		return "Broken Jar Container";
	}

	@Override
	public String description() {
		return "Could not read plugin jar " + shortSource() + ": " + message;
	}

	
	
}
