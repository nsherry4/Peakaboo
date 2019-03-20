package net.sciencestudio.bolt.plugin.core.issue;

import net.sciencestudio.bolt.plugin.core.container.BoltContainer;

/**
 * This container contains no plugins
 */
public abstract class BoltEmptyContainerIssue implements BoltIssue {

	private BoltContainer<?> container;
	
	public BoltEmptyContainerIssue(BoltContainer<?> container) {
		this.container = container;
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
