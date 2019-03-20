package net.sciencestudio.bolt.plugin.core.issue;

import net.sciencestudio.bolt.plugin.core.container.BoltContainer;

/**
 * This container is broken in some way
 */
public abstract class BoltBrokenContainerIssue implements BoltIssue {
	
	private BoltContainer<?> container;

	public BoltBrokenContainerIssue(BoltContainer<?> container) {
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
