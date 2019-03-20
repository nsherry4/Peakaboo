package net.sciencestudio.bolt.plugin.java.issue;

import net.sciencestudio.bolt.plugin.core.issue.BoltEmptyContainerIssue;
import net.sciencestudio.bolt.plugin.java.container.BoltJarContainer;

public class BoltEmptyJarIssue extends BoltEmptyContainerIssue {

	private BoltJarContainer<?> container;
	
	public BoltEmptyJarIssue(BoltJarContainer<?> container) {
		this.container = container;
	}
	
	@Override
	public String title() {
		return "Empty Jar Container";
	}

	@Override
	public String description() {
		return "The Jar Container " + shortSource() + " does not seem to contain any plugins";
	}
	
	@Override
	public boolean hasFix() {
		return container.isDeletable();
	}
	
	
	@Override
	public boolean fix() {
		return container.delete();
	}
	
	@Override
	public String fixName() {
		return "Delete";
	}
	
	@Override
	public boolean isFixDestructuve() {
		return true;
	}
	
	@Override
	public String shortSource() {
		return container.getSourceName();
	}

	@Override
	public String longSource() {
		return container.getSourcePath();
	}
	
}
