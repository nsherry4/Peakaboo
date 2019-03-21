package net.sciencestudio.bolt.plugin.java.issue;

import net.sciencestudio.bolt.plugin.core.issue.BoltEmptyContainerIssue;
import net.sciencestudio.bolt.plugin.java.BoltJavaPlugin;
import net.sciencestudio.bolt.plugin.java.container.BoltJarContainer;

public class BoltEmptyJarIssue<T extends BoltJavaPlugin> extends BoltEmptyContainerIssue<T> {

	public BoltEmptyJarIssue(BoltJarContainer<T> container) {
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
