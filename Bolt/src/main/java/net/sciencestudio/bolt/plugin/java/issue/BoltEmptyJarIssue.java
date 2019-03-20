package net.sciencestudio.bolt.plugin.java.issue;

import net.sciencestudio.bolt.plugin.core.issue.BoltEmptyContainerIssue;
import net.sciencestudio.bolt.plugin.java.container.BoltJarContainer;

public class BoltEmptyJarIssue extends BoltEmptyContainerIssue {

	public BoltEmptyJarIssue(BoltJarContainer<?> container) {
		super(container);
	}
	
	@Override
	public String title() {
		return "Empty Jar Container";
	}

	@Override
	public String description() {
		return "The Jar Container " + shortSource() + " does not seem to contain any plugins";
	}
	

	
}
