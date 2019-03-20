package net.sciencestudio.bolt.plugin.java.issue;

import net.sciencestudio.bolt.plugin.core.issue.BoltBrokenContainerIssue;
import net.sciencestudio.bolt.plugin.java.container.BoltJarContainer;

public class BoltBrokenJarIssue extends BoltBrokenContainerIssue {

	private String message;
	
	public BoltBrokenJarIssue(BoltJarContainer<?> container, String message) {
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
